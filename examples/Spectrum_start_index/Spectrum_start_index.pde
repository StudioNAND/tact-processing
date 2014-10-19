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
  tact = new Tact (this, 5);
  
  // Create a Tact sensor and tell it which parts 
  // to get of the sensor's signal spectrum.
  sensor = tact.addSensor(0, 28, 32, 2, 64);
  
  // Start listening on serial port
  tact.start ();
  
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
    vertex (i * xstep, height - values[i] * ystep);
  }
  // End drawing line
  endShape ();
  
  text ("BIAS: " + sensor.bias(), 30, 90);
}

void keyPressed () {
  // Figure out which current start 
  // index the tact sensor delivers
  int start = sensor.start();
  
  // Check if left arrow key was pressed,
  // and present index is bigger than 0
  if (keyCode == LEFT && start > 0) {
    // Reduce index by 1
    sensor.start(start - 1);
  }
  // Same for the right arrow key
  if (keyCode == RIGHT && start < 128) {
    sensor.start(start + 1);
  }
  
  println("Starting now at " + sensor.start());
}
