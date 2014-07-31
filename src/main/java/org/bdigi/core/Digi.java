
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
        rxtx = false;
        fftin = new double[Constants.FFT_SIZE];
        fftout = new double[Constants.BINS];
        fftptr = 0;
        fftctr = 0;
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
        rxtx = v;
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
     * Called by the receive thread to display
     * the current power spectrum.  Overload this
     * in a GUI
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
    public void puttext(String s) {

    }

    public void setAudioInput(String name) {
        audioInput = Audio.createInput(this, name);
    }

    public void setAudioOutput(String name) {
        audioOutput = Audio.createOutput(this, name);
    }

    public void start() {
        if (ioThread != null) {
            ioThread.abort();
        }
        ioThread = new IoThread();
        ioThread.start();
    }

    public void stop() {
        ioThread.abort();
    }

    private boolean doReceiveTask() {
        if (audioInput == null) {
            return false;
        }
        double recvbuf[] = audioInput.read();
        int len = recvbuf.length;
        for (int i = 0; i < len; i++) {
            double v = recvbuf[i];
            if (decimator.decimate(v)) {
                mode.receiveData(decimator.getValue());
            }
            fftin[fftptr++] = v;
            fftptr = FFT_MASK;
            if (++fftctr >= FFT_WINDOW) {
                fftctr = 0;
                fft.powerSpectrum(fftin, fftout);
                showSpectrum(fftout);
            }
        }
        return true;
    }

    private boolean doTransmitTask() {
        if (audioOutput == null) {
            return false;
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
