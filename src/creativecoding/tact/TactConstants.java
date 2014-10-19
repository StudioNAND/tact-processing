/**
 * Copyright (C) 2013 Studio NAND
 * 
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */

package creativecoding.tact;

/**
 * <p>Collection of relevant constants.</p>
 * 
 * @author Steffen Fiedler, <a href="http://www.nand.io" target="_blank">www.nand.io</a>
 * @since 0.1
 */
public interface TactConstants {
	
	/**
	 * 
	 */
	public static final String BIAS = "bias";
	
	/**
	 * 
	 */
	public static final String BIAS_PEAK = "bias and peak";
	
	/**
	 * 
	 */
	public static final String PEAK = "peak";
	
	/**
	 * 
	 */
	public static final String SPECTRUM = "spectrum";
	
	/**
	 * Constant for the <code>TactSpectrum</code> signal minimum.
	 */
	public static final float AMPLITUDE_MIN = 0;
	/**
	 * Constant for the <code>TactSpectrum</code> signal maximum.
	 */
	public static final float AMPLITUDE_MAX = 1024;
	
	public static final int DEFAULT_SPECTRUM_BUFFER_SIZE = 8;
	public static final int DEFAULT_SPECTRUM_READINGS = 32;
	public static final int DEFAULT_SPECTRUM_START = 32;
	public static final int DEFAULT_SPECTRUM_STEP = 1;
	
	public static final String DEFAULT_MODE = SPECTRUM;
	
	public static final int PROTOCOL_COMMAND_COUNT_LIMIT = 10;
	
	public static final int PROTOCOL_COMMAND_SPECTRUM = 0;
	
	public static final int PROTOCOL_COMMAND_PEAK = 1;
	
	public static final int PROTOCOL_COMMAND_BIAS = 2;
	
	public static final int PROTOCOL_SENSOR_INDEX_LIMIT = 64;
	
	public static final int PROTOCOL_TAG_COMMAND_ID = 1088;
	
	public static final int PROTOCOL_TAG_END_OF_TRANSMISSION = 2123;
	
	public static final int PROTOCOL_TAG_SENSOR_INDEX = 1024;

	public static final int PROTOCOL_TAG_VALUE_COUNT = 1098;

	public static final int PROTOCOL_TAG_VERSION = 2124;

	public static final int PROTOCOL_VALUE_COUNT_LIMIT = 1024;
}