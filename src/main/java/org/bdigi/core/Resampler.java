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

public class Resampler {
    public interface RS {
        public boolean decimate(double v);
        public boolean decimate(double r, double i);
        public void interpolate(double v, double buf[]);
        public void interpolate(double r, double i, double rbuf[], double ibuf[]);

        public double getR();
        public double getI();
    }

    public static class RS1 implements RS {
        private double r; double i;
        public boolean decimate(double v) { r = v ; return true; }
        public boolean decimate(double r, double i) { this.r=r; this.i=i; return true; }

        public void interpolate(double v, double buf[]) { buf[0] = v; }
        public void interpolate(double r, double i, double rbuf[], double ibuf[])

        { rbuf[0]=r; ibuf[0] = i; }
        public double getR() { return r; }
        public double getI() { return i; }
    }

    /**
     * ### decimation : 2
     */
    public static class RS2 implements RS {
        private double r0=0.0, r1=0.0, r2=0.0, r3=0.0;
        private double i0=0.0, i1=0.0, i2=0.0, i3=0.0;
        private int idx = 0;
        private double r = 0.0;
        private double i = 0.0;
        public double getR(){ return r; }
        public double getI(){ return i; }

        public boolean decimate(double v) {
            r0=r1; r1=r2; r2=r3; r3=v;
            if (++idx >= 2) {
                idx = 0;
                r = r1*0.28791 + r2*0.56223;
                return true;
            } else {
                return false;
            }
        }

        public boolean decimate(double r, double i) {
            r0=r1; r1=r2; r2=r3; r3=r;
            i0=i1; i1=i2; i2=i3; i3=i;
            if (++idx >= 2) {
                idx = 0;
                this.r = r1*0.28791 + r2*0.56223;
                this.i = i1*0.28791 + i2*0.56223;
                return true;
            } else {
                return false;
            }
        }

        public void interpolate(double v, double buf[]) {
            r0 = r1; r1 = r2; r2 = v;
            buf[0] = r0 * 0.27639 + r1 * 0.72361;
            buf[1] = r1 * 1.00000;
        }

        public void interpolate(double r, double i, double rbuf[], double ibuf[]) {

            r0 = r1; r1 = r2; r2 = r;
            i0 = i1; i1 = i2; i2 = i;
            rbuf[0] = r0 * 0.27639 + r1 * 0.72361;
            ibuf[0] = i0 * 0.27639 + i1 * 0.72361;
            rbuf[1] = r1 * 1.00000;
            ibuf[1] = i1 * 1.00000;
        }

    }

    /**
     * ### decimation : 3
     */
    public static class RS3 implements RS {
        private double r0=0.0, r1=0.0, r2=0.0, r3=0.0, r4=0.0;
        private double i0=0.0, i1=0.0, i2=0.0, i3=0.0, i4=0.0;
        private int idx = 0;
        private double r = 0.0;
        private double i = 0.0;
        public double getR(){ return r; }
        public double getI(){ return i; }

        public boolean decimate(double v) {
            r0=r1; r1=r2; r2=r3; r3=r4; r4=v;
            if (++idx >= 3) {
                idx = 0;
                r = r1*0.23529 + r2*0.47117 + r3*0.23529;
                return true;
            } else {
                return false;
            }
        }

        public boolean decimate(double r, double i) {
            r0=r1; r1=r2; r2=r3; r3=r4; r4=r;
            i0=i1; i1=i2; i2=i3; i3=i4; i4=i;
            if (++idx >= 3) {
                idx = 0;
                this.r = r1*0.23529 + r2*0.47117 + r3*0.23529;
                this.i = i1*0.23529 + i2*0.47117 + i3*0.23529;
                return true;
            } else {
                return false;
            }
        }

        public void interpolate(double v, double buf[]) {
            r0 = r1; r1 = r2; r2 = v;
            buf[0] = r0 * 0.22654 + r1 * 0.77346;
            buf[1] = r1 * 1.00000;
            buf[2] = r1 * 0.77346 + r2 * 0.22654;
        }

