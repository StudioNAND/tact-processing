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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import creativecoding.tact.TactSpectrum;
import creativecoding.tact.TactEvent;
import creativecoding.tact.TactListener;
import creativecoding.tact.TactSensor;

import processing.core.*;
import processing.serial.Serial;

/**
 * <p>A Tact sensor implementation for the Procesing environment.</p>
 * 
 * @author Steffen Fiedler, <a href="http://www.nand.io" target="_blank">www.nand.io</a>
 * @since 0.1
 */
public class Tact {
	
	/**
	 * Constant for the default baud rate.
	 */
	public static final int BAUD_RATE = 115200;
	
	private ArrayList<TactListener> listeners = new ArrayList<TactListener> ();
	
	private int buffer = 0;
	private boolean firstByte = true;
	
	private int sensorIndex = 0;
	private float[] bufferTemp = new float[0];
	
	/**
	 * Flag if update cycle is currently active.
	 */
	private boolean running = false;
	
	/**
	 * Update cycle rest period.
	 */
	public int sleep = 2;
	
	/**
	 * Tact serial update thread.
	 */
	private Thread thread;
	
	/**
	 * List of all created sensors.
	 * @see #addSensor(String)
	 * @since 0.1
	 */
	public List<TactSensor> sensors;
	
	/**
	 * Point in time until update thread shall be running irrespectively 
	 * of the <code>running</code> flag. This timer is based on 
	 * <code>PApplet</code>'s <code>millis</code> attribute and used 
	 * to awaite initial handshake when starting up connection between 
	 * <code>Tact</code> and sensor.
	 */
	private long runUntil = 0;
	
	Method tactEvent;
	
	PApplet parent;
	Serial serial;
	
	/**
	 * Tact core 
	 * 
	 * <code>Tact tact = new Tact(this, 5);</code>
	 * 
	 * @param parent reference to the main sketch instantiation
	 * @since 0.1
	 */
	public Tact (PApplet parent) {
		this.parent = parent;
		sensors = new ArrayList<TactSensor>();
		
		try {
			tactEvent = parent.getClass ().getMethod ("tactEvent", new Class[] { TactEvent.class });
			// Registers PApplet on exit listener
			// to stop serial communication
			parent.registerMethod ("dispose", this);
		} catch (Exception e) {
			// No such method, ignore anyway ...
			// System.out.println ("[Tact] No listener found: \"tactEvent\"");
		}
	}
	
	/**
	 * Called by PApplet on exit to stop sensor communication.
	 */
	public void dispose () {
		stop();
	}
	
	/**
	 * Starts Tact sensor communication. By doing so Tact will 
	 * initiate an ongoing serial thread that constantly requests 
	 * signal updates for all registered sensors.
	 * 
	 * @param serialIndex the serial port to listen to. This 
	 *                    index corresponds to the order within 
	 *                    the processing.Serial.list() array.
	 * @since 0.1
	 */
	public void start (final int serialIndex) {
		start (serialIndex, BAUD_RATE);
	}
	
	/**
	 * Starts Tact sensor communication with specific baud rate. 
	 * By doing so Tact will initiate an ongoing serial thread that 
	 * constantly requests signal updates for all registered sensors.
	 * 
	 * @param serialIndex the serial port to listen to. This index 
	 *                    corresponds to the order within the 
	 *                    processing.Serial.list() array.
	 * @param baudrate pulses per second.
	 * @since 0.1
	 */
	public void start (final int serialIndex, final int baudrate) {
		
		// Check if Serial is availble (in pool)
		if (Serial.list ().length >= serialIndex) {
			
			// Initiate Serial connection
			serial = new Serial (parent, Serial.list ()[serialIndex], baudrate);
			// First of all, clear the port.
			serial.clear ();
			
			// Start handshake process ... 
			serial.write ('V');
			serial.write (10);
			// Allow this response to happen within the 
			// next two seconds ...
			runUntil = parent.millis () + 2000;
			
			// Start the update thread, waiting for inital 
			// response. This thread to commit suicide when 
			// there is no response after "runUntil" is up.
			thread = new Thread (new TactUpdateThread ());
			thread.start ();
			
		}else{
			// If Serial init went wrong...
			System.err.println ("[Tact] The provided Serial index " + serialIndex + " is out of bounds and thereby not available. Please make sure it's one of the following:");
			
			// List available Serial ports
			for (int i=0; i < Serial.list ().length; i++)
				System.out.println ("[" + i + "]\t" + Serial.list ()[i]);
			
			// Stop all activities
			stop();
		}
	}
	
