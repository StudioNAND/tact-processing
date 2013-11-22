/**
 * Tact for Processing example
 * Studio NAND (http://www.nand.io), Nov 2013
 *
 * https://github.com/StudioNAND/tact-processing 
 */

import creativecoding.tact.*;
import processing.serial.*;

// Counts how many times we've crossed
// the threshold already
int counter = 0;
// Stores if we've already crossed 
// the threshold limit.
boolean belowThreshold = false;

Tact tact;
TactSensor sensor;

void setup() {
  size(800, 600);
  
  // Create new Tact toolkit
  tact = new Tact(this);
  
  // Create a Tact sensor and tell Tact which parts 
  // to get of the sensor's signal spectrum.
  sensor = tact.addSensor("myTact-A", 44, 32);
  
  // Start listing on Serial port 5
  tact.start(5);
}

void draw() {
  // Clear background
  background(0);
  
  // Figure out threshold value based 
  // on mouse-y position (inverted)
  float threshold = 1 - float(mouseY) / height;
  
  // Y position of the threshold on the screen 
  // for drawing the indicator line
  float yThreshold = (1 - threshold) * height;
  
  // Same for the current bias y-position
  float yBias = (1 - sensor.bias()) * height;
  
  // If threshold is below (above) the 
  // by the mouse defined limit..
  if (threshold < sensor.bias()) {
    // If the status flag is not "true" yet,
    // then we've just crossed the threshold 
    // limit -> counter has to go up!
    if (belowThreshold == false) {
      // Set the flag to "true" and count
      belowThreshold = true;
      counter = counter + 1;
    }
  }else{
    // If we're not below it, just check 
    // if we've been and if so, reset the status flag
    if (belowThreshold == true) {
      belowThreshold = false;
    }
  }
  
  // Draw the threshold indicator line
  stroke(255);
  strokeWeight(3);
  line(0, yThreshold, width, yThreshold);
  
  // Draw the bias indicator line
  strokeWeight(12);
  if (threshold < sensor.bias())
    stroke(255, 0, 102);
  line(0, yBias, 180, yBias);
  
  // Draw the counter
  noStroke();
  fill(255);
  textSize(190);
  text(counter, (width - textWidth(str(counter)))/2 , height/2 + 80);
}

// Stop and close the connection
// to the Tact sensor
void exit() {
  tact.stop();
}