        public void interpolate(double r, double i, double rbuf[], double ibuf[]) {

            r0 = r1; r1 = r2; r2 = r;
            i0 = i1; i1 = i2; i2 = i;
            rbuf[0] = r0 * 0.22654 + r1 * 0.77346;
            ibuf[0] = i0 * 0.22654 + i1 * 0.77346;
            rbuf[1] = r1 * 1.00000;
            ibuf[1] = i1 * 1.00000;
            rbuf[2] = r1 * 0.77346 + r2 * 0.22654;
            ibuf[2] = i1 * 0.77346 + i2 * 0.22654;
        }

    }

    /**
     * ### decimation : 4
     */
    public static class RS4 implements RS {
        private double r0=0.0, r1=0.0, r2=0.0, r3=0.0, r4=0.0, r5=0.0;
        private double i0=0.0, i1=0.0, i2=0.0, i3=0.0, i4=0.0, i5=0.0;
        private int idx = 0;
        private double r = 0.0;
        private double i = 0.0;
        public double getR(){ return r; }
        public double getI(){ return i; }

        public boolean decimate(double v) {
            r0=r1; r1=r2; r2=r3; r3=r4; r4=r5; r5=v;
            if (++idx >= 4) {
                idx = 0;
                r = r1*0.12812 + r2*0.31142 + r3*0.30972 + r4*0.18624;
                return true;
            } else {
                return false;
            }
        }

        public boolean decimate(double r, double i) {
            r0=r1; r1=r2; r2=r3; r3=r4; r4=r5; r5=r;
            i0=i1; i1=i2; i2=i3; i3=i4; i4=i5; i5=i;
            if (++idx >= 4) {
                idx = 0;
                this.r = r1*0.12812 + r2*0.31142 + r3*0.30972 + r4*0.18624;
                this.i = i1*0.12812 + i2*0.31142 + i3*0.30972 + i4*0.18624;
                return true;
            } else {
                return false;
            }
        }

        public void interpolate(double v, double buf[]) {
            r0 = r1; r1 = r2; r2 = v;
            buf[0] = r0 * 0.40838 + r1 * 0.59162;
            buf[1] = r0 * 0.09180 + r1 * 0.92315 + r2 * -0.01496;
            buf[2] = r1 * 1.00000;
            buf[3] = r1 * 0.81295 + r2 * 0.18705;
        }

        public void interpolate(double r, double i, double rbuf[], double ibuf[]) {

            r0 = r1; r1 = r2; r2 = r;
            i0 = i1; i1 = i2; i2 = i;
            rbuf[0] = r0 * 0.40838 + r1 * 0.59162;
            ibuf[0] = i0 * 0.40838 + i1 * 0.59162;
            rbuf[1] = r0 * 0.09180 + r1 * 0.92315 + r2 * -0.01496;
            ibuf[1] = i0 * 0.09180 + i1 * 0.92315 + i2 * -0.01496;
            rbuf[2] = r1 * 1.00000;
            ibuf[2] = i1 * 1.00000;
            rbuf[3] = r1 * 0.81295 + r2 * 0.18705;
            ibuf[3] = i1 * 0.81295 + i2 * 0.18705;
        }

    }

    /**
     * ### decimation : 5
     */
    public static class RS5 implements RS {
        private double r0=0.0, r1=0.0, r2=0.0, r3=0.0, r4=0.0, r5=0.0, r6=0.0;
        private double i0=0.0, i1=0.0, i2=0.0, i3=0.0, i4=0.0, i5=0.0, i6=0.0;
        private int idx = 0;
        private double r = 0.0;
        private double i = 0.0;
        public double getR(){ return r; }
        public double getI(){ return i; }

        public boolean decimate(double v) {
            r0=r1; r1=r2; r2=r3; r3=r4; r4=r5; r5=r6; r6=v;
            if (++idx >= 5) {
                idx = 0;
                r = r1*0.12133 + r2*0.23952 + r3*0.23637 + r4*0.23952 +
                r5*0.12133;
                return true;
            } else {
                return false;
            }
        }

