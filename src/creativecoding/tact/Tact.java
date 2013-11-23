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
	
	private boolean running = false;
	
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
		} catch (Exception e) {
			// No such method, ignore anyway ...
			// System.out.println ("No listener found");
		}
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
	public void start (int serialIndex) {
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
	public void start (int serialIndex, int baudrate) {
		
		// Check if Serial is availble (in pool)
		if (Serial.list ().length >= serialIndex) {
			
			// Initiate Serial connection
			serial = new Serial (parent, Serial.list ()[serialIndex], baudrate);
			// First of all, clear the port.
			serial.clear ();
			
			running = true;
			
			thread = new Thread (new TactUpdateThread ());
			thread.start ();
			
		}else{
			// If Serial init went wrong...
			System.err.println ("The provided Serial index " + serialIndex + " is out of bounds and thereby not available. Please make sure it's one of the following:");
			
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
	public TactSensor addSensor(String name) {
		TactSensor s = new TactSensor (name);
		sensors.add (s);
		return s;
	}
	
	public TactSensor addSensor (String name, int start, int length) {
		TactSensor s = new TactSensor (name, start, length);
		sensors.add (s);
		return s;
	}
	
	public TactSensor addSensor (String name, int start, int length, int step) {
		TactSensor s = new TactSensor (name, start, length, step);
		sensors.add (s);
		return s;
	}
	
	public TactSensor addSensor (String name, int start, int length, int step, int bufferSize) {
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
			running = false;
			thread.interrupt ();
			serial.stop ();
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
	
	private void parseMessage (int value) {
		if (value >= 0 && value < 1024) {
			// Append to value spectrum
			bufferTemp = PApplet.append (bufferTemp, value);
			
		}else if (value == 2000) {
			// Clear the temporary value array
			// to begin with a fresh list
			bufferTemp = new float[0];
		
		}else if (value == 2001) {
			// Finish filling up value array by copying 
			// temp version into the processable counterpart.
			// A wrapped signal - the TactSpectrum
			TactSpectrum spectrum = new TactSpectrum (parent.millis (), bufferTemp.clone (), sensors.get (sensorIndex).start ());
			
			try {
				// Update the designated sensor instance
				// by assining the received spectrum.
				sensors.get (sensorIndex).push (spectrum);
				
				// Tell all listeners (PApplet etc.) that 
				// there new data is available. 
				dispatchEvent (new TactEvent (this, sensors.get (sensorIndex)));
				
			}catch (Exception e) {
				System.err.println("Could not assign TactSpectrum to sensor with index " + sensorIndex + ".");
			}
			
			bufferTemp = new float[0];
			
		}else if (value >= 3000 && value < 3008) {
			sensorIndex = value - 3000;
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
				System.err.println("Disabling tacteEvent() because of an error");
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
	public static final boolean isPowerOfTwo (int value) {
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
			
			while (running) {
				
				if (sensors.size () == 0)
					return;
				
				for (int i=0; i < sensors.size (); i++) {
					
					// Request values
					serial.write ('g');
					serial.write (Integer.toString (i));
					serial.write (';');
					serial.write (Integer.toString (sensors.get (i).start ()));
					serial.write (';');
					serial.write (Integer.toString (sensors.get (i).readings ()));
					serial.write (';');
					serial.write (Integer.toString (sensors.get (i).step ()));
					serial.write (';');
					serial.write (10);
					
					while (serial.available () > 0) {
						
						int b = serial.read();
						
						if (firstByte) {
							buffer = b;
							firstByte = false;
						}else{ 
							buffer += b << 8;
							// Do something neat with
							// incoming value
							parseMessage(buffer);
						    
							// Rest byte-buffer
							buffer = 0;
							firstByte = true;
						}
					}
				}
				
				// Get some rest...
				try {
					Thread.sleep (sleep);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}
}