/*
 * Glow - GL Object Wrapper
 * Copyright (C) 2020 the Chipper developers
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.playsawdust.chipper.glow.audio.analog;

import com.playsawdust.chipper.glow.audio.SampleIterator;

public class ConstantSampleIterator implements SampleIterator {
	protected short[] sampleData;
	
	@Override
	public void open(int frequency, int channels) {
		sampleData = new short[channels];
	}

	@Override
	public void next(short[] buffer) {
		System.arraycopy(sampleData, 0, buffer, 0, Math.min(sampleData.length, buffer.length));
	}

	@Override
	public void close() {
		sampleData = null;
	}
	
	/**
	 * Gets the backing array that stores the sample data. Modifying this array will change the values of the samples
	 * this iterator provides from now on. While the Iterator is closed, there is no sample format and the needed buffer
	 * size is unknown, so this method will return null. While the Iterator is open, the returned array will be large
	 * enough to store one sample frame of data (in other words, one sample for each channel). 
	 */
	public short[] getFrameBuffer() {
		return sampleData;
	}
}