        public boolean decimate(double r, double i) {
            r0=r1; r1=r2; r2=r3; r3=r4; r4=r5; r5=r6; r6=r;
            i0=i1; i1=i2; i2=i3; i3=i4; i4=i5; i5=i6; i6=i;
            if (++idx >= 5) {
                idx = 0;
                this.r = r1*0.12133 + r2*0.23952 + r3*0.23637 + r4*0.23952 +
                r5*0.12133;
                this.i = i1*0.12133 + i2*0.23952 + i3*0.23637 + i4*0.23952 +
                i5*0.12133;
                return true;
            } else {
                return false;
            }
        }

        public void interpolate(double v, double buf[]) {
            r0 = r1; r1 = r2; r2 = v;
            buf[0] = r0 * 0.33423 + r1 * 0.66577;
            buf[1] = r0 * 0.09350 + r1 * 0.91444 + r2 * -0.00794;
            buf[2] = r1 * 1.00000;
            buf[3] = r0 * -0.00794 + r1 * 0.91444 + r2 * 0.09350;
            buf[4] = r1 * 0.66577 + r2 * 0.33423;
        }

        public void interpolate(double r, double i, double rbuf[], double ibuf[]) {

            r0 = r1; r1 = r2; r2 = r;
            i0 = i1; i1 = i2; i2 = i;
            rbuf[0] = r0 * 0.33423 + r1 * 0.66577;
            ibuf[0] = i0 * 0.33423 + i1 * 0.66577;
            rbuf[1] = r0 * 0.09350 + r1 * 0.91444 + r2 * -0.00794;
            ibuf[1] = i0 * 0.09350 + i1 * 0.91444 + i2 * -0.00794;
            rbuf[2] = r1 * 1.00000;
            ibuf[2] = i1 * 1.00000;
            rbuf[3] = r0 * -0.00794 + r1 * 0.91444 + r2 * 0.09350;
            ibuf[3] = i0 * -0.00794 + i1 * 0.91444 + i2 * 0.09350;
            rbuf[4] = r1 * 0.66577 + r2 * 0.33423;
            ibuf[4] = i1 * 0.66577 + i2 * 0.33423;
        }

    }

    /**
     * ### decimation : 6
     */
    public static class RS6 implements RS {
        private double r0=0.0, r1=0.0, r2=0.0, r3=0.0, r4=0.0, r5=0.0, r6=0.0, r7=0.0
        ;
        private double i0=0.0, i1=0.0, i2=0.0, i3=0.0, i4=0.0, i5=0.0, i6=0.0, i7=0.0
        ;
        private int idx = 0;
        private double r = 0.0;
        private double i = 0.0;
        public double getR(){ return r; }
        public double getI(){ return i; }

        public boolean decimate(double v) {
            r0=r1; r1=r2; r2=r3; r3=r4; r4=r5; r5=r6; r6=r7; r7=v;
            if (++idx >= 6) {
                idx = 0;
                r = r1*0.08386 + r2*0.19211 + r3*0.18908 + r4*0.18852 +
                r5*0.19112 + r6*0.10968;
                return true;
            } else {
                return false;
            }
        }

        public boolean decimate(double r, double i) {
            r0=r1; r1=r2; r2=r3; r3=r4; r4=r5; r5=r6; r6=r7; r7=r;
            i0=i1; i1=i2; i2=i3; i3=i4; i4=i5; i5=i6; i6=i7; i7=i;
            if (++idx >= 6) {
                idx = 0;
                this.r = r1*0.08386 + r2*0.19211 + r3*0.18908 + r4*0.18852 +
                r5*0.19112 + r6*0.10968;
                this.i = i1*0.08386 + i2*0.19211 + i3*0.18908 + i4*0.18852 +
                i5*0.19112 + i6*0.10968;
                return true;
            } else {
                return false;
            }
        }

