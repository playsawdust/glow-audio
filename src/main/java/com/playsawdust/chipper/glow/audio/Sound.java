package com.playsawdust.chipper.glow.audio;

/**
 * Represents a lightweight 16-bit signed audio resource that is immediately available in memory.
 */
public class Sound {
	protected int frequency;
	protected int channels;
	protected short[] data;
	
	/**
	 * Creates a Sound from the data provided
	 * @param frequency the sample frequency, in Hertz
	 * @param channels  how many audio channels are interleaved in the data
	 * @param data      16-bit samples for all channels, interleaved so the array starts with the first sample of each
	 *                  channel, then the second sample of each channel, and so on.
	 */
	public Sound(int frequency, int channels, short[] data) {
		this.frequency = frequency;
		this.channels = channels;
		this.data = data;
	}
	
	public int getFrequency() {
		return frequency;
	}
	
	public int getChannelCount() {
		return channels;
	}
	
	public short[] getData() {
		return data;
	}
	
	public Sound toMono() {
		if (channels==1) return this;
		
		short[] monoData = new short[data.length/channels];
		for(int i=0; i<monoData.length; i++) {
			int src = i*channels;
			int avgSample = 0;
			for(int j=0; j<channels; j++) {
				int loc = src+j;
				if (loc>=data.length) continue;
				avgSample+= data[src+j];
			}
			monoData[i] = (short) (avgSample / channels); //This will be quieter than we'd like, but avoids the risk of overdrive.
		}
		
		return new Sound(frequency, 1, monoData);
	}
}
