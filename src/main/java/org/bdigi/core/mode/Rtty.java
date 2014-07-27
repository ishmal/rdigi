package org.bdigi.core.mode;

import org.bdigi.core.Digi;

/**
 * Created by Bob on 7/24/2014.
 */
public class Rtty extends FskBase{

    private final static String props =
           "{" +
           "'name' : 'rtty'," +
           "'tooltip' : 'radio teletype'" +
           "'controls' : [" +
           "   {" +
           "   'type' : 'radio'," +
           "   'name' : 'rate'," +
           "   'value' : 45.45," +
           "   'values' : [45.45,50,75,100]" +
           "   }," +
           "   {" +
           "   'type' : 'radio'," +
           "   'name' : 'shift'," +
           "   'value' : 170," +
           "   'values' : [85,170,200,450,850]" +
           "   }," +
           "   {" +
           "   'type' : 'boolean'," +
           "   'name' : 'inv'," +
           "   'value' : false" +
           "   }," +
           "   {" +
           "   'type' : 'boolean'," +
           "   'name' : 'uos'," +
           "   'value' : false" +
           "   }" +
           "]" +
           "}";

    private boolean inv = false;
    private boolean uos = false;
    private int parityType;
    private int state;
    private int bitcount;
    private int code;
    private boolean parityBit;
    private int counter;
    private int symbollen;
    private int halfsym;
    private int symptr;
    private boolean symarray[];

    private final static int NRBITS    = 5;//todo: make this selectable
    private final static int msbit     = 1<<(NRBITS-1);

    public Rtty(Digi par) {
        super(par, props, 1000);
        parityType = ParityNone;
        state     = RxIdle;
        bitcount  = 0;
        code      = 0;
        parityBit = false;
        counter   = 0;
        symbollen = 0;
        halfsym   = 0;
        symptr    = 0;
        symarray  = new boolean[0];
    }

    private final static char[][] table = {
        {0, 0}, // 0x00 NUL
        {'E','3'}, // 0x01
        {'\n','\n'}, // 0x02 LF
        {'A','-'}, // 0x03
        {' ',' '}, // 0x04 SPACE
        {'S','\''}, // 0x05
        {'I','8'}, // 0x06
        {'U','7'}, // 0x07
        {'\n','\n'}, // 0x08 CR
        {'D','$'}, // 0x09
        {'R','4'}, // 0x0a
        {'J',7}, // 0x0b 7=bell
        {'N',','}, // 0x0c
        {'F','!'}, // 0x0d
        {'C',':'}, // 0x0e
        {'K','{'}, // 0x0f
        {'T','5'}, // 0x10
        {'Z','+'}, // 0x11
        {'L','}'}, // 0x12
        {'W','2'}, // 0x13
        {'H','#'}, // 0x14
        {'Y','6'}, // 0x15
        {'P','0'}, // 0x16
        {'Q','1'}, // 0x17
        {'O','9'}, // 0x18
        {'B','?'}, // 0x19
        {'G','&'}, // 0x1a
        {0,0}, // 0x1b FIGS
        {'M','.'}, // 0x1c
        {'X','/'}, // 0x1d
        {'V','='}, // 0x1e
        {0,0}  // 0x1f LTRS
    };

    private final static int NUL   = 0x00;
    private final static int SPACE = 0x04;
    private final static int CR    = 0x08;
    private final static int LF    = 0x02;
    private final static int LTRS  = 0x1f;
    private final static int FIGS  = 0x1b;

    /**
     * Enumerations for parity types
     */
    private final static int ParityNone = 0;
    private final static int ParityOne  = 1;
    private final static int ParityZero = 2;
    private final static int ParityOdd  = 3;
    private final static int ParityEven = 4;

    public void setRate(double rate) {
        super.setRate(rate);
        symbollen = (int)Math.round(getSamplesPerSymbol());
        symarray = new boolean[symbollen];
        halfsym = (symbollen>>1);
        symptr = 0;
    }

    public boolean getInv() {
        return inv;
    }
    public void setInv(boolean v) {
        inv = v;
    }

    public boolean getUos() {
        return uos;
    }
    public void setUos(boolean v) {
        uos = v;
    }


