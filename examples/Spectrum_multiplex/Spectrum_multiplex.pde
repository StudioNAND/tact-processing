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

// Min and maxima of bias values 
// that have been measured so far.
float biasMin = Float.MAX_VALUE;
float biasMax = Float.MIN_VALUE;

void setup() {
  
  size (1024, 600);
  strokeWeight (5);
  
  // Init Tact sensor
  tact = new Tact (this);
  
  // Tell Tact which parts to get 
  // of the sensor's signal spectrum.
  tact.spectrumStart (80);
  tact.spectrumLength (32);
  
  // Array to store sensor inputs
  sensors = new TactSensor[sensorNum];
  
  // For each of those inputs
  for (int i=0; i < sensorNum; i++) {
    // Initialize sensor
    sensors[i] = tact.addSensor ("myTact-" + i);
  }
  
  // Start listening on serial port 5
  tact.start (5);
}

void draw() {
  
  // Define how 
  float barWidth = width / float (sensors.length);
  
  // For each sensor ...
  for (int i=0; i < sensors.length; i++) {
    
    // Grab sensor from list at index "i"
    TactSensor sensor = sensors[i];
    
    // Update min and max value based to adjust
    // spectrum boundaries for optimal mapping
    if (sensor.bias() < biasMin)
      biasMin = sensor.bias();
    if (sensor.bias() > biasMax)
      biasMax = sensor.bias();
    
    // Define red value, controlled by the current 
    // bias of "sensor", one of the inputs
    int red = int (map (sensor.bias(), biasMin, biasMax, 0, 255));
    
    // Draw red rectangle and fill
    // it with 
    noStroke ();
    fill (red, 0, 0);
    rect (barWidth * i, 0, barWidth, height);
    
    // Grab sensor signal spectrum
    float[] values = sensor.latestValues();
    
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

// Stop Tact Serial communication
// when sketch shuts down.
void exit () {
  tact.stop ();
}
