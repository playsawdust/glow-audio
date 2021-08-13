package com.playsawdust.chipper.glow.audio;

import java.util.List;

public interface AudioSystem extends AudioResource {
	void init() throws AudioException;
	void init(String deviceName) throws AudioException;
	List<String> getDevices();
	void poll() throws AudioException;
	
}
