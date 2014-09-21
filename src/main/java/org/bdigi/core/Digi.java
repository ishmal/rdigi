
package org.bdigi.core;


import org.bdigi.core.audio.Audio;
import org.bdigi.core.audio.AudioInput;
import org.bdigi.core.audio.AudioOutput;
import org.bdigi.core.mode.Mode;
import org.bdigi.core.mode.Navtex;
import org.bdigi.core.mode.Psk;
import org.bdigi.core.mode.Rtty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Digi {

    private AudioInput audioInput;
    private AudioOutput audioOutput;

    private FFT fft;

    private HashMap<String, Mode> modes;
    private Mode mode;

    private int decimation;
    private Resampler.RS decimator;
    private Resampler.RS interpolator;
    private double[] interpbuf;

    private Config config;
    private IoThread ioThread;

    private double frequency;
    private double sampleRate;
    private boolean rxtx;
    private final static int FFT_WINDOW = 400;
    private final static int FFT_MASK = Constants.FFT_SIZE - 1;
    private int fftptr;
    private int fftctr;
    private double fftin[];
    private double fftout[];

    public Digi() {

        frequency = 1000.0;
        sampleRate = 6300.0;

        modes = new HashMap<String,Mode>();
        mode = new Psk(this);
        modes.put("psk", mode);
        modes.put("rtty", new Rtty(this));
        modes.put("navtex", new Navtex(this));

        fft = new FFT(Constants.FFT_SIZE);
        decimation = 7;
        decimator = Resampler.create(decimation);
        interpolator = Resampler.create(decimation);
        interpbuf = new double[decimation];

        rxtx = false;
        fftin = new double[Constants.FFT_SIZE];
        fftout = new double[Constants.BINS];
        fftptr = 0;
        fftctr = 0;
        config = new Config(this);
        config.loadFile();
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

    public HashMap<String, Mode> getModes() {
        return modes;
    }

    public void setMode(Mode m) {
        double f = getFrequency();
        m.setFrequency(f);
        mode = m;
    }

    /**
     * Override this in gui if redraw is required
     * @param name
     * @param m
     */
    public void addMode(String name, Mode m) {
        modes.put(name, m);
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
        rxtx = v;
        if (rxtx) {
            pauseDisplay();
            if (audioInput != null) {
                audioInput.stop();
            }
            if (audioOutput != null) {
                audioOutput.start();
            }

        } else {
            resumeDisplay();
            if (audioOutput != null) {
                audioOutput.stop();
            }
            if (audioInput != null) {
                audioInput.start();
            }
        }
    }

    public void setAfc(boolean v) {
        //do stuff
    }

    public void setAgc(boolean v) {

    }


    public void receiveData(double v) {
    }

    public void status(String s) {

    }

    /**
     * Override this in the GUI to pause a display for any reason, such
     * as transmitting
     */
    public void pauseDisplay() {

    }

    /**
     * Override this in the GUI to resume a paused display
     */
    public void resumeDisplay() {

    }

    /**
     * Called by the receive thread to display
     * the current power spectrum.  Overload this
     * in a UI
     * @param ps double-valued power spectrum array
     */
    public void showSpectrum(double ps[]) {
        //override this in a GUI
    }

    /**
     * Called by the current mode to display its
     * interpretation of the signal
     * @param xs an array of [x,y] doubles, each
     *    should range from -1.0 to 1.0
     */
    public void showScope(double xs[][]) {
        //override this in a gui
    }

    /**
     * Called by the current mode to display text
     * Overload this in the UI
     * @param s the string to display
     */
    public void puttext(String s) {

    }

    /**
     * Override this to get text to send
     * @return
     */
    public String gettext() {
        return "";
    }

    public void setAudioInput(String name) {
        setTx(false);
        if (audioInput != null) {
            audioInput.stop();
        }
        audioInput = Audio.createInput(this, name);
        audioInput.start();
        start();
    }

    public void setAudioOutput(String name) {
        setTx(false);
        if (audioOutput != null) {
            audioOutput.stop();
        }
        audioOutput = Audio.createOutput(this, name);
        start();
    }

    public void start() {
        if (ioThread != null) {
            ioThread.abort();
        }
        ioThread = new IoThread();
        ioThread.start();
        trace(("Start"));
    }

    public void stop() {
        ioThread.abort();
        if (audioInput != null) {
            audioInput.stop();
        }
        if (audioOutput != null) {
            audioOutput.stop();
        }
    }

    private boolean doReceiveTask() {
        if (audioInput == null) {
            return false;
        }
        double recvbuf[] = audioInput.read();
        if (recvbuf == null)
            return true;
        int len = recvbuf.length;
        for (int i = 0; i < len; i++) {
            double v = recvbuf[i];
            if (decimator.decimate(v)) {
                double dv = decimator.getR();
                mode.receiveData(dv);
                fftin[fftptr++] = dv;
                fftptr &= FFT_MASK;
                if (++fftctr >= FFT_WINDOW) {
                    fftctr = 0;
                    fft.powerSpectrum(fftin, fftout);
                    showSpectrum(fftout);
                }
            }
        }
        return true;
    }

    private boolean doTransmitTask() {
        if (audioOutput == null) {
            return false;
        }
        double data[] = mode.getTransmitData();
        int len = data.length;
        for (int i=0 ; i<len ; i++) {
            double v = data[i];
            interpolator.interpolate(v, interpbuf);
            audioOutput.write(interpbuf);
        }
        return true;
    }

    class IoThread extends Thread {

        private boolean keepGoing;

        public IoThread() {
            super("IoThread");
            keepGoing = false;
        }

        public void run() {
            keepGoing = true;
            while (keepGoing) {
                if (rxtx) {
                    if (!doTransmitTask()) {
                        keepGoing = false;
                    }
                } else {
                    if (!doReceiveTask()) {
                        keepGoing = false;
                    }
                }
            }
        }

        public void abort() {
            keepGoing = false;
        }

    }//IoThread



}
