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

/**
 * This is an unrolled polyphase resampler, designed for speed.  Enjoy!
 */
public class Resampler {


	//#########################################################
	//###  DECIMATION : 2
	//#########################################################
	private final static double c0200 = -0.00000;
	private final static double c0201 = 6.73393e-18;
	private final static double c0202 = 0.287914;
	private final static double c0203 = 0.452254;
	private final static double c0204 = 0.109973;
	private final static double c0205 = 0.00000;
	private final static double d21 = c0201 + c0203;
	private final static double d22 = c0202 + c0204;

	//#########################################################
	//###  DECIMATION : 3
	//#########################################################
	private final static double c0300 = -0.00000;
	private final static double c0301 = -0.00665934;
	private final static double c0302 = 0.0318310;
	private final static double c0303 = 0.181130;
	private final static double c0304 = 0.318310;
	private final static double c0305 = 0.271694;
	private final static double c0306 = 0.106103;
	private final static double c0307 = 0.00932308;
	private final static double c0308 = -0.00000;
	private final static double d31 = c0301 + c0303;
	private final static double d32 = c0302 + c0304 + c0306;
	private final static double d33 = c0305 + c0307;

	//#########################################################
	//###  DECIMATION : 4
	//#########################################################
	private final static double c0400 = -0.00000;
	private final static double c0401 = -0.00357305;
	private final static double c0402 = 2.84852e-18;
	private final static double c0403 = 0.0428519;
	private final static double c0404 = 0.131690;
	private final static double c0405 = 0.220520;
	private final static double c0406 = 0.244937;
	private final static double c0407 = 0.186237;
	private final static double c0408 = 0.0909025;
	private final static double c0409 = 0.0219296;
	private final static double c0410 = 7.73526e-19;
	private final static double c0411 = -0.00000;
	private final static double d41 = c0401 + c0404;
	private final static double d42 = c0402 + c0405 + c0408;
	private final static double d43 = c0403 + c0406 + c0409;
	private final static double d44 = c0407 + c0410;

	//#########################################################
	//###  DECIMATION : 5
	//#########################################################
	private final static double c0500 = -0.00000;
	private final static double c0501 = -0.00196172;
	private final static double c0502 = -0.00336679;
	private final static double c0503 = 0.00849726;
	private final static double c0504 = 0.0449745;
	private final static double c0505 = 0.103355;
	private final static double c0506 = 0.163178;
	private final static double c0507 = 0.196726;
	private final static double c0508 = 0.186985;
	private final static double c0509 = 0.139359;
	private final static double c0510 = 0.0778281;
	private final static double c0511 = 0.0286021;
	private final static double c0512 = 0.00411497;
	private final static double c0513 = -0.000885547;
	private final static double c0514 = -0.00000;
	private final static double d51 = c0501 + c0505;
	private final static double d52 = c0502 + c0506 + c0510;
	private final static double d53 = c0503 + c0507 + c0511;
	private final static double d54 = c0504 + c0508 + c0512;
	private final static double d55 = c0509 + c0513;

	//#########################################################
	//###  DECIMATION : 6
	//#########################################################
	private final static double c0600 = -0.00000;
	private final static double c0601 = -0.00116344;
	private final static double c0602 = -0.00296700;
	private final static double c0603 = 1.80051e-18;
	private final static double c0604 = 0.0144470;
	private final static double c0605 = 0.0438880;
	private final static double c0606 = 0.0850224;
	private final static double c0607 = 0.127510;
	private final static double c0608 = 0.157800;
	private final static double c0609 = 0.165248;
	private final static double c0610 = 0.147236;
	private final static double c0611 = 0.110447;
	private final static double c0612 = 0.0675699;
	private final static double c0613 = 0.0312787;
	private final static double c0614 = 0.00882135;
	private final static double c0615 = 8.47823e-19;
	private final static double c0616 = -0.000767670;
	private final static double c0617 = -0.00000;
	private final static double d61 = c0601 + c0606;
	private final static double d62 = c0602 + c0607 + c0612;
	private final static double d63 = c0603 + c0608 + c0613;
	private final static double d64 = c0604 + c0609 + c0614;
	private final static double d65 = c0605 + c0610 + c0615;
	private final static double d66 = c0611 + c0616;

