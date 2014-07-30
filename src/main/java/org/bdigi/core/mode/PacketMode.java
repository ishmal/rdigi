/**
 * bdigi DSP tool
 *
 * Authors:
 *   Bob Jamison
 *
 * Copyright (c) 2014 Bob Jamison
 * 
 *  This file is part of the bdigi library.
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 3 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.bdigi.core.mode;

import org.bdigi.core.Crc;
import org.bdigi.core.Digi;
import org.bdigi.core.Property;
import org.bdigi.core.mode.Mode;

import java.util.Arrays;

/**
 * Mode for AX-25 packet communications.
 *
 * Note:  apparently 4800s/s seems to be necessary for this to work on 1200baud
 *  
 * @link http://www.tapr.org/pub_ax25.html
 */    
public class PacketMode extends FskBase {


    public static class Addr {

        private String call;
        private int ssid;
        private int add[]; //cache

        public Addr(String call, int ssid) {
            this.call = call;
            this.ssid = ssid;
        }

        public int[] encoded() {
            if (add == null) {
                add = new int[7];
                for (int i = 0; i < 7; i++) {
                    if (i < call.length())
                        add[i] = (((int) call.charAt(i)) << 1);
                    else if (i == 6)
                        add[i] = (0x60 | (ssid << 1));
                    else
                        add[i] = 0x40;   // shifted space
                }
            }
            return add;
        }

        public String toString() {
            return (ssid >= 0) ? call + "-" + ssid : call;
        }

    }//Addr

    public static class Packet {

        private Addr dest;
        private Addr src;
        private Addr rpts[];
        private int ctrl;
        private int pid;
        private int info[];


        public Packet(Addr dest, Addr src, Addr rpts[], int ctrl, int pid, int[] info) {
            this.dest = dest;
            this.src = src;
            this.rpts = rpts;
            this.ctrl = ctrl;
            this.pid = pid;
            this.info = info;
        }


        public int[] toOctets() {
            int buf[] = new int[1024];
            int ptr = 0;
            buf[ptr++] = 0x7e; // flag
            int addbuf[] = dest.encoded();
            for (int i = 0; i < addbuf.length; i++)
                buf[ptr++] = addbuf[i];
            addbuf = src.encoded();
            for (int i = 0; i < addbuf.length; i++)
                buf[ptr++] = addbuf[i];
            for (int ridx = 0; ridx < this.rpts.length; ridx++) {
                addbuf = rpts[ridx].encoded();
                for (int i = 0; i < addbuf.length; i++)
                    buf[ptr++] = addbuf[i];
            }
            buf[ptr++] = this.ctrl;
            buf[ptr++] = this.pid;
            int ilen = info.length;
            for (int iidx = 0; iidx < ilen; iidx++) {
                buf[buf.length] = info[iidx];
            }
            Crc crc = new Crc();
            for (int bidx = 0; bidx < buf.length; bidx++) {
                crc.update(buf[bidx]);
            }
            int crcv = crc.getValue();
            int fcslo = (crcv & 0xff) ^ 0xff;
            int fcshi = (crcv >> 8) ^ 0xff;
            buf[ptr++] = fcslo;
            buf[ptr++] = fcshi;
            buf[ptr] = 0x7e; // flag
            return Arrays.copyOf(buf, ptr);
        }

        public String toString() {
            StringBuilder buf = new StringBuilder();
            buf.append(src.toString() + "=>" + dest.toString());

            for (int ridx = 0; ridx < rpts.length; ridx++) {
                buf.append(":").append(rpts[ridx].toString());
            }
            buf.append(" [").append(pid).append("]: ");
            if (pid != 0) {
                for (int i = 0; i < info.length; i++)
                    buf.append((char) info[i]);
            } else {
                buf.append("{").append(info[0]).append(",").append(info.length).append("}");
                for (int i = 0; i < info.length; i++)
                    buf.append((char) info[i]);
            }
            return buf.toString();
        }
        private static Addr getAddr(int arr[], int offset) {
            StringBuilder b = new StringBuilder();
            for (int i = 0; i < 6; i++) {
                b.append((char) (arr[offset + i] >> 1));
            }
            String call = b.toString();
            int ssid = (arr[offset + 6] >> 1) & 0xf;
            return new Addr(call, ssid);
        }

