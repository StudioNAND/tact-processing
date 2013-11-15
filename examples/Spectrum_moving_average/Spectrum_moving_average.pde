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
  noFill ();
  
  // Init Tact toolkit with a buffer size 
  // of 32 spectra. That means that tact will 
  // store the last 31 signals + the current.
  tact = new Tact (this, 32);
  
  // Tell Tact which parts to get 
  // of the sensor's signal spectrum.
  tact.spectrumStart(40);
  tact.spectrumLength(32);
  
  // Register a sensor
  sensor = tact.addSensor ("myTact-A");
  
  // Start listening on serial port 5
  tact.start (5);
}

void draw () {
  background (37, 239, 203);
  
  // The dark line, representing the
  // present sensor signal.
  stroke (90, 170, 130);
  strokeWeight (8);
  drawGraph (sensor.latestValues());
  
  // White line, the moving average -
  // which is the running avg. of the 
  // 32 last signal spectra.
  stroke (255);
  strokeWeight (3);
  drawGraph (sensor.movingAverage().values);
}

/* This functions draws a signal spectrum 
 * based on an array of floats as line graph.
 */
void drawGraph (float[] values) {
  // Distance between single points of the plot
  float xstep = width / float(values.length - 1);
  
  // Start drawing the line  
  beginShape ();
  // For every data point in the list ...
  for (int i=0; i < values.length; i++) {
    // Add new vertex to line shape
    vertex (i * xstep, values[i] * (height / 1024f));
  }
  // End drawing the line
  endShape ();
}

// Stop Tact Serial communication
// when sketch shuts down.
void exit () {
  tact.stop ();
}