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
 * <p>The purpose of this class is to wrap a </p>
 * 
 * <p>The following instanciation creates a sensor instances that will deliver signal 
 * spectra which start at 44 and contain 32 measure points with a step size of 2. 
 * The sensor instance will also store the 64 most recent <code>TactSpectrum</code> 
 * in its <code>buffer</code>.</p>
 * <pre>
 * TactSensor s = tact.addSensor("myTact-A", 44, 32, 2, 64);
 * </pre>
 * 
 * @author Steffen Fiedler, <a href="http://www.nand.io" target="_blank">www.nand.io</a>
 * @see Tact#addSensor(String)
 * @since 0.1
 */
public class TactSensor implements TactConstants {
	
	/**
	 * Readable identifier as <code>String</code>.
	 */
	private String name;
	
	/**
	 * Total number of received sensor updates.
	 */
	private long receivedCount = 0;
	
	/**
	 * Number of measures that will be made when 
	 * retrieving the signal spectrum.
	 * @see TactSpectrum#length()
	 */
	private int readings;
	
	/**
	 * Signal spectrum start index.
	 * @see TactSpectrum#start()
	 */
	private int start;
	
	/**
	 * Signal spectrum step size. Number of units between 
	 * the measure points within the signal spectrum.
	 */
	private int step;
	
	/**
	 * List of previously received signal spectra. This buffer thereby 
	 * stores <code>TactSpectrum</code> instances in chronological order. 
	 * The latest is located at the end (length - 1).<br />
	 * <pre>
	 * TactSpectrum latest = sensor.buffer[sensor.length() - 1];
	 * </pre>
	 * The number of signal spectra that the buffer holds is defined by 
	 * default through {@link TactConstants#DEFAULT_SPECTRUM_BUFFER_SIZE}, 
	 * and can be modified when creating the sensor.
	 * <pre>
	 * // Creates a sensor with a buffer size of 64
	 * tact.addSensor("mySensor-A", 44, 32, 2, 64);
	 * </pre>
	 * 
	 * @see #length()
	 * @since 0.1
	 */
	public TactSpectrum[] buffer;
	
	/**
	 * Bias buffer storing the 1024 most recent values. 
	 * Structured in chronological order is the latest 
	 * value located at the end of the list.
	 * 
	 * @see #bias()
	 * @since 0.1
	 */
	public float[] bias;
	
	/**
	 * Peak buffer storing the 1024 most recent values. 
	 * Structured in chronological order is the latest 
	 * value located at the end of the list.
	 * 
	 * @see #peak()
	 * @since 0.1
	 */
	public float[] peak;
	
	/**
	 * Minimum bias value measured so far.
	 */
	private float biasMin = Float.MAX_VALUE;
	/**
	 * Maximum peak value measured so far.
	 */
	private float biasMax = Float.MIN_VALUE;
	
	/**
	 * Minimum peak value measured so far.
	 */
	private float peakMin = Float.MAX_VALUE;
	/**
	 * Maximum peak value measured so far.
	 */
	private float peakMax = Float.MIN_VALUE;
	
	
	/**
	 * Creates a Tact sensor instance.
	 * 
	 * @param name readable idendifier as <code>String</code>.
	 * @param start of the signal spectrum.
	 * @param readings of the signal spectrum.
	 * @param step number of signal measures after each a reading 
	 *             is taken from the sensor's signal spectrum
	 * @param bufferSize size of the <code>TactSpectrum</code> buffer.
	 * @since 0.1
	 */
	public TactSensor (String name, int start, int readings, int step, int bufferSize) {
		this.name = name;
		this.readings = readings;
		this.start = start;
		this.step = step;
		
		buffer = new TactSpectrum[bufferSize];
		bias = new float[1024];
		peak = new float[1024];
		
		Date date = new Date ();
		
		for (int i=0; i < bufferSize; i++)
			buffer[i] = new TactSpectrum (date.getTime (), start, readings, step);
	}
	
	/**
	 * Creates a Tact sensor instance.
	 * 
	 * @param name readable identifier as <code>String</code>.
	 * @param start of the signal spectrum.
	 * @param readings of the signal spectrum.
	 * @param step number of signal measures after each a reading 
	 *             is taken from the sensor's signal spectrum
	 * @since 0.1
	 */
	public TactSensor (String name, int start, int readings, int step) {
		this (name, start, readings, step, DEFAULT_SPECTRUM_BUFFER_SIZE);
	}
	
