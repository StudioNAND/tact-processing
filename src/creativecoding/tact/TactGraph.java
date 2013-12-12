/**
 * Copyright (C) 2013 Studio NAND
 * 
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */

package creativecoding.tact;

import java.util.HashMap;

import processing.core.PApplet;
import processing.core.PGraphics;

/**
 * @author Steffen Fiedler, <a href="http://www.nand.io" target="_blank">www.nand.io</a>
 * @since 0.1
 */
public class TactGraph {
	
	/**
	 * Theme key signal graph stroke color.
	 */
	public static final String THEME_GRAPH = "graph";
	/**
	 * Theme key axis guide stroke color. 
	 */
	public static final String THEME_AXIS_GUIDE = "axis-guide";
	/**
	 * Theme key axis helper stroke color.
	 */
	public static final String THEME_AXIS_HELPER = "axis-helper";
	/**
	 * Theme key axis stroke color.
	 */
	public static final String THEME_AXIS_LINE = "axis-line";
	/**
	 * Theme key axis label fill color.
	 */
	public static final String THEME_AXIS_LABEL = "axis-label";
	/**
	 * Theme key axis marker stroke color.
	 */
	public static final String THEME_AXIS_MARKER = "axis-marker";
	/**
	 * Theme key bin stroke color.
	 */
	public static final String THEME_BIN_STROKE = "bin-stroke";
	/**
	 * Theme key bin fill color.
	 */
	public static final String THEME_BIN_FILL = "bin-fill";
	
	private static final int AXIS_MARKER_X_NUM = 4;
	private static final int AXIS_MARKER_Y_NUM = 4;
	private static final int AXIS_MARKER_X_LENGTH = 4;
	private static final int AXIS_MARKER_Y_LENGTH = 4;
	
	/**
	 * Theme for colorizing all present <code>TactGraph</code> instances.
	 */
	public static HashMap<String, Integer> theme;
	
	/**
	 * Theme based on dark colors for bright backgrounds.
	 */
	public static HashMap<String, Integer> dark;
	/**
	 * Theme based on bright colors for dark backgrounds.
	 */
	public static HashMap<String, Integer> bright;
	
	private PApplet parent;
	
	/**
	 * Diagram x position in sketch canvas.
	 */
	public float x = 0;
	/**
	 * Diagram y position in sketch canvas.
	 */
	public float y = 0;
	/**
	 * Diagram total width.
	 */
	public float width = 400;
	/**
	 * Diagram total height.
	 */
	public float height = 300;
	
	/**
	 * Graph offset on the left side of the diagram. This value 
	 * represents the distance between the left side of the 
	 * bounding box and the y-axis in pixel.
	 */
	public float offsetX = 60;
	/**
	 * Graph offset on the bottom side of the diagram. This value 
	 * represents the distance between the bottom side of the 
	 * bounding box an the x-axis in pixel.
	 */
	public float offsetY = 40;
	
	private boolean displayGuides = true;
	
	private boolean displayAxis = true;
	private int axisStrokeWeight = 1;
	private float axisTextSize = 10;
	
	private boolean displayHelper = false;
	private int helperStrokeWeight = 1;
	
	private int segmentStrokeWeight = 3;
	
	
	public TactGraph (PApplet parent, float x, float y, float width, float height) {
		this.parent = parent;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		if (theme == null) {
			dark = new HashMap<String, Integer> ();
			
			dark.put (THEME_GRAPH, (255 << 24) | (0 << 16) | (0 << 8) | 0);
			dark.put (THEME_AXIS_GUIDE, (200 << 24) | (100 << 16) | (100 << 8) | 100);
			dark.put (THEME_AXIS_HELPER, (80 << 24) | (130 << 16) | (130 << 8) | 130);
			dark.put (THEME_AXIS_LABEL, (255 << 24) | (20 << 16) | (20 << 8) | 20);
			dark.put (THEME_AXIS_LINE, (255 << 24) | (60 << 16) | (60 << 8) | 60);
			dark.put (THEME_AXIS_MARKER, (255 << 24) | (90 << 16) | (90 << 8) | 90);
			dark.put (THEME_BIN_FILL, (40 << 24) | (210 << 16) | (210 << 8) | 210);
			dark.put (THEME_BIN_STROKE, (120 << 24) | (180 << 16) | (180 << 8) | 180);
			
			bright = new HashMap<String, Integer> ();
			bright.put (THEME_GRAPH, (255 << 24) | (250 << 16) | (250 << 8) | 250);
			bright.put (THEME_AXIS_GUIDE, (200 << 24) | (100 << 16) | (100 << 8) | 100);
			bright.put (THEME_AXIS_HELPER, (60 << 24) | (130 << 16) | (130 << 8) | 130);
			bright.put (THEME_AXIS_LABEL, (255 << 24) | (220 << 16) | (220 << 8) | 220);
			bright.put (THEME_AXIS_LINE, (255 << 24) | (160 << 16) | (160 << 8) | 160);
			bright.put (THEME_AXIS_MARKER, (255 << 24) | (190 << 16) | (190 << 8) | 190);
			bright.put (THEME_BIN_FILL, (40 << 24) | (210 << 16) | (210 << 8) | 210);
			bright.put (THEME_BIN_STROKE, (120 << 24) | (180 << 16) | (180 << 8) | 180);
			
			theme = dark;
		}
	}
	
