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
  noStroke ();
  
  // Create new Tact toolkit
  tact = new Tact(this, 5);
  
  // Create a sensor and tell Tact only to return 
  // the bias and peak values for the given spectrum range.
  sensor = tact.addSensor(0, 44, 32, 2, Tact.BIAS_PEAK);
  
  // Start listing on Serial port
  tact.start();
}

void draw() {
  // Clear background with two column 
  fill (190, 0, 70);
  rect (0, 0, width/2, height);
  fill (0, 180, 160);
  rect (width/2, 0, width/2, height);
    
  // Define scale based on sensor data
  float biasHeight = sensor.bias() * height;
  float peakHeight = sensor.peak() * height;
  
  // Draw bars based on peak and bias
  fill (255, 0, 100);
  rect (0, height - biasHeight, width/2, biasHeight);
  fill (0, 255, 200);
  rect (width/2, height - peakHeight, width/2, peakHeight);
  
  // Render labels
  fill (255);
  text ("BIAS", 20, 30);
  text ("PEAK", width / 2 + 20, 30);
}