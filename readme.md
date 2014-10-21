# Tact for Processing

A Processing library to communicate with the Tact sensor – an electronic circuit that enables experiments with capacitive sensing.

----


This library belongs to the following repositories:

 * **[Tact Arduino sketches](https://github.com/StudioNAND/tact-arduino-sketch)** and **[Tact Arduino library](https://github.com/philippschmitt/tact-arduino)** (by Philipp Schmitt) to drive the circuit interface. Those scripts are for handling serial requests and serving sensor data.
 * This project - **Tact for Processing**, for retrieving and processing sensor data in an easy manner.

## Installation
To install simply download the [latest release](https://s3-eu-west-1.amazonaws.com/tact/processing/releases/tact.zip). Extract the archive and move the *tact* folder into the libraries folder of your processing directory. To figure out where the Processing sketches are located on your computer, open the *Preferences* window and check the "Sketchbook location". Make sure to restart Processing after putting everything into place.

Examples and Reference material come with each release.

## Usage
The following snippets outline all necessary steps for communicating with a Tact circuit. To quickly test your sensor setup, consider running one of the [examples](tree/master/examples) on GitHub or simply open them from Processing's example browser after installing the library on your machine.

### Setup
To get started, you've to include the `creativecoding.tact` and `processing.serial` libraries using Sketch → Import library. Serial is a dependency of the tact library, but does not come with it to prevent multiple imports. Please make sure to import both. Then simply declare a single Tact and at least one TactSensor instance.

```java
import creativecoding.tact.*;
import processing.serial.*;

Tact tact;
TactSensor sensor;
```

Next up, instantiate the wrapper and start-up sensor communication by calling `startUpdates()`.

```java
void setup() {
    tact = new Tact(this, 5);
    sensor = tact.addSensor(0);
    tact.startUpdates();
}
```

These three lines will invoke an automatic update procedure that makes the latest values for all added sensors available. Additional settings include the buffer size, signal spectrum start and length, etc – consult the examples or reference for further details.

### Accessing sensor data
All sensor data is frequently updated and made accessible via one of the several getters. For example TactSensor's `bias()` function.

```java
void draw() {
    background(0);
    rect(0, 0, sensor.bias() * width);
}
```

Likewise, each signal spectrum that becomes available will be published via the `tactEvent()` function. Simply overwrite it in your Processing sketch and receive frequent update notifications.

```java
void tactEvent(TactEvent event) {
    println(event.sensor.name() + ": " + event.sensor.bias());
}
```

If your more interessted in turning one of your classes into a TactEvent delegate. Simply implement the TactListener interface and overwrite its callback function. Remember that there is no need for that when your class already extends PApplet.

```java
class MyTactListener implements TactListener {

	public void tactEvent(TactEvemt event) {
		System.out.println("Just received " + event.type + " event.");
	}
}
```

Make sure to also register your listener instances.

```java
// Instantiate your listener
MyTactListener myListener = new MyTactListener();

// Create Tact instance and add listener
// to list of TactEvent delegates
Tact tact = new Tact(this, 5);
tact.addTactListener(myListener);

// Removes listener from delegates
tact.removeTactListener(myListener);
```

### Example
The following example outlines the fundamental sketch structure.

```java
import creativecoding.tact.*;
import processing.serial.*;

Tact tact;
TactSensor sensor;

void setup() {
  size(800, 600);
  
  // Create new Tact wrapper
  // listen on serial port 5
  tact = new Tact(this, 5);
  
  // Create a Tact sensor and monitor
  // incoming values at sensor pin 0
  sensor = tact.addSensor(0);
  
  // Start frequent updates
  tact.startUpdates();
}

void draw() {
  // Clear background
  background(0);
  
  // Define diameter based on sensor peak
  float diameter = sensor.peak() * min(width, height);
  
  // Draw ellipse in center of the sketch
  ellipse (width/2, height/2, diameter, diameter);
}
```

### Graph plotting utility class

![Signal spectrum graph and bias-peak histogram](https://s3-eu-west-1.amazonaws.com/tact/processing/assets/tact-graph.gif)

This processing project comes with a visualization utility class for plotting out spectrum, bias and peak charts. The following example outlines how to render a simple signal spectrum of a given sensor.

```java
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
  tact.startUpdates();
  
  // Create signal graph instance
  // by naming position and dimension
  graphSpectrum = new TactGraph (this, 10, 20, width - 40, height - 20);
}

void draw() {
  background(255);
  // Render graph with given sensor 
  // and section the signal in 8 groups (bins)
  graphSpectrum.spectrum (sensor, 8);
}
```

Its pretty straight forward and meant to show all relevant information for debugging the sensor and your code.

Beside plotting the spectrum we've implemented methods for displaying bias and peak histograms. And included two different default color themes. The following snipped will allow you to switch between both when Processing's key event is triggered.

```java
void keyReleased () {
  TactGraph.theme = (black) ? TactGraph.bright : TactGraph.dark;
}
```

### Speeding it up
This library project develops simultaneously with our explorations in the field of capacitive touch. Its intention is to provide rapid experiments, without refusing access to the raw data - less code, no constrains. One of the major pitfalls when working with Arduino and multiple sensors is the lack of speed and responsiveness due to the time it takes when transferring multiple signal spectrums. Therefore we've implemented the option to do the heavy lifting on the micro-controller while only transmitting the result. Specify the value you are interested in when adding the sensor:

```java
TactSensor sensor = tact.addSensor(0, Tact.BIAS);
```

Which in that case will only request and receive `bias()` data from the sensor at pin 0. Choose from the following options for peak or bias and peak values.

* `Tact.BIAS`
* `Tact.BIAS_PEAK`
* `Tact.PEAK`
* `Tact.SPECTRUM` (default)

You might want to gear your program, since the first three cases will not provide any signal spectrum.

```java
if (mySensor.hasSpectrum() == false) {
   println("Accessing the mySensor.buffer[0] wouldn't be a good idea.");
}
```

### Source
The Source code is contained within the download and also available on [github](tree/master/src).

### Questions?
Any questions or feedback? Contact me on [twitter](http://www.twitter.com/steffen_fiedler). For bug reports, please use the [issue tracker](issues).

## Who made this?
This library has been developed by [Studio NAND](http://www.nand.io/?ref=github-tact-processing). It’s based on many smart ideas – so cheers to everyone who contributed her/his time to investigate capacitive technology! Special thanks to the team around [Ivan Poupyrev](http://www.ivanpoupyrev.com/) for all the research within this field and [Mads Hobye](http://www.hobye.dk/) for his [Arduino tutorial](http://www.instructables.com/id/Touche-for-Arduino-Advanced-touch-sensing/) about advanced touch sensing. All of that has been a big help and inspiration.

## Copyright
Copyright (c) 2013 [Studio NAND](http://www.nand.io/?ref=github-tact-processing). Licensed under the GNU Lesser General Public License. See *license.txt* for further details.

Any example code is released into the public domain.
