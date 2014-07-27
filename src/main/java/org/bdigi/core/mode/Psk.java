package org.bdigi.core.mode;

import org.bdigi.core.Digi;

/**
 * Created by Bob on 7/26/2014.
 */
public class Psk extends Mode {

    public Psk(Digi par) {
        super(par, "", 1000.0);
    }


/*
var props = {
name : "psk",
tooltip: "phase shift keying",
controls : [
{
name: "rate",
type: "choice",
get value() { return self.getRate(); },
set value(v) { self.setRate(parseFloat(v)); },
values : [
{ name :  "31", value :  31.25 },
{ name :  "63", value :  62.50 },
{ name : "125", value : 125.00 }
]
},
{
name: "qpsk",
type: "boolean",
get value() { return self.getQpskMode(); },
set value(v) { self.setQpskMode(v); }
}
]
};
*/

    public double getBandwidth() {
        return getRate();
    }


    var ilp,qlp;
    var symbollen, halfSym;

    public void setRate(double rate) {
        super.setRate(rate);
        ilp = Biquad.lowPass(rate*0.5, self.getSampleRate());
        qlp = Biquad.lowPass(rate*0.5, self.getSampleRate());
        //bpf = FIR.bandpass(13, -0.7*this.getRate(), 0.7*this.getRate(), this.getSampleRate());
        symbollen = self.getSamplesPerSymbol()|0;
        halfSym = symbollen >> 1;
        };
    setRate(31.25);

    var lastSign = -1;
    var samples = 0;

    this.receive = function(z) {
    var i = ilp.update(z.r);
    var q = qlp.update(z.i);
    scopeOut(i, q);
    var sign = (i > 0) ? 1 : -1; //Math.sign() not on Chrome
    if (sign != lastSign) {
    samples=0;
    } else {
    samples++;
    }
    if ((samples%symbollen) === halfSym) {
    processSymbol(i, q);
    //processBit(sign>0);
    }
    lastSign = sign;
    };

    var SSIZE = 200;
    var scopedata = new Array(SSIZE);
    var sctr = 0;
    var log = Math.log;
    var ssctr = 0;
    function scopeOut(i,q) {
    if (! (++ssctr & 1)) return; //skip items
    scopedata[sctr++] = [log(i + 1) * 30.0, log(q + 1) * 30.0];
    if (sctr >= SSIZE) {
    par.showScope(scopedata);
    sctr = 0;
    scopedata = new Array(SSIZE);
    }
    }

    //var decoder = Viterbi.decoder(5, 0x17, 0x19)

    var qpskMode = false;
    this.getQpskMode  = function() {
    return qpskMode;
    };
    this.setQpskMode = function(v) {
    qpskMode = v;
    };


    function angleDiff(a, b) {
    var diff = a-b;
    while (diff > Math.PI)
    diff -= twopi;
    while (diff < -Math.PI)
    diff += twopi;
    //println("%f %f %f".format(a, b, diff))
    return diff;
    }

    var diffScale = 255.0 / Math.PI;

    /**
     * Return the scaled distance of the angle v from "from".
     * Returns a positive value 0..255  for
     * 0 radians to +- pi
     */
    function distance(v, from) {
    var diff = Math.PI - Math.abs(Math.abs(v-from) - Math.PI);
    return Math.floor(diff * diffScale);
    }

    var twopi  = Math.PI * 2.0;
    var halfpi = Math.PI * 0.5;

    var code      = 0;
    var lastv     = 0.0;
    var count     = 0;
    var lastBit   = false;


    function processSymbol(i,q) {

    var dv, d00, d01, d10, d11;

    var vn = Math.atan2(q, i);

    if (qpskMode) {
                /**/
    dv  = angleDiff(vn,  lastv);
    d00 = distance(dv, Math.PI);
    d01 = distance(dv,  halfpi);
    d10 = distance(dv, -halfpi);
    d11 = distance(dv,     0.0);
    var bm = [d00, d01, d10, d11];
    //println("%6.3f %6.3f %6.3f  :  %3d %3d %3d %3d".format(lastv, vn, dv, d00, d01, d10, d11))
    var bits = decoder.decodeOne(bm);
    var len = bits.length;
    for (var idx=0 ; idx < len ; idx++)
    processBit(bits[idx]);
    lastv = vn;
                /**/
    } else { //bpsk
                /**/
    dv  = angleDiff(vn,  lastv);
    d00 = distance(dv, Math.PI);
    d11 = distance(dv,     0.0);
    //println("%6.3f %6.3f %6.3f  :  %3d %3d".format(lastv, vn, dv, d00, d11))
    var bit = d11 < d00;
    lastv = vn;
                /**/
    processBit(bit);
    }
    }


    function processBit(bit) {
    //println("bit: " + bit)
    if ((!bit) && (!lastBit)) {
    code >>= 1;   //remove trailing 0
    if (code !== 0) {
    //println("code:" + Varicode.toString(code))
    var ascii = Varicode.decodeTable[code];
    if (ascii) {
    var chr = ascii;
    if (chr == 10 || chr == 13)
    par.puttext("\n");
    else
    par.puttext(String.fromCharCode(chr));
    code = 0;
    }
    }
    code = 0;
    }
    else
    {
    code <<= 1;
    if (bit) code += 1;
    }
    lastBit = bit ;
    }

    //###################
    //# transmit
    //###################


    function getNextTransmitBuffer() {
    var ch = par.gettext();
    if (tx<0) {

    } else {


    }

    }

    var txBuf = [];
    var txPtr = 0;

    this.transmit = function() {

    if (txPtr >= txBuf.length) {
    txBuf = getNextTransmitBuffer();
    txPtr = 0;
    }
    var txv = txBuf[txPtr++];
    return txv;
    };

}// PskMode2

