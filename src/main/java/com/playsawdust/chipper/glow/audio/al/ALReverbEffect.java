/*
 * Glow - GL Object Wrapper
 * Copyright (C) 2020 the Chipper developers
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.playsawdust.chipper.glow.audio.al;

import org.lwjgl.openal.AL10;
import org.lwjgl.openal.EXTEfx;

public class ALReverbEffect {
	int handle;
	
	public ALReverbEffect() {
		handle = EXTEfx.alGenEffects();
		EXTEfx.alEffecti(handle, EXTEfx.AL_EFFECT_TYPE, EXTEfx.AL_EFFECT_REVERB);
	}
	
	/**
	 * Reverb Modal Density controls the coloration of the late reverb. Lowering the
	 * value adds more coloration to the late reverb.
	 * 
	 * @param f (0.0 ... 1.0), default 1.0
	 */
	public void setDensity(float f) {
		EXTEfx.alEffectf(handle, EXTEfx.AL_REVERB_DENSITY, f);
	}
	
	/**
	 * The Reverb Diffusion property controls the echo density in the reverberation
	 * decay. It’s set by default to 1.0, which provides the highest density.
	 * Reducing diffusion gives the reverberation a more “grainy” character that is
	 * especially noticeable with percussive sound sources. If you set a diffusion
	 * value of 0.0, the later reverberation sounds like a succession of distinct
	 * echoes.
	 * 
	 * @param f (0.0 ... 1.0), default 1.0
	 */
	public void setDiffusion(float f) {
		EXTEfx.alEffectf(handle, EXTEfx.AL_REVERB_DIFFUSION, f);
	}
	
	/**
	 * The Reverb Gain property is the master volume control for the reflected sound
	 * (both early reflections and reverberation) that the reverb effect adds to all
	 * sound sources. It sets the maximum amount of reflections and reverberation
	 * added to the final sound mix. The value of the Reverb Gain property ranges
	 * from 1.0 (0db) (the maximum amount) to 0.0 (-100db) (no reflected sound at
	 * all).
	 * 
	 * @param f (0.0 ... 1.0), default 0.32
	 */
	public void setGain(float f) {
		EXTEfx.alEffectf(handle, EXTEfx.AL_REVERB_GAIN, f);
	}
	
	/**
	 * The Reverb Gain HF property further tweaks reflected sound by attenuating it
	 * at high frequencies. It controls a low-pass filter that applies globally to
	 * the reflected sound of all sound sources feeding the particular instance of
	 * the reverb effect. The value of the Reverb Gain HF property ranges from 1.0
	 * (0db) (no filter) to 0.0 (-100db) (virtually no reflected sound).
	 * 
	 * @param f (0.0 ... 1.0), default 0.89
	 */
	public void setGainHF(float f) {
		EXTEfx.alEffectf(handle, EXTEfx.AL_REVERB_GAINHF, f);
	}
	
	/**
	 * The Decay Time property sets the reverberation decay time. It ranges from 0.1
	 * (typically a small room with very dead surfaces) to 20.0 (typically a large
	 * room with very live surfaces).
	 * 
	 * @param f (0.1 ... 20.0), default 1.49
	 */
	public void setDecayTime(float f) {
		EXTEfx.alEffectf(handle, EXTEfx.AL_REVERB_DECAY_TIME, f);
	}
	
	/**
	 * The Decay HF Ratio property sets the spectral quality of the Decay Time
	 * parameter. It is the ratio of high-frequency decay time relative to the time
	 * set by Decay Time. The Decay HF Ratio value 1.0 is neutral: the decay time is
	 * equal for all frequencies. As Decay HF Ratio increases above 1.0, the
	 * high-frequency decay time increases so it’s longer than the decay time at low
	 * frequencies. You hear a more brilliant reverberation with a longer decay at
	 * high frequencies. As 103/144 the Decay HF Ratio value decreases below 1.0,
	 * the high-frequency decay time decreases so it’s shorter than the decay time
	 * of the low frequencies. You hear a more natural reverberation.
	 * 
	 * @param f (0.1 ... 2.0), default 0.83
	 */
	public void setDecayHFRatio(float f) {
		EXTEfx.alEffectf(handle, EXTEfx.AL_REVERB_DECAY_HFRATIO, f);
	}
	
	/**
	 * The Reflections Gain property controls the overall amount of initial
	 * reflections relative to the Gain property. (The Gain property sets the
	 * overall amount of reflected sound: both initial reflections and later
	 * reverberation.) The value of Reflections Gain ranges from a maximum of 3.16
	 * (+10 dB) to a minimum of 0.0 (-100 dB) (no initial reflections at all), and
	 * is corrected by the value of the Gain property. The Reflections Gain property
	 * does not affect the subsequent reverberation decay.
	 * 
	 * <p>
	 * You can increase the amount of initial reflections to simulate a more narrow
	 * space or closer walls, especially effective if you associate the initial
	 * reflections increase with a reduction in reflections delays by lowering the
	 * value of the Reflection Delay property. To simulate open or semi-open
	 * environments, you can maintain the amount of early reflections while reducing
	 * the value of the Late Reverb Gain property, which controls later reflections.
	 * 
	 * @param f (0.0 ... 3.16), default 0.05
	 */
	public void setReflectionsGain(float f) {
		EXTEfx.alEffectf(handle, EXTEfx.AL_REVERB_REFLECTIONS_GAIN, f);
	}
	
	/**
	 * The Reflections Delay property is the amount of delay between the arrival
	 * time of the direct path from the source to the first reflection from the
	 * source. It ranges from 0 to 300 milliseconds. You can reduce or increase
	 * Reflections Delay to simulate closer or more distant reflective surfaces—and
	 * therefore control the perceived size of the room.
	 * 
	 * @param f (0.0 ... 0.3), default 0.007
	 */
	public void setReflectionsDelay(float f) {
		EXTEfx.alEffectf(handle, EXTEfx.AL_REVERB_REFLECTIONS_DELAY, f);
	}
	
	/**
	 * The Late Reverb Gain property controls the overall amount of later
	 * reverberation relative to the Gain property. (The Gain property sets the
	 * overall amount of both initial reflections and later reverberation.) The
	 * value of Late Reverb Gain ranges from a maximum of 10.0 (+20 dB) to a minimum
	 * of 0.0 (-100 dB) (no late reverberation at all).
	 * 
	 * <p>
	 * Note that Late Reverb Gain and Decay Time are independent properties: If you
	 * adjust Decay Time without changing Late Reverb Gain, the total intensity (the
	 * averaged square of the amplitude) of the late reverberation remains constant.
	 * 
	 * @param f (0.0 ... 10.0), default 1.26
	 */
	public void setLateReverbGain(float f) {
		EXTEfx.alEffectf(handle, EXTEfx.AL_REVERB_LATE_REVERB_GAIN, f);
	}
	
	/**
	 * The Late Reverb Delay property defines the begin time of the late
	 * reverberation relative to the time of the initial reflection (the first of
	 * the early reflections). It ranges from 0 to 100 milliseconds. Reducing or
	 * increasing Late Reverb Delay is useful for simulating a smaller or larger
	 * room.
	 * 
	 * @param f (0.0 ... 0.1), default 0.011
	 */
	public void setLateReverbDelay(float f) {
		EXTEfx.alEffectf(handle, EXTEfx.AL_REVERB_LATE_REVERB_DELAY, f);
	}
	
	/**
	 * The Air Absorption Gain HF property controls the distance-dependent
	 * attenuation at high frequencies caused by the propagation medium. It applies
	 * to reflected sound only. You can use Air Absorption Gain HF to simulate sound
	 * transmission through foggy air, dry air, smoky atmosphere, and so on. The
	 * default value is 0.994 (-0.05 dB) per meter, which roughly corresponds to
	 * typical condition of atmospheric humidity, temperature, and so on. Lowering
	 * the value simulates a more absorbent medium (more humidity in the air, for
	 * example); raising the value simulates a less absorbent medium (dry desert
	 * air, for example).
	 * 
	 * @param f (0.892 ... 1.0), default 0.994
	 */
	public void setAirAbsorptionGainHF(float f) {
		EXTEfx.alEffectf(handle, EXTEfx.AL_REVERB_AIR_ABSORPTION_GAINHF, f);
	}
	
	/**
	 * The Room Rolloff Factor property is one of two methods available to attenuate
	 * the reflected sound (containing both reflections and reverberation) according
	 * to source-listener distance. It’s defined the same way as OpenAL’s Rolloff
	 * Factor, but operates on reverb sound instead of direct-path sound. Setting
	 * the Room Rolloff Factor value to 1.0 specifies that the reflected sound will
	 * decay by 6 dB every time the distance doubles. Any value other than 1.0 is
	 * equivalent to a scaling factor applied to the quantity specified by ((Source
	 * listener distance) - (Reference Distance)). Reference Distance is an OpenAL
	 * source parameter that specifies the inner border for distance rolloff
	 * effects: if the source comes closer to the listener than the reference
	 * distance, the direct-path sound isn’t increased as the source comes closer to
	 * the listener, and neither is the reflected sound.
	 * 
	 * <p>
	 * The default value of Room Rolloff Factor is 0.0 because, by default, the
	 * Effects Extension reverb effect naturally manages the reflected sound level
	 * automatically for each sound source to simulate the natural rolloff of
	 * reflected sound vs. distance in typical rooms. (Note that this isn’t the case
	 * if the source property flag AL_AUXILIARY_SEND_FILTER_GAIN_AUTO is set to
	 * AL_FALSE) You can use Room Rolloff Factor as an option to automatic control
	 * so you can exaggerate or replace the default automatically-controlled
	 * rolloff.
	 * 
	 * @param f (0.0 ... 10.0), default 0.0
	 */
	public void setRoomRoloffFactor(float f) {
		EXTEfx.alEffectf(handle, EXTEfx.AL_REVERB_ROOM_ROLLOFF_FACTOR, f);
	}
	
	/**
	 * When this flag is set, the high-frequency decay time automatically stays
	 * below a limit value that’s derived from the setting of the property Air
	 * Absorption HF. This limit applies regardless of the setting of the property
	 * Decay HF Ratio, and the limit doesn’t affect the value of Decay HF Ratio.
	 * This limit, when on, maintains a natural sounding reverberation decay by
	 * allowing you to increase the value of Decay Time without the risk of getting
	 * an unnaturally long decay time at high frequencies. If this flag is set to
	 * AL_FALSE, high-frequency decay time isn’t automatically limited.
	 * 
	 * @param limit true or false, default true
	 */
	public void setDecayHFLimit(boolean limit) {
		EXTEfx.alEffecti(handle, EXTEfx.AL_REVERB_DECAY_HFLIMIT, (limit) ? AL10.AL_TRUE : AL10.AL_FALSE);
	}
}