	/**
	 * Adds a new Tact sensor.
	 * 
	 * @param name sensor identifier as <code>String</code>.
	 * @return the instantiated sensor.
	 * @since 0.1
	 */
	public TactSensor addSensor(final String name) {
		TactSensor s = new TactSensor (name);
		sensors.add (s);
		return s;
	}
	
	/**
	 * Adds a new Tact sensor.
	 * 
	 * @param name sensor identifier as <code>String</code>.
	 * @param start index where signal reading are taken from.
	 * @param readings total number of measurements taken 
	 *        from the signal spectrum.
	 * @return sensor instance as <code>TactSensor</code>.
	 * @since 0.1
	 */
	public TactSensor addSensor (final String name, final int start, final int readings) {
		TactSensor s = new TactSensor (name, start, readings);
		sensors.add (s);
		return s;
	}
	
	/**
	 * Adds a new Tact sensor.
	 * 
	 * @param name sensor identifier as <code>String</code>.
	 * @param start index where signal readings are taken from.
	 * @param readings total number of measurements taken from 
	 *        the sensor's signal spectrum.
	 * @param step width between measure points.
	 * @return sensor instance as <code>TactSensor</code>.
	 * @since 0.1
	 */
	public TactSensor addSensor (final String name, final int start, final int readings, final int step) {
		TactSensor s = new TactSensor (name, start, readings, step);
		sensors.add (s);
		return s;
	}
	
	/**
	 * Adds a new Tact sensor.
	 * 
	 * @param name identifier as <code>String</code>.
	 * @param start index where signal readings are taken from.
	 * @param length total number or measurements taken from 
	 *        the sensor's signal spectrum.
	 * @param step width between measure points.
	 * @param bufferSize number stored <code>TactSpectrum</code> 
	 *        instances that previously have been received.
	 * @return sensor instance as <code>TactSensor</code>.
	 * @since 0.1
	 */
	public TactSensor addSensor (final String name, final int start, final int length, final int step, final int bufferSize) {
		TactSensor s = new TactSensor (name, start, length, step, bufferSize);
		sensors.add (s);
		return s;
	}
	
	/**
	 * Stops all Tact updates.
	 * @since 0.1
	 */
	public void stop () {
		if (running) {
			thread.interrupt ();
			serial.stop ();
			running = false;
		}
	}
	
	/**
	 * If <code>Tacat</code> sensor communication is present. 
	 * This flag will be enabled by the <code>start()</code> 
	 * method and keeps the serial-update thread alive, until 
	 * <code>stop()</code> is triggered. 
	 * @return status if <code>Tact</code> is active as <code>
	 *         boolean</code> flag.
	 * @since 0.1
	 */
	public boolean isRunning () {
		return running;
	}
	
	/**
	 * Parses and acts on incoming serial data.
	 */
	protected void receive () {
		while (serial.available () > 0) {
			
			int b = serial.read();
			
			if (firstByte) {
				buffer = b;
				firstByte = false;
			}else{ 
				buffer += b << 8;
				
				// Do something neat with
				// incoming value
				
				// If sensor connection is not established yet - still awaiting
				// the initial handshake ...
				if (!running) {
					if (buffer >= 5000) {
						running = true;
						
						final int version = buffer - 5000;
						
						System.out.println ("[Tact] says \"Hi\" - Now up and running version " + version + ", good to go!");
					}
				}else if (buffer >= 0 && buffer < 1024) {
					// Append to value spectrum
					bufferTemp = PApplet.append (bufferTemp, buffer);
					
				}else if (buffer >= 2000 && buffer < 2999) {
					
					// Number of spectrum data points that are about
					// to be transmitted from the Tact sensor
					//final int length = buffer - 2000;
					
					// Clear the temporary value array
					// to begin with a fresh list
					bufferTemp = new float[0];
				
				}else if (buffer == 2999) {
					
					// Finish filling up value array by copying 
					// temp version into the processable counterpart.
					// A wrapped signal - the TactSpectrum
					TactSpectrum spectrum = new TactSpectrum (parent.millis (), bufferTemp.clone (), sensors.get (sensorIndex).start (), sensors.get (sensorIndex).step ());
					
					// Update the designated sensor instance
					// by assining the received spectrum.
					sensors.get (sensorIndex).push (spectrum);
					
					try {
						// Tell all listeners (PApplet etc.) that 
						// there new data is available. 
						dispatchEvent (new TactEvent (this, sensors.get (sensorIndex)));
						
					}catch (Exception e) {
						System.err.println("[Tact] Could not assign TactSpectrum to sensor with index " + sensorIndex + ".");
					}
					
					bufferTemp = new float[0];
					
				}else if (buffer >= 3000 && buffer < 3999) {
					sensorIndex = buffer - 3000;
				}else{
					System.out.println ("[Tact] Received unknown byte " + buffer);
				}
			    
				// Rest byte-buffer
				buffer = 0;
				firstByte = true;
			}
		}
	}
	
