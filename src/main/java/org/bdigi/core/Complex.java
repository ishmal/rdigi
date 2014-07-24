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
package org.bdigi.core;

import java.util.Arrays;
import java.util.List;

/**
 * Simple Complex value type, with various operations.
 * TODO:  recode this when value types become available in Java
 */
public class Complex {
	private double r;
	private double i;

	/**
	 * Constructor
	 * @param r real part
	 * @param i imaginary part
	 */
	public Complex(double r, double i) {
		this.r = r;
		this.i = i;
	}

	/**
	 * Constructor
	 * @param r real part
	 */
	public Complex(double r) {
		this.r = r;
		this.i = 0;
	}

	public double getR() {
		return r;
	}

	public double getI() {
		return i;
	}

	/**
	 * Addition
	 * @param other complex value to add to this one
	 * @return new complex sum
	 */
	public Complex add(Complex other) {
		return new Complex(r+other.r, i+other.i);
	}

	/**
	 * Addition (mutable)
	 * @param other omplex value to add to this one
	 */
	public void addTo(Complex other) {
		r += other.r; i += other.i;
	}

	/**
	 * Addition (mutable)
	 * @param or real part
	 * @param oi imaginary part
	 */
	public void addTo(double or, double oi) {
		r += or; i += oi;
	}

	/**
	 * Subtraction
	 * @param other complex value to subtract from this one
	 * @return complex difference
	 */
	public Complex sub(Complex other) {
		return new Complex(r-other.r, i-other.i);
	}

	/**
	 * Subtraction (mutable)
	 * @param other complex value to subtract from this one
	 */
	public void subFrom(Complex other) {
		r -= other.r; i -= other.i;
	}

	/**
	 * Subtraction
	 * @param or real part
	 * @param oi imaginary part
	 */
	public void subFrom(double or, double oi) {
		r -= or; i-= oi;
	}

	/**
	 * Multiplication
	 * @param other complex by which to multiply this value
	 * @return new complex product
	 */
	public Complex mul(Complex other) {
		return new Complex(r*other.r - i*other.i, r*other.i + i*other.r);
	}

	/**
	 * Multiplication
	 * @param or real by which to multiply this value
	 * @param oi imaginary part by which to multiply this value
	 * @return new complex product
	 */
	public Complex mul(double or, double oi) {
		return new Complex(r*or - i*oi, r*oi + i*or);
	}

	/**
	 * Multiplication (mutable)
	 * @param other complex by which to multiply this value
	 */
	public void mulBy(Complex other) {
		double rp = r*other.r - i*other.i;
		double ip = r*other.i + i*other.r;
		r = rp; i = ip;
	}

	/**
	 * Multiplication (mutable)
	 * @param or real by which to multiply this value
	 * @param oi imaginary part by which to multiply this value
	 */
	public void mulBy(double or, double oi) {
		double rp = r*or - i*oi;
		double ip = r*oi + i*or;
		r = rp; i = ip;
	}

	/**
	 * Scaling
	 * @param v value by which to scale this complex
	 * @return new scaled complex value
	 */
	public Complex scale(double v) {
		return new Complex(r*v, i*v);
	}

	/**
	 * Scaling
	 * @param v value by which to scale this complex
	 */
	public void scaleTo(double v) {
		r *= v; i *= v;
	}

	/**
	 * Magnitude
	 * @return magnitude of this complex
	 */
	public double mag() {
		return r*r + i*i;
	}

	/**
	 * Absolute value
	 * @return absolute value of this complex
	 */
	public double abs() {
		return Math.hypot(r, i);
	}

	/**
	 * Argument
	 * @return argument of this complex
	 */
	public double arg() {
		return Math.atan2(i, r);
	}

	/**
	 * Constant complex zero
	 */
	public final static Complex ZERO = new Complex(0,0);

	/**
	 * Constant complex one
	 */
	public final static Complex ONE = new Complex(1,0);

}


