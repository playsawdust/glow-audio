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