        public void interpolate(double v, double buf[]) {
            r0 = r1; r1 = r2; r2 = v;
            buf[0] = r0 * 0.44281 + r1 * 0.55719;
            buf[1] = r0 * 0.19844 + r1 * 0.80894 + r2 * -0.00738;
            buf[2] = r0 * 0.05390 + r1 * 0.96423 + r2 * -0.01813;
            buf[3] = r1 * 1.00000;
            buf[4] = r0 * -0.00477 + r1 * 0.91499 + r2 * 0.08978;
            buf[5] = r1 * 0.71563 + r2 * 0.28437;
        }

        public void interpolate(double r, double i, double rbuf[], double ibuf[]) {

            r0 = r1; r1 = r2; r2 = r;
            i0 = i1; i1 = i2; i2 = i;
            rbuf[0] = r0 * 0.44281 + r1 * 0.55719;
            ibuf[0] = i0 * 0.44281 + i1 * 0.55719;
            rbuf[1] = r0 * 0.19844 + r1 * 0.80894 + r2 * -0.00738;
            ibuf[1] = i0 * 0.19844 + i1 * 0.80894 + i2 * -0.00738;
            rbuf[2] = r0 * 0.05390 + r1 * 0.96423 + r2 * -0.01813;
            ibuf[2] = i0 * 0.05390 + i1 * 0.96423 + i2 * -0.01813;
            rbuf[3] = r1 * 1.00000;
            ibuf[3] = i1 * 1.00000;
            rbuf[4] = r0 * -0.00477 + r1 * 0.91499 + r2 * 0.08978;
            ibuf[4] = i0 * -0.00477 + i1 * 0.91499 + i2 * 0.08978;
            rbuf[5] = r1 * 0.71563 + r2 * 0.28437;
            ibuf[5] = i1 * 0.71563 + i2 * 0.28437;
        }

    }

    /**
     * ### decimation : 7
     */
    public static class RS7 implements RS {
        private double r0=0.0, r1=0.0, r2=0.0, r3=0.0, r4=0.0, r5=0.0, r6=0.0, r7=0.0
        , r8=0.0;
        private double i0=0.0, i1=0.0, i2=0.0, i3=0.0, i4=0.0, i5=0.0, i6=0.0, i7=0.0
        , i8=0.0;
        private int idx = 0;
        private double r = 0.0;
        private double i = 0.0;
        public double getR(){ return r; }
        public double getI(){ return i; }

        public boolean decimate(double v) {
            r0=r1; r1=r2; r2=r3; r3=r4; r4=r5; r5=r6; r6=r7; r7=r8; r8=v;
            if (++idx >= 7) {
                idx = 0;
                r = r1*0.08145 + r2*0.16168 + r3*0.15962 + r4*0.15876 +
                r5*0.15962 + r6*0.16168 + r7*0.08145;
                return true;
            } else {
                return false;
            }
        }

        public boolean decimate(double r, double i) {
            r0=r1; r1=r2; r2=r3; r3=r4; r4=r5; r5=r6; r6=r7; r7=r8; r8=r;
            i0=i1; i1=i2; i2=i3; i3=i4; i4=i5; i5=i6; i6=i7; i7=i8; i8=i;
            if (++idx >= 7) {
                idx = 0;
                this.r = r1*0.08145 + r2*0.16168 + r3*0.15962 + r4*0.15876 +
                r5*0.15962 + r6*0.16168 + r7*0.08145;
                this.i = i1*0.08145 + i2*0.16168 + i3*0.15962 + i4*0.15876 +
                i5*0.15962 + i6*0.16168 + i7*0.08145;
                return true;
            } else {
                return false;
            }
        }

        public void interpolate(double v, double buf[]) {
            r0 = r1; r1 = r2; r2 = v;
            buf[0] = r0 * 0.38208 + r1 * 0.61792;
            buf[1] = r0 * 0.18197 + r1 * 0.82298 + r2 * -0.00495;
            buf[2] = r0 * 0.05639 + r1 * 0.95530 + r2 * -0.01169;
            buf[3] = r1;
            buf[4] = r0 * -0.01169 + r1 * 0.95530 + r2 * 0.05639;
            buf[5] = r0 * -0.00495 + r1 * 0.82298 + r2 * 0.18197;
            buf[6] = r1 * 0.61792 + r2 * 0.38208;
        }

