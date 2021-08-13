/*
 * Glow - GL Object Wrapper
 * Copyright (C) 2020 the Chipper developers
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.playsawdust.chipper.glow.audio.al;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.joml.Vector3d;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALC11;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.openal.ALUtil;
import org.lwjgl.openal.EnumerateAllExt;

import com.playsawdust.chipper.glow.audio.AudioException;
import com.playsawdust.chipper.glow.audio.AudioSystem;

/**
 * AudioSystem is the starting point for getting sounds moving. At a minimum, you need to create an instance of this
 * class and call init() on it. At some point later on, call close() or delete()
 */
public class ALAudioSystem implements AudioSystem {
	private static final long ALC_NULL = 0L;
	
	private boolean hasInit = false;
	private ALCCapabilities alcCaps = null;
	private ALCapabilities alCaps = null;
	private long device = 0L;
	private long context = 0L;
	private ALAudioListener listener = new ALAudioListener();
	
	private int maxEphemeralSources = 8;
	private ArrayDeque<ALAudioSource> inactiveEphemeral = new ArrayDeque<>();
	private ArrayList<ALAudioSource> activeEphemeral = new ArrayList<>();
	
	private ArrayList<ALAudioSource> persistentSources = new ArrayList<>();
	
	public ALAudioSystem() {
		alcCaps = ALC.getCapabilities();
	}
	
	@Override
	public void init() throws AudioException {
		init(null);
	}
	
	public void init(String deviceName) throws AudioException {
		if (alcCaps==null) alcCaps = ALC.getCapabilities();
		if (hasInit) throw new AudioException("This system has already been initialized");
		
		device = ALC10.alcOpenDevice(deviceName);
		if (device==0) {
			if (deviceName==null) {
				throw new AudioException("Could not open the default audio device.");
			} else {
				throw new AudioException("Could not open the audio device \""+deviceName+"\"");
			}
		}
		
		context = ALC10.alcCreateContext(device, new int[] {
				ALC11.ALC_MONO_SOURCES, 65535-32,
				ALC11.ALC_STEREO_SOURCES, 32,
				0
		});
		ALC10.alcMakeContextCurrent(context);
		
		checkErrorALC(context);
		
		alCaps = AL.createCapabilities(alcCaps);
		
		checkErrorAL();
		
		hasInit = true;
		
		
		int deviceFrequency = ALC10.alcGetInteger(device, ALC10.ALC_FREQUENCY);
		//System.out.println("Device frequency: "+deviceFrequency);
		
		if (alcCaps.OpenALC11) {
			int maxSources = ALC11.alcGetInteger(device, ALC11.ALC_MONO_SOURCES);
			//System.out.println("Limit for number of sources: "+maxSources);
		}
		
	}
	
	/**
	 * Gets the AudioListener, of which there is only ever one. The AudioSystem is responsible for freeing this resource.
	 */
	public ALAudioListener getListener() {
		return listener;
	}
	
	public long getContext() { return context; }
	public long getDevice() { return device; }
	
	/**
	 * Creates a new AudioSource. Freeing the returned source is the caller's responsibility.
	 */
	public ALAudioSource createSource() {
		ALAudioSource result = new ALAudioSource();
		persistentSources.add(result);
		return result;
	}
	
	/**
	 * Gets an AudioSource from the system pool. These sources are meant for ephemeral or environmental sounds, and no
	 * methods on them may be called once they transition to the PLAYING State.
	 * @return an AudioSource if one is available, otherwise Optional.empty()
	 */
	public Optional<ALAudioSource> getSource() {
		if (activeEphemeral.size()>=maxEphemeralSources) return Optional.empty(); //TODO: Kill furthest/quietest sounds?
		
		if (inactiveEphemeral.isEmpty()) {
			ALAudioSource result = new ALAudioSource();
			activeEphemeral.add(result);
			return Optional.of(result);
		} else {
			ALAudioSource result = inactiveEphemeral.pop();
			result.reset();
			
			activeEphemeral.add(result);
			return Optional.of(result);
		}
	}
	
	
	public void poll() {
		ArrayList<ALAudioSource> goingInactive = new ArrayList<>();
		for(ALAudioSource source : activeEphemeral) {
			if (source.getState()==ALAudioSource.State.STOPPED) {
				goingInactive.add(source);
			} else {
				if (source.isStreaming()) {
					
				}
			}
		}
		for(ALAudioSource source : goingInactive) source.poll();
		
		activeEphemeral.removeAll(goingInactive);
		inactiveEphemeral.addAll(goingInactive);
		goingInactive.clear();
		
		for(ALAudioSource source : persistentSources) {
			if (source.isDeleted()) {
				goingInactive.add(source);
			} else {
				source.poll();
			}
		}
		persistentSources.removeAll(goingInactive);
	}
	
	public void delete() {
		if (!hasInit) return;
		
		for(ALAudioSource source : activeEphemeral) {
			source.stop();
			source.delete();
		}
		activeEphemeral.clear();
		for(ALAudioSource source : inactiveEphemeral) {
			source.stop();
			source.delete();
		}
		inactiveEphemeral.clear();
		System.out.println("Cleaning up AL/ALC");
		
		ALC10.alcDestroyContext(context);
		context = 0L;
		alcCaps = null;
		alCaps = null;
		ALC10.alcCloseDevice(device);
		device = 0L;
		hasInit = false;
	}
	
	private static void checkErrorALC(long context) throws AudioException {
		int error = ALC10.alcGetError(context);
		if (error!=0) {
			String errorString = ALC10.alcGetString(context, error);
			if (errorString==null) {
				throw new AudioException("ALC encountered error "+error);
			} else {
				throw new AudioException("ALC encountered Error "+error+": "+errorString);
			}
		}
	}
	
	private static void checkErrorAL() throws AudioException {
		int error = AL10.alGetError();
		if (error!=0) {
			String errorString = AL10.alGetString(error);
			if (errorString==null) {
				throw new AudioException("AL encountered error "+error);
			} else {
				throw new AudioException("AL encountered Error "+error+": "+errorString);
			}
		}
	}
	
	public void setListenerPosition(Vector3d pos) {
		
	}

	@Override
	public List<String> getDevices() {
		if (alcCaps.ALC_ENUMERATE_ALL_EXT | alcCaps.OpenALC11) {
			return ALUtil.getStringList(ALC_NULL, EnumerateAllExt.ALC_ALL_DEVICES_SPECIFIER);
		} else {
			return ALUtil.getStringList(ALC_NULL, ALC10.ALC_DEVICE_SPECIFIER);
		}
	}
}