	/**
	 * Creates a Tact sensor instance.
	 * 
	 * @param name readable identifier as <code>String</code>.
	 * @param start of the signal spectrum.
	 * @param readings number of signal spectrum measure points.
	 * @since 0.1
	 */
	public TactSensor (String name, int start, int readings) {
		this (name, start, readings, DEFAULT_SPECTRUM_STEP);
	}
	
	/**
	 * Creates a Tact sensor instance.
	 * 
	 * @param name readable identifier as <code>String</code>.
	 * @since 0.1
	 */
	public TactSensor (String name) {
		this (name, DEFAULT_SPECTRUM_START, DEFAULT_SPECTRUM_READINGS);
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
		
		if (biasMax < b.bias ())
			biasMax = b.bias ();
		
		if (biasMin > b.bias ())
			biasMin = b.bias ();
		
		if (peakMax < b.peak ())
			peakMax = b.peak ();
		
		if (peakMin > b.peak ())
			peakMin = b.peak ();
		
		// Shift exisitng peak histogram
		for (int i=1; i < peak.length; i++)
			peak[i-1] = peak[i];
		// Add present buffer-peak
		peak[peak.length - 1] = b.peak ();
		
		// Level up received counter
		receivedCount++;
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
		
		int start = (receivedCount < buffer.length) ? buffer.length - (int) (receivedCount) : 0;
		int devide = (receivedCount < buffer.length) ? (int) receivedCount : buffer.length;
		
		for (int i=start; i < buffer.length; i++) {
			
			time += buffer[i].time;
			for (int j=0; j < buffer[i].values.length; j++)
				values[j] += buffer[i].values[j];
		}
		
		for (int i=0; i < values.length; i++)
			values[i] /= devide;
		
		return new TactSpectrum (time / devide, values, buffer[0].start (), buffer[0].step());
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
		return (float) latestSpectrum().max() / AMPLITUDE_MAX;
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
	
	/**
	 * Maximum bias value that has been measured so far. This 
	 * value will be <code>Float.MIN_VALUE</code> if no signal 
	 * has been received yet. Use <code>resetBias()</code> to 
	 * reset this constrain.
	 * 
	 * @return maximum bias value measured so far as <code>float</code>.
	 * @see #minBias()
	 * @see #resetBias()
	 * @since 0.1
	 */
	public float maxBias () {
		return biasMax;
	}
	
	/**
	 * Maximum peak value that has been measured so far. This 
	 * value will be <code>Float.MIN_VALUE</code> if no singal 
	 * has been received yet. Use <code>resetPeak()</code> to 
	 * reset this constrain.
	 * 
	 * @return maximum peak value measured so far as <code>float</code>.
	 * @see #minPeak()
	 * @see #resetPeak()
	 * @since 0.1
	 */
	public float maxPeak () {
		return peakMax;
	}
	
	/**
	 * Minimum bias value that has been measured so far. This 
	 * value will be <code>Float.MAX_VALUE</code> if no signal 
	 * has been received yet. Use <code>resetBias()</code> to 
	 * reset this constrain.
	 * 
	 * @return minimum bias value measured so far as <code>float</code>.
	 * @see #maxBias()
	 * @see #resetBias()
	 * @ince 0.1
	 */
	public float minBias () {
		return biasMin;
	}
	
	/**
	 * Minimum peak value that has been measured so far. This 
	 * value will be <code>Float.MAX_VALUE</code> if no signal 
	 * has been received yet. Use <code>resetPeak()</code> to 
	 * reset this constrain.
	 * 
	 * @return minimum peak value measured so far as <code>float</code>.
	 * @see #maxPeak()
	 * @see #resetPeak()
	 * @since 0.1
	 */
	public float minPeak () {
		return peakMin;
	}
	
	/**
	 * Number of measurements taken from the sensor's signal 
	 * spectrum. This value represents the amount of values that 
	 * all <code>TactSpectrum</code> instances of this sensor in 
	 * <code>buffer</code> can store; their <code>length()</code>.
	 * 
	 * @see TactSpectrum#length()
	 * @return 0.1
	 */
	public int readings () {
		return readings;
	}
	
	/**
	 * Sets the number of measurements (readings) taken from the 
	 * signal of the sensor. The width of the read part results 
	 * from this value times <code>step</code>, which represents 
	 * the gap between the readings.<br />
	 * Calling this method will invoke <code>reset()</code> to 
	 * underline structural differences with previous spectra.
	 * 
	 * @param readings number of measurepoints taken from the 
	 *                 signal as <code>int</code>.
	 * @see #reset()
	 * @simce 0.1
	 */
	public void readings (int readings) {
		// Only perform when needed
		if (this.readings != readings) {
			this.readings = readings;
			reset ();
		}
	}
	
	/**
	 * Start index of the delivered spectrum within the sensor 
	 * signal. This value can be initially set when adding the 
	 * sensor.
	 * 
	 * @return Index within the signal where first 
	 *         measurement is taken as <code>int</code>.
	 * @since 0.1
	 */
	public int start () {
		return start;
	}
	
	/**
	 * Sets the start index of the of the spectrum within
	 * the sensor signal.<br />
	 * Calling this method will invoke <code>reset()</code> to 
	 * underline structural differences with previous spectra.
	 * 
	 * @param start Index within the signal where first 
	 *        measurement is taken as <code>int</code>.
	 * @see #reset()
	 * @since 0.1
	 */
	public void start (int start) {
		// Only perform when needed
		if (this.start != start) {
			this.start = start;
			reset ();
		}
	}
	
	/**
	 * Step size for which measurments are made. The value -1 
	 * represents the number of units between all measure points.
	 * 
	 * @return Step-width for made measures as <code>int</code>.
	 * @since 0.1
	 */
	public int step () {
		return step;
	}
	
	/**
	 * Sets the step width between all measure points. A step 
	 * width of 3 results in a gap of two measure units between 
	 * the resulting values.<br />
	 * Calling this method will invoke <code>reset()</code> to 
	 * underline structural differences with previous spectra. 
	 * 
	 * @param step number of measure units after which a 
	 *             reading will be made.
	 * @see #reset()
	 * @since 0.1
	 */
	public void step (int step) {
		// Only perform when needed
		if (this.step != step) {
			this.step = step;
			reset ();
		}
	}
	
	/**
	 * Number of received sensor updates since start or 
	 * <code>reset()</code> call.
	 * 
	 * @return Total number of received updates as
	 *         <code>long</code>.
	 * @see #reset()
	 * @since 0.1
	 */
	public long receivedCount () {
		return receivedCount;
	}
	
	/**
	 * Resets <code>bias()</code> min and max values. Thoses are 
	 * constantly checked and as the case maybe updated. This functions 
	 * allows to reset those constrains by assigning <code>
	 * Float.MAX_VALUE</code> to <code>minBias()</code> and 
	 * vice versa.
	 * 
	 * @see #minBias()
	 * @see #maxBias()
	 * @since 0.1
	 */
	public void resetBias () {
		biasMax = Float.MIN_VALUE;
		biasMin = Float.MAX_VALUE;
	}
	
	/**
	 * Resets <code>peak()</code> min and max values. Those are 
	 * constantly checked and as the case maybe updated. This function 
	 * allows to reset those constrains by assigning <code>
	 * Float.MAX_VALUE</code> to <code>minPeak()</code> and vice versa.
	 * 
	 * @see #minPeak()
	 * @see #maxPeak()
	 * @since 0.1
	 */
	public void resetPeak () {
		peakMax = Float.MIN_VALUE;
		peakMin = Float.MAX_VALUE;
	}
	
	/**
	 * Resets buffers and signal boundaries. Method is called when 
	 * performing <code>readings()</code>, <code>start()</code> and 
	 * <code>step()</code> setters to ensure comparability within 
	 * present and previous <code>TactSpectrum</code> instances. 
	 * Invoking this method will also reset the <code>receivedCount</code>.
	 * 
	 * @see #receivedCount()
	 * @see #readings(int)
	 * @see #start(int)
	 * @see #step(int)
	 * @since 0.1
	 */
	public void reset () {
		// Resets min- and maxima that have 
		// been recorded so far.
		resetBias ();
		resetPeak ();
		
		// Clear histograms
		bias = new float[bias.length];
		peak = new float[peak.length];
		// Clear buffer
		buffer = new TactSpectrum[buffer.length];
		
		receivedCount = 0;
	}
}
