package org.bdigi.core.mode;

import org.bdigi.core.Constants;
import org.bdigi.core.Digi;
import org.bdigi.core.Nco;
import org.bdigi.core.Resampler;

/**
 * Created by vjamiro on 7/24/14.
 */
public class Mode {

    Digi par;
    int decimation;
    double sampleRate;
    Resampler.Instance decimator;
    Resampler.Instance interpolator;
    Nco nco;

    public Mode(Digi par, Object props, double sampleRateHint) {
        this.par = par;

        adjustAfc();

        decimation = (int)(par.getSampleRate() / sampleRateHint);
        sampleRate = par.getSampleRate() / decimation;

        decimator = Resampler.create(decimation);
        interpolator = Resampler.create(decimation);

        nco = new Nco(frequency, par.getSampleRate());


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
    }

    public double getRate() {
        return rate;
    }

    public double getSampleRate() {
        return sampleRate;
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

    }



}
