/*
 * Glow - GL Object Wrapper
 * Copyright (C) 2020 the Chipper developers
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.playsawdust.chipper.glow.audio;

import com.playsawdust.chipper.glow.audio.al.ALAudioSystem;

/**
 * Tagging interface which indicates that a class represents or maintains native resources in the AL.
 * 
 * <p>Methods on instances of these classes MUST ONLY be called from the same thread that the {@link ALAudioSystem}
 * instance was created on, and responsibility for closing or deleting instances SHOULD be carefully considered.
 * 
 * <p>{@link #close()} is the same as {@link #delete()} - both have the same effect of freeing the native resources, and
 * methods on this instance MUST NOT be called after calling either of these methods.
 */
public interface AudioResource extends AutoCloseable {
	public default void close() {
		delete();
	}
	
	public void delete();
}
