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

import java.util.EventObject;

import creativecoding.tact.TactSensor;

/**
 * <p>A <code>TactEvent</code> delivers sensor updates.</p>
 * 
 * @author Steffen Fiedler, <a href="http://www.nand.io">www.nand.io</a>
 * @since 0.1
 */
public class TactEvent extends EventObject {
	
	private static final long serialVersionUID = 1L;
	
	public TactSensor sensor;
	
	public TactEvent (Object source, TactSensor sensor) {
		super (source);
		this.sensor = sensor;
	}
}