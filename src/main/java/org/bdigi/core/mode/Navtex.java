package org.bdigi.core.mode;

import org.bdigi.core.Digi;
import org.bdigi.core.Property;

/**
 *
 */
public class Navtex extends FskBase {

    private boolean uos;
    private boolean inv;
    private int state;
    private int bitcount;
    private int code;
    private boolean parityBit;
    private int bitMask;
    private char table[][];

    public Navtex(Digi par) {
        super(par,
            new Property.Mode("navtex", "Navtex mode",
            new Property.Boolean("inv", "invert", false),
            new Property.Boolean("uos", "unshift on space", false)
            ),
          1000.0);
        setShift(170.0);
        setRate(100.0);
        uos = false;
        inv = false;
        state = RxSync1;
        bitcount  = 0;
        code      = 0;
        parityBit = false;
        bitMask   = 0;
        generateTable();
    }

    private void generateTable() {
        char t[][] = new char[128][2];
        t[0x3a] = new char[]{'Q',  '1'};  /*0111010*/
        t[0x72] = new char[]{'W',  '2'};  /*1110010*/
        t[0x35] = new char[]{'E',  '3'};  /*0110101*/
        t[0x55] = new char[]{'R',  '4'};  /*1010101*/
        t[0x17] = new char[]{'T',  '5'};  /*0010111*/
        t[0x6a] = new char[]{'Y',  '6'};  /*1101010*/
        t[0x39] = new char[]{'U',  '7'};  /*0111001*/
        t[0x59] = new char[]{'I',  '8'};  /*1011001*/
        t[0x47] = new char[]{'O',  '9'};  /*1000111*/
        t[0x5a] = new char[]{'P',  '0'};  /*1011010*/
        t[0x71] = new char[]{'A',  '-'};  /*1110001*/
        t[0x69] = new char[]{'S', '\''};  /*1101001*/
        t[0x65] = new char[]{'D',  '$'};  /*1100101*/
        t[0x6c] = new char[]{'F',  '!'};  /*1101100*/
        t[0x56] = new char[]{'G',  '&'};  /*1010110*/
        t[0x4b] = new char[]{'H',  '#'};  /*1001011*/
        t[0x74] = new char[]{'J',    7};  /*1110100*/
        t[0x3c] = new char[]{'K',  '['};  /*0111100*/
        t[0x53] = new char[]{'L',  ']'};  /*1010011*/
        t[0x63] = new char[]{'Z',  '+'};  /*1100011*/
        t[0x2e] = new char[]{'X',  '/'};  /*0101110*/
        t[0x5c] = new char[]{'C',  ':'};  /*1011100*/
        t[0x1e] = new char[]{'V',  '='};  /*0011110*/
        t[0x27] = new char[]{'B',  '?'};  /*0100111*/
        t[0x4d] = new char[]{'N',  ','};  /*1001101*/
        t[0x4e] = new char[]{'M',  '.'};  /*1001110*/
        t[0x1d] = new char[]{ ' ',  ' '};
        t[0x0f] = new char[]{'\n', '\n'}; //actually \r
        t[0x1b] = new char[]{'\n', '\n'};
        table = t;
    }

    private final static int NUL    = 0x2b;
    private final static int SPACE  = 0x1d;
    private final static int CR     = 0x0f;
    private final static int LF     = 0x1b;
    private final static int LTRS   = 0x2d;
    private final static int FIGS   = 0x36;
    private final static int ALPHA  = 0x78;
    private final static int BETA   = 0x66;
    private final static int SYNC   = 0x00;
    private final static int REPEAT = 0x33;


/*
var props = {
name : "navtex",
tooltip: "international naval teleprinter",
controls : [
{
name: "inv",
type: "boolean",
get value() { return self.getInverted(); },
set value(v) { self.setInverted(v); }
},
{
name: "UoS",
type: "boolean",
get value() { return self.getUnshiftOnSpace(); },
set value(v) { self.setUnshiftOnSpace(v); }
}
]
};
*/


    public boolean getUos() {
        return uos;
    }
    public void setUos(boolean v) {
        uos = v;
    }
    
    public boolean getInv() {
        return inv;
    }
    public void setInv(boolean v) {
        inv = v;
    }

    private final static int RxSync1 = 0;
    private final static int RxSync2 = 1;
    private final static int RxData  = 2;

    /**
     * Since there is no start or stop bit, we must sync ourselves.
     * But syncing is very simple.  We shift the bits through four 7-bit
     * shift registers.  When all four have valid characters, we consider
     * it to be synced.
     */
    private int errs  = 0;
    private int sync1 = 0;
    private int sync2 = 0;
    private int sync3 = 0;
    private int sync4 = 0;

