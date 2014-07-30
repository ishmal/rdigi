package org.bdigi.core.mode;

import org.bdigi.core.Complex;
import org.bdigi.core.Digi;
import org.bdigi.core.Property;
import org.bdigi.core.filter.FIR;
import org.bdigi.core.filter.Filter;
import org.bdigi.core.filter.Window;

/**
 *
 */
public class FskBase extends Mode {

    private boolean inv;


    public FskBase(Digi par, Property props, double sampleRateHint) {
        super(par, props, sampleRateHint);
        inv = false;
    }

    private Filter mf;
    private Filter sf;

    public void booleanControl(String name, boolean value) {
        if ("inv".equals(name)) {
            setInv(value);
        }
     }

    public void radioControl(String name, String value) {
        double d = Double.parseDouble(value);
        if ("rate".equals(name)) {
            setRate(d);
        } else if ("shift".equals(name)) {
            setShift(d);
        }
    }

    public void setRate(double v) {
        super.setRate(v);
        adjust();
    }

    private double shift;

    public double getShift() {
        return shift;
    }

    public void setShift(double v) {
        shift = v;
    }

    public double getBandwidth() {
        return shift;
    }

    public boolean getInv() {
        return inv;
    }
    public void setInv(boolean v) {
        inv = v;
    }

    private void adjust() {
        mf = FIR.bandpass(13, -0.75 * getRate(), -0.25 * getRate(), getSampleRate(), Window.rectangle);
        sf = FIR.bandpass(13, 0.25 * getRate(), 0.75 * getRate(), getSampleRate(), Window.rectangle);
    }

    public boolean isMiddleBit(boolean bit) {
        return false;
    }

    public void receive(Complex v) {

    }

    public void receiveBit(boolean v) {

    }
}
