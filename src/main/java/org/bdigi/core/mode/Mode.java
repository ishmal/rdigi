package org.bdigi.core.mode;

import org.bdigi.core.*;

/**
 * Created by vjamiro on 7/24/14.
 */
public class Mode {

    Digi par;
    Property.Mode props;
    int decimation;
    double sampleRate;
    Resampler.X decimator;
    Resampler.X interpolator;
    Nco nco;

    public Mode(Digi par, Property.Mode props, double sampleRateHint) {
        this.par = par;
        this.props = props;

        adjustAfc();

        decimation = (int)(par.getSampleRate() / sampleRateHint);
        sampleRate = par.getSampleRate() / decimation;

        decimator = new Resampler.X(decimation);
        interpolator = new Resampler.X(decimation);

        nco = new Nco(frequency, par.getSampleRate());


    }

    public Property.Mode getProperties() {
        return props;
    }

    protected void error(String msg) {
        par.error("mode: " + msg);
    }

    protected void trace(String msg) {
        par.trace("mode: " + msg);
    }


    private double frequency;

    public void setFrequency(double v) {
        frequency = v;
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
        Complex cx = nco.mixNext(v);
        if (decimator.decimate(cx)) {
            receive(decimator.getValue());
        }
    }

    public void receive(Complex v) {
        //overload this
    }

}
