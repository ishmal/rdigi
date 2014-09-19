package org.bdigi.core.mode;

import org.bdigi.core.*;

/**
 * Created by vjamiro on 7/24/14.
 */
public class Mode {

    Digi par;
    Property props;
    int decimation;
    double sampleRate;
    Resampler.RS decimator;
    Resampler.RS interpolator;
    Nco nco;
    Complex NULL_COMPLEX_ARRAY[];
    Complex ZERO_COMPLEX_ARRAY[];
    private final static int ZERO_SIZE = 1024;
     private double interpr[];
    private double interpi[];

    public Mode(Digi par, Property props, double sampleRateHint) {
        this.par = par;
        this.props = props;
        props.setMode(this);

        adjustAfc();

        decimation = (int)(par.getSampleRate() / sampleRateHint);
        sampleRate = par.getSampleRate() / decimation;

        decimator = Resampler.create(decimation);
        interpolator = Resampler.create(decimation);

        nco = new Nco(frequency, par.getSampleRate());

        NULL_COMPLEX_ARRAY = new Complex[0];
        ZERO_COMPLEX_ARRAY = new Complex[ZERO_SIZE];
        for (int i=0 ; i<ZERO_SIZE ; i++) {
            ZERO_COMPLEX_ARRAY[i] = new Complex(0);
        }

        interpr = new double[decimation];
        interpi = new double[decimation];

    }

    public Property getProperties() {
        return props;
    }

    protected void error(String msg) {
        par.error("mode: " + msg);
    }

    protected void trace(String msg) {
        par.trace("mode: " + msg);
    }

    public void booleanControl(String name, boolean value) {

    }

    public void radioControl(String name, String value) {

    }


    private double frequency;

    public void setFrequency(double v) {
        frequency = v;
        nco.setFrequency(v);
        adjustAfc();
    }

    public double getFrequency() {
        return frequency;
    }

    public double getBandwidth() {
        return 0;
    }

    private double rate;

    public void setRate(double v) {
        rate = v;
        samplesPerSymbol = sampleRate / rate;
    }

    public double getRate() {
        return rate;
    }

    public double getSampleRate() {
        return sampleRate;
    }

    private double samplesPerSymbol;

    public double getSamplesPerSymbol() {
        return samplesPerSymbol;
    }

    int loBin, freqBin, hiBin;

    private void adjustAfc() {
        double freq = frequency;
        double fs = par.getSampleRate();
        double bw = getBandwidth();
        double binWidth = fs * 0.5 / Constants.BINS;
        loBin = (int)((freq-bw*0.707) / binWidth);
        freqBin = (int)(freq / binWidth);
        hiBin = (int) ((freq+bw*0.707) / binWidth);
        //console.log("afc: " + loBin + "," + freqBin + "," + hiBin);
    }

    public void receiveData(double v) {
        double cs[] = nco.next();
        if (decimator.decimate(v*cs[0], v*cs[1])) {
            receive(new Complex(decimator.getR(), decimator.getI()));
        }
    }

    public void receive(Complex v) {
        //overload this
    }

    int tidx = 0;
    Complex tdata[] = new Complex[0];
    int tdatalen = 0;

    public double[] getTransmitData() {
        if (tidx >= tdatalen) {
            tdata = transmit();
            tidx = 0;
            tdatalen = tdata.length;
            if (tdatalen==0)
                return new double[0];
        }

        Complex v = tdata[tidx++];
        interpolator.interpolate(v.getR(), v.getI(), interpr, interpi);
        double outbuf[] = new double[decimation];
        for (int i=0 ; i<decimation ; i++) {
            double cs[] = nco.next();
            double cr = cs[0];
            double ci = cs[1];
            double ir = interpr[i];
            double ii = interpi[i];
            double vr = cr * ii - ci * ir;
            double vi = cr * ir - ci * ii;
            double outval = Math.hypot(vr, vi);
            outbuf[i] = outval;
        }
        return outbuf;
    }


    /**
     * Override this in each mode
     * @return An
     */
    public Complex[] transmit() {
        return ZERO_COMPLEX_ARRAY;
    }

}
