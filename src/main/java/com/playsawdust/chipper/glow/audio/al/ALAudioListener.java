package com.playsawdust.chipper.glow.audio.al;

import java.nio.FloatBuffer;

import org.joml.Quaternionf;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.openal.AL10;
import org.lwjgl.system.MemoryStack;

import com.playsawdust.chipper.glow.audio.AudioResource;

public class ALAudioListener implements AudioResource {
	
	protected ALAudioListener() {
	}
	
	public ALAudioListener setGain(float gain) {
		if (gain<0.0f) gain=0.0f; //Can't be negative
		AL10.alListenerf(AL10.AL_GAIN, gain);
		return this;
	}
	
	public ALAudioListener setPosition(Vector3dc pos) {
		AL10.alListener3f(AL10.AL_POSITION, (float) pos.x(), (float) pos.y(), (float) pos.z());
		return this;
	}
	
	public ALAudioListener setPosition(Vector3fc pos) {
		AL10.alListener3f(AL10.AL_POSITION, pos.x(), pos.y(), pos.z());
		return this;
	}
	
	public ALAudioListener setPosition(float x, float y, float z) {
		AL10.alListener3f(AL10.AL_POSITION, x, y, z);
		return this;
	}
	
	public ALAudioListener setVelocity(Vector3dc v) {
		AL10.alListener3f(AL10.AL_VELOCITY, (float) v.x(), (float) v.y(), (float) v.z());
		return this;
	}
	
	public ALAudioListener setVelocity(Vector3fc v) {
		AL10.alListener3f(AL10.AL_VELOCITY, v.x(), v.y(), v.z());
		return this;
	}
	
	public ALAudioListener setVelocity(float x, float y, float z) {
		AL10.alListener3f(AL10.AL_VELOCITY, x, y, z);
		return this;
	}
	
	public ALAudioListener setOrientation(Vector3f at, Vector3f up) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			
			FloatBuffer buf = stack.floats(6);
			at.get(0, buf);
			up.get(3, buf);
			buf.position(0);
			
			AL10.alListenerfv(AL10.AL_ORIENTATION, buf);
		}
		
		return this;
	}
	
	
	public float getGain() {
		return AL10.alGetListenerf(AL10.AL_GAIN);
	}
	
	public Vector3f getPosition() {
		return getVector3f(AL10.AL_POSITION, null);
	}
	
	public Vector3f getPosition(Vector3f dest) {
		return getVector3f(AL10.AL_POSITION, dest);
	}
	
	public Vector3f getVelocity() {
		return getVector3f(AL10.AL_VELOCITY, null);
	}
	
	public Vector3f getVelocity(Vector3f dest) {
		return getVector3f(AL10.AL_VELOCITY, dest);
	}
	
	public Vector3f getLookAt() {
		return getLookAt(null);
	}
	
	public Vector3f getLookAt(Vector3f dest) {
		if (dest==null) dest = new Vector3f();
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer buf = stack.mallocFloat(6);
			
			AL10.alGetListenerfv(AL10.AL_ORIENTATION, buf);
			dest.set(0, buf);
		}
		
		return dest;
	}
	
	public Vector3f getUp() {
		return getUp(null);
	}
	
	public Vector3f getUp(Vector3f dest) {
		if (dest==null) dest = new Vector3f();
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer buf = stack.mallocFloat(6);
			
			AL10.alGetListenerfv(AL10.AL_ORIENTATION, buf);
			dest.set(3, buf);
		}
		
		return dest;
	}
	
	public Quaternionf getOrientation(Quaternionf result) {
		if (result==null) result = new Quaternionf();
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer buf = stack.mallocFloat(6);
			
			AL10.alGetListenerfv(AL10.AL_ORIENTATION, buf);
			result.identity().lookAlong(buf.get(0), buf.get(1), buf.get(2), buf.get(3), buf.get(4), buf.get(5));
		}
		
		return result;
	}
	
	
	private static Vector3f getVector3f(int property, Vector3f dest) {
		if (dest==null) dest = new Vector3f();
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer buf = stack.mallocFloat(3);
			AL10.alGetListenerfv(property, buf);
			
			dest.set(buf);
		}
		
		return dest;
	}

	@Override
	public void delete() {
		/* 
		 * Do nothing. Does not actually "manage native resources" but really needs the contractual threading
		 * guarantees of AudioResource
		 */
	}
}
