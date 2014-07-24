/**
 * bdigi DSP tool
 *
 * Authors:
 *   Bob Jamison
 *
 * Copyright (c) 2014 Bob Jamison
 *
 *  This file is part of the bdigi library.
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 3 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.bdigi.core.filter;

/**
 * A variety of windowing definitions
 */
public abstract class Window {

	/**
	 * Generate a set of windowing coefficients according to the specific implementation
	 * @param size the number of coefficients, or width of window
	 * @return double array of windowing coefficients
	 */
	public abstract double[] get(int size);

	/**
	 * Rectangular window
	 */
	public final static Window rectangle = new Window() {

		public double[] get(int size) {
			double xs[] = new double[size];
			for (int i=0 ; i<size ; i++) {
				xs[i] = 1;
			}
			return xs;
		}
	};

	/**
	 * Bartlett window
	 */
	public final static Window bartlett = new Window() {

		public double[] get(int size) {
			double xs[] = new double[size];
			for (int i=0 ; i<size ; i++) {
				xs[i] = 2 / (size - 1) * ((size - 1) / 2 - Math.abs(i - (size - 1) / 2));
			}
			return xs;
		}
	};

	/**
	 * Blackman window
	 */
	public final static Window blackman = new Window() {

		public double[] get(int size) {
			double alpha = 0.16;
			double a0 = (1 - alpha) / 2;
			double a1 = 0.5;
			double a2 = alpha * 0.5;
			double xs[] = new double[size];
			for (int i=0 ; i<size ; i++) {
				xs[i] = a0 - a1 * Math.cos(2.0 * Math.PI * i / (size - 1)) + a2 * Math.cos(4 * Math.PI * i / (size - 1));
			}
			return xs;
		}
	};

	/**
	 * Cosine window
	 */
	public final static Window cosine = new Window() {

		public double[] get(int size) {
			double xs[] = new double[size];
			for (int i=0 ; i<size ; i++) {
				xs[i] = Math.cos(Math.PI * i / (size - 1) - Math.PI / 2);
			}
			return xs;
		}
	};

	/**
	 * Gauss window
	 */
	public final static Window gauss = new Window() {

		public double[] get(int size) {
			double alpha = 0.16;
			double xs[] = new double[size];
			for (int i=0 ; i<size ; i++) {
				xs[i] = Math.pow(Math.E, -0.5 * Math.pow((i - (size - 1) / 2) / (alpha * (size - 1) / 2), 2));
			}
			return xs;
		}
	};

	/**
	 * Hamming window
	 */
	public final static Window hamming = new Window() {

		public double[] get(int size) {
			double alpha = 0.16;
			double xs[] = new double[size];
			for (int i=0 ; i<size ; i++) {
				xs[i] = 0.54 - 0.46 * Math.cos(2.0 * Math.PI * i / (size - 1));
			}
			return xs;
		}
	};

	/**
	 * Hann window
	 */
	public final static Window hann = new Window() {

		public double[] get(int size) {
			double alpha = 0.16;
			double xs[] = new double[size];
			for (int i=0 ; i<size ; i++) {
				xs[i] = 0.5 - 0.5 * Math.cos(2.0 * Math.PI * i / (size - 1));
			}
			return xs;
		}
	};

}
