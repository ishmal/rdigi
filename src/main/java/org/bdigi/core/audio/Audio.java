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

package org.bdigi.core.audio;


import org.bdigi.core.Digi;

import javax.sound.sampled.*;
import java.util.HashMap;
import java.util.Map;


public class Audio {

    /**
     * Implementation of an AudioInput on the JVM
     */
    public static class Input implements AudioInput {

        private Digi par;
        private Info info;
        private TargetDataLine line;
        private int bufsize;
        private byte[] buf;
        private double[] vbuf;
        private double[][] bytesToDouble;
        private static final double shortToDouble  = 1.0 / 32768.0;
        private int readsize;

        public Input(Digi par, Info info) {
            this.par = par;
            this.info = info;
            generateBytesToDouble();
            try {
                line = AudioSystem.getTargetDataLine(info.getFormat(), info.getMixerInfo());
                bufsize = line.getBufferSize();
                buf = new byte[bufsize*2];
                vbuf = new double[bufsize];
                readsize = (bufsize / 4) & 0xfffe;
            } catch (LineUnavailableException e) {
                par.error("Can't open input: " + e);
            }
        }

        public boolean open() {
            if (line == null) {
                par.error("input open: line not initialized");
                return false;
            }
            if (line.isRunning())
                line.close();
            try {
                line.open(info.getFormat());
                line.start();
            } catch (LineUnavailableException e) {
                par.error("input open: " + e);
                return false;
            }
            //par.trace("line open");
            return true;
        }
            
        public boolean close() {
            line.stop();
            line.close();
            //par.trace("line close")
            return true;
        }
            
        public double getSampleRate() {
            return 44100.0;
        }
            
        /**
         * Table for taking a short sample from the input stream as two bytes, and
         * using those bytes to look up a double value, rather than calculating it
         */
        private void generateBytesToDouble() {

            double arr[][] = new double[256][256];
            for (int hi=0 ; hi<256 ; hi++) {
                for (int lo=0 ; lo<256 ; lo++) {
                    double v = ((double)((hi << 8) + lo)) * shortToDouble;
                    arr[hi][lo] = v;
                }
            }
            bytesToDouble = arr;
        }


        public double[] read() {
            int numBytes = line.read(buf, 0, readsize);
            if (numBytes <= 0) {
                return null;
            } else {
                int vptr = 0;
                for (int i=0 ; i<numBytes ; i+=2) {
                    double dval = bytesToDouble[buf[i] & 0xff][buf[i+1] & 0xff];
                    vbuf[vptr++] = dval;
                }
                double packet[] = new double[vptr];
                System.arraycopy(vbuf, 0, packet, 0, vptr);
                return packet;
            }
        }
        
        
    }//Input

    
    
    /**
     * Implementation of an AudioOutputDevice on the JVM
     */
    public static class Output implements AudioOutput {

        private Digi par;
        private Info info;
        private SourceDataLine line;
        private int frameSize;
        private int framesPerBuffer;
        private int bufsize;
        private byte buf[];
        private int bptr;
        private final static double doubleToShort = 32767.0;
    
        public Output (Digi par, Info info) {
            this.par = par;
            this.info = info;
            try {
                line = AudioSystem.getSourceDataLine(info.getFormat(), info.getMixerInfo());
                frameSize = line.getFormat().getFrameSize();
                framesPerBuffer = line.getBufferSize() / 8;
                bufsize = 4096 * frameSize;
                buf = new byte[bufsize];
                bptr = 0;
            } catch (LineUnavailableException e) {
                par.error("output initialize: " + e);
            }
        }

        public boolean open() {
            if (line == null) {
                par.error("output open: line not initialized");
                return false;
            }
            try {
                line.open(info.getFormat(), bufsize);
                line.start();
            } catch (LineUnavailableException e) {
                par.error("output open: " + e);
            }
            return true;
        }
            
        public boolean close() {
            line.close();
            return true;
        }
            
        public double getSampleRate() {
            return 44100.0;
        }
            
        /*
         * What we expect is an array of doubles, -1.0 to 1.0
         */
        public boolean write(double inbuf[]) {
            int len = inbuf.length;
            for (int i=0 ; i<len ; i++) {
                double ival = inbuf[i];
                int iv = (int)(doubleToShort * ival);
                byte hi = (byte)((iv >> 8) & 0xff);
                byte lo = (byte)((iv     ) & 0xff);
                buf[bptr++] = hi;
                buf[bptr++] = lo;
                if (bptr >= bufsize) {
                    line.write(buf, 0, bufsize);
                    bptr = 0;
                }
            }
            return true;
        }

    }//Output




    /**
     * Data class describing available audio devices
     */
    public static class Info {
        private AudioFormat format;
        private Mixer.Info mixerInfo;

       public Info(AudioFormat format, Mixer.Info mixerInfo){
           this.format = format;
           this.mixerInfo = mixerInfo;
       }

        public AudioFormat getFormat() {
            return format;
        }

        public Mixer.Info getMixerInfo() {
            return mixerInfo;
        }
    }


    /**
     * List conforming audio input devices
     */
    private static Map<String, Info> inputDevices;
    public static Map<String, Info> getInputDevices() {
        if (inputDevices == null) {
            inputDevices = new HashMap<String, Info>();
            AudioFormat audioFormat = new AudioFormat(44100.0f, 16, 1, true, true);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
            inputDevices = new HashMap<String, Info>();
            for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
                Mixer m = AudioSystem.getMixer(mixerInfo);
                if (m.isLineSupported(info)) {
                    inputDevices.put(mixerInfo.getName(), new Info(audioFormat, mixerInfo));
                }
            }
        }
        return inputDevices;
    }


     
    /**
     * List conforming audio output devices
     */
    private static Map<String, Info> outputDevices;
    public static Map<String, Info> getOutputDevices() {
        if (outputDevices == null) {
            outputDevices = new HashMap<String, Info>();
            AudioFormat audioFormat = new AudioFormat(44100.0f, 16, 1, true, true);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
            inputDevices = new HashMap<String, Info>();
            for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
                Mixer m = AudioSystem.getMixer(mixerInfo);
                if (m.isLineSupported(info)) {
                    outputDevices.put(mixerInfo.getName(), new Info(audioFormat, mixerInfo));
                }
            }
        }
        return outputDevices;
    }
        

    /**
     * Create an audio input device by name.  If device is not in the list,
     * return an error
     */
    public static Input createInput(Digi par, String name) {
        Info dev = inputDevices.get(name);
        if (dev != null) {
            return new Input(par, dev);
        } else {
            par.error("Input audio device not found: " + name);
            return null;
        }
    }
        
    /**
     * Create an audio output device by name .  If name does not exist, return an error
     */
    public static Output createOutput(Digi par, String name) {
        Info dev = outputDevices.get(name);
        if (dev != null) {
            return new Output(par, dev);
        } else {
            par.error("Output audio device not found: " + name);
            return null;
        }
    }

}





