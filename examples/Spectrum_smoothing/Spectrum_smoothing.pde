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
  
  size (1024, 600);
  noFill ();
  textSize (13);
  
  // Init Tact sensor
  tact = new Tact (this);
  // Registers a new sensor
  sensor = tact.addSensor (0, 48, 32, 1, 28);
  // Start listening on serial port 5
  tact.start (5);
}

void draw () {
  
  background (33, 188, 245);
  stroke (255);
  strokeWeight (3);
  
  // Divide height into four equal parts
  float quarter = height / 4;
  
  // The orignal, raw signal from the tact sensor
  drawGraph (sensor.latestValues(), 0, quarter);
  text ("real-time, raw", 10, 30);
  
  // The smoothed real-time signal
  drawGraph (sensor.latestSpectrum().smooth(), quarter, quarter);
  text ("real-time, smoothed", 10, quarter);
  
  // The moving average signal based on the tact buffer. 
  drawGraph (sensor.movingAverage().values, quarter * 2, quarter);
  text ("moving average, raw", 10, quarter * 2);
  
  // The smoothed moving average signal
  drawGraph (sensor.movingAverage().smooth(), quarter * 3, quarter);
  text ("moving average, smoothed", 10, quarter * 3);
}

/* This functions draws a signal spectrum 
 * based on an array of floats as line graph.
 */
void drawGraph (float[] values, float yGraph, float heightGraph) {
  // Distance between single points of the plot
  float xstep = width / float(values.length - 1);
  
  // Start drawing the line  
  beginShape ();
  // For every data point in the list ...
  for (int i=0; i < values.length; i++) {
    // Add new vertex to line shape
    vertex (i * xstep, yGraph + (heightGraph - values[i] * (heightGraph / 1024f)));
  }
  // End drawing the line
  endShape ();
}
