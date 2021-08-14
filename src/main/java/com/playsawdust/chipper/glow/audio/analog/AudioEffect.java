/*
 * Glow - GL Object Wrapper
 * Copyright (C) 2020 the Chipper developers
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.playsawdust.chipper.glow.audio.analog;

import java.util.Arrays;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.playsawdust.chipper.glow.audio.SampleIterator;

/**
 * Represents both a consumer and producer for audio data.
 */
public abstract class AudioEffect implements SampleIterator {
	protected int frequency;
	protected int channels;
	protected short[] sourceBuffer;
	protected @Nullable SampleIterator source;
	protected boolean isOpen = false;
	
	/**
	 * Sets the source of this effect. If source is set to null, this effect will behave as if a source is present and
	 * providing all zeroes.
	 * @param src the source to set.
	 */
	public void setSource(@Nullable SampleIterator src) {
		assertClosed();
		this.source = src;
	}
	
	@Override
	public void open(int frequency, int channels) {
		assertClosed();
		
		this.frequency = frequency;
		this.channels = channels;
		this.sourceBuffer = new short[channels];
		if (source!=null) source.open(frequency, channels);
	}
	
	@Override
	public void next(short[] buffer) {
		assertOpen();
		
		if (source==null) {
			Arrays.fill(sourceBuffer, (short) 0);
 		} else {
 			source.next(sourceBuffer);
 		}
		
		apply(sourceBuffer, buffer);
	}
	
	/**
	 * Applies this effect to the sample frame in src, writing the result into dest. src and dest MUST each be at least
	 * numChannels entries long.
	 * @param src  a buffer containing one sample frame (one sample for each channel) of source data
	 * @param dest a buffer to hold output data
	 */
	public abstract void apply(short[] src, short[] dest);
	
	@Override
	public void close() {
		if (!isOpen) return; //Double-close is a valid no-op
		if (source!=null) source.close();
	}
	
	protected void assertOpen() {
		if (!isOpen) throw new IllegalStateException("AudioBus must be open for this operation.");
	}
	
	protected void assertClosed() {
		if (isOpen) throw new IllegalStateException("AudioBus must be closed for this operation.");
	}
}
