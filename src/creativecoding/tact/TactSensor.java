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

import java.util.Date;

import creativecoding.tact.TactSpectrum;
import creativecoding.tact.TactConstants;

/**
 * <p>A <code>TactSensor</code> represents a top-level capacitive Tact sensor.</p>
 * 
 * @author Steffen Fiedler, <a href="http://www.nand.io" target="_blank">www.nand.io</a>
 * @see Tact#addSensor(String)
 * @since 0.1
 */
public class TactSensor {
	
	/**
	 * @exclude
	 * Readable identifier as <code>String</code>.
	 */
	private String name;
	
	public TactSpectrum[] buffer;
	
	public float[] bias;
	public float[] peak;
	
	/**
	 * Creates a Tact sensor instance.
	 * 
	 * @param name readable idendifier as <code>String</code>.
	 * @param bufferSize size of the <code>TactSpectrum</code> buffer.
	 * @param spectrumStart start of the signal spectrum.
	 * @param spectrumLength length of the signal spectrum.
	 * @since 0.1
	 */
	public TactSensor (String name, int bufferSize, int spectrumStart, int spectrumLength) {
		this.name = name;
		buffer = new TactSpectrum[bufferSize];
		bias = new float[1024];
		peak = new float[1024];
		
		Date date = new Date ();
		
		for (int i=0; i < bufferSize; i++)
			buffer[i] = new TactSpectrum (date.getTime (), spectrumStart, spectrumLength);
	}
	
	/**
	 * Populates buffer with new new values set. This set (signal spectrum)
	 * that has been recorded with a Tact sensor at a specific point in time. 
	 * The most recent (last) added TactSpectrum will always be located at the end 
	 * of the buffer array.<br />
	 * <br />
	 * <code>TactSpectrum recent = sensor.buffer[sensor.buffer.length - 1];</code><br />
	 * <br />
	 * When adding a new TactSpectrum, the bias and peak histograms will also be 
	 * updated using the relevant attributes of the given buffer. This method is only 
	 * used by the tact-core after processing the appropriate Serial events.
	 * 
	 * @param b the buffer that is about to be assigend to the existing set of 
	 *          TactSpectrum instances which have been recorded before.
	 * @return <code>void</code>
	 * @see #bias
	 * @see #buffer
	 * @see #peak
	 * @see TactSensor#latestSpectrum()
	 * @since 0.1
	 */
	public void push (TactSpectrum b) {
		
		// Update buffer and shift all exisiting entries 
		// one index forward, to add presnt at the end
		for (int i=1; i < buffer.length; i++)
			buffer[i-1] = buffer[i];
		buffer[buffer.length - 1] = b;
		
		// Shift existing bias histrogram
		for (int i=1; i < bias.length; i++)
			bias[i-1] = bias[i];
		// Add present buffer-bias
		bias[bias.length - 1] = b.bias ();
		
		// Shift exisitng peak histogram
		for (int i=1; i < peak.length; i++)
			peak[i-1] = peak[i];
		// Add present buffer-peak
		peak[peak.length - 1] = b.peak ();
	}
	
	/**
	 * Generates a moving average (MAV) of the present <code>buffer</code>. 
	 * Thereby all TactSpectrum will be included as subsets to compute 
	 * the overall average per {@link TactSpectrum#values} datum.<br />
	 * <br />
	 * <code>float[] mav = sensor.movingAverage();</code>
	 * 
	 * @return an array of <code>float</code> values that contains 
	 *         the moving avaerage of the present {@link #buffer}.
	 * @see #buffer
	 * @see TactSpectrum#values
	 * @since 0.1
	 */
	public TactSpectrum movingAverage () {
		
		float values[] = new float[buffer[0].length ()];
		long time = 0;
		
		for (int i=0; i < buffer.length; i++) {
			
			time += buffer[i].time;
			for (int j=0; j < buffer[i].values.length; j++)
				values[j] += buffer[i].values[j];
		}
		
		for (int i=0; i < values.length; i++)
			values[i] /= buffer.length;
		
		return new TactSpectrum (time / buffer.length, values, buffer[0].start ());
	}
	
	/**
	 * Readable identifier of the <code>TactSensor</code>. 
	 * This term has been defined with the instantiation 
	 * of the sensor.<br />
	 * <br />
	 * <code>sensor = Tact.addSensor("myTact-A");</code>
	 * 
	 * @return name of the sensor as <code>String</code>
	 * @see Tact#addSensor(String)
	 * @since 0.1
	 */
	public String name () {
		return name;
	}
	
	/**
	 * Size of the sensor <code>buffer</code>. Thereby the number 
	 * of {@link TactSpectrum} instances that will be stored in 
	 * retrospective. Invoking this method is a short version for 
	 * inspecting the length of the <code>buffer</code> manually:<br />
	 * <br />
	 * <code>int l = sensor.buffer.length;</code>
	 * 
	 * @return number of TactSpectrum instances that will be stored 
	 *         in retrospective as <code>int</code>.
	 * @see #buffer
	 * @since 0.1
	 */
	public int length () {
		return buffer.length;
	}
	
	/**
	 * The method used for accessing the most recent processed Tact value set 
	 * (signal spectrum). The result represents a Tact signal at a specific 
	 * point in time, that has been added via <code>push(TactSpectrum)</code>. It's 
	 * taken from the internal <code>buffer</code> where it's located at the end of 
	 * the array. To take hold manually:<br />
	 * <br />
	 * <code>TactSpectrum s = sensor.buffer[sensor.buffer.length - 1];</code>
	 * 
	 * @return most recently assigned {@link TactSpectrum}; or a blank 
	 *         instance that has been instantiated by the 
	 *         <code>TactSensor()</code> constructor.
	 * @see #buffer
	 * @see TactSpectrum
	 * @since 0.1
	 */
	public TactSpectrum latestSpectrum () {
		return buffer[buffer.length - 1];
	}
	
	/**
	 * Most recent value set (signal spectrum). It thereby has been taken from 
	 * the latest <code>TactSpectrum</code> which was assigned to the buffer 
	 * and is meant to provide an easier access to the present raw signal.<br />
	 * 
	 * <pre>
	 * float[] set = sensor.latestValues();
	 * for (int i=0; i < set.length; i++) {
	 *    System.out.println(set[i]);
	 * }
	 * </pre>
	 * 
	 * @return an array of <code>float[]</code> values that present the Tact value 
	 *         set (signal spectrum).
	 * @see TactSpectrum#values
	 * @since 0.1
	 */
	public float[] latestValues () {
		return buffer[buffer.length - 1].values;
	}
	
	/**
	 * Signal <code>bias</code> of the most recent spectrum.  
	 * 
	 * @return present sensor bias as <code>float</code> value.
	 * @see TactSpectrum#bias()
	 * @since 0.1
	 */
	public float bias () {
		return latestSpectrum().bias();
	}
	
	/**
	 * Signal <code>peak</code> of the most recent spectrum.
	 * 
	 * @return present sensor peak as <code>flaot</code> value.
	 * @see TactSpectrum#peak()
	 * @since 0.1
	 */
	public float peak () {
		return (float) latestSpectrum().max() / TactConstants.AMPLITUDE_MAX;
	}
	
	/**
	 * Signal <code>bins</code> of the most recent spectrum.
	 * 
	 * @param resolution 
	 * @return
	 * @see TactSpectrum#bins(int)
	 * @since 0.1
	 */
	public float[] bins (int resolution) {
		return latestSpectrum ().bins (resolution);
	}
}