	public void spectrum (TactSensor sensor, int segmentResoltion) {
		
		if (segmentResoltion > 0)
			bins (new float[][] {sensor.latestSpectrum ().bins (segmentResoltion)}, TactConstants.AMPLITUDE_MIN, TactConstants.AMPLITUDE_MAX);
		
		if (displayGuides)
			drawGuides (new float[] {sensor.minBias (), sensor.maxBias ()}, new float[] {sensor.minPeak (), sensor.maxPeak ()});
		
		graph (sensor.latestValues (), sensor.latestSpectrum ().start (), sensor.latestSpectrum ().end (), TactConstants.AMPLITUDE_MIN, TactConstants.AMPLITUDE_MAX);
		
		if (displayHelper)
			drawHelper(sensor);
	}
	
	public void spectrum (TactSensor sensor) {
		spectrum (sensor, 0);
	}
	
	public void bias (TactSensor sensor) {
		if (displayGuides)
			drawGuides (null, new float[] {sensor.minBias (), sensor.maxBias ()});
		graph (sensor.bias, 0, sensor.bias.length, 0f, 1f);
	}
	
	public void peak (TactSensor sensor) {
		if (displayGuides)
			drawGuides (null, new float[] {sensor.minPeak (), sensor.maxPeak ()});
		graph (sensor.peak, 0, sensor.peak.length, 0f, 1f);
	}
	
	public void graph (float[] values, float xMin, float xMax, float yMin, float yMax) {
		graph (new float[][]{ values }, xMin, xMax, yMin, yMax);
	}
	
	public void bins (float[][] values, float yMin, float yMax) {
		
		if (values.length < 1)
			return;
		
		float xStep = (width - offsetX) / values[0].length;
		float yStep = (height - offsetY) / (yMax - yMin);
		
		for (int j=0; j < values.length; j++) {
			for (int i=0; i < values[j].length; i++) {
				
				float h = yStep * values[j][i];
				
				parent.g.stroke (theme.get (THEME_BIN_STROKE));
				parent.g.strokeWeight (segmentStrokeWeight);
				parent.g.line (
					x + offsetX + xStep * i,
					y + height - offsetY - h,
					x + offsetX + xStep * (i+1),
					y + height - offsetY - h
				);
				
				parent.g.fill (theme.get (THEME_BIN_FILL));
				parent.g.noStroke ();
				parent.g.rect (
					x + offsetX + xStep * i,
					y + height - offsetY - h,
					xStep,
					h
				);
			}
		}
	}
	
	public void graph (float[][] values, float xMin, float xMax, float yMin, float yMax) {
		
		if (displayAxis)
			drawAxis (xMin, xMax, yMin, yMax);
		
		if (values.length < 1)
			return;
				
		float xStep = (width - offsetX) / (values[0].length - 1);
		float yStep = (height - offsetY) / (yMax - yMin);
		
		parent.g.stroke (theme.get (THEME_GRAPH));
		parent.g.noFill ();
		for (int j=0; j < values.length; j++) {
			parent.g.beginShape ();
			for (int i=0; i < values[j].length; i++)
				parent.g.vertex (x + offsetX + xStep * i, y + (height - offsetY) - yStep * values[j][i]);
			parent.g.endShape ();
		}
	}
	
	private void drawGuides (float[] xvalues, float[] yvalues) {
		
		parent.g.stroke (theme.get (THEME_AXIS_GUIDE));
		parent.g.strokeWeight (1);
		
		if (xvalues != null) {
			for (float xv : xvalues) {
				float xp = x + offsetX + xv * (width - offsetX);
				float yp = y;
				
				parent.g.beginShape (PGraphics.LINES);
				while (yp < y + height - offsetY) {
					parent.g.vertex (xp, yp);
					yp += 3;
				}
				parent.g.endShape ();
			}
		}
		
		if (yvalues != null) {
			for (float yv : yvalues) {
				float yp = y + (1 - yv) * (height - offsetY);
				float xp = x + offsetX;
				
				parent.g.beginShape (PGraphics.LINES);
				while (xp < x + width) {
					parent.g.vertex (xp, yp);
					xp += 3;
				}
				parent.g.endShape ();
			}
		}
	}
	
