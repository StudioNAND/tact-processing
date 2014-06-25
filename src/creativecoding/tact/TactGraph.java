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
 * <p>A util class to render <code>Tact</code> data diagrams.</p>
 * 
 * @author Steffen Fiedler, <a href="http://www.nand.io" target="_blank">www.nand.io</a>
 * @since 0.1
 */
public class TactGraph {
	
	/**
	 * Theme key for signal graph stroke color.
	 */
	public static final String THEME_GRAPH = "graph";
	/**
	 * Theme key for axis guide stroke color. 
	 */
	public static final String THEME_AXIS_GUIDE = "axis-guide";
	/**
	 * Theme key for axis helper stroke color.
	 */
	public static final String THEME_AXIS_HELPER = "axis-helper";
	/**
	 * Theme key for axis stroke color.
	 */
	public static final String THEME_AXIS_LINE = "axis-line";
	/**
	 * Theme key for axis label fill color.
	 */
	public static final String THEME_AXIS_LABEL = "axis-label";
	/**
	 * Theme key for axis marker stroke color.
	 */
	public static final String THEME_AXIS_MARKER = "axis-marker";
	/**
	 * Theme key for bin stroke color.
	 */
	public static final String THEME_BIN_STROKE = "bin-stroke";
	/**
	 * Theme key for bin fill color.
	 */
	public static final String THEME_BIN_FILL = "bin-fill";
	/**
	 * Theme key for diagram title font color.
	 */
	public static final String THEME_TITLE_FILL = "title-fill";
	
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
	
	/**
	 * Flag for displaying background guides in graph area.
	 */
	public boolean displayGuides = true;
	
	/**
	 * Flag for showing and hiding diagram axes.
	 */
	public boolean displayAxis = true;
	private int axisStrokeWeight = 1;
	private float axisTextSize = 10;
	
	/**
	 * Flag for showing and hiding diagram title caption.
	 */
	public boolean displayTitle = true;
	
	private boolean displayHelper = false;
	private int helperStrokeWeight = 1;
	
	private int segmentStrokeWeight = 3;
	
