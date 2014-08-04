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
            r = r2*0.90451;
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
            r = r2*0.90451;
            i = i2*0.90451;
            return true;
        } else {
            return false;
        }
    };

    public void interpolate(double v, double buf[]) {
        r0 = r1; r1 = r2; r2 = v;
        buf[0] = 0;
        buf[1] = r1 * 0.90451;
    };

    public void interpolate(double r, double i, double rbuf[], double ibuf[]) {
                
        r0 = r1; r1 = r2; r2 = r;
        i0 = i1; i1 = i2; i2 = i;
        rbuf[0] = 0.0;
        ibuf[0] = 0.0;
        rbuf[1] = r1 * 0.90451;
        ibuf[1] = i1 * 0.90451;
    };

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
            r = r1*0.21783 + r2*0.48959 + r3*0.21783;
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
            r = r1*0.21783 + r2*0.48959 + r3*0.21783;
            i = i1*0.21783 + i2*0.48959 + i3*0.21783;
            return true;
        } else {
            return false;
        }
    };

    public void interpolate(double v, double buf[]) {
        r0 = r1; r1 = r2; r2 = v;
        buf[0] = r1 * 0.21783 + r2 * -0.06380;
        buf[1] = r1 * 0.61719;
        buf[2] = r0 * -0.06380 + r1 * 0.21783;
    };

    public void interpolate(double r, double i, double rbuf[], double ibuf[]) {
                
        r0 = r1; r1 = r2; r2 = r;
        i0 = i1; i1 = i2; i2 = i;
        rbuf[0] = r1 * 0.21783 + r2 * -0.06380;
        ibuf[0] = i1 * 0.21783 + i2 * -0.06380;
        rbuf[1] = r1 * 0.61719;
        ibuf[1] = i1 * 0.61719;
        rbuf[2] = r0 * -0.06380 + r1 * 0.21783;
        ibuf[2] = i0 * -0.06380 + i1 * 0.21783;
    };

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
            r = r1*0.00480 + r2*0.29652 + r3*0.37867 + r4*0.25042;
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
            r = r1*0.00480 + r2*0.29652 + r3*0.37867 + r4*0.25042;
            i = i1*0.00480 + i2*0.29652 + i3*0.37867 + i4*0.25042;
            return true;
        } else {
            return false;
        }
    };

    public void interpolate(double v, double buf[]) {
        r0 = r1; r1 = r2; r2 = v;
        buf[0] = 0;
        buf[1] = r0 * 0.00480 + r1 * 0.29652 + r2 * -0.02949;
        buf[2] = r1 * 0.46578;
        buf[3] = r0 * -0.05762 + r1 * 0.25042;
    };

    public void interpolate(double r, double i, double rbuf[], double ibuf[]) {
                
        r0 = r1; r1 = r2; r2 = r;
        i0 = i1; i1 = i2; i2 = i;
        rbuf[0] = 0.0;
        ibuf[0] = 0.0;
        rbuf[1] = r0 * 0.00480 + r1 * 0.29652 + r2 * -0.02949;
        ibuf[1] = i0 * 0.00480 + i1 * 0.29652 + i2 * -0.02949;
        rbuf[2] = r1 * 0.46578;
        ibuf[2] = i1 * 0.46578;
        rbuf[3] = r0 * -0.05762 + r1 * 0.25042;
        ibuf[3] = i0 * -0.05762 + i1 * 0.25042;
    };

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
            r = r1*0.07325 + r2*0.23311 + r3*0.31859 + r4*0.23311 + 
                r5*0.07325;
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
            r = r1*0.07325 + r2*0.23311 + r3*0.31859 + r4*0.23311 + 
                r5*0.07325;
            i = i1*0.07325 + i2*0.23311 + i3*0.31859 + i4*0.23311 + 
                i5*0.07325;
            return true;
        } else {
            return false;
        }
    };

    public void interpolate(double v, double buf[]) {
        r0 = r1; r1 = r2; r2 = v;
        buf[0] = r1 * 0.07092 + r2 * -0.03560;
        buf[1] = r0 * 0.00233 + r1 * 0.26871 + r2 * -0.02747;
        buf[2] = r1 * 0.37354;
        buf[3] = r0 * -0.02747 + r1 * 0.26871 + r2 * 0.00233;
        buf[4] = r0 * -0.03560 + r1 * 0.07092;
    };

    public void interpolate(double r, double i, double rbuf[], double ibuf[]) {
                
        r0 = r1; r1 = r2; r2 = r;
        i0 = i1; i1 = i2; i2 = i;
        rbuf[0] = r1 * 0.07092 + r2 * -0.03560;
        ibuf[0] = i1 * 0.07092 + i2 * -0.03560;
        rbuf[1] = r0 * 0.00233 + r1 * 0.26871 + r2 * -0.02747;
        ibuf[1] = i0 * 0.00233 + i1 * 0.26871 + i2 * -0.02747;
        rbuf[2] = r1 * 0.37354;
        ibuf[2] = i1 * 0.37354;
        rbuf[3] = r0 * -0.02747 + r1 * 0.26871 + r2 * 0.00233;
        ibuf[3] = i0 * -0.02747 + i1 * 0.26871 + i2 * 0.00233;
        rbuf[4] = r0 * -0.03560 + r1 * 0.07092;
        ibuf[4] = i0 * -0.03560 + i1 * 0.07092;
    };

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
            r = r1*0.00110 + r2*0.12515 + r3*0.22836 + r4*0.27379 + 
                r5*0.19920 + r6*0.10546;
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
            r = r1*0.00110 + r2*0.12515 + r3*0.22836 + r4*0.27379 + 
                r5*0.19920 + r6*0.10546;
            i = i1*0.00110 + i2*0.12515 + i3*0.22836 + i4*0.27379 + 
                i5*0.19920 + i6*0.10546;
            return true;
        } else {
            return false;
        }
    };

    public void interpolate(double v, double buf[]) {
        r0 = r1; r1 = r2; r2 = v;
        buf[0] = 0;
        buf[1] = r0 * 0.00110 + r1 * 0.12030 + r2 * -0.02951;
        buf[2] = r0 * 0.00485 + r1 * 0.25787 + r2 * -0.01442;
        buf[3] = r1 * 0.31182;
        buf[4] = r0 * -0.02361 + r1 * 0.24061 + r2 * 0.00125;
        buf[5] = r0 * -0.04141 + r1 * 0.10420;
    };

    public void interpolate(double r, double i, double rbuf[], double ibuf[]) {
                
        r0 = r1; r1 = r2; r2 = r;
        i0 = i1; i1 = i2; i2 = i;
        rbuf[0] = 0.0;
        ibuf[0] = 0.0;
        rbuf[1] = r0 * 0.00110 + r1 * 0.12030 + r2 * -0.02951;
        ibuf[1] = i0 * 0.00110 + i1 * 0.12030 + i2 * -0.02951;
        rbuf[2] = r0 * 0.00485 + r1 * 0.25787 + r2 * -0.01442;
        ibuf[2] = i0 * 0.00485 + i1 * 0.25787 + i2 * -0.01442;
        rbuf[3] = r1 * 0.31182;
        ibuf[3] = i1 * 0.31182;
        rbuf[4] = r0 * -0.02361 + r1 * 0.24061 + r2 * 0.00125;
        ibuf[4] = i0 * -0.02361 + i1 * 0.24061 + i2 * 0.00125;
        rbuf[5] = r0 * -0.04141 + r1 * 0.10420;
        ibuf[5] = i0 * -0.04141 + i1 * 0.10420;
    };

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
            r = r1*0.03499 + r2*0.11298 + r3*0.19817 + r4*0.24057 + 
                r5*0.19817 + r6*0.11298 + r7*0.03499;
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
            r = r1*0.03499 + r2*0.11298 + r3*0.19817 + r4*0.24057 + 
                r5*0.19817 + r6*0.11298 + r7*0.03499;
            i = i1*0.03499 + i2*0.11298 + i3*0.19817 + i4*0.24057 + 
                i5*0.19817 + i6*0.11298 + i7*0.03499;
            return true;
        } else {
            return false;
        }
    };

    public void interpolate(double v, double buf[]) {
        r0 = r1; r1 = r2; r2 = v;
        buf[0] = r1 * 0.03420 + r2 * -0.02115;
        buf[1] = r0 * 0.00079 + r1 * 0.13135 + r2 * -0.02904;
        buf[2] = r0 * 0.00278 + r1 * 0.22721 + r2 * -0.01341;
        buf[3] = r1 * 0.26740;
        buf[4] = r0 * -0.01341 + r1 * 0.22721 + r2 * 0.00278;
        buf[5] = r0 * -0.02904 + r1 * 0.13135 + r2 * 0.00079;
        buf[6] = r0 * -0.02115 + r1 * 0.03420;
    };

    public void interpolate(double r, double i, double rbuf[], double ibuf[]) {
                
        r0 = r1; r1 = r2; r2 = r;
        i0 = i1; i1 = i2; i2 = i;
        rbuf[0] = r1 * 0.03420 + r2 * -0.02115;
        ibuf[0] = i1 * 0.03420 + i2 * -0.02115;
        rbuf[1] = r0 * 0.00079 + r1 * 0.13135 + r2 * -0.02904;
        ibuf[1] = i0 * 0.00079 + i1 * 0.13135 + i2 * -0.02904;
        rbuf[2] = r0 * 0.00278 + r1 * 0.22721 + r2 * -0.01341;
        ibuf[2] = i0 * 0.00278 + i1 * 0.22721 + i2 * -0.01341;
        rbuf[3] = r1 * 0.26740;
        ibuf[3] = i1 * 0.26740;
        rbuf[4] = r0 * -0.01341 + r1 * 0.22721 + r2 * 0.00278;
        ibuf[4] = i0 * -0.01341 + i1 * 0.22721 + i2 * 0.00278;
        rbuf[5] = r0 * -0.02904 + r1 * 0.13135 + r2 * 0.00079;
        ibuf[5] = i0 * -0.02904 + i1 * 0.13135 + i2 * 0.00079;
        rbuf[6] = r0 * -0.02115 + r1 * 0.03420;
        ibuf[6] = i0 * -0.02115 + i1 * 0.03420;
    };

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
            r = r1*0.01322 + r2*0.03922 + r3*0.07264 + r4*0.11402 + 
                r5*0.14759 + r6*0.16043 + r7*0.14759 + r8*0.11402 + r9*0.07264 + 
                r10*0.03922 + r11*0.01322;
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
            r = r1*0.01322 + r2*0.03922 + r3*0.07264 + r4*0.11402 + 
                r5*0.14759 + r6*0.16043 + r7*0.14759 + r8*0.11402 + r9*0.07264 + 
                r10*0.03922 + r11*0.01322;
            i = i1*0.01322 + i2*0.03922 + i3*0.07264 + i4*0.11402 + 
                i5*0.14759 + i6*0.16043 + i7*0.14759 + i8*0.11402 + i9*0.07264 + 
                i10*0.03922 + i11*0.01322;
            return true;
        } else {
            return false;
        }
    };

    public void interpolate(double v, double buf[]) {
        r0 = r1; r1 = r2; r2 = v;
        buf[0] = r1 * 0.01307 + r2 * -0.00968;
        buf[1] = r0 * 0.00014 + r1 * 0.04810 + r2 * -0.01924;
        buf[2] = r0 * 0.00080 + r1 * 0.09012 + r2 * -0.01845;
        buf[3] = r0 * 0.00176 + r1 * 0.13050 + r2 * -0.01213;
        buf[4] = r0 * 0.00197 + r1 * 0.15972 + r2 * -0.00498;
        buf[5] = r1 * 0.17038;
        buf[6] = r0 * -0.00498 + r1 * 0.15972 + r2 * 0.00197;
        buf[7] = r0 * -0.01213 + r1 * 0.13050 + r2 * 0.00176;
        buf[8] = r0 * -0.01845 + r1 * 0.09012 + r2 * 0.00080;
        buf[9] = r0 * -0.01924 + r1 * 0.04810 + r2 * 0.00014;
        buf[10] = r0 * -0.00968 + r1 * 0.01307;
    };

    public void interpolate(double r, double i, double rbuf[], double ibuf[]) {
                
        r0 = r1; r1 = r2; r2 = r;
        i0 = i1; i1 = i2; i2 = i;
        rbuf[0] = r1 * 0.01307 + r2 * -0.00968;
        ibuf[0] = i1 * 0.01307 + i2 * -0.00968;
        rbuf[1] = r0 * 0.00014 + r1 * 0.04810 + r2 * -0.01924;
        ibuf[1] = i0 * 0.00014 + i1 * 0.04810 + i2 * -0.01924;
        rbuf[2] = r0 * 0.00080 + r1 * 0.09012 + r2 * -0.01845;
        ibuf[2] = i0 * 0.00080 + i1 * 0.09012 + i2 * -0.01845;
        rbuf[3] = r0 * 0.00176 + r1 * 0.13050 + r2 * -0.01213;
        ibuf[3] = i0 * 0.00176 + i1 * 0.13050 + i2 * -0.01213;
        rbuf[4] = r0 * 0.00197 + r1 * 0.15972 + r2 * -0.00498;
        ibuf[4] = i0 * 0.00197 + i1 * 0.15972 + i2 * -0.00498;
        rbuf[5] = r1 * 0.17038;
        ibuf[5] = i1 * 0.17038;
        rbuf[6] = r0 * -0.00498 + r1 * 0.15972 + r2 * 0.00197;
        ibuf[6] = i0 * -0.00498 + i1 * 0.15972 + i2 * 0.00197;
        rbuf[7] = r0 * -0.01213 + r1 * 0.13050 + r2 * 0.00176;
        ibuf[7] = i0 * -0.01213 + i1 * 0.13050 + i2 * 0.00176;
        rbuf[8] = r0 * -0.01845 + r1 * 0.09012 + r2 * 0.00080;
        ibuf[8] = i0 * -0.01845 + i1 * 0.09012 + i2 * 0.00080;
        rbuf[9] = r0 * -0.01924 + r1 * 0.04810 + r2 * 0.00014;
        ibuf[9] = i0 * -0.01924 + i1 * 0.04810 + i2 * 0.00014;
        rbuf[10] = r0 * -0.00968 + r1 * 0.01307;
        ibuf[10] = i0 * -0.00968 + i1 * 0.01307;
    };

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
