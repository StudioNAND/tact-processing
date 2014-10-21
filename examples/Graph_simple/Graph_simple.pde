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
TactGraph graphSpectrum;

void setup() {
  size (800, 600);
  
  // Create new Tact Sensor wrapper
  // listen on serial port 5
  tact = new Tact(this, 5);
  // Instantiate sensor at pin 0
  sensor = tact.addSensor (0, 0, 64, 3);
  
  // Start frequent updates
  tact.start();
  
  // Create signal spectrum graph instance
  // by naming position and dimenstion
  graphSpectrum = new TactGraph (this, 10, 20, width - 40, height - 20);
}

void draw() {
  background(255);
  // Render graph with given sensor 
  // and section the signal in 8 groups (bins)
  graphSpectrum.spectrum (sensor, 8);
}