	//#########################################################
	//###  DECIMATION : 7
	//#########################################################
	private final static double c0700 = -0.00000;
	private final static double c0701 = -0.000738756;
	private final static double c0702 = -0.00222959;
	private final static double c0703 = -0.00194649;
	private final static double c0704 = 0.00376483;
	private final static double c0705 = 0.0180421;
	private final static double c0706 = 0.0417122;
	private final static double c0707 = 0.0722011;
	private final static double c0708 = 0.103761;
	private final static double c0709 = 0.129071;
	private final static double c0710 = 0.141661;
	private final static double c0711 = 0.138195;
	private final static double c0712 = 0.119674;
	private final static double c0713 = 0.0910713;
	private final static double c0714 = 0.0595247;
	private final static double c0715 = 0.0318653;
	private final static double c0716 = 0.0124668;
	private final static double c0717 = 0.00224596;
	private final static double c0718 = -0.000901830;
	private final static double c0719 = -0.000571381;
	private final static double c0720 = -0.00000;
	//notice the diagonals
	private final static double d71 = c0701 + c0707;
	private final static double d72 = c0702 + c0708 + c0714;
	private final static double d73 = c0703 + c0709 + c0715;
	private final static double d74 = c0704 + c0710 + c0716;
	private final static double d75 = c0705 + c0711 + c0717;
	private final static double d76 = c0706 + c0712 + c0718;
	private final static double d77 = c0713 + c0719;

	private final static double idx = 0;

	private final static double r0 = 0.0;
	private final static double r1 = 0.0;
	private final static double r2 = 0.0;
	private final static double r3 = 0.0;
	private final static double r4 = 0.0;
	private final static double r5 = 0.0;
	private final static double r6 = 0.0;
	private final static double r7 = 0.0;
	private final static double r8 = 0.0;
	private final static double r9 = 0.0;

	private final static double i0 = 0.0;
	private final static double i1 = 0.0;
	private final static double i2 = 0.0;
	private final static double i3 = 0.0;
	private final static double i4 = 0.0;
	private final static double i5 = 0.0;
	private final static double i6 = 0.0;
	private final static double i7 = 0.0;
	private final static double i8 = 0.0;
	private final static double i9 = 0.0;

	public interface Observer {
		public void process(double v);
	}

	public static abstract class Instance {
		int decimation;
		double buf[];
		int idx;

		public abstract void decimate(double v, Observer f);

		public abstract void interpolate(double v, double buf[]);

		public Instance(int decimation) {
			this.decimation = decimation;
			this.buf = new double[decimation];
			idx = 0;
		}
	}

	static class Resampler1 extends Instance {
		public Resampler1() {
			super(1);
		}

		public void decimate(double v, Observer f) {
			f.process(v);
		}

		public void interpolate(double v, double buf[]) {
			buf[0] = v;
		}
	}


	static class Resampler2 extends Instance {
		public Resampler2() {
			super(2);
		}

		double r0, r1, r2, r3;

		public void decimate(double v, Observer f) {
			buf[idx++] = v;
			if (idx >= decimation) {
				idx = 0;
				r0 = r2;
				r1 = r3;
				r2 = buf[0];
				r3 = buf[1];
				f.process(r1 * d21 + r2 * d22);
			}
		}

		public void interpolate(double v, double buf[]) {
			r0 = r1;
			r1 = r2;
			r2 = v;
			buf[0] = /*r0 * c0200 + */r1 * c0202 + r2 * c0204;
			buf[1] = r0 * c0201 + r1 * c0203/* + r2 * c0205*/;
		}
	}

	static class Resampler3 extends Instance {
		public Resampler3() {
			super(3);
		}

		double r0, r1, r2, r3, r4;


		public void decimate(double v, Observer f) {
			buf[idx++] = v;
			if (idx >= decimation) {
				idx = 0;
				r0 = r3;
				r1 = r4;
				r2 = buf[0];
				r3 = buf[1];
				r4 = buf[2];
				f.process(r1 * d31 + r2 * d32 + r3 * d33);
			}
		}

		public void interpolate(double v, double buf[]) {
			r0 = r1;
			r1 = r2;
			r2 = v;
			buf[0] = r0 * c0300 + r1 * c0303 + r2 * c0306;
			buf[1] = r0 * c0301 + r1 * c0304 + r2 * c0307;
			buf[2] = r0 * c0302 + r1 * c0305 + r2 * c0308;
		}

	}

	static class Resampler4 extends Instance {
		public Resampler4() {
			super(4);
		}

		double r0, r1, r2, r3, r4, r5;


		public void decimate(double v, Observer f) {
			buf[idx++] = v;
			if (idx >= decimation) {
				idx = 0;
				r0 = r4;
				r1 = r5;
				r2 = buf[0];
				r3 = buf[1];
				r4 = buf[2];
				r5 = buf[3];
				f.process(r1 * d41 + r2 * d42 + r3 * d43 + r4 * d44);
			}
		}