	/**
	 * Creates a graph plotting helper instance. The aim of 
	 * this class is to provide simple access to <code>TactSensor</code> 
	 * related diagram drawing functionality.
	 * 
	 * @param parent Processing sketch instance
	 * @param x diagram x position
	 * @param y diagram y position
	 * @param width diagram total width
	 * @param height diagram total height
	 * @since 0.1
	 */
	public TactGraph (PApplet parent, final float x, final float y, final float width, final float height) {
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
			dark.put (THEME_TITLE_FILL, (255 << 24) | (80 << 16) | (80 << 8) | 80);
			
			bright = new HashMap<String, Integer> ();
			bright.put (THEME_GRAPH, (255 << 24) | (250 << 16) | (250 << 8) | 250);
			bright.put (THEME_AXIS_GUIDE, (200 << 24) | (100 << 16) | (100 << 8) | 100);
			bright.put (THEME_AXIS_HELPER, (60 << 24) | (130 << 16) | (130 << 8) | 130);
			bright.put (THEME_AXIS_LABEL, (255 << 24) | (220 << 16) | (220 << 8) | 220);
			bright.put (THEME_AXIS_LINE, (255 << 24) | (160 << 16) | (160 << 8) | 160);
			bright.put (THEME_AXIS_MARKER, (255 << 24) | (190 << 16) | (190 << 8) | 190);
			bright.put (THEME_BIN_FILL, (40 << 24) | (210 << 16) | (210 << 8) | 210);
			bright.put (THEME_BIN_STROKE, (120 << 24) | (180 << 16) | (180 << 8) | 180);
			bright.put (THEME_TITLE_FILL, (255 << 24) | (200 << 16) | (200 << 8) | 200);
			
			theme = dark;
		}
	}
	
	/**
	 * Renders <code>TactSpectrum</code> within the 
	 * given diagram dimensions. Define a bin resolution 
	 * for also plotting the signal in groups of n-bins, 
	 * whereby n must be power of two; not exceeding the 
	 * <code>length</code> of the <code>TactSpectrum</code>.
	 * 
	 * @param sensor sensor source for retrieving latest spectrum from
	 * @param bins number of groups the signal shall be segmented into. 
	 *             This value must be power of two and shall not exceede 
	 *             the total amount of spectrum <code>values</code>.
	 * @see TactSpectrum#values
	 * @see TactSpectrum#bins(int)
	 * @since 0.1
	 */
	public void spectrum (final TactSensor sensor, final int bins) {
		
		// Draw signal segmentation into n-bins 
		if (bins > 0)
			bins (new float[][] {sensor.latestSpectrum ().bins (bins)}, TactConstants.AMPLITUDE_MIN, TactConstants.AMPLITUDE_MAX);
		
		if (displayGuides)
			drawGuides (new float[] {sensor.minBias (), sensor.maxBias ()}, new float[] {sensor.minPeak (), sensor.maxPeak ()});
		
		if (displayTitle)
			drawTitle ("Spectrum");
		
		graph (sensor.latestValues (), sensor.latestSpectrum ().start, sensor.latestSpectrum ().end, TactConstants.AMPLITUDE_MIN, TactConstants.AMPLITUDE_MAX);
		
		if (displayHelper)
			drawHelper(sensor);
	}
	
	/**
	 * Renders <code>TactSpectrum</code> within the given 
	 * diagram dimensions. Define a bin resolution for also 
	 * plotting the signal in groups of n-bins, whereby n 
	 * must be power of two; not exceeding the <code>length</code> 
	 * of the <code>TactSpectrum</code>.<br /> 
	 * For also plotting the min- and maxima bounds, use the 
	 * {@link #spectrum(TactSensor)} version of this method.
	 * 
	 * 
	 * @param spectrum to plot as diagram.
	 * @param bins number of groups to simplify signal.
	 * @see TactSpectrum#values
	 * @see TactSpectrum#bins(int)
	 * @since 0.1
	 */
	public void spectrum (final TactSpectrum spectrum, final int bins) {
		
		// Draw signal segmentation into n-bins 
		if (bins > 0)
			bins (new float[][] {spectrum.bins (bins)}, TactConstants.AMPLITUDE_MIN, TactConstants.AMPLITUDE_MAX);
		
		if (displayTitle)
			drawTitle ("Spectrum");
		
		graph (spectrum.values, spectrum.start, spectrum.end, TactConstants.AMPLITUDE_MIN, TactConstants.AMPLITUDE_MAX);
	}
	
	/**
	 * Renders <code>TactSpectrum</code> within the given 
	 * diagram dimension.
	 * 
	 * @param sensor source for retrieving latest spectrum.
	 * @see TactSpectrum#values
	 * @since 0.1
	 */
	public void spectrum (final TactSensor sensor) {
		spectrum (sensor, 0);
	}
	
	/**
	 * Renders <code>TactSpectrum</code> within the given diagram dimensions. 
	 * For also plotting the min- and maxima bounds, use the 
	 * {@link #spectrum(TactSensor)} version of this method.
	 * 
	 * @param spectrum to plot as diagram.
	 * @see TactSpectrum#values
	 * @since 0.1
	 */
	public void spectrum (final TactSpectrum spectrum) {
		spectrum (spectrum, 0);
	}
	
	/**
	 * Renders <code>bias</code> histogram of a given 
	 * <code>TactSpectrum</code>.
	 * 
	 * @param sensor sensor holding the array of chronological 
	 *               <code>bias</code> values.
	 * @see TactSensor#bias
	 * @since 0.1
	 */
	public void bias (final TactSensor sensor) {
		if (displayGuides)
			drawGuides (null, new float[] {sensor.minBias (), sensor.maxBias ()});
		
		if (displayTitle)
			drawTitle ("Bias");
		
		graph (sensor.bias, 0, sensor.bias.length, 0f, 1f);
	}
	
	/**
	 * Renders <code>preak</code> histogram of a given 
	 * <code>TactSpectrum</code>.
	 * 
	 * @param sensor holding the array of chronological 
	 *               <code>peak</code> values.
	 * @see TactSensor#peak
	 * @since 0.1
	 */
	public void peak (final TactSensor sensor) {
		if (displayGuides)
			drawGuides (null, new float[] {sensor.minPeak (), sensor.maxPeak ()});
		
		if (displayTitle)
			drawTitle ("Peak");
		
		graph (sensor.peak, 0, sensor.peak.length, 0f, 1f);
	}
	
	/**
	 * Renders graph for given <code>values</code> set.
	 * 
	 * @param values list of single values to render.
	 * @param xMin the minimum value on the x-axis.
	 * @param xMax the maximum value on the x-axis.
	 * @param yMin the minimum value on the y-axis.
	 * @param yMax the maximum value on the y-axis.
	 * @since 0.1
	 */
	public void graph (final float[] values, final float xMin, final float xMax, final float yMin, final float yMax) {
		graph (new float[][]{ values }, xMin, xMax, yMin, yMax);
	}
	
	/**
	 * Renders signal spectrum grouped into bins.
	 * 
	 * @param values of the graph that is to plot.
	 * @param yMin the minimum value on the y-axis.
	 * @param yMax the maximum value on the y-axis.
	 * @since 0.1
	 */
	public void bins (final float[][] values, final float yMin, final float yMax) {
		
		if (values.length < 1)
			return;
		
		final float xStep = (width - offsetX) / values[0].length;
		final float yStep = (height - offsetY) / (yMax - yMin);
		
		for (int j=0; j < values.length; j++) {
			for (int i=0; i < values[j].length; i++) {
				
				final float h = yStep * values[j][i];
				
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
	
	/**
	 * Render signal spctrum as graph.
	 * 
	 * @param values 
	 * @param xMin
	 * @param xMax
	 * @param yMin
	 * @param yMax
	 * @since 0.1
	 */
	public void graph (final float[][] values, final float xMin, final float xMax, final float yMin, final float yMax) {
		
		if (displayAxis)
			drawAxis (xMin, xMax, yMin, yMax);
		
		if (values.length < 1)
			return;
				
		final float xStep = (width - offsetX) / (values[0].length - 1);
		final float yStep = (height - offsetY) / (yMax - yMin);
		
		parent.g.stroke (theme.get (THEME_GRAPH));
		parent.g.noFill ();
		for (int j=0; j < values.length; j++) {
			parent.g.beginShape ();
			for (int i=0; i < values[j].length; i++)
				parent.g.vertex (x + offsetX + xStep * i, y + (height - offsetY) - yStep * values[j][i]);
			parent.g.endShape ();
		}
	}
	
	/**
	 * Render guide as lines in diagram background.
	 * 
	 * @param xvalues list of positions on the x-axis.
	 * @param yvalues list of positions on the y-axis.
	 * @since 0.1
	 */
	private void drawGuides (final float[] xvalues, final float[] yvalues) {
		
		parent.g.stroke (theme.get (THEME_AXIS_GUIDE));
		parent.g.strokeWeight (1);
		
		if (xvalues != null) {
			for (float xv : xvalues) {
				final float xp = x + offsetX + xv * (width - offsetX);
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
				final float yp = y + (1 - yv) * (height - offsetY);
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
			
			final int index = (int) (((parent.mouseX - x - offsetX) / (float) (width - offsetX)) * sensor.readings ());
			String xlabel = Integer.toString (sensor.start () + sensor.step () * index);
			
			parent.g.text (xlabel, parent.mouseX + 5, y + height - offsetY - 3);
		}
	}
	
	/**
	 * Renders title in upper-left corner of the diagram canvas.
	 * 
	 * @param title to render as <code>String</code>.
	 * @since 0.1
	 */
	private void drawTitle (final String title) {
		parent.g.fill (theme.get (THEME_TITLE_FILL));
		parent.g.text (title, x + offsetX + 10, y + 15);
	}
	
	/**
	 * Render diagram x and y axis.
	 * 
	 * @param xMin the minimum value on the x-axis.
	 * @param xMax the maximum value on the x-axis.
	 * @param yMin the minimum value on the y-axis.
	 * @param yMax the maximum value on the y-axis.
	 * @sine 0.1
	 */
	private void drawAxis (final float xMin, final float xMax, final float yMin, final float yMax) {
		
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
			
			final float axisStep = (width - offsetX) / AXIS_MARKER_X_NUM;
			final float xpos = x + offsetX + axisStep * i;
			
			parent.g.stroke (theme.get (THEME_AXIS_MARKER));
			parent.g.line (xpos, y + height - offsetY, xpos, y + height - offsetY + AXIS_MARKER_X_LENGTH);
			
			// Marker helper
			if (i > 0) {
				parent.g.stroke (theme.get (THEME_AXIS_HELPER));
				parent.g.strokeWeight (helperStrokeWeight);
				parent.g.line (xpos, y, xpos, y + height - offsetY - axisStrokeWeight);
			}
			
			final float label = xMin + markStepX * i;
			
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
			
			final float axisStep = (height - offsetY) / AXIS_MARKER_Y_NUM;
			final float ypos = y + axisStep * i;
			
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
			final float label = yMax - markStepY * i;
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
