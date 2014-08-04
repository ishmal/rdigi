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

}
