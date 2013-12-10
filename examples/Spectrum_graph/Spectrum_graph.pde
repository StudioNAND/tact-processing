/**
 * Tact for Processing example
 * Studio NAND (http://www.nand.io), Nov 2013
 *
 * https://github.com/StudioNAND/tact-processing
 */

import creativecoding.tact.*;
import processing.serial.*;

Tact tact;
TactSensor sensor;

void setup () {
  size (800, 600);
  strokeWeight (5);

  // Init Tact sensor
  tact = new Tact (this);
  
  // Create a Tact sensor and tell it which parts 
  // to get of the sensor's signal spectrum.
  sensor = tact.addSensor("myTact-A", 44, 32);
  
  // Start listening on serial port 5
  tact.start (5);
}

void draw () {

  // Clear background
  background (255);
  
  // Get the latest sensor values
  float[] values = sensor.latestValues ();

  // Define draw-grid step width based on value-range
  // and sketch-window dimensions, so all looks neat...
  float xstep = float(width) / (values.length - 1);
  float ystep = float(height) / 1024;

  // Start drawing new shape
  beginShape ();
  // For each value...
  for (int i=0; i < values.length; i++) {
    // Add vertex to line shape
    vertex (i * xstep, height - values[i] * ystep);
  }
  // End drawing line
  endShape ();
}

// Called each time when tact received
// fresh data from one of the tact sensors
void tactEvent (TactEvent e) {
  println ("Sensor bias: " + e.sensor.bias());
}

// Stop Tact Serial communication
// when sketch shuts down.
void exit () {
  tact.stop ();
}