        public void interpolate(double r, double i, double rbuf[], double ibuf[]) {

            r0 = r1; r1 = r2; r2 = r;
            i0 = i1; i1 = i2; i2 = i;
            rbuf[0] = r0 * 0.38208 + r1 * 0.61792;
            ibuf[0] = i0 * 0.38208 + i1 * 0.61792;
            rbuf[1] = r0 * 0.18197 + r1 * 0.82298 + r2 * -0.00495;
            ibuf[1] = i0 * 0.18197 + i1 * 0.82298 + i2 * -0.00495;
            rbuf[2] = r0 * 0.05639 + r1 * 0.95530 + r2 * -0.01169;
            ibuf[2] = i0 * 0.05639 + i1 * 0.95530 + i2 * -0.01169;
            rbuf[3] = r1 * 1.00000;
            ibuf[3] = i1 * 1.00000;
            rbuf[4] = r0 * -0.01169 + r1 * 0.95530 + r2 * 0.05639;
            ibuf[4] = i0 * -0.01169 + i1 * 0.95530 + i2 * 0.05639;
            rbuf[5] = r0 * -0.00495 + r1 * 0.82298 + r2 * 0.18197;
            ibuf[5] = i0 * -0.00495 + i1 * 0.82298 + i2 * 0.18197;
            rbuf[6] = r1 * 0.61792 + r2 * 0.38208;
            ibuf[6] = i1 * 0.61792 + i2 * 0.38208;
        }

    }

    /**
     * ### decimation : 11
     */
    public static class RS11 implements RS {
        private double r0=0.0, r1=0.0, r2=0.0, r3=0.0, r4=0.0, r5=0.0, r6=0.0, r7=0.0
        , r8=0.0, r9=0.0, r10=0.0, r11=0.0, r12=0.0;
        private double i0=0.0, i1=0.0, i2=0.0, i3=0.0, i4=0.0, i5=0.0, i6=0.0, i7=0.0
        , i8=0.0, i9=0.0, i10=0.0, i11=0.0, i12=0.0;
        private int idx = 0;
        private double r = 0.0;
        private double i = 0.0;
        public double getR(){ return r; }
        public double getI(){ return i; }

        public boolean decimate(double v) {
            r0=r1; r1=r2; r2=r3; r3=r4; r4=r5; r5=r6; r6=r7; r7=r8; r8=r9; r9=r10;
            r10=r11; r11=r12; r12=v;
            if (++idx >= 11) {
                idx = 0;
                r = r1*0.04883 + r2*0.09744 + r3*0.09703 + r4*0.09671 +
                r5*0.09651 + r6*0.09645 + r7*0.09651 + r8*0.09671 + r9*0.09703 +
                r10*0.09744 + r11*0.04883;
                return true;
            } else {
                return false;
            }
        }

        public boolean decimate(double r, double i) {
            r0=r1; r1=r2; r2=r3; r3=r4; r4=r5; r5=r6; r6=r7; r7=r8; r8=r9; r9=r10;
            r10=r11; r11=r12; r12=r;
            i0=i1; i1=i2; i2=i3; i3=i4; i4=i5; i5=i6; i6=i7; i7=i8; i8=i9; i9=i10;
            i10=i11; i11=i12; i12=i;
            if (++idx >= 11) {
                idx = 0;
                this.r = r1*0.04883 + r2*0.09744 + r3*0.09703 + r4*0.09671 +
                r5*0.09651 + r6*0.09645 + r7*0.09651 + r8*0.09671 + r9*0.09703 +
                r10*0.09744 + r11*0.04883;
                this.i = i1*0.04883 + i2*0.09744 + i3*0.09703 + i4*0.09671 +
                i5*0.09651 + i6*0.09645 + i7*0.09651 + i8*0.09671 + i9*0.09703 +
                i10*0.09744 + i11*0.04883;
                return true;
            } else {
                return false;
            }
        }

