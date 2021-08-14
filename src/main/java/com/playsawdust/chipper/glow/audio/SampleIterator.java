package com.playsawdust.chipper.glow.audio;

/**
 * Represents a non-seekable ordered sequence of sample data. This data is provided one sample frame at a time. A sample
 * frame is a single sample for each channel in the stream.
 * 
 * <p>
 * SampleIterators require opening before use to communicate the desired audio characteristics, and closing after use.
 * Instances can be reopened after closing, but not rewinded; the samples produced after reopening will not necessarily
 * be the same samples produced after the initial opening.
 * 
 * <p>
 * It is strongly suggested that users SHOULD stick to mono or stereo, but if the following channel numbers occur, the
 * channel configuration will be per the WAV "default channel ordering":
 * <ul>
 *   <li>1 / Mono: Center
 *   <li>2 / Stereo: Left, Right
 *   <li>4 / 3.1: Left, Right, Center, LowFrequency
 *   <li>6 / 5.1: FrontLeft, FrontRight, FrontCenter, LowFrequency, BackLeft, BackRight
 *   <li>8 / 7.1: FrontLeft, FrontRight, FrontCenter, LowFrequency, BackLeft, BackRight, SideLeft, SideRight
 * <ul>
 * 
 * <p>
 * If an Iterator does not support extra channels, it SHOULD EITHER emit a stereo signal in the first two channels, OR
 * write its mono signal identically into both stereo channels. If a stereo source is asked for a monaural output, it
 * SHOULD emit its left channel instead of mixing its output signal down.
 * 
 * 
 * <p>While closed, the Iterator is considered to have no valid format, and any native resources like file handles will
 * be released. It is a valid no-op to close an Iterator multiple times, but opening twice will throw. It is also an
 * error condition to request the next sample frame while the Iterator is closed.
 * 
 */
public interface SampleIterator extends AutoCloseable {
	/**
	 * Open this Iterator and configure it for a particular frequency and channel configuration. Opens and configures
	 * any sources this Iterator depends on.
	 * 
	 * @param frequency the sample frequency for this Iterator. Each channel will have this many samples per second.
	 * @param channels the number of channels. SHOULD be 1 or 2, but implementations MUST gracefully accept 4, 6, or 8.
	 */
	void open(int frequency, int channels);
	
	/**
	 * Gets the next sample frame from this Iterator. The sample frame consists of one sample from each channel in this
	 * audio source. If there are no more samples available from this source, or a channel is known but unsupported,
	 * zeroes are inserted.
	 * 
	 * @param buffer the destination for the sample frame. buffer.length must be greater than or equal to the number of
	 *               channels the Iterator was opened with.
	 */
	void next(short[] buffer);
	
	/**
	 * Closes this Iterator, invalidates any format information or buffer data it maintains, and releases any resources
	 * associated with this Iterator.
	 */
	void close();
}
