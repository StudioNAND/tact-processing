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

void setup() {
  
  size(800, 600);
  
  // Create new Tact toolkit
  tact = new Tact(this, 5);
  
  // Create a sensor and tell Tact only to return 
  // the bias value for the given spectrum range.
  sensor = tact.addSensor(0, 44, 32, Tact.BIAS);
  
  // Start listing on Serial port
  tact.start();
}

void draw() {
  // Clear background
  background(0);
  // Define diameter based on sensor bias
  float diameter = sensor.bias() * min(width, height);
  // Draw ellipse in sketch center
  ellipse (width/2, height/2, diameter, diameter);
}
