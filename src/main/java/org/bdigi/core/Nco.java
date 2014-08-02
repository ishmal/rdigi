package org.bdigi.core;

/**
 * Numerically-controlled oscillator.
 * Has error adjustment for AFC,
 */
public class Nco {

    private double table[][];
    private long freq;
    private long phase;
    private long err;
    private double hzToInt;
    private long maxErr;
    private long minErr;

    public Nco(double frequency, double sampleRate) {

        generateTable();
        hzToInt = 4294967296.0 / sampleRate;
        maxErr = (long)(50*hzToInt);
        minErr = -maxErr;
        setFrequency(frequency);
    }

    private void generateTable() {
        double twopi = Math.PI * 2.0;
        int two16 = 65536;
        double delta = twopi / two16;
        table = new double[two16][2];

        for (int idx = 0 ; idx < two16 ; idx++) {
            double angle = delta * idx;
            table[idx][0] = Math.cos(angle);
            table[idx][1] = Math.sin(angle);
        }
    }

    public void setFrequency(double frequency) {
        freq = (long) (frequency * hzToInt);
    }

    public void setError(double v) {
        err = (long)(err * 0.9 + v * 100000.0);
        if (err > maxErr) {
            err = maxErr;
        } else if (err < minErr) {
            err = minErr;
        }
    }

    public double[] next() {
        phase = (phase + (freq + err)) & 0xffffffff;
        return table[((int)(phase>>16)) & 0xffff];
    }

    public Complex mixNext(double v) {
        phase = (phase + (freq + err)) & 0xffffffff;
        double cs[] = table[((int)(phase>>16)) & 0xffff];
        return new Complex(cs[0]*v, cs[1]*v);
    }

}