        public void interpolate(double v, double buf[]) {
            r0 = r1; r1 = r2; r2 = v;
            buf[0] = r0 * 0.42552 + r1 * 0.57448;
            buf[1] = r0 * 0.28635 + r1 * 0.71580 + r2 * -0.00215;
            buf[2] = r0 * 0.17122 + r1 * 0.83623 + r2 * -0.00745;
            buf[3] = r0 * 0.08613 + r1 * 0.92636 + r2 * -0.01248;
            buf[4] = r0 * 0.03059 + r1 * 0.98151 + r2 * -0.01209;
            buf[5] = r1 * 1.00000;
            buf[6] = r0 * -0.01209 + r1 * 0.98151 + r2 * 0.03059;
            buf[7] = r0 * -0.01248 + r1 * 0.92636 + r2 * 0.08613;
            buf[8] = r0 * -0.00745 + r1 * 0.83623 + r2 * 0.17122;
            buf[9] = r0 * -0.00215 + r1 * 0.71580 + r2 * 0.28635;
            buf[10] = r1 * 0.57448 + r2 * 0.42552;
        }

        public void interpolate(double r, double i, double rbuf[], double ibuf[]) {

            r0 = r1; r1 = r2; r2 = r;
            i0 = i1; i1 = i2; i2 = i;
            rbuf[0] = r0 * 0.42552 + r1 * 0.57448;
            ibuf[0] = i0 * 0.42552 + i1 * 0.57448;
            rbuf[1] = r0 * 0.28635 + r1 * 0.71580 + r2 * -0.00215;
            ibuf[1] = i0 * 0.28635 + i1 * 0.71580 + i2 * -0.00215;
            rbuf[2] = r0 * 0.17122 + r1 * 0.83623 + r2 * -0.00745;
            ibuf[2] = i0 * 0.17122 + i1 * 0.83623 + i2 * -0.00745;
            rbuf[3] = r0 * 0.08613 + r1 * 0.92636 + r2 * -0.01248;
            ibuf[3] = i0 * 0.08613 + i1 * 0.92636 + i2 * -0.01248;
            rbuf[4] = r0 * 0.03059 + r1 * 0.98151 + r2 * -0.01209;
            ibuf[4] = i0 * 0.03059 + i1 * 0.98151 + i2 * -0.01209;
            rbuf[5] = r1 * 1.00000;
            ibuf[5] = i1 * 1.00000;
            rbuf[6] = r0 * -0.01209 + r1 * 0.98151 + r2 * 0.03059;
            ibuf[6] = i0 * -0.01209 + i1 * 0.98151 + i2 * 0.03059;
            rbuf[7] = r0 * -0.01248 + r1 * 0.92636 + r2 * 0.08613;
            ibuf[7] = i0 * -0.01248 + i1 * 0.92636 + i2 * 0.08613;
            rbuf[8] = r0 * -0.00745 + r1 * 0.83623 + r2 * 0.17122;
            ibuf[8] = i0 * -0.00745 + i1 * 0.83623 + i2 * 0.17122;
            rbuf[9] = r0 * -0.00215 + r1 * 0.71580 + r2 * 0.28635;
            ibuf[9] = i0 * -0.00215 + i1 * 0.71580 + i2 * 0.28635;
            rbuf[10] = r1 * 0.57448 + r2 * 0.42552;
            ibuf[10] = i1 * 0.57448 + i2 * 0.42552;
        }

    }

    public static RS create(int decimation) {
        switch (decimation) {
            case 2 : return new RS2();
            case 3 : return new RS3();
            case 4 : return new RS4();
            case 5 : return new RS5();
            case 6 : return new RS6();
            case 7 : return new RS7();
            default :
                throw new IllegalArgumentException("Decimation " + decimation + " is not supported");

        }
    }
}