        public static Packet create(int data[]) {


            int pos = 0;
            Addr dest = getAddr(data, pos);
            pos += 7;
            Addr src = getAddr(data, pos);
            pos += 7;
            Addr buf[] = new Addr[10];
            int i;
            for (i = 0; i < 8; i++) {
                if (pos >= data.length - 7 || ((data[pos - 1] & 128) == 0))
                    break;
                buf[i] = getAddr(data, pos);
                pos += 7;
            }
            Addr rpts[] = Arrays.copyOf(buf, i);
            int ctrl = data[pos++];

            int typ = ((ctrl & 1) == 0) ? IFRAME : ((ctrl & 2) == 0) ? SFRAME : UFRAME;

            int pid = (typ == IFRAME) ? data[pos] : 0;
            if (typ == IFRAME) pos++;

            int info[] = Arrays.copyOfRange(data, pos, data.length);

            return new Packet(dest, src, rpts, 0, 0, info);
        }

    }//Packet

    public final static int PID_X25 = 0x01;  // ISO 8208/CCITT X.25 PLP
    public final static int PID_TCPIP_COMP = 0x06;  // Compressed TCP/IP packet. Van Jacobson (RFC 1144)
    public final static int PID_TCPIP_UNCOMP = 0x07;  // Uncompressed TCP/IP packet. Van Jacobson (RFC 1144)
    public final static int PID_FRAG = 0x08;  // Segmentation fragment
    public final static int PID_AX25_FLAG1 = 0x10;  // AX.25 layer 3 implemented.
    public final static int PID_AX25_FLAG2 = 0x20;  // AX.25 layer 3 implemented.
    public final static int PID_AX25_MASK = 0x30;  // AX.25 layer 3 implemented.
    public final static int PID_TEXNET = 0xc3;  // TEXNET datagram protocol
    public final static int PID_LQP = 0xc4;  // Link Quality Protocol
    public final static int PID_APPLETALK = 0xca;  // Appletalk
    public final static int PID_APPLETALK_ARP = 0xcb;  // Appletalk ARP
    public final static int PID_ARPA_IP = 0xcc;  // ARPA Internet Protocol
    public final static int PID_ARPA_ARP = 0xcd;  // ARPA Address Resolution
    public final static int PID_FLEXNET = 0xce;  // FlexNet
    public final static int PID_NETROM = 0xcf;  // NET/ROM
    public final static int PID_NO_3 = 0xf0;  // No layer 3 protocol implemented.
    public final static int PID_ESCAPE = 0xff;  // Escape character. Next octet contains more Level 3 protocol information.

    /**
     * Frame identifiers
     */
    public final static int FID_NONE = 0;  // Not an ID
    public final static int FID_C = 1;  // Layer 2 Connect Request
    public final static int FID_SABM = 2;  // Layer 2 Connect Request
    public final static int FID_D = 3;  // Layer 2 Disconnect Request
    public final static int FID_DISC = 4;  // Layer 2 Disconnect Request
    public final static int FID_I = 5;  // Information frame
    public final static int FID_RR = 6;  // Receive Ready. System Ready To Receive
    public final static int FID_RNR = 7;  // Receive Not Ready. TNC Buffer Full
    public final static int FID_NR = 8;  // Receive Not Ready. TNC Buffer Full
    public final static int FID_RJ = 9;  // Reject Frame. Out of Sequence or Duplicate
    public final static int FID_REJ = 10;  // Reject Frame. Out of Sequence or Duplicate
    public final static int FID_FRMR = 11;  // Frame Reject. Fatal Error
    public final static int FID_UI = 12;  // Unnumbered Information Frame. "Unproto"
    public final static int FID_DM = 13;  // Disconnect Mode. System Busy or Disconnected.

