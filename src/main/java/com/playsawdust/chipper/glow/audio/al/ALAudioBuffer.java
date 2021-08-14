/*
 * Glow - GL Object Wrapper
 * Copyright (C) 2020 the Chipper developers
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.playsawdust.chipper.glow.audio.al;

import java.util.ArrayList;

import org.lwjgl.openal.AL10;

import com.playsawdust.chipper.glow.audio.AudioResource;
import com.playsawdust.chipper.glow.audio.Sound;

/**
 * Represents a sound buffer in the AL. This is used internally by the library to play Sounds.
 */
public class ALAudioBuffer implements AudioResource {
	private final int handle;
	private ArrayList<ALAudioSource> users = new ArrayList<>();
	
	public ALAudioBuffer() {
		this.handle = AL10.alGenBuffers();
	}
	
	public void loadEntire(Sound sound) {
		AL10.alBufferData(handle, (sound.getChannelCount()==1)? AL10.AL_FORMAT_MONO16 : AL10.AL_FORMAT_STEREO16, sound.getData(), sound.getFrequency());
	}
	
	public void addUser(ALAudioSource source) {
		users.add(source);
	}
	
	public void freeUser(ALAudioSource source) {
		users.remove(source);
	}
	
	public boolean isInUse() {
		return !users.isEmpty();
	}
	
	public boolean checkInUse() {
		users.removeIf((it)->it.getState()==ALAudioSource.State.STOPPED);
		return !users.isEmpty();
	}
	
	public int getHandle() {
		return handle;
	}
	
	public void delete() {
		AL10.alDeleteBuffers(handle);
	}
}
