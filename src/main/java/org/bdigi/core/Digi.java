
package org.bdigi.core;


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
    private Mode mode;

    private int decimation;
    private Resampler.D decimator;
    private Resampler.D interpolator;

    public Digi() {

        pskMode = new Psk(this);
        rttyMode = new Rtty(this);
        navtexMode = new Navtex(this);
        mode = pskMode;

        fft = new FFT(Constants.FFT_SIZE);
        decimation = 7;
        decimator = Resampler.create(decimation);
        interpolator = Resampler.create(decimation);


    }

    public void error(String msg) {
        System.out.println("digi err: " + msg);
    }

    public  void trace(String msg) {
        System.out.println("digi: " + msg);
    }


    public double getSampleRate() {
        return 0;
    }



    public void receiveData(double v) {
        if (decimator.decimate(v)) {
            mode.receiveData(decimator.getValue());
        }
    }

    public void status(String s) {

    }

    public void puttext(String s) {

    }















}
