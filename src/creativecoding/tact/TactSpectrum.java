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

import creativecoding.tact.TactConstants;

/**
 * <p>A <code>TactSpectrum</code> wraps a Tact sensor signal in an obeject.</p>
 * 
 * @author Steffen Fiedler, <a href="http://www.nand.io" target="_blank">www.nand.io</a>
 * @since 0.1
 */
public class TactSpectrum {
	
	/**
	 * Start index of signal spectrum readinds.
	 */
	private int start;
	/**
	 * End index of signal spectrum readings.
	 */
	private int end;
	/**
	 * Step width between signal spectrum readinds.
	 */
	private int step;
	
	/**
	 * Signal spectrum values. These are the core values that represent 
	 * the sensor reading. The first one at [0] references to the 
	 * <code>start</code> index within the spectrum, whereas the following 
	 * ones are measured with an interval of <code>step</code>.
	 */
	public float[] values;
	
	/**
	 * Timestamp when signal was received.
	 */
	public long time;
	
	/**
	 * Creates a new <code>TactSpectrum</code> instance. This 
	 * will hold a Tact value set (signal spectrum) which has 
	 * been received at a specific point in time.<br />
	 * <br />
	 * 
	 * @param time the point in time when the value set has been 
	 *             received/recorded as <code>long</code> (UNIX stamp).
	 * @param values the Tact signal as an array of <code>float</code> 
	 *               values
	 * @param start
	 */
	public TactSpectrum (long time, float[] values, int start, int step) {
		this.time = time;
		this.values = values;
		this.start = start;
		this.step = step;
		end = start + values.length * step;
	}
	
	public TactSpectrum (long time, int start, int length, int step) {
		this(time, new float[length], start, step);
	}
	
	/**
	 * Signal maxima in <code>values</code> list.
	 * 
	 * @return absolute value for maxima in spectrum.
	 * @since 0.1
	 */
	public float max() {
		float max = values[0];
		for (int i=1; i < values.length; i++)
			if (values[i] > max)
				max = values[i];
		return max;
	}
	
	/**
	 * Signal minima in <code>values</code> list.
	 * 
	 * @return absolut value for minimum in spectrum.
	 * @since 0.1
	 */
	public float min() {
		float min = values[0];
		for (int i=1; i < values.length; i++)
			if (values[i] < min)
				min = values[i];
		return min;
	}
	
	/**
	 * Size of the represented signal spectrum.
	 * 
	 * @return 
	 * @see #values
	 * @since 0.1
	 */
	public int length () {
		return values.length;
	}
	
	/**
	 * Start index where measurments are taken from sensor signal. 
	 * This attribute is defined on instantiation and can not be modified.
	 * 
	 * @return Start index within signal spectrum 
	 *         where <code>values</code> are taken 
	 *         from as <code>int</code>.
	 * @since 0.1
	 */
	public int start () {
		return start;
	}
	
	/**
	 * End index where measurements are taken from sensor signal. 
	 * This attribute is defined on instantiation and can not be modified.
	 * 
	 * @return End index within signal spectrum where 
	 *         <code>values</code> are taken from as 
	 *         <code>int</code>.
	 * @since 0.1
	 */
	public int end () {
		return end;
	}
	
	public int step () {
		return step;
	}
	
	public float bias () {
		return (float) maxAt () / length();
	}
	
	public float peak () {
		return max () / TactConstants.AMPLITUDE_MAX;
	}
	
	/**
	 * Spectrum index where signal maximum is present. This index 
	 * referes to the location of the value within the 
	 * <code>values</code> array. To determine the absolute signal 
	 * index: add <code>maxAt()</code> times <code>step</code> to 
	 * the <code>start</code> index.
	 * 
	 * @return Position of the signal maximum within the 
	 *         <code>values</code> array as <code>int</code>.
	 * @since 0.1
	 */
	public int maxAt () {
		int index = 0;
		float max = values[index];
		for (int i=1; i < values.length; i++) {
			if (values[i] > max) {
				max = values[i];
				index = i;
			}
		}
		return index;
	}
	
	/**
	 * Spectrum index where signal minimum is present. 
	 * This index refers to the location of the value within 
	 * the <code>values</code> array. To determine the absolte 
	 * signal index: add <code>minAt()</code> times <code>step
	 * </code> to the <code>start</code> index.
	 * <pre>
	 * int sigMinIndex = start + minAt() * step;
	 * </pre>
	 * 
	 * @return Position of the signal minimum within the 
	 *         <code>values</code> array as <code>int</code>.
	 * @since 0.1
	 */
	public int minAt () {
		int index = 0;
		float min = values[index];
		for (int i=1; i < values.length; i++) {
			if (values[i] < min) {
				min = values[i];
				index = i;
			}
		}
		return index;
	}
	
	public float[] smooth() {
		float[] smooth = new float[values.length];
		
		if (values.length > 2) {
			
			smooth[0] = (values[0] + values[1]) / 2;
			smooth[smooth.length - 1] = (values[values.length - 2] + values[values.length - 1]) / 2;
			
			for (int i=1; i < smooth.length - 1; i++) 
				smooth[i] = (values[i-1] + values[i] + values[i+1]) / 3;
		}
		
		return smooth;
	}
	
	/**
	 * Groups signal spectrum into bins. The number of 
	 * bins must be power of two and shall not exceede 
	 * the number of actual <code>values</code>.
	 * 
	 * @param resolution number of groups. Power of two 
	 *        integer value; not exceeding spectrum 
	 *        <code>values</code> length.
	 * @return signal spectrum grouped into bins as list 
	 *         of <code>float</code> values.
	 * @since 0.1
	 */
	public float[] bins (int resolution) {
		// Check if resolution is pow2-valid
		if (Tact.isPowerOfTwo (resolution)) {
			
			// Init empty bins and define bin size
			float[] bins = new float[resolution];
			int binSize = length () / resolution;
			
			// Add up spectrum volume per bin
			for (int i=0; i < values.length; i++) 
				bins[(int) Math.floor (i / binSize)] += values[i];
			
			// Device each bin by the number of spectrum entities
			for (int i=0; i < bins.length; i++)
				bins[i] /= binSize;
			
			// Here you go...
			return bins;
		}else{
			// Print error message if given resolution
			// is not valid in terms of non-pow-2
			System.err.println ("Can't use bins() with a non power-of-two resolution. Make sure that it follows the concept of 2, 4, 8, ... etc.");
			
			// Return empty bin array
			return new float[0];
		}
	}
}