	private void drawHelper (TactSensor sensor) {
		if (parent.mouseX > x + offsetX && parent.mouseX < x + width && parent.mouseY > y && parent.mouseY < y + height - offsetY) {
			parent.g.line (parent.mouseX, y, parent.mouseX, y + height - offsetY);
			
			int index = (int) (((parent.mouseX - x - offsetX) / (float) (width - offsetX)) * sensor.readings ());
			String xlabel = Integer.toString (sensor.start () + sensor.step () * index);
			
			parent.g.text (xlabel, parent.mouseX + 5, y + height - offsetY - 3);
		}
	}
	
	private void drawAxis (float xMin, float xMax, float yMin, float yMax) {
		
		parent.g.stroke (theme.get (THEME_AXIS_LINE));
		parent.g.strokeWeight (axisStrokeWeight);
		parent.g.line (x + offsetX, y, x +offsetX, y + height - offsetY);
		parent.g.line (x + offsetX, y + height - offsetY, x + width, y + height - offsetY);
		
		float markStepX = (xMax - xMin) / AXIS_MARKER_X_NUM;
		float markStepY = (yMax - yMin) / AXIS_MARKER_Y_NUM;
		
		if (markStepX > 5 && markStepX % 1 != 0)
			markStepX += markStepX % 1;
		
		if (markStepY > 5 && markStepY % 1 != 0)
			markStepY += markStepY % 1;
		
		for (int i=0; i <= AXIS_MARKER_X_NUM; i++) {
			
			float axisStep = (width - offsetX) / AXIS_MARKER_X_NUM;
			float xpos = x + offsetX + axisStep * i;
			
			parent.g.stroke (theme.get (THEME_AXIS_MARKER));
			parent.g.line (xpos, y + height - offsetY, xpos, y + height - offsetY + AXIS_MARKER_X_LENGTH);
			
			// Marker helper
			if (i > 0) {
				parent.g.stroke (theme.get (THEME_AXIS_HELPER));
				parent.g.strokeWeight (helperStrokeWeight);
				parent.g.line (xpos, y, xpos, y + height - offsetY - axisStrokeWeight);
			}
			
			float label = xMin + markStepX * i;
			
			parent.g.fill (theme.get (THEME_AXIS_LABEL));
			parent.g.textSize (axisTextSize);
			
			if (label > 5 || label % 1 == 0) {
				parent.g.text ((int) label, xpos - parent.textWidth (Integer.toString ((int)label)) / 2, y + height - offsetY + axisTextSize * 2.1f);
			}else{
				String s = Integer.toString ((int) label);
				parent.g.text (s, xpos - parent.textWidth (s) / 2, y + height - offsetY);
			}
		}
		
		// Draw axis and helpers for along the y-axis.
		// For the fixed resolution of AXIS_MARKER_Y_NUM ...
		for (int i=0; i <= AXIS_MARKER_Y_NUM; i++) {
			
			float axisStep = (height - offsetY) / AXIS_MARKER_Y_NUM;
			float ypos = y + axisStep * i;
			
			// Draw vertical helper extension along the y-axis.
			// These are the short lines next to the axis labels.
			parent.g.stroke (theme.get (THEME_AXIS_MARKER));
			parent.g.line (x + offsetX - AXIS_MARKER_Y_LENGTH, ypos, x + offsetX, ypos);
			
			// Draw the horizontal marker helper across the full 
			// width of the diagram (along the y-axis).
			if (i != AXIS_MARKER_Y_NUM) {
				parent.g.stroke (theme.get (THEME_AXIS_HELPER));
				parent.g.strokeWeight (helperStrokeWeight);
				parent.g.line (x + offsetX + axisStrokeWeight, ypos, x + width, ypos);
			}
			
			// Display the text labels along the y-axis
			float label = yMax - markStepY * i;
			parent.g.fill (theme.get (THEME_AXIS_LABEL));
			parent.g.textSize (axisTextSize);
			
			// Select appropriate format depending on present value 
			if (label > 5 || label % 1 == 0) {
				parent.g.text ((int) label, x + offsetX - parent.textWidth (Integer.toString ((int)label)) - 10, ypos + axisTextSize / 2);
			}else{
				String s = Float.toString (label);
				parent.g.text (s, x + offsetX - parent.textWidth (s) - 10, ypos + axisTextSize / 2);
			}
		}
	}
}
