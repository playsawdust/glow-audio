package com.playsawdust.chipper.glow.audio.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;

import com.playsawdust.chipper.glow.audio.Sound;
import com.playsawdust.chipper.glow.io.DataSlice;
import com.playsawdust.chipper.glow.io.riff.ListRiffChunk;
import com.playsawdust.chipper.glow.io.riff.RiffChunk;
import com.playsawdust.chipper.glow.io.riff.RiffInputStream;

public class WavInput {
	public static Sound read(InputStream in) throws IOException {
		RiffChunk chunk = new RiffInputStream(in).readChunk();
		in.close();
		if (chunk instanceof ListRiffChunk) {
			ListRiffChunk list = (ListRiffChunk) chunk;
			
			if (list.getListType().equals("WAVE")) {
				//Grab the two chunks we *need*
				RiffChunk fmtChunk = null;
				RiffChunk dataChunk = null;
				
				for(RiffChunk child : list.getChildren()) {
					if (child.getChunkType().equals("fmt")) fmtChunk = child;
					if (child.getChunkType().equals("data")) dataChunk = child;
					//System.out.println(child.getChunkType());
				}
				
				if (fmtChunk==null) throw new IOException("No formatting data present in this file.");
				if (dataChunk==null) throw new IOException("No waveform data present in this file.");
				
				DataSlice formatSlice = fmtChunk.getChunkData();
				formatSlice.seek(0L); //reset the slice position just in case, but keep it in LE
				int sampleFormat = formatSlice.readI16u();
				if (sampleFormat!=0x0001) throw new IOException("Unknown sample format 0x"+Integer.toHexString(sampleFormat));
				//System.out.println("SampleFormat: "+Integer.toHexString(sampleFormat));
				int numChannels = formatSlice.readI16u();
				//System.out.println("Number of channels: "+numChannels);
				int samplesPerSecond = formatSlice.readI32s();
				int bytesPerSecond = formatSlice.readI32s();
				//System.out.println("Samples per second: "+samplesPerSecond+" bytesPerSecond: "+bytesPerSecond);
				
				int blockAlignment = formatSlice.readI16u();
				int bitsPerSample = formatSlice.readI16u();
				if ((bitsPerSample%8)!=0) throw new IOException("Unusual data encoding!");
				//System.out.println("Bytes per sample: "+blockAlignment+" BitsPerSample: "+bitsPerSample);
				
				//System.out.println("Marker position: "+formatSlice.position()+" length: "+formatSlice.length());
				
				//int cbSize = formatSlice.readI16u();
				//System.out.println("cbSize: "+cbSize);
				
				
				DataSlice waveformData = dataChunk.getChunkData();
				waveformData.seek(0L);
				
				//bitsPerSample /= numChannels;
				int bytesPerSample = bitsPerSample/8;
				int sampleCount = (int) (waveformData.length() / (bytesPerSample*numChannels));
				//if (numChannels>1) sampleCount/=numChannels;
				short[] sampleData = new short[sampleCount*numChannels];
				
				//System.out.println("Bytes per sample: "+bytesPerSample);
				for(int i=0; i<sampleCount; i++) {
					for(int j=0; j<numChannels; j++) {
						sampleData[i*numChannels+j] = readShortSample(bytesPerSample, waveformData);
						
						//if (waveformData.position()+4>waveformData.length()) break;
						//sampleData[i] = waveformData.readI16s();
					}
				}
				return new Sound(samplesPerSecond, numChannels, sampleData);
				//return sampleData;
			} else {
				throw new IOException("File is a valid RIFF but does not contain WAV data.");
			}
		} else {
			throw new IOException("File is a valid RIFF but does not contain WAV data.");
		}
	}
	
	private static short readShortSample(int bytesPerSample, DataSlice in) throws IOException {
		switch(bytesPerSample) {
		case 1:
			int value = in.read() - 128;
			return (short) (value * 256);
		case 2:
			return in.readI16s();
		case 3:
			int a = in.read() & 0xFF;
			int b = in.read() & 0xFF;
			int c = in.read() & 0xFF;
			
			
			
			if (in.getByteOrder()==ByteOrder.BIG_ENDIAN) {
				int reconstruct = (a << 16) | (b << 8) | c;
				if ((a & 0x80) != 0) reconstruct |= 0xFF000000;
				
				//System.out.println("Reconstructing "+Integer.toHexString(a)+" "+Integer.toHexString(b)+" "+Integer.toHexString(c)+" BE ->"+Integer.toHexString(reconstruct));
				
				return (short) (reconstruct / 256);
				//return (short) ((a << 8) | b); //throw away the lowest order bits in c
			} else {
				int reconstruct = (c << 16) | (b << 8) | a;
				if ((c & 0x80) != 0) reconstruct |= 0xFF000000;
				
				//System.out.println("Reconstructing "+Integer.toHexString(a)+" "+Integer.toHexString(b)+" "+Integer.toHexString(c)+" LE ->"+Integer.toHexString(reconstruct));
				
				return (short) (reconstruct / 256);
			}
		case 4:
			return (short) (in.readI32s() >> 16);
			default:
				System.out.println("Reconstructing nonsense");
				return 0;
		}
	}
}
