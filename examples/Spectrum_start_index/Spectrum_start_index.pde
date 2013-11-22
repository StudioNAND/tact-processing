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
  
  size(800, 600);
  textSize (24);
  
  // Init Tact sensor
  tact = new Tact (this);
  
  // Create a Tact sensor and tell it which parts 
  // to get of the sensor's signal spectrum.
  sensor = tact.addSensor("myTact-A", 44, 32);
  
  // Start listening on serial port 5
  tact.start (5);
  
  println ("Use the LEFT and RIGHT keys to change the start index of the sensor spectrum.");
}

void draw () {
  // Clear background
  background (4, 255, 80);
  
  // Get the latest sensor values
  float[] values = sensor.latestValues ();

  // Define draw-grid step width based on value-range
  // and sketch-window dimensions, so all looks neat...
  float xstep = float(width) / (values.length - 1);
  float ystep = float(height) / 1024;

  // Set graph drawing properties
  noFill ();
  stroke (255);
  strokeWeight (4);

  // Start drawing new shape
  beginShape ();
  // For each value...
  for (int i=0; i < values.length; i++) {
    // Add vertex to line shape
    vertex (i * xstep, values[i] * ystep);
  }
  // End drawing line
  endShape ();
  
  text ("BIAS: " + sensor.bias(), 30, 90);
}

void keyPressed () {
  // Figure out which current start 
  // index the tact sensor delivers
  int start = tact.spectrumStart();
  
  // Check if left arrow key was pressed,
  // and present index is bigger than 0
  if (keyCode == LEFT && start > 0) {
    // Reduce index by 1
    tact.spectrumStart(start - 1);
  }
  // Same for the right arrow key
  if (keyCode == RIGHT && start < 128) {
    tact.spectrumStart(start + 1);
  }
}

// Stop Tact Serial communication
// when sketch shuts down.
void exit () {
  tact.stop();
}