    private int countbits(int n) {
        int c = 0;
        while (n>0) {
            n &= n-1;
            c++;
        }
        return c;
    }

    private boolean parityOf(int c) {
        switch (parityType) {
            case ParityOdd  : return (countbits(c) & 1) != 0;
            case ParityEven : return (countbits(c) & 1) == 0;
            case ParityZero : return false;
            case ParityOne  : return true;
            default         : return false;   //None or unknown
        }
    }


    private final static int RxIdle   = 0;
    private final static int RxStart  = 1;
    private final static int RxData   = 2;
    private final static int RxStop   = 3;
    private final static int RxParity = 4;


    /**
     * We wish to sample data at the end of a symbol period, with
     * Use a cirsular delay line to check if we have a mark-to-space transition,
     * then get a correction so that we align on symbol centers.  Once we do that,
     * we are hopefully aligned on a trailing edge, then we can sense a mark or
     * space by which has the most bits.
     *
     * |<-----symbollen ---->| Now
     *
     * ----3----|
     *          |
     *          |-----3------
     *
     *          |<---corr-->|
     *
     *
     *
     * While reading bits, are most of the bits set? Then it's
     * a mark, else a space.
     *
     * |<-----symbollen ---->| Now
     *  |------------------|
     *  |                  |
     * -|                  |-
     *
     */
    public void receiveBit(boolean bit) {

        symarray[symptr++] = bit;
        symptr %= symbollen;
        boolean last = symarray[symptr];
        boolean isMarkToSpace = false;
        int corr = 0;
        int ptr = symptr;
        int sum = 0;
        for (int pp=0 ; pp<symbollen ; pp++) {
            sum += (symarray[ptr++]) ? 1 : 0;
            ptr %= symbollen;
        }
        boolean isMark = (sum > halfsym);
        if (last && !bit) {
            if (Math.abs(halfsym-sum)<6) {
                isMarkToSpace = true;
                corr = sum;
            }
        }

        switch (state) {

            case RxIdle :
                //console.log("RxIdle");
                if (isMarkToSpace) {
                    state     = RxStart;
                    counter   = corr; //lets us re-center
                }
                break;
            case RxStart :
                //console.log("RxStart");
                if (--counter <= 0) {
                    if (!isMark) {
                        state     = RxData;
                        code      = 0;
                        parityBit = false;
                        bitcount  = 0;
                        counter   = symbollen;
                    } else {
                        state = RxIdle;
                    }
                }
                break;
            case RxData :
                //console.log("RxData");
                if (--counter <= 0) {
                    counter = symbollen;
                    //code = (code<<1) + isMark; //msb
                    code = (code>>>1) + ((isMark) ? msbit : 0); //lsb?
                    if (++bitcount >= NRBITS) {
                        state = (parityType == ParityNone) ? RxStop : RxParity;
                    }
                }
                break;
            case RxParity :
                //console.log("RxParity");
                if (--counter <= 0) {
                    state     = RxStop;
                    parityBit = isMark;
                }
                break;
            case RxStop :
                //console.log("RxStop");
                if (--counter <= 0) {
                    if (isMark) {
                        outCode(code);
                    }
                    state = RxIdle;
                }
                break;
        }
    }; // processBit



    private boolean shifted = false;

    private int reverse(int v, int size) {
        int a = v;
        int b = 0;
        while (size-- >0) {
            b += a & 1;
            b <<= 1;
            a >>= 1;
        }
        return b;
    }


    private void outCode(int rawcode) {
        //println("raw:" + rawcode)
        //rawcode = reverse(rawcode, 5);
        int code = rawcode & 0x1f;
        if (code == NUL) {
        } else if (code == FIGS) {
            shifted = true;
        } else if (code == LTRS) {
            shifted = false;
        } else if (code == SPACE) {
            par.puttext(" ");
            if (uos)
                shifted = false;
        } else if (code == CR || code == LF) {
            par.puttext("\n");
            if (uos)
                shifted = false;
        } else {
            char v[] = table[code];
            if (v != null) {
                char c = (shifted) ? v[1] : v[0];
                if (c != 0)
                    par.puttext(""+c);
            }
        }
    }


}