    public final static int IFRAME = 0;
    public final static int SFRAME = 1;
    public final static int UFRAME = 2;



    private int state;
    private int bitcount;
    private int octet;
    private int ones;
    private int bufPtr;
    private final static int RXLEN = 4096;
    private int rxbuf[];
    private boolean lastBit;
    private Crc crc;


    public PacketMode(Digi par) {
        super(par,
            new Property.Mode("packet", "Packet mode",
                new Property.Boolean("inv", "invert", false),
                new Property.Boolean("uos", "unshift on space", false)
            ), 4800);
        setShift(200.0);
        setRate(300.0);
        state = RxStart;
        bitcount = 0;
        octet    = 0;
        ones     = 0;
        bufPtr   = 0;
        rxbuf = new int[RXLEN];
        lastBit = false;
        crc = new Crc();
    }
    
    /**
    var props = {
        name : "packet",
        tooltip: "AX.25 and APRS",
        controls : [
            {
            name: "rate",
            type: "choice",
            tooltip: "packet data rate",
            get value() { return self.getRate(); },
            set value(v) { self.setRate(parseFloat(v)); },
            values : [
                { name :  "300", value :  300.0 },
                { name : "1200", value : 1200.0 }
                ]
            },
            {
            name: "shift",
            type: "choice",
            tooltip: "frequency distance between mark and space",
            get value() { return self.getShift(); },
            set value(v) { self.setShift(parseFloat(v)); },
            values : [
                { name :  "200", value :  200.0 },
                { name : "1000", value : 1000.0 }
                ]
            }
        ]
    };
    */
    
    
     
    
    
    public void trace(String msg) {
        par.trace("packet:" + msg);
    }
    

    private final static int RxStart = 0;  //the initial state
    private final static int RxTxd   = 1;  //after the first flag, wait until no more flags
    private final static int RxData  = 2;  //after the flag.  all octets until another flag
    private final static int RxFlag1 = 3;  //Test whether we have a flag or a stuffed bit
    private final static int RxFlag2 = 4;  //It was a flag.  grab the last bit
    
    private final static int FLAG = 0x7e;   // 01111110 , the start/stop flag
    

    /**
     * Attempt to decode a packet.  It will be in NRZI form, so when
     * we sample at mid-pulse (period == halflen) we need to sense then
     * if the bit has flipped or not.  Do -not- check this for every sample.
     * the packet will be in the form:
     * 01111110 76543210 76543210 76543210 01234567 01234567 01111110
     *   flag    octet     octet   octet    fcs_hi   fcs_lo    flag
     */
    public void receiveBit(boolean inBit) {
    
        if (!isMiddleBit(inBit)) {
            return;
        }

        //shift right for the next bit, since ax.25 is lsb-first
        octet = (octet >> 1) & 0x7f;  //0xff? we dont want the msb
        boolean bit = (inBit == lastBit); //google "nrzi"
        lastBit = inBit;
        if (bit) 
            { ones += 1 ; octet |= 128; }
        else
            ones = 0;

        switch (state) {

            case RxStart :
                //trace("RxStart");
                //trace("st octet: %02x".format(octet));
                if (octet == FLAG) {
                    state = RxTxd;
                    bitcount = 0;
                }
                break;

            case RxTxd :
                //trace("RxTxd");
                if (++bitcount >= 8) {
                    //trace("txd octet: %02x".format(octet));
                    bitcount = 0;
                    if (octet != FLAG) {
                        state    = RxData;
                        rxbuf[0] = octet & 0xff;
                        bufPtr   = 1;
                    }
                }
                break;

            case RxData :
                //trace("RxData");
                if (ones == 5) { // 111110nn, next bit will determine
                    state = RxFlag1;
                } else {
                    if (++bitcount >= 8) {
                        bitcount = 0;
                        if (bufPtr >= RXLEN) {
                            //trace("drop")
                            state = RxStart;
                        } else {
                            rxbuf[bufPtr++] = octet & 0xff;
                        }
                    }
                }
                break;

            case RxFlag1 :
                //trace("RxFlag");
                if (bit) { //was really a 6th bit. 
                    state = RxFlag2;
                } else { //was a zero.  drop it and continue
                    octet = (octet << 1) & 0xfe;
                    state = RxData;
                }
                break;

            case RxFlag2 :
                //we simply wanted that last bit
                processPacket(rxbuf, bufPtr);
                for (int rdx=0 ; rdx < RXLEN ; rdx++)
                    rxbuf[rdx] = 0;
                state = RxStart;
                break;
            
            default :
                //dont know
                   
        }//switch
    };
    

     
    private String rawPacket(int ibytes[], int offset, int len) {
        String str = "";
        for (int i=0 ; i<len ; i++) {
            int b = (ibytes[offset + i]); // >> 1;
            str += (char) b;
        }
        return str;
    }        
    
