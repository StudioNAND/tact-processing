/**
 * Tact for Processing example
 * Studio NAND (http://www.nand.io), Nov 2013
 *
 * https://github.com/StudioNAND/tact-processing
 */

import creativecoding.tact.*;
import processing.serial.*;
import java.io.FileWriter;
import java.util.*;
import java.text.*;

Tact tact;
TactSensor sensor;

// We want to log each sensor event with an ISO 8601 date string
// (https://en.wikipedia.org/wiki/ISO_8601) for easier usage with
// other programming/scripting languages such as JavaScript, Python etc.
// For this, we need our time zone and a date formatter
TimeZone timeZone;
DateFormat isoDateFormat;

// Buffer to hold rows that shall be 
// written to CSV document.
String[] buffer = new String[] {"time,bias,peak"};
// Magic that does the file writing bit
FileWriter writer;

// The number of records that have 
// already been save to CSV doc
int counter = 0;

void setup() {
  
  size(800, 600);
  
  // Create new Tact toolkit
  tact = new Tact(this);
  
  // This will slow down the tact sensor
  // by waiting 70 milli-seconds between 
  // each update cycle.
  tact.sleep = 70;
  
  // Create a Tact sensor and tell it which parts 
  // to get of the sensor's signal spectrum.
  sensor = tact.addSensor("myTact-A", 44, 32);
  
  // Create the empty CSV document 
  // (existing once will be replaced)
  try {
    writer = new FileWriter(sketchPath("data/" + sensor.name() + ".csv"));
    writer.close();
  }catch(Exception e) {
    println("Oh boy - failed while trying to create the csv...");
  }
  
  // Start up the Tact sensor by 
  // listing on Serial port 5
  // (you might have to adjust that)
  tact.start(5);
  
  timeZone = TimeZone.getDefault();
  isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
  isoDateFormat.setTimeZone( timeZone );
}

void draw() {
  background (0);
  
  // Figure out what text to display
  String text = str(counter) + "\nrecords";
  
  // Draw text sketch-centred
  textSize(110);
  textAlign(CENTER);
  text (text, width /2, height /2 - 55); 
}

/* Everytime there is fresh data from one of 
 * the Tact sensors, this function will be triggered.
 */
void tactEvent(TactEvent e) {
  // Get hold of the current signal spectrum
  TactSpectrum s = e.sensor.latestSpectrum();
  // Put together the fresh CSV row and store 
  // it inside the buffer, until we've collected enough
  String dateString = isoDateFormat.format( new Date() );
  String row = dateString + "," + s.bias() + "," + s.peak();
  buffer = append(buffer, row);
  
  // If there are more than 50 
  // records inside the buffer...
  if (buffer.length > 50) {
    // Save them!
    writeToFile();
  }
}

void writeToFile() {
  
  // Create an empty buffer as copy 
  // with the length of the original
  String[] bufferCopy = new String[buffer.length];
  
  // Copy the buffer into a tmp version 
  // to separate logging from CSV-writing
  arrayCopy(buffer, bufferCopy);
  
  // Clear the main buffer and 
  // continue using its copy
  buffer = new String[0];
  
  // Write the buffer copy into the CSV document
  // by opening the exiting one, setting the pointer 
  // to the end of the file and finally appending it 
  // with the recorded rows.
  try {
    writer = new FileWriter(sketchPath("data/" + sensor.name() + ".csv"), true);
    writer.write("\n" + join(bufferCopy, "\n"));
    writer.close();
    
    // Level up the counter when finished
    counter = counter + bufferCopy.length;
  }catch (Exception e) {
    // Just in case anything goes wrong, tell us about it
    println("Oh boy - failed while trying to append those lines...");
  }
}

// Stop and close the connection
// to the Tact sensor
void exit() {
  // Stop the sensor, after this 
  // point there won't be any more 
  // tactEvents()
  tact.stop();
  
  // Write the rest of the buffer
  // to the CSV log
  writeToFile();
}
