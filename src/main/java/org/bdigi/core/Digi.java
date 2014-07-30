
package org.bdigi.core;


import org.bdigi.core.audio.Audio;
import org.bdigi.core.audio.AudioInput;
import org.bdigi.core.audio.AudioOutput;
import org.bdigi.core.mode.Mode;
import org.bdigi.core.mode.Navtex;
import org.bdigi.core.mode.Psk;
import org.bdigi.core.mode.Rtty;

public class Digi {

    private AudioInput audioInput;
    private AudioOutput audioOutput;

    private FFT fft;

    private Rtty rttyMode;
    private Psk pskMode;
    private Navtex navtexMode;
    private Mode modes[];
    private Mode mode;

    private int decimation;
    private Resampler.D decimator;
    private Resampler.D interpolator;

    private Config config;

    private double frequency;
    private double sampleRate;

    public Digi() {

        frequency = 1000.0;
        sampleRate = 6300.0;

        pskMode = new Psk(this);
        rttyMode = new Rtty(this);
        navtexMode = new Navtex(this);
        modes = new Mode[]{pskMode,rttyMode,navtexMode};
        mode = pskMode;

        fft = new FFT(Constants.FFT_SIZE);
        decimation = 7;
        decimator = Resampler.create(decimation);
        interpolator = Resampler.create(decimation);

        config = new Config(this);
    }

    public void error(String msg) {
        System.out.println("digi err: " + msg);
    }

    public  void trace(String msg) {
        System.out.println("digi: " + msg);
    }

    public Config getConfig() {
        return config;
    }

    public Mode[] getModes() {
        return modes;
    }

    public void setMode(Mode m) {
        mode = m;
    }

    public double getFrequency() {
        return frequency;
    }
    public void setFrequency(double v) {
        frequency = v;
        mode.setFrequency(v);
    }

    public double getBandwidth() {
        return mode.getBandwidth();
    }

    public double getSampleRate() {
        return sampleRate;
    }

    public void setTx(boolean v) {
        //do stuff
    }

    public void setAfc(boolean v) {
        //do stuff
    }

    public void setAgc(boolean v) {

    }


    public void receiveData(double v) {
        if (decimator.decimate(v)) {
            mode.receiveData(decimator.getValue());
        }
    }

    public void status(String s) {

    }

    public void showScope(double xs[][]) {
        //
    }
    public void puttext(String s) {

    }

    public void setAudioInput(String name) {
        audioInput = Audio.createInput(this, name);
    }

    public void setAudioOutput(String name) {
        audioOutput = Audio.createOutput(this, name);
    }
}
