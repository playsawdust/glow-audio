/*
 * Glow - GL Object Wrapper
 * Copyright (C) 2020 the Chipper developers
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.playsawdust.chipper.glow.audio.al;

import java.nio.IntBuffer;

import org.joml.Vector3dc;
import org.joml.Vector3fc;
import org.lwjgl.openal.AL10;
import org.lwjgl.system.MemoryStack;

import com.playsawdust.chipper.glow.audio.AudioResource;
import com.playsawdust.chipper.glow.audio.Sound;

import blue.endless.tinyevents.RunnableEvent;

public class ALAudioSource implements AudioResource {
	final int handle;
	private ALAudioBuffer buffer = null;
	private ALAudioBuffer backBuffer = null;
	private boolean streaming = false;
	private boolean deleted = false;
	private State lastState = State.INITIAL;
	
	public RunnableEvent onStop = RunnableEvent.create();
	
	
	
	protected ALAudioSource() {
		this.handle = AL10.alGenSources();
	}
	
	/**
	 * Resets the parameters of this AudioSource to the same as a fresh instance
	 */
	public void reset() {
		stop();
		setPitch(1.0f);
		setGain(1.0f);
		setPosition(0, 0, 0);
		setVelocity(0, 0, 0);
		setRelative(false);
		
		lastState = State.STOPPED;
		onStop = RunnableEvent.create(); //Dump all listeners
	}
	
	/**
	 * Sets the pitch multiplier for this source. The default is 1.0f
	 */
	public ALAudioSource setPitch(float pitch) {
		AL10.alSourcef(handle, AL10.AL_PITCH, pitch);
		return this;
	}
	
	/**
	 * Sets the gain of this source. The default of 1.0f is 100%
	 */
	public ALAudioSource setGain(float gain) {
		AL10.alSourcef(handle, AL10.AL_GAIN, gain);
		return this;
	}
	
	public ALAudioSource setPosition(Vector3dc position) {
		AL10.alSource3f(handle, AL10.AL_POSITION, (float) position.x(), (float) position.y(), (float) position.z());
		return this;
	}
	
	public ALAudioSource setPosition(Vector3fc position) {
		AL10.alSource3f(handle, AL10.AL_POSITION, position.x(), position.y(), position.z());
		return this;
	}
	
	public ALAudioSource setPosition(float x, float y, float z) {
		AL10.alSource3f(handle, AL10.AL_POSITION, x, y, z);
		return this;
	}
	
	public ALAudioSource setVelocity(Vector3dc v) {
		AL10.alSource3f(handle, AL10.AL_VELOCITY, (float) v.x(), (float) v.y(), (float) v.z());
		return this;
	}
	
	public ALAudioSource setVelocity(Vector3fc v) {
		AL10.alSource3f(handle, AL10.AL_VELOCITY, v.x(), v.y(), v.z());
		return this;
	}
	
	public ALAudioSource setVelocity(float x, float y, float z) {
		AL10.alSource3f(handle, AL10.AL_VELOCITY, x, y, z);
		return this;
	}
	
	public ALAudioSource setRelative(boolean relative) {
		AL10.alSourcei(handle, AL10.AL_SOURCE_RELATIVE, (relative) ? AL10.AL_TRUE : AL10.AL_FALSE);
		return this;
	}
	
	public State getState() {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer buf = stack.mallocInt(1);
			AL10.alGetSourcei(handle, AL10.AL_SOURCE_STATE, buf);
			
			return State.valueOf(buf.get(0));
		}
	}
	
	public boolean isStreaming() {
		return streaming;
	}
	
	/*
	 * Causes this source to play an AL buffer directly by its handle. Chances are this is not the method you want.
	 */
	/*
	public void playBuffer(int buffer) {
		AL10.alSourceStop(handle);
		AL10.alSourcei(handle, AL10.AL_BUFFER, buffer);
		AL10.alSourcePlay(handle);
	}*/
	
	public void playSound(Sound sound) {
		if (buffer==null) {
			buffer = new ALAudioBuffer();
		}
		AL10.alBufferData(buffer.getHandle(), (sound.getChannelCount()==1) ? AL10.AL_FORMAT_MONO16 : AL10.AL_FORMAT_STEREO16, sound.getData(), sound.getFrequency());
		
		AL10.alSourceStop(handle);
		AL10.alSourcei(handle, AL10.AL_BUFFER, buffer.getHandle());
		AL10.alSourcePlay(handle);
		lastState = State.PLAYING;
		
		int error = AL10.alGetError();
		if (error!=0) System.out.println("Source error "+error);
	}
	
	public void stop() {
		AL10.alSourceStop(handle);
		poll();
	}
	
	public void setPaused(boolean pause) {
		if (pause) {
			AL10.alSourcePause(handle);
		} else {
			if (getState()!=State.PAUSED) return;
			AL10.alSourcePlay(handle);
		}
	}
	
	/**
	 * Updates this source and fires events.
	 */
	public void poll() {
		State cur = getState();
		if (lastState==State.PLAYING && cur==State.STOPPED) {
			onStop.fire();
		}
		lastState = cur;
	}
	
	public boolean isDeleted() {
		return deleted;
	}
	
	public void delete() {
		if (buffer!=null) {
			buffer.delete();
			buffer = null;
		}
		if (backBuffer!=null) {
			backBuffer.delete();
			backBuffer = null;
		}
		AL10.alDeleteSources(handle);
		deleted = true;
	}
	
	public static enum State {
		INITIAL,
		PLAYING,
		PAUSED,
		STOPPED;
		
		public static State valueOf(int i) {
			switch(i) {
			case AL10.AL_INITIAL: return INITIAL;
			case AL10.AL_PLAYING: return PLAYING;
			case AL10.AL_PAUSED: return PAUSED;
			case AL10.AL_STOPPED: return STOPPED;
			default: return STOPPED;
			}
		}
	}

	public RunnableEvent onStop() {
		return onStop;
	}
}