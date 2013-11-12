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
  strokeWeight (3);

  // Init Tact sensor
  tact = new Tact (this);
  
  // Tell Tact which parts to get 
  // of the sensor's signal spectrum.
  tact.spectrumStart(40);
  tact.spectrumLength(32);
  
  sensor = tact.addSensor("Sensor A");
  
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

  // Draw graph whereby value-entry is 
  // represented by a single line section

  // For each value...
  for (int i=1; i < values.length; i++) {
    // Draw line section
    line (
      (i-1) * xstep, 
      values[i-1] * ystep, 
      i * xstep, 
      values[i] * ystep
    );
  }
}

// 
void tactEvent(TactEvent e) {
  println("Signal length: " + e.sensor.latestValues().length);
}

// Stop Tact Serial communication
// when sketch shuts down.
void exit () {
  tact.stop ();
}
