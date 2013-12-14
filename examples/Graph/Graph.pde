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

TactGraph graphSpectrum;
TactGraph graphBias;
TactGraph graphPeak;

boolean black = false;

void setup () {
  size (900, 750);
  
  // Create new Tact toolkit
  tact = new Tact(this);
  // Create a Tact sensor and tell it which parts 
  // to get of the sensor's signal spectrum.
  sensor = tact.addSensor ("myTact-A", 48, 32, 2);
  
  // Start up the Tact sensor by 
  // listing on Serial port 5
  // (you might have to adjust that)
  tact.start(5);
  
  float w = width - 40;
  //
  graphSpectrum = new TactGraph (this, 10, 40, w, height * 0.45f);
  graphBias = new TactGraph (this, 10, height * 0.55f, w, height * 0.2f);
  graphPeak = new TactGraph (this, 10, height * 0.8f, w, height * 0.2f);
}

void draw () {
  
  // Clears screen either with a black 
  // or white background fill.
  if (black) {
    background (0);
  }else{
    background (255);
  }
  
  // Renders all three diagrams based on 
  // the present sensor data.
  graphSpectrum.spectrum (sensor, 8);
  graphBias.bias (sensor);
  graphPeak.peak (sensor);
}

public void keyReleased () {
  black = !black;
  // Updates the TactGraph theme,
  // either dark or bright ...
  TactGraph.theme = (black) ? TactGraph.bright : TactGraph.dark;
}

// Stop and close the connection
// to the Tact sensor
public void stop () {
  tact.stop ();
}
