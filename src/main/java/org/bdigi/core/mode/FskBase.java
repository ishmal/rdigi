package org.bdigi.core.mode;

import org.bdigi.core.Complex;
import org.bdigi.core.Digi;
import org.bdigi.core.filter.FIR;
import org.bdigi.core.filter.Filter;
import org.bdigi.core.filter.Window;

/**
 *
 */
public class FskBase extends Mode {

    public FskBase(Digi par, Props props, double sampleRateHint) {
        super(par, props, sampleRateHint);
    }

    private Filter mf;
    private Filter sf;

    public void setRate(double v) {
        super.setRate(v);
        adjust();
    }

    private void adjust() {
        mf = FIR.bandpass(13, -0.75*getRate(), -0.25*getRate(), getSampleRate(), Window.rectangle);
        sf = FIR.bandpass(13,  0.25*getRate(),  0.75*getRate(), getSampleRate(), Window.rectangle);
    }

    public void receive(Complex v) {

    }

    public void receiveBit(boolean v) {

    }
}
