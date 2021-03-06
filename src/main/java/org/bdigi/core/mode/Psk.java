package org.bdigi.core.mode;

import org.bdigi.core.Complex;
import org.bdigi.core.Convolutional;
import org.bdigi.core.Digi;
import org.bdigi.core.Property;
import org.bdigi.core.filter.Biquad;
import org.bdigi.core.filter.Filter;

/**
 * Created by Bob on 7/26/2014.
 */
public class Psk extends Mode {

    Filter ilp,qlp;
    int symbollen, halfsym;
    boolean encodeTable[][];
    int decodeTable[];
    Convolutional.Decoder decoder;

    public Psk(Digi par) {
        super(par,
            new Property("psk", "phase shift keying").
                radio("rate", "bits/second", "31.25", "31.25", "62.5", "123").
                bool("qpsk", "qpsk mode", false),
            1000);
        setRate(31.25);
        generateTables();
    }

    /**
     * This contains the definitions of the bit patterns for the Varicode set
     * of characters.
     *
     * A "from" and a "to" table are also provided.
     */
    private final static String varicodeDescription[] = {
        "1010101011",  //  0  00  NUL Null character
        "1011011011",  //  1  01  SOH Start of Header
        "1011101101",  //  2  02  STX Start of Text
        "1101110111",  //  3  03  ETX End of Text
        "1011101011",  //  4  04  EOT End of Transmission
        "1101011111",  //  5  05  ENQ Enquiry
        "1011101111",  //  6  06  ACK Acknowledgment
        "1011111101",  //  7  07  BEL Bell
        "1011111111",  //  8  08  BS  Backspace
        "11101111",    //  9  09  HT  Horizontal Tab
        "11101",       // 10  0A  LF  Line feed
        "1101101111",  // 11  0B  VT  Vertical Tab
        "1011011101",  // 12  0C  FF  Form feed
        "11111",       // 13  0D  CR  Carriage return
        "1101110101",  // 14  0E  SO  Shift Out
        "1110101011",  // 15  0F  SI  Shift In
        "1011110111",  // 16  10  DLE Data Link Escape
        "1011110101",  // 17  11  DC1 Device Control 1 (XON)
        "1110101101",  // 18  12  DC2 Device Control 2
        "1110101111",  // 19  13  DC3 Device Control 3 (XOFF)
        "1101011011",  // 20  14  DC4 Device Control 4
        "1101101011",  // 21  15  NAK Negative Acknowledgement
        "1101101101",  // 22  16  SYN Synchronous Idle
        "1101010111",  // 23  17  ETB End of Trans. Block
        "1101111011",  // 24  18  CAN Cancel
        "1101111101",  // 25  19  EM  End of Medium
        "1110110111",  // 26  1A  SUB Substitute
        "1101010101",  // 27  1B  ESC Escape
        "1101011101",  // 28  1C  FS  File Separator
        "1110111011",  // 29  1D  GS  Group Separator
        "1011111011",  // 30  1E  RS  Record Separator
        "1101111111",  // 31  1F  US  Unit Separator
        "1",           // 32  20  SP
        "111111111",   // 33  21  !
        "101011111",   // 34  22  "
        "111110101",   // 35  23  #
        "111011011",   // 36  24  $
        "1011010101",  // 37  25  %
        "1010111011",  // 38  26  &
        "101111111",   // 39  27  '
        "11111011",    // 40  28  (
        "11110111",    // 41  29  )
        "101101111",   // 42  2A  *
        "111011111",   // 43  2B  +
        "1110101",     // 44  2C  ",
        "110101",      // 45  2D  -
        "1010111",     // 46  2E  .
        "110101111",   // 47  2F  /
        "10110111",    // 48  30  0
        "10111101",    // 49  31  1",  //
        "11101101",    // 50  32  2
        "11111111",    // 51  33  3
        "101110111",   // 52  34  4
        "101011011",   // 53  35  5
        "101101011",   // 54  36  6
        "110101101",   // 55  37  7
        "110101011",   // 56  38  8
        "110110111",   // 57  39  9
        "11110101",    // 58  3A  :
        "110111101",   // 59  3B  ;
        "111101101",   // 60  3C  <
        "1010101",     // 61  3D  =
        "111010111",   // 62  3E  >
        "1010101111",  // 63  3F  ?
        "1010111101",  // 64  40  @
        "1111101",     // 65  41  A
        "11101011",    // 66  42  B
        "10101101",    // 67  43  C
        "10110101",    // 68  44  D
        "1110111",     // 69  45  E
        "11011011",    // 70  46  F
        "11111101",    // 71  47  G
        "101010101",   // 72  48  H
        "1111111",     // 73  49  I
        "111111101",   // 74  4A  J
        "101111101",   // 75  4B  K
        "11010111",    // 76  4C  L
        "10111011",    // 77  4D  M
        "11011101",    // 78  4E  N
        "10101011",    // 79  4F  O
        "11010101",    // 80  50  P
        "111011101",   // 81  51  Q
        "10101111",    // 82  52  R
        "1101111",     // 83  53  S
        "1101101",     // 84  54  T
        "101010111",   // 85  55  U
        "110110101",   // 86  56  V
        "101011101",   // 87  57  W
        "101110101",   // 88  58  X
        "101111011",   // 89  59  Y
        "1010101101",  // 90  5A  Z
        "111110111",   // 91  5B  [
        "111101111",   // 92  5C  \
        "111111011",   // 93  5D  ]
        "1010111111",  // 94  5E  ^
        "101101101",   // 95  5F  _
        "1011011111",  // 96  60  `
        "1011",        // 97  61  a
        "1011111",     // 98  62  b
        "101111",      // 99  63  c
        "101101",      //100  64  d
        "11",          //101  65  e
        "111101",      //102  66  f
        "1011011",     //103  67  g
        "101011",      //104  68  h
        "1101",        //105  69  i
        "111101011",   //106  6A  j
        "10111111",    //107  6B  k
        "11011",       //108  6C  l
        "111011",      //109  6D  m
        "1111",        //110  6E  n
        "111",         //111  6F  o
        "111111",      //112  70  p
        "110111111",   //113  71  q
        "10101",       //114  72  r
        "10111",       //115  73  s
        "101",         //116  74  t
        "110111",      //117  75  u
        "1111011",     //118  76  v
        "1101011",     //119  77  w
        "11011111",    //120  78  x
        "1011101",     //121  79  y
        "111010101",   //122  7A  z
        "1010110111",  //123  7B  {
        "110111011",   //124  7C  |
        "1010110101",  //125  7D  }
        "1011010111",  //126  7E  ~
        "1110110101"   //127  7F  DEL  Delete
    };

