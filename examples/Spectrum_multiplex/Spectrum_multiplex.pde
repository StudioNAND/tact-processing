/**
 * Tact for Processing example
 * Studio NAND (http://www.nand.io), Nov 2013
 *
 * https://github.com/StudioNAND/tact-processing
 */

import creativecoding.tact.*;
import processing.serial.*;

// Number of muliplexed sensor inputs
int sensorNum = 3;

Tact tact;
TactSensor[] sensors;

void setup() {
  
  size (1024, 600);
  strokeWeight (5);
  
  // Init Tact sensor
  tact = new Tact (this, 5);
  
  // Array to store sensor inputs
  sensors = new TactSensor[sensorNum];
  
  // For each of those inputs
  for (int i=0; i < sensorNum; i++) {
    // Initialize sensor
    sensors[i] = tact.addSensor (i, 48, 32, 2);
  }
  
  // Start listening on serial port
  tact.start ();
}

void draw() {
  
  // Define how 
  float barWidth = width / float (sensors.length);
  
  // For each sensor ...
  for (int i=0; i < sensors.length; i++) {
    
    // Grab sensor from list at index "i"
    TactSensor sensor = sensors[i];
    TactSpectrum mav = sensor.movingAverage();
    
    // Define red value, controlled by the current 
    // bias of "sensor", one of the inputs
    int red = int (map (mav.peak(), sensor.minPeak(), sensor.maxPeak(), 255, 155));
    
    // Draw red rectangle and fill
    // it with 
    noStroke ();
    fill (red, 0, 0);
    rect (barWidth * i, 0, barWidth, height);
    
    // Grab sensor signal spectrum
    float[] values = mav.smooth();
    
    // Draw signal-spectrum as graph
    // on top of the red rectangle. 
    stroke (255);
    noFill ();
    beginShape ();
    for (int v=0; v < values.length; v++) {
      vertex(barWidth * i + (barWidth / values.length) * v, (height / 1024f) * values[v]);
    }
    endShape ();
  }
}
