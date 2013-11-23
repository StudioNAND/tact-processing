/**
 * Tact for Processing example
 * Studio NAND (http://www.nand.io), Nov 2013
 *
 * https://github.com/StudioNAND/tact-processing 
 */

import creativecoding.tact.*;
import processing.serial.*;

// The number of bins in which the 
// sensor signal will be grouped.
int numberOfBins = 8;

Tact tact;
TactSensor sensor;

void setup() {
  
  size (800, 600);
  
  // Create new Tact toolkit
  tact = new Tact (this);
  
  // Create a Tact sensor and tell it which parts 
  // to get of the sensor's signal spectrum.
  sensor = tact.addSensor("myTact-A", 44, 32);
    
  // Start listing on Serial port 5
  tact.start (5);
}

void draw() {
  
  // Get the current sensor signal grouped 
  // into separated bins, whereby the number 
  // is defined by "numberOfBins"
  float[] bins = sensor.bins (numberOfBins);
  // Figure out how with a rectangle - that 
  // represents a bin - must be to fill the 
  // sketch width when we draw all bins
  float binWidth = width / numberOfBins;
  
  // Disable line strokes
  noStroke();
  
  // For each bin in the bins array
  for (int i=0; i < bins.length; i++) {
    // Set the fill colour based on 
    // the bin-value at "i"
    fill (20, 255 - bins[i] / 4, 0);
    // Draw the rectangle for the i'th bin
    rect (i * binWidth, 0, binWidth, height);
  }
  
  // If the mouse is pressed...
  if (mousePressed) {
    
    // Set the line attributes 
    // to white and 5px width 
    stroke(255);
    strokeWeight (5);
    // No fill, otherwise our line 
    // will be rendered as a polygon
    noFill();
    
    // Get the smooth version
    // of the current sensor signal
    float[] values = sensor.latestSpectrum().smooth();
    
    // Start drawing
    beginShape();
    
    // For each data-point in the signal-spectrum
    for (int i=0; i < values.length; i++) {
      // Calculate the x and y position of the point
      float xstep = width / float (values.length - 1);
      float ystep = height / 1024f;
      // Place the vertex
      vertex(i * xstep, ystep * values[i]);
    }
    
    // Stop drawing the line when all 
    // points of the graph have been drawn
    endShape();
  }
}

void keyPressed () {
  // Double the number of bins
  numberOfBins = numberOfBins * 2;
  
  // If the number of bins 
  // exceed 16, reset it to 2
  if (numberOfBins > 16) {
    numberOfBins = 2;
  }
}

// Stop and close the connection
// to the Tact sensor
void exit() {
  tact.stop ();
}