    private void generateTables() {
    
        int len = varicodeDescription.length;
        encodeTable = new boolean[len][];
        decodeTable = new int[1024];
        for (int i=0 ; i<len ; i++) {
            String s = varicodeDescription[i];
            int slen = s.length();
            boolean barr[] = new boolean[slen];
            for (int j=0 ; j<slen ; j++) {
                barr[j] = (s.charAt(j) == '1');
            }
            encodeTable[i] = barr;
            int ival = Integer.parseInt(s, 2);
            decodeTable[ival] = i;
        }
    
    }

    @Override
    public void booleanControl(String name, boolean value) {
        if ("qpsk".equals(name)) {
            setQpskMode(value);
        }
    }

    @Override
    public void radioControl(String name, String value) {
        double d = Double.parseDouble(value);
        if ("rate".equals(name)) {
            setRate(d);
        }
    }

    @Override
    public double getBandwidth() {
        return getRate();
    }


    @Override
    public void setRate(double rate) {
        super.setRate(rate);
        ilp = Biquad.lowpass(rate * 0.5, getSampleRate());
        qlp = Biquad.lowpass(rate * 0.5, getSampleRate());
        //ilp = FIR.lowpass(13, rate * 0.5, getSampleRate(), Window.hann);
        //qlp = FIR.lowpass(13, rate * 0.5, getSampleRate(), Window.hann);
        //bpf = FIR.bandpass(13, -0.7*this.getRate(), 0.7*this.getRate(), this.getSampleRate());
        symbollen = (int)Math.round(getSamplesPerSymbol());
        halfsym = symbollen >> 1;
    }


    int lastSign = -1;
    int samples = 0;