		public void interpolate(double v, double buf[]) {
			r0 = r1;
			r1 = r2;
			r2 = v;
			buf[0] = r0 * c0400 + r1 * c0404 + r2 * c0408;
			buf[1] = r0 * c0401 + r1 * c0405 + r2 * c0409;
			buf[2] = r0 * c0402 + r1 * c0406 + r2 * c0410;
			buf[3] = r0 * c0403 + r1 * c0407 + r2 * c0411;
		}

	}

	static class Resampler5 extends Instance {
		public Resampler5() {
			super(5);
		}

		double r0, r1, r2, r3, r4, r5, r6;


		public void decimate(double v, Observer f) {
			buf[idx++] = v;
			if (idx >= decimation) {
				idx = 0;
				r0 = r5;
				r1 = r6;
				r2 = buf[0];
				r3 = buf[1];
				r4 = buf[2];
				r5 = buf[3];
				r6 = buf[4];
				f.process(r1 * d51 + r2 * d52 + r3 * d53 + r4 * d54 + r5 * d55);
			}
		}

		public void interpolate(double v, double buf[]) {
			r0 = r1;
			r1 = r2;
			r2 = v;
			buf[0] = r0 * c0500 + r1 * c0505 + r2 * c0510;
			buf[1] = r0 * c0501 + r1 * c0506 + r2 * c0511;
			buf[2] = r0 * c0502 + r1 * c0507 + r2 * c0512;
			buf[3] = r0 * c0503 + r1 * c0508 + r2 * c0513;
			buf[4] = r0 * c0504 + r1 * c0509 + r2 * c0514;
		}
	}

	static class Resampler6 extends Instance {
		public Resampler6() {
			super(6);
		}

		double r0, r1, r2, r3, r4, r5, r6, r7;


		public void decimate(double v, Observer f) {
			buf[idx++] = v;
			if (idx >= decimation) {
				idx = 0;
				r0 = r6;
				r1 = r7;
				r2 = buf[0];
				r3 = buf[1];
				r4 = buf[2];
				r5 = buf[3];
				r6 = buf[4];
				r7 = buf[5];
				f.process(r1 * d61 + r2 * d62 + r3 * d63 + r4 * d64 + r5 * d65 + r6 * d66);
			}
		}

		public void interpolate(double v, double buf[]) {
			r0 = r1;
			r1 = r2;
			r2 = v;
			buf[0] = r0 * c0600 + r1 * c0606 + r2 * c0612;
			buf[1] = r0 * c0601 + r1 * c0607 + r2 * c0613;
			buf[2] = r0 * c0602 + r1 * c0608 + r2 * c0614;
			buf[3] = r0 * c0603 + r1 * c0609 + r2 * c0615;
			buf[4] = r0 * c0604 + r1 * c0610 + r2 * c0616;
			buf[5] = r0 * c0605 + r1 * c0611 + r2 * c0617;
		}
	}

	static class Resampler7 extends Instance {
		public Resampler7() {
			super(7);
		}

		double r0, r1, r2, r3, r4, r5, r6, r7, r8;


		public void decimate(double v, Observer f) {
			buf[idx++] = v;
			if (idx >= decimation) {
				idx = 0;
				r0 = r7;
				r1 = r8;
				r2 = buf[0];
				r3 = buf[1];
				r4 = buf[2];
				r5 = buf[3];
				r6 = buf[4];
				r7 = buf[5];
				r8 = buf[6];
				f.process(r1 * d71 + r2 * d72 + r3 * d73 + r4 * d74 * r5 * d75 + r6 * d76 + r7 * d77);
			}
		}

		public void interpolate(double v, double buf[]) {
			r0 = r1;
			r1 = r2;
			r2 = v;
			buf[0] = r0 * c0700 + r1 * c0707 + r2 * c0714;
			buf[1] = r0 * c0701 + r1 * c0708 + r2 * c0715;
			buf[2] = r0 * c0702 + r1 * c0709 + r2 * c0716;
			buf[3] = r0 * c0703 + r1 * c0710 + r2 * c0717;
			buf[4] = r0 * c0704 + r1 * c0711 + r2 * c0718;
			buf[5] = r0 * c0705 + r1 * c0712 + r2 * c0719;
			buf[6] = r0 * c0706 + r1 * c0713 + r2 * c0720;
		}
	}


	public static Instance create(int decimation) {

		switch (decimation) {
			case 1:
				return new Resampler1();
			case 2:
				return new Resampler2();
			case 3:
				return new Resampler3();
			case 4:
				return new Resampler4();
			case 5:
				return new Resampler5();
			case 6:
				return new Resampler6();
			case 7:
				return new Resampler7();
			default:
				throw new IllegalArgumentException("Decimation " + decimation + " not supported");
		}

	}


}
