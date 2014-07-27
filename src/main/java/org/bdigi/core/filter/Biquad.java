package org.bdigi.core.filter;

import org.bdigi.core.Complex;

/**
 * Factory for Biquad filters
 */
public class Biquad {

    public static class BqFilter implements Filter {
    
        private double x1r, x2r, y1r, y2r, x1i, x2i, y1i, y2i;
        private double b0, b1, b2, a1, a2;
        
        public BqFilter(double b0, double b1, double b2, double a1, double a2) {
            this.b0 = b0; this.b1 = b1; this.b2 = b2; this.a1 = a1; this.a2 = a2;
        }

        public double update(double x) {
            double y = b0*x + b1*x1r + b2*x2r - a1*y1r - a2*y2r;
            x2r = x1r; x1r = x; y2r = y1r; y1r = y;
            return y;
        }

        public Complex update(Complex x) {
            double r = x.getR(); double i = x.getI();
            double yr = b0*r + b1*x1r + b2*x2r - a1*y1r - a2*y2r;
            double yi = b0*i + b1*x1i + b2*x2i - a1*y1i - a2*y2i;
            x2r = x1r; x1r = r; y2r = y1r; y1r = yr;
            x2i = x1i; x1i = i; y2i = y1i; y1i = yi;
            return new Complex(yr, yi);
        }
    }

    public static BqFilter lowpass(double frequency, double sampleRate, double q) {
        double freq = 2.0 * Math.PI * frequency / sampleRate;
        double alpha = Math.sin(freq) / (2.0 * q);
        double b0 = (1.0 - Math.cos(freq)) / 2.0;
        double b1 =  1.0 - Math.cos(freq);
        double b2 = (1.0 - Math.cos(freq)) / 2.0;
        double a0 = 1.0 + alpha;
        double a1 = -2.0 * Math.cos(freq);
        double a2 = 1.0 - alpha;
        return new BqFilter(b0/a0, b1/a0, b2/a0, a1/a0, a2/a0);
    }
    public static BqFilter lowpass(double frequency, double sampleRate) {
        return lowpass(frequency, sampleRate, 0.707);
    }

    public static BqFilter highpass(double frequency, double sampleRate, double q) {
        double freq = 2.0 * Math.PI * frequency / sampleRate;
        double alpha = Math.sin(freq) / (2.0 * q);
        double b0 =  (1.0 + Math.cos(freq)) / 2.0;
        double b1 = -(1.0 + Math.cos(freq));
        double b2 =  (1.0 + Math.cos(freq)) / 2.0;
        double a0 = 1.0 + alpha;
        double a1 = -2.0 * Math.cos(freq);
        double a2 = 1.0 - alpha;
        return new BqFilter(b0/a0, b1/a0, b2/a0, a1/a0, a2/a0);
    }
    public static BqFilter highpass(double frequency, double sampleRate) {
        return highpass(frequency, sampleRate, 0.707);
    }

    public static BqFilter bandpass(double frequency, double sampleRate, double q) {
        double freq = 2.0 * Math.PI * frequency / sampleRate;
        double alpha = Math.sin(freq) / (2.0 * q);
        double b0 = Math.sin(freq) / 2.0;   // = q*alpha
        double b1 = 0.0;
        double b2 = -Math.sin(freq) / 2.0;  // = -q*alpha
        double a0 = 1.0 + alpha;
        double a1 = -2.0 * Math.cos(freq);
        double a2 = 1.0 - alpha;
        return new BqFilter(b0/a0, b1/a0, b2/a0, a1/a0, a2/a0);
    }
    public static BqFilter bandpass(double frequency, double sampleRate) {
        return highpass(frequency, sampleRate, 0.707);
    }

    public static BqFilter bandreject(double frequency, double sampleRate, double q) {
        double freq = 2.0 * Math.PI * frequency / sampleRate;
        double alpha = Math.sin(freq) / (2.0 * q);
        double b0 = 1.0;
        double b1 = -2.0 * Math.cos(freq);
        double b2 = 1.0;
        double a0 = 1.0 + alpha;
        double a1 = -2.0 * Math.cos(freq);
        double a2 = 1.0 - alpha;
        return new BqFilter(b0/a0, b1/a0, b2/a0, a1/a0, a2/a0);
    }
    public static BqFilter bandreject(double frequency, double sampleRate) {
        return highpass(frequency, sampleRate, 0.707);
    }

}