    @Override
    public void receive(Complex z) {
        double i = ilp.update(z.getR());
        double q = qlp.update(z.getI());
        scopeOut(i, q);
        int sign = (i > 0.0) ? 1 : -1;
        if (sign != lastSign) {
            samples=0;
        } else {
            samples++;
        }
        if ((samples%symbollen) == halfsym) {
            processSymbol(i, q);
            //processBit(sign>0);
        }
        lastSign = sign;
    }

    int SSIZE = 200;
    double scopedata[][] = new double[SSIZE][2];
    int sctr = 0;
    int ssctr = 0;
    private void scopeOut(double i, double q) {
        if ((++ssctr % 0xf )==0) return; //skip items (odd?)
        scopedata[sctr++] = new double[]{Math.log(i + 1) * 2.0, Math.log(q + 1) * 2.0};
        if (sctr >= SSIZE) {
            par.showScope(scopedata);
            sctr = 0;
            scopedata = new double[SSIZE][2];
        }
    }

    //int decoder = Viterbi.decoder(5, 0x17, 0x19)

    private boolean qpskMode = false;
    public boolean getQpskMode () {
        return qpskMode;
    };
    public void setQpskMode(boolean v) {
        qpskMode = v;
    };


    private double angleDiff(double a, double b) {
        double diff = a-b;
        while (diff > Math.PI)
            diff -= twopi;
        while (diff < -Math.PI)
            diff += twopi;
        //println("%f %f %f".format(a, b, diff))
        return diff;
    }

    private static final double diffScale = 255.0 / Math.PI;

    /**
     * Return the scaled distance of the angle v from "from".
     * Returns a positive value 0..255  for
     * 0 radians to +- pi
     */
    private double distance(double v, double from) {
        double diff = Math.PI - Math.abs(Math.abs(v-from) - Math.PI);
        return Math.floor(diff * diffScale);
    }

    private final static double twopi  = Math.PI * 2.0;
    private final static double halfpi = Math.PI * 0.5;

    private int code = 0;
    private double lastv = 0.0;
    private int count = 0;
    private boolean lastBit = false;


    private void processSymbol(double i, double q) {

        double dv, d00, d01, d10, d11;
        double vn = Math.atan2(q, i);

        if (qpskMode) { /**/
            dv  = angleDiff(vn,  lastv);
            d00 = distance(dv, Math.PI);
            d01 = distance(dv,  halfpi);
            d10 = distance(dv, -halfpi);
            d11 = distance(dv,     0.0);
            /*int bm[] = new int[]{d00, d01, d10, d11};
            //println("%6.3f %6.3f %6.3f  :  %3d %3d %3d %3d".format(lastv, vn, dv, d00, d01, d10, d11))
            boolean bits[] = decoder.decodeOne(bm);
            int len = bits.length;
            for (int idx=0 ; idx < len ; idx++)
                processBit(bits[idx]);*/
            lastv = vn; /**/
        } else { //bpsk
            dv  = angleDiff(vn,  lastv);
            d00 = distance(dv, Math.PI);
            d11 = distance(dv,     0.0);
            //println("%6.3f %6.3f %6.3f  :  %3d %3d".format(lastv, vn, dv, d00, d11))
            boolean bit = d11 < d00;
            lastv = vn;
            processBit(bit);
        }
    }


    private void processBit(boolean bit) {
        //println("bit: " + bit)
        if ((!bit) && (!lastBit)) {
            code >>= 1;   //remove trailing 0
            if (code != 0) {
                //println("code:" + Varicode.toString(code))
                int chr = decodeTable[code & 0x3ff];
                if (chr != 0) {
                    if (chr == 10 || chr == 13)
                        par.puttext("\n");
                    else
                        par.puttext("" + (char)chr);
                    code = 0;
                }
            }
            code = 0;
        } else {
            code <<= 1;
            if (bit) code += 1;
        }
        lastBit = bit ;
    }

    //###################
    //# transmit
    //###################

    /*
    private int[] getNextTransmitBuffer() {
        int ch = par.gettext();
        if (tx<0) {

        } else {

    }

    int txBuf[];
    int txPtr = 0;

    public double tranmit() {

    if (txPtr >= txBuf.length) {
        txBuf = getNextTransmitBuffer();
        txPtr = 0;
    }
    int txv = txBuf[txPtr++];
        return txv;
    };

    */

}// PskMode2

