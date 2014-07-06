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
  
  size(1024, 600);
  
  // Create new Tact toolkit
  tact = new Tact(this);
  
  // Create a Tact sensor and tell it which parts 
  // to get of the sensor's signal spectrum.
  sensor = tact.addSensor("myTact-A", 44, 32);
  
  // Start listening on Serial port 5
  tact.start(5);
  
  // Set fill colour for all shapes 
  // and disable outlines
  fill (255, 178, 0);
  noStroke();
}

void draw () {
  
  background(255, 255, 91);
  
  // Get all bias values 
  float[] bias = sensor.bias;
  
  // For each histogram element
  for (int i=0; i < bias.length; i++) {
    // Define the width of one bar element 
    // so that all fit into the sketch
    float xstep = float(width) / bias.length;
    // Figure out the height of the bar based 
    // on the bias value at "i"
    float rectHeight = height * bias[i];
    // Finally draw the bar
    rect (xstep * i, height - rectHeight, xstep, rectHeight);
  }
}
