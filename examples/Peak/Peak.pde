/**
 * Tact for Processing example
 * Studio NAND (http://www.nand.io), Okt 2014
 *
 * https://github.com/StudioNAND/tact-processing 
 */

import creativecoding.tact.*;
import processing.serial.*;

Tact tact;
TactSensor sensor;

void setup() {
  
  size(800, 600);
  fill (255);
  
  // Create new Tact toolkit
  tact = new Tact(this, 5);
  
  // Create a sensor and tell Tact only to return 
  // the peak value for the given spectrum range.
  sensor = tact.addSensor(0, 44, 32, Tact.PEAK);
  
  // Start listing on Serial port
  tact.start();
}

void draw() {
  // Clear background
  background(0);
  
  // Define scale based on sensor bias
  float rectWidth = sensor.peak() * width;
  
  // Draw rectangle
  rect (0, 0, rectWidth, height);
}
