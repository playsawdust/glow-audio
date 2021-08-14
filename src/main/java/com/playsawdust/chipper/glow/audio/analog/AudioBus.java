package com.playsawdust.chipper.glow.audio.analog;

import java.util.Arrays;
import java.util.List;

import com.playsawdust.chipper.glow.audio.SampleIterator;

/**
 * Represents a linear arrangement of AudioEffects. Effects can be added, removed, and rearranged, and sources attached
 * and detached, as long as the Iterator is closed.
 */
public class AudioBus implements SampleIterator {
	protected SampleIterator source;
	protected List<AudioEffect> chain;
	protected boolean isOpen = false;
	protected short[] dry;
	protected short[] wet;
	protected int frequency = 0;
	protected int numChannels = 0;
	
	protected double dryGain = 1.0f;
	protected double wetGain = 1.0f;
	
	/**
	 * Appends an AudioEffect to the end of this Bus. Can only be done while the Bus is closed.
	 * @param effect The AudioEffect to add
	 */
	public void add(AudioEffect effect) {
		assertClosed();
		chain.add(effect);
		stitch();
	}
	
	/**
	 * Adds an AudioEffect to the bus in the specified position. If there is an existing AudioEffect in this position,
	 * it and any subsequent Effects are shifted to the right (adds one to their indices).
	 * @param position The index (starting from zero) the effect should be put in
	 * @param effect The AudioEffect to add to this Bus
	 * @see List#add(int, Object)
	 */
	public void add(int position, AudioEffect effect) {
		assertClosed();
		chain.add(position, effect);
		stitch();
	}
	
	
	/**
	 * Open this AudioBus for sample output and configure the audio format. Double-opening, as with connecting this
	 * bus to two consumers, is not allowed.
	 */
	@Override
	public void open(int frequency, int channels) {
		assertClosed();
		this.frequency = frequency;
		this.numChannels = channels;
		
		if (!chain.isEmpty()) {
			chain.get(chain.size()-1).open(frequency, channels);
		} else {
			if (source!=null) source.open(frequency, channels);
		}
		isOpen = true;
	}

	@Override
	public void next(short[] buffer) {
		assertOpen();
		
		//Get dry data
		short[] dry = new short[numChannels];
		if (source!=null) {
			source.next(dry);
		} else {
			Arrays.fill(dry, (short) 0);
		}
		
		if (!chain.isEmpty()) {
			chain.get(chain.size()-1).next(buffer);
		} else {
			if (source!=null) {
				source.next(buffer);
			} else {
				Arrays.fill(buffer, (short) 0);
			}
		}
	}

	/**
	 * Closes this AudioBus, all effects, and the source AudioSource that feeds it.
	 */
	@Override
	public void close() {
		if (!isOpen) return; //Duplicate closes are a legal no-op
		if (!chain.isEmpty()) {
			chain.get(chain.size()-1).close();
		} else {
			if (source!=null) source.close();
		}
		isOpen = false;
	}
	
	protected void stitch() {
		if (!chain.isEmpty()) {
			if (source!=null) {
				//chain.get(0).setSource(source);
				
				if (chain.size()>1) {
					for(int i=0; i<chain.size(); i++) {
						chain.get(i).setSource(chain.get(i-1));
					}
				}
			}
		}
	}
	
	protected void assertOpen() {
		if (!isOpen) throw new IllegalStateException("AudioBus must be open for this operation.");
	}
	
	protected void assertClosed() {
		if (isOpen) throw new IllegalStateException("AudioBus must be closed for this operation.");
	}
}