    private boolean processPacket(int data[], int len) {

        //trace("raw:" + len)
        if (len < 14)
            return true;
        String str = rawPacket(data, 14, len-2);
        trace("txt: " + str);
        crc.reset();
        for (int i=0 ; i < len ; i++) {
            crc.updateLE(data[i]);
        }
        int v = crc.getValueLE();
        trace("crc: " + Integer.toHexString(v));
        //theory is, if you calculate the CRC of the data, -including- the crc field,
        //a correct result will always be 0xf0b8
        if (v == 0xf0b8) {
            Packet p = Packet.create(data);
            par.puttext(p.toString() + "\n");
        }
        return true;
    }

    /*
    //################################################
    //# T R A N S M I T
    //################################################
    private var txShifted = false
    def txencode(str: String) : Seq[Int] =
        {
        var buf = scala.collection.mutable.ListBuffer[Int]()
        for (c <- str)
            {
            if (c == ' ')
                buf += Baudot.BAUD_SPACE
            else if (c == '\n')
                buf += Baudot.BAUD_LF
            else if (c == '\r')
                buf += Baudot.BAUD_CR
            else
                {
                var uc = c.toUpper
                var code = Baudot.baudLtrsToCode.get(uc)
                if (code.isDefined)
                    {
                    if (txShifted)
                        {
                        txShifted = false
                        buf += Baudot.BAUD_LTRS
                        }
                    buf += code.get
                    }
                else
                    {
                    code = Baudot.baudFigsToCode.get(uc)
                    if (code.isDefined)
                        {
                        if (!txShifted)
                            {
                            txShifted = true
                            buf += Baudot.BAUD_FIGS
                            }
                        buf += code.get
                        }
                    }
                }
            }
        buf.toSeq
        }
    
    def txnext : Seq[Int] =
        {
        //var str = "the quick brown fox 1a2b3c4d"
        var str = par.gettext
        var codes = txencode(str)
        codes
        }
    
    
    private var desiredOutput = 4096;

    /o*
     * Overridden from Mode.  This method is called by
     * the audio interface when it needs a fresh buffer
     * of sampled audio data at its sample rate.  If the
     * mode has no current data, then it should send padding
     * in the form of what is considered to be an "idle" signal
     o/                             
    override def transmit : Option[Array[Complex]] =
        {
        var symbollen = samplesPerSymbol.toInt
        var buf = scala.collection.mutable.ListBuffer[Complex]()
        var codes = txnext
        for (code <- codes)
            {
            for (i <- 0 until symbollen) buf += spaceFreq
            var mask = 1 
            for (i <- 0 until 5)
                {
                var bit = (code & mask) == 0
                var f = if (bit) spaceFreq else markFreq
                for (j <- 0 until symbollen) buf += f
                mask <<= 1
                }
            for (i <- 0 until symbollen) buf += spaceFreq
            }
        
        var pad = desiredOutput - buf.size
        for (i <- 0 until pad)
            buf += spaceFreq
        //var res = buf.toArray.map(txFilter.update)
        None
    }
    
    */

}