	/**
	 * @param event
	 * @since 0.1
	 */
	private synchronized void dispatchEvent (TactEvent event) {
		
		// Dispatch to PApplet sketch
		if (tactEvent != null) {
			try {
				tactEvent.invoke (parent, new Object[] { event });
			}catch (Exception ex) {
				System.err.println("[Tact] Disabling tacteEvent() because of an error");
				ex.printStackTrace ();
				tactEvent = null;
			}
		}
		
		// Dispatch event to
		if (listeners.size () > 0) {
			Iterator<TactListener> i = listeners.iterator ();
			while (i.hasNext ()) {
				i.next ().tactEvent (event);
			}
		}
	}
	
	/**
	 * Registers a listener that implements <code>TactListener</code>. 
	 * This listenerer will be informed about processed Tact signals 
	 * via by <code>Tact</code> dispatched <code>TactEvent</code> instances.
	 * 
	 * @param o object to receive <code>TactEvent</code> updates.
	 * @see #removeTactListener(TactListener)
	 * @since 0.1
	 */
	public synchronized void addTactListener (TactListener o) {
		listeners.add (o);
	}
	
	/**
	 * Unregisters a listener that implements <code>TactListener</code>. 
	 * This listener previously received by <code>Tact</code> dispatched 
	 * <code>TactEvent</code> instances about sensor updates.
	 * 
	 * @param o object to unregister from <code>TactEvent</code> updates.
	 * @see #addTactListener(TactListener)
	 * @since 0.1
	 */
	public synchronized void removeTactListener (TactListener o) {
		listeners.remove (o);
	}
	
	/**
	 * Checks if value is power of two. This method is needed by a couple 
	 * of setters that have to perform this kind of test.<br />
	 * Thanks to birryree, http://stackoverflow.com/questions/5082314/power-of-2-formula-help
	 * 
	 * @param value value to test.
	 * @return <code>true</code> if value is power-of-two; 
	 *         <code>false</code> otherwise.
	 * @since 0.1
	 */
	public static final boolean isPowerOfTwo (final int value) {
		return (value & (value - 1)) == 0;
	}
	
	/**
	 * @exclude
	 * Tact serial update thread.
	 * @author Steffen Fiedler (http://www.nand.io/)
	 * @since 0.1
	 */
	public class TactUpdateThread implements Runnable {
		
		public void run () {
			
			if (sensors.size () == 0) {
				System.err.println ("[Tact] updates stopped. There are no sensors registered. Create one using tact.addSensor(\"tact1\")");
				stop();
				return;
			}
									
			// If either sensor init is processed and "running" is true 
			// or handshake is still awaited, which means that "runUntil"
			// time is still active ...
			while (running || parent.millis () < runUntil) {
				
				// If everything is up and running, 
				// request and process sensor data.
				if (running) {
					
					// For each single sensor ...
					for (int i=0; i < sensors.size (); i++) {
						
						// Request values
						serial.write ('G');
						serial.write (' ');
						serial.write (Integer.toString (i));
						serial.write (' ');
						serial.write (Integer.toString (sensors.get (i).start ()));
						serial.write (' ');
						serial.write (Integer.toString (sensors.get (i).readings ()));
						serial.write (' ');
						serial.write (Integer.toString (sensors.get (i).step ()));
						serial.write (10);
						
						// Process response ...
						receive();
						
						// Get some rest...
						try {
							Thread.sleep (sleep);
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
						}
					}
				}else{
					
					// Await sensor's handshake response
					// until "runUntil" time is up ...
					receive ();
				}
			}
			
			// Cancel this thread if the sensor connection has not been 
			// initialised so far and the designated time span is up.
			if (!running && parent.millis () >= runUntil) {
				System.err.println ("[Tact] Sensor is not responding. Please check the connection and make sure that it is running the right Arduino sketch.");
				// Destroy thread :(
				stop();
			}
		}
	}
}