    private void shift7(boolean bit) {
        int a = (bit) ? 1 : 0;
        int b = (sync1 >> 6) & 1;
        sync1 = ((sync1 << 1) + a) & 0x7f;
        a = b;
        b = (sync2 >> 6) & 1;
        sync2 = ((sync2 << 1) + a) & 0x7f;
        a = b;
        b = (sync3 >> 6) & 1;
        sync3 = ((sync3 << 1) + a) & 0x7f;
        a = b;
        sync4 = ((sync4 << 1) + a) & 0x7f;
    }



    public void receiveBit(boolean bit) {

        if (isMiddleBit(bit)) {
            return;
        }

        switch(state) {
            case RxSync1 :
                //trace("RxSync1")
                state    = RxSync2;
                bitcount = 0;
                code     = 0;
                errs     = 0;
                sync1    = 0;
                sync2    = 0;
                sync3    = 0;
                sync4    = 0;
                break;
            case RxSync2 :
                //trace("RxSync2")
                shift7(bit);
                //trace(sync1.toHexString + ", "+  sync2.toHexString + ", " +
                //     sync3.toHexString + ", " + sync4.toHexString);
                //trace("bit: " + bit);
                if (isValid(sync1) && isValid(sync2) &&
                    isValid(sync3) && isValid(sync4)) {
                    processCode(sync1);
                    processCode(sync2);
                    processCode(sync3);
                    processCode(sync4);
                    state = RxData;
                }
            break;
            case RxData :
                //trace("RxData");
                code = ((code<<1) + ((bit) ? 1 : 0)) & 0x7f;
                //trace("code: " + code);
                if (++bitcount >= 7) {
                    if (processCode(code) != ResultFail) { //we want Ok or Soft
                        //stay in RxData.  ready for next code
                        code     = 0;
                        bitcount = 0;
                    } else {
                        code     = 0;
                        bitcount = 0;
                        errs++;
                        if (errs > 3) {
                            state = RxSync1;
                           //trace("return to sync")
                        }
                    }
                }
            break;
            default:

        }//switch
    };

    private boolean shifted = false;

    private int reverse(int v, int len) {
        int a = v;
        int b = 0;
        for (int i=0 ; i < len ; i++) {
            b = (b<<1) + (a&1);
            a >>= 1;
        }
        return b;
    }

    private final static int ResultOk   = 0;
    private final static int ResultSoft = 1;
    private final static int ResultFail = 2;
    private final static int ResultEom  = 3;

    //Sitor-B is in either DX (data) or RX (repeat) mode
    private boolean dxMode = true;

    private int q3 = 0;
    private int q2 = 0;
    private int q1 = 0;

    private void qadd(int v) {
        q3 = q2;
        q2 = q1;
        q1 = v;
    }


    private boolean isValid(int code) {
        return (table[code] != null) ||
        code == NUL  || code == LTRS  ||
        code == FIGS || code == ALPHA ||
        code == BETA || code == SYNC  ||
        code == REPEAT;
    }

    private int processCode(int code) {
        //trace("code: " + code.toHexString + " mode: " + dxMode)
        int res = ResultOk;
        if (code == REPEAT) {
            qadd(code);
            shifted = false;
            dxMode = false;
        } else if (code == ALPHA) {
            shifted = false;
            dxMode = true;
        } else {
            if (dxMode) {
                if (!isValid(code))
                res = ResultSoft;
                qadd(code); //dont think.  just queue it
                dxMode = false; //for next time
            } else { //symbol
                if (isValid(code)) {
                    processCode2(code);
                } else {
                    if (isValid(q3)) {
                        int c = processCode2(q3);
                        par.status("FEC replaced :" + c);
                        res = ResultSoft;
                    } else {
                        processCode2(-1);
                        res = ResultFail;
                    }
                }
                dxMode = true; // next time
            }//rxmode
        }//symbol
        return res;
    }

    private char lastChar = '@';


    private char processCode2(int code) {
        char res = '@';
        if (code == 0) {
            //shouldnt happen
        } else if (code < 0) {
            //par.puttext("_");
           res = '_';
        } else if (code == ALPHA || code == REPEAT) {
            //shouldnt be here
        } else if (code == LTRS) {
            shifted = false;
        } else if (code == FIGS) {
            shifted = true;
        } else {
            char v[] = table[code];
            if (v != null) {
                char c = (shifted) ? v[1] : v[0];
                par.puttext(""+c);
                res = c;
            }
        }
        lastChar = res;
        return res;
    }

}
