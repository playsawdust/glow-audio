/*
 * Glow - GL Object Wrapper
 * Copyright (C) 2020 the Chipper developers
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.playsawdust.chipper.glow.audio;

import java.util.List;

public interface AudioSystem extends AudioResource {
	void init() throws AudioException;
	void init(String deviceName) throws AudioException;
	List<String> getDevices();
	void poll() throws AudioException;
	
}
