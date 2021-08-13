/*
 * Glow - GL Object Wrapper
 * Copyright (C) 2020 the Chipper developers
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.playsawdust.chipper.glow.audio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.joml.Vector3f;

import com.playsawdust.chipper.glow.audio.al.ALAudioListener;
import com.playsawdust.chipper.glow.audio.al.ALAudioSource;
import com.playsawdust.chipper.glow.audio.al.ALAudioSystem;
import com.playsawdust.chipper.glow.audio.io.WavInput;

public class Test {
	
	public static void main(String[] args) {
		try {
			
			//Short wav with 16-bit signed PCM data; an ideal import candidate
			Sound sample = WavInput.read(new FileInputStream("jazz.wav"));
			
			//24-bit samples! Oh my!
			//Sound sample = WavInput.read(new FileInputStream("387313__thpsounds__city-ambience-exterior-cars-passing-by-unedited.wav"));
			//16-bit stereo version
			//Sound sample = WavInput.read(new FileInputStream("city_24.wav"));
			
			try (ALAudioSystem audio = new ALAudioSystem()) {
				System.out.println(audio.getDevices());
				
				audio.init();
				
				//You don't need to set these, these are the defaults.
				ALAudioListener listener = audio.getListener();
				listener.setPosition(0,0,0);
				listener.setGain(1.0f);
				listener.setVelocity(0, 0, 0);
				listener.setOrientation(new Vector3f(0,0,-1), new Vector3f(0,1,0));
				
				//AudioBuffer buffer = audio.getBuffer(sample);
				//int buffer = AL10.alGenBuffers();
				//AL10.alBufferData(buffer, (sample.getChannelCount()==1) ? AL10.AL_FORMAT_MONO16 : AL10.AL_FORMAT_STEREO16, sample.getData(), sample.getFrequency());
				
				
				
				ALAudioSource source = audio.getSource().orElseThrow();
				source.onStop().register(()->{System.out.println("STOP"); });
				source.playSound(sample);
				
				while(source.getState()==ALAudioSource.State.PLAYING) {
					try {
						Thread.sleep(100L);
						audio.poll();
					} catch (InterruptedException ex) {}
				}
				audio.poll();
				
				//source.delete();
				//AL10.alDeleteBuffers(buffer);
				
			} catch (AudioException e) {
				e.printStackTrace();
			}
			
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		
		
		
		
		//JFrame testFrame = new JFrame("Test");
		//testFrame.setSize(800, 600);
		
		
		/*
		//ALC.create();
		ALCCapabilities caps = ALC.getCapabilities();
		//AL10.alGetError();
		boolean alc10 = caps.OpenALC10;
		boolean alc11 = caps.OpenALC11;
		int majorVersion = ALC10.alcGetInteger(ALC_NULL, ALC10.ALC_MAJOR_VERSION);
		int minorVersion = ALC10.alcGetInteger(ALC_NULL, ALC10.ALC_MINOR_VERSION);
		
		
		System.out.println("ALC10: "+alc10+" 11: "+alc11);
		
		String defaultDevice = "";
		
		
		if (caps.ALC_ENUMERATION_EXT) {
			List<String> deviceNames = ALUtil.getStringList(ALC_NULL, ALC10.ALC_DEVICE_SPECIFIER);
			defaultDevice = ALC10.alcGetString(ALC_NULL, ALC10.ALC_DEFAULT_DEVICE_SPECIFIER);
			
			System.out.println("Audio Devices: "+deviceNames.toString());
			System.out.println("Default Audio Device: "+defaultDevice);
		}
		
		if (caps.ALC_ENUMERATE_ALL_EXT) {
			List<String> deviceNames = ALUtil.getStringList(ALC_NULL, EnumerateAllExt.ALC_ALL_DEVICES_SPECIFIER);
			defaultDevice = ALC10.alcGetString(ALC_NULL, EnumerateAllExt.ALC_DEFAULT_ALL_DEVICES_SPECIFIER);
			
			System.out.println("Audio Devices: "+deviceNames.toString());
			System.out.println("Default Audio Device: "+defaultDevice);
		}
		
		// Actual init from example
		
		long device = ALC10.alcOpenDevice(defaultDevice);
		if (device==0) throw new UnsupportedOperationException();
		
		long context = ALC10.alcCreateContext(device, (IntBuffer) null);
		ALC10.alcMakeContextCurrent(context);
		
		ALC10.alcGetError(device);
		
		ALCapabilities alCaps = AL.createCapabilities(caps);
		
		
		
		boolean eax2 = AL10.alIsExtensionPresent("EAX2.0");
		System.out.println("EAX2.0: "+eax2);
		
		
		List<String> extensions = ALUtil.getStringList(device, ALC10.ALC_EXTENSIONS);
		System.out.println("Supported Extensions: "+extensions);
		*/
		
		
		
		
		
		
		//err = AL10.alGetError();
		//if (err!=0) System.out.println("Error: "+err);
		
		//String deviceExtensions = AL10.alGetString(AL10.AL_EXTENSIONS);
		//List<String> deviceExtensionsSplit = new ArrayList<String>();
		//for(String s : deviceExtensions.split(" ")) deviceExtensionsSplit.add(s);
		//System.out.println("Device Extensions: "+deviceExtensionsSplit);
		
		//ALC10.alcDestroyContext(context);
		//ALC10.alcCloseDevice(device);
	}
}
