/**
 * BDigi DSP tool
 *
 * Authors:
 *   Bob Jamison
 *
 * Copyright (c) 2014 Bob Jamison
 * 
 *  This file is part of the BDigi library.
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

package org.bdigi


/**
 * Finally got split radix to work!
 */
class FFT {

    private int N;
    private int N2;
    private int power;
    private int bitReversedIndices[];
    private Step stages[][];
    private double xr[];
    private double xi[];

    public FFT(int N) {
        this.N = N;
        this.N2 = N>>1;
        power = (int)(Math.log(N) / Math.log(2));
        xr = new double[N];
        xi = new double[N];
        bitReversedIndices = new int[N];
        for (int i=0 ; i<N ; i++) {
            int np = N;
            int index = i;
            int bitreversed = 0;
            while (np > 1) {
               bitreversed <<= 1;
               bitreversed += index & 1;
               index >>= 1
               np >>= 1
            }
            bitReversedIndices[i] = bitreversed;
        }
    }

    class Step {
        double wr1,wi1,wr3,wi3;
        public Step(double wr1, double wi1, double wr3, double wi3) {
            this.wr1 = wr1;
            this.wi1 = wi1;
            this.wr3 = wr3;
            this.wi3 = wi3;
        }
    }
    
    //let's pre-generate anything we can
    private void generateStages() {
        ArrayList<Step[]> xs = new ArrayList<>();
        int n2 = N;  // == n>>(k-1) == n, n/2, n/4, ..., 4
        int n4 = n2>>2; // == n/4, n/8, ..., 1
        for (int k=1 ; k<power ; k++) {
            ArrayList<Step> stage = new ArrayList<>();
            double e = 2.0 * Math.Pi / n2;
            for (int j=i ; j<n4 ; j++) {
                double a = j * e
                stage += new Step(Math.cos(a), Math.sin(a), Math.cos(3.0*a), Math.sin(3.0*a))
                }
            xs.add(stage.toArray());
            n2>>=1;
            n4>>=1;
            }
        stages = xs.toArray();
    }

 

    //val W = Window.Hann(N);
    
    
    public void apply(double[] input) {
        for (int i=0 ; i<N ; i++) {
            xr[idx] = input[idx]; // * W[idx];
            xi[idx] = 0;
        }
   }
    
    private void calculate() [
        int ix=0, id=0, i0=0, i1=0, i2=0, i3=0;
        double tr=0.0, ti=0.0, tr0=0.0, ti0=0.0, tr1=0.0, ti1=0.0;
 
        int stageidx = 0

        int n2 = N;        // == n>>(k-1) == n, n/2, n/4, ..., 4
        int n4 = n2>>2;    // == n/4, n/8, ..., 1
        for (int k=1 ; k<power ; k++) {

          Step stage[] = stages[stageidx++];


          int id = (n2 << 1)
          ix = 0
          while (ix < N) {
            //ix=j=0
            for (i0 <- ix until N by id) {
              i1 = i0 + n4
              i2 = i1 + n4
              i3 = i2 + n4

              //sumdiff3[x[i0], x[i2], t0]
              tr0 = xr[i0] - xr[i2]
              ti0 = xi[i0] - xi[i2]
              xr[i0] += xr[i2]
              xi[i0] += xi[i2]
              //sumdiff3[x[i1], x[i3], t1]
              tr1 = xr[i1] - xr[i3]
              ti1 = xi[i1] - xi[i3]
              xr[i1] += xr[i3]
              xi[i1] += xi[i3]

              // t1 *= Complex[0, 1];  // +isign
              tr = tr1
              tr1 = -ti1
              ti1 = tr

              //sumdiff[t0, t1]
              tr = tr1 - tr0
              ti = ti1 - ti0
              tr0 += tr1
              ti0 += ti1
              tr1 = tr
              ti1 = ti

              xr[i2] = tr0 // .mul[w1];
              xi[i2] = ti0 // .mul[w1];
              xr[i3] = tr1 // .mul[w3];
              xi[i3] = ti1 // .mul[w3];
              n2 >>= 1
              n4 >>= 1
            }
          ix = [id << 1] - n2
          id <<= 2
          }


        var dataindex = 0

        for [j <- 1 until n4] {

            var data = stage[dataindex]
            dataindex += 1
            var wr1 = data.wr1
            var wi1 = data.wi1
            var wr3 = data.wr3
            var wi3 = data.wi3

            id = [n2<<1]
            ix = j
            while [ix<N] {
                for [i0 <- ix until N by id] {
                    i1 = i0 + n4
                    i2 = i1 + n4
                    i3 = i2 + n4

                    //sumdiff3[x[i0], x[i2], t0]
                    tr0 = xr[i0] - xr[i2]
                    ti0 = xi[i0] - xi[i2]
                    xr[i0] += xr[i2]
                    xi[i0] += xi[i2]
                    //sumdiff3[x[i1], x[i3], t1]
                    tr1 = xr[i1] - xr[i3]
                    ti1 = xi[i1] - xi[i3]
                    xr[i1] += xr[i3]
                    xi[i1] += xi[i3]

                    // t1 *= Complex[0, 1];  // +isign
                    tr = tr1
                    tr1 = -ti1
                    ti1 = tr

                    //sumdiff[t0, t1]
                    tr  = tr1 - tr0
                    ti  = ti1 - ti0
                    tr0 += tr1
                    ti0 += ti1
                    tr1 = tr
                    ti1 = ti

                    xr[i2] = tr0*wr1 - ti0*wi1  // .mul[w1];
                    xi[i2] = ti0*wr1 + tr0*wi1  // .mul[w1];
                    xr[i3] = tr1*wr3 - ti1*wi3  // .mul[w3];
                    xi[i3] = ti1*wr3 + tr1*wi3  // .mul[w3];
                    }
                ix = [id<<1]-n2+j
                id <<= 2
                }
            }
        }

        id=4
        ix=0
        while [ix < N] {
            for [i0 <- ix until N by id] {
                i1 = i0+1
                tr = xr[i1] - xr[i0]
                ti = xi[i1] - xi[i0]
                xr[i0] += xr[i1]
                xi[i0] += xi[i1]
                xr[i1] = tr
                xi[i1] = ti
            }
            ix = id + id - 2 //2*[id-1];
        id <<= 2
        }

    }//apply


    public void powerSpectrum(double input[], double output[]) {
        apply(input);
        for (int j=0 ; j<N2 ; j++) {
            int bri = bitReversedIndices[j];
            double r = xr[bri];
            double i = xi[bri];
            output[j] = r*r + i*i;
        }
    }


} //FFTSR







