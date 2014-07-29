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

package org.bdigi.core;


public class Convolutional {


    public static class Codec {
    
        protected int k;
        protected int size;
        protected int poly1;
        protected int poly2;
		/**
		 * "output" contains 2 bits in positions 0 and 1 describing the state machine
		 * for each bit delay, ie: for k = 7 there are 128 possible state pairs.
		 * the modulo-2 addition for polynomial 1 is in bit 0
		 * the modulo-2 addition for polynomial 2 is in bit 1
		 * the allowable state outputs are 0, 1, 2 and 3
		 */
        protected int output[];
        
        public Codec(int k, int poly1, int poly2) {
            this.k = k;
            this.poly1 = poly1;
            this.poly2 = poly2;
            size = 1<<k;
            output = new int[size];
            for (int i=0 ; i<size ; i++) {
			    output[i] = (parity(poly1 & i) << 1) | (parity(poly2 & i));
            }

        }


		public int parity(int v) {
			return (Integer.bitCount(v) & 1);
		}

		public String toBits(int v) {
			String s = java.lang.Integer.toString(v, 2);
			if (s.length() < 2) s = "0" + s;
			return s;     
		}
	
		public String toString() {
			StringBuilder buf = new StringBuilder();
			buf.append("===== Viterbi decoder ======\n");
			buf.append("    k    : " + k + "\n");
			buf.append("    poly1: " + poly1 + "\n");
			buf.append("    poly2: " + poly2 + "\n");
			buf.append("    size : " + output.length + "\n");
			for (int i=0 ; i<output.length ; i++) {
				buf.append("    " + i + " : " + toBits(output[i]) + "\n");
			}
			return buf.toString();
		}

   }



	/**
	 * Viterbi convolutional code decoder
	 */
	public static class Decoder extends Codec {
	
	    private int chunksize;
	    private int depth;
	    private int nrStates;
	    private int currPtr;
	    private int prevPtr;
        private int lastMetric;
        private int metrics[][];//error metrics
        private int history[][];//the state history table
        private int seq[];
        private int distanceTable[][];

        /**
         * Constructor
         * @param k the constraint of the convolutional code
         * @param poly1 the left-hand generator polynomial
         * @param poly2 the right-hand generator polynomial   
         */
	    public Decoder(int k, int poly1, int poly2, int chunksize) {
	        super(k, poly1, poly2);
	        this.chunksize = chunksize;
	        depth = chunksize * 8;
	        nrStates = 1 << (k - 1);
	        currPtr = 0;
	        prevPtr = depth - 1;
	        lastMetric = 0;
	        metrics = new int[depth][nrStates];
	        history = new int[depth][nrStates];
	        seq = new int[depth];
	        genDistanceTable();
	    }
        public Decoder(int k, int poly1, int poly2) {
            this(k, poly1, poly2, 8);
        }


		public void printMetrics(int distance) {
			System.out.println("==== Metrics/History ====");
			int pp = prevPtr;
			for (int i=0 ; i<distance ; i++) {
				for (int j=0 ; j<nrStates ; j++) {
					System.out.print(String.format("(%5d %5d) ", metrics[pp][j], history[pp][j]));               
				}
                System.out.println();
				pp -= 1;
				if (pp < 0)
					pp = depth - 1;
			}            
		}
        public void printMetrics() {
            printMetrics(8);
        }
	
		public void reset() {
			metrics = new int[depth][nrStates];
			history = new int[depth][nrStates];
			currPtr = 0;
			prevPtr = depth - 1;
		}
	
		/**
		 * Used to provide a good Euclidean distance for symbols with values 0..255
		 * The table is square, and is created for the origin 0,0 so
		 * distance(00 -> xy) = table(x)(y).  
		 * To get distances to the other three poles, just reverse the corresponding
		 * values.     
		 * So...
		 * distance(01 -> xy) = output(x)(255-y)  
		 * distance(10 -> xy) = output(255-x)(y)  
		 * distance(11 -> xy) = output(255-x)(255-y)  
		 *                              
		 */
		private void genDistanceTable() {
			int arr[][] = new int[256][256];
			for (int i=0 ; i<256 ; i++) {
			    for (int j=0 ; j<256 ; j++) {
				    int dist = (int) Math.round(Math.sqrt((i * i) + (j * j)));
				    arr[i][j] = dist;
				}
			}
			distanceTable = arr;
		}
	
		public boolean[] traceback() {
		
			/**
			 * First, select the state having the smallest accumulated
			 * error metric and save the state number of that state.
			 * Since currPtr has been incremented after the last sample,
			 * prevPtr points at the last recorded values.                  
			 */         
			int min  = Integer.MAX_VALUE;
			int best = 0;
			for (int i=0 ; i < nrStates ; i++) {
				int v = metrics[prevPtr][i];
				if (v < min)  {
					min  = v;
					best = i;
				}
			}
			//println("p: " + prevPtr + " best: " + best)
		
			/**
			 * Working backward through the state history table, for the
			 * selected state, select a new state which is listed in the
			 * state history table as being the predecessor to that state.
			 * Save the state number of each selected state. This step is
			 * called traceback.      
			 */
			int p = prevPtr;
			int ps = best;
			seq[p] = best;
			for (int i=1 ; i<depth-1 ; i++) {
				ps = history[p][ps];
				//println("p: " + p + " ps:" + ps)
				seq[p--] = ps;
				if (p < 0)
					p = depth-1;
				}

			lastMetric = metrics[prevPtr][best] - metrics[p][ps];
		
			/**
			 * Now work forward through the list of selected states saved in
			 * the previous steps. Look up what input bit corresponds to a
			 * transition from each predecessor state to its successor state.
			 * That is the bit that must have been encoded by the convolutional
			 * encoder.
			 */                 
			boolean res[] = new boolean[chunksize];
			for (int i=0 ; i<chunksize ; i++) {
				boolean bval = ((seq[p++] & 1) != 0);
				if (p >= depth)
					p = 0;
				res[i] = bval;
				}
		
			return res;
		}


		/**
		 * @param sym0 bit with range 0..255
		 * @param sym1 bit with range 0..255
		 * @return decoded symbol if successful, else -1          
		 */         
		public boolean[] decodeOne(int sym0, int sym1) {

			/**
			 * The decoding process begins with building the accumulated error
			 * metric for some number of received channel symbol pairs, and the
			 * history of what states preceded the states at each time instant t
			 * with the smallest accumulated error metric. Once this information
			 * is built up, the Viterbi decoder is ready to recreate the sequence
			 * of bits that were input to the convolutional encoder when the
			 * message was encoded for transmission.
			 *          
			 * Note that in our metric tables, larger number means smaller error.   
			 * The output table is twice the size of nrStates, so check lower and upper
			 * half and record the better value.                        
			 */                 
			/**
			 * For a soft decision decoder, a branch metric is measured using the
			 * Euclidean distance. 
			 *
			 */
			int branchMetric[] = new int[]{
				distanceTable[    sym0][    sym1],
				distanceTable[    sym0][255-sym1],
				distanceTable[255-sym0][    sym1],
				distanceTable[255-sym0][255-sym1]
			};       
			
			return decodeOne(branchMetric);
		}
  
		/**
		 * @param branchMetric  An array with a positive integer distance
		 * from each of 00, 01, 10, and 11
		 * @return decoded symbol if successful, else -1
		 */         
		public boolean[] decodeOne(int branchMetric[]) {
			//println("(%3d,%3d) : %4d %4d %4d %4d".format(sym0, sym1,
			//    branchMetric(0), branchMetric(1), branchMetric(2), branchMetric(3)))

			/**
			 * For each state at this point in time, add the branch metric to
			 * the metrics of two incoming states at the previous call.  Choose the lesser
			 * of the two.  Record the metric, and indicate which incoming state was
			 * chosen.
			 */
			for (int n=0 ; n<nrStates ; n++) {
				int s0      = n;
				int s1      = n + nrStates;
				int p0      = s0 >> 1;
				int p1      = s1 >> 1;
				int metric0 = metrics[prevPtr][p0] + branchMetric[output[s0]];
				int metric1 = metrics[prevPtr][p1] + branchMetric[output[s1]];

				//println("p0: %d : %5d  p1: %d : %5d".format(p0, metric0, p1, metric1))
				if (metric0 < metric1) {
					metrics[currPtr][n] = metric0;
					history[currPtr][n] = p0;
				} else {
					metrics[currPtr][n] = metric1;
					history[currPtr][n] = p1;
				}

			}

			//advance to record these values
			prevPtr = currPtr;
			currPtr = (currPtr + 1) % depth;

			//have we received a complete chunk? Then process it and
			//return the decoded bits
			boolean res[] = new boolean[0];
			if ((currPtr % chunksize) == 0) {
				res = traceback();
			} else {
				//check if values are growing too large. if so, then adjust
				int halfMax = Integer.MAX_VALUE / 2;

				if (metrics[currPtr][0] > halfMax) {
					for (int i=0 ; i<depth ; i++)
                        for (int j=0 ; j<nrStates ; j++)
						metrics[i][j] -= halfMax;
				}
				if (metrics[currPtr][0] < -halfMax) {
                    for (int i=0 ; i<depth ; i++)
                        for (int j=0 ; j<nrStates ; j++)
						metrics[i][j] += halfMax;
				}
			}

			return res;
		}



		/**
		 * @param syms, seq of sym0, sym1 typles
		 * @return decoded bits
		 */
		public boolean[] decode(int syms[][]) {
            int len = syms.length;
            boolean out[] = new boolean[len*2];
            int outp = 0;
            for (int i=0 ; i<len ; i++) {
                int sym[] = syms[i];
                boolean res[] = decodeOne(sym[0], sym[1]);
                out[outp++] = res[0];
                out[outp++] = res[1];
            }
            return out;
		}

		/**
		 * @param dibit two bits in the 0 and 1 position
		 * @return decoded symbol if mod of chunkSize or we want to flush
		 */
		public boolean[] decodeOneHard(int dibit) {
			//255 for true 0 for false.  hard decisions
			int sym1 = ((dibit & 1) != 0) ? 255 : 0;
			int sym0 = ((dibit & 2) != 0) ? 255 : 0;
			return decodeOne(sym0, sym1);
		}

		public boolean[] decodeHard(int dibits[]) {
            int len = dibits.length;
            boolean out[] = new boolean[len*2];
            int outp = 0;
			for (int i=0 ; i<len ; i++) {
                boolean res[] = decodeOneHard(dibits[i]);
                out[outp++] = res[0];
                out[outp++] = res[1];
            }
            return out;
		}

	}//Decoder





	public static class Encoder extends Codec {


        private int state;
        private int statemask;

        public Encoder(int k, int poly1, int poly2) {
            super(k, poly1, poly2);
            state = 0;
            statemask = size-1;
        }

		public void reset() {
			state = 0;
		}

		public int encode(boolean bit) {
			int bval = (bit) ? 1 : 0;
			state = ((state << 1) | bval) & statemask;
			return output[state];
		}

		public int[] encodeBits(boolean bits[]) {
		    int len = bits.length;
		    int xs[] = new int[len];
		    for (int i=0 ; i<len ; i++) {
		        xs[i] = encode(bits[i]);
		    }
			return xs;
		}

		public int[] encodeWord(int word, int cnt) {
			int c = cnt - 1;
			boolean arr[] = new boolean[cnt];
			for (int i=0 ; i<cnt ; i++) {
				boolean v = (((word >> c--) & 1) != 0);
				arr[i] = v;
			}
			return encodeBits(arr);
		}
		public int[] encodeWord(int word) {
		    return encodeWord(word, 8);
		}

		public int[] encodeWords(int words[], int cnt) {
            int len = words.length;
            int out[] = new int[len*2];
            int outp = 0;
            for (int i=0 ; i<len ; i++) {
                int word = words[i];
                int res[] = encodeWord(word, cnt);
                out[outp++] = res[0];
                out[outp++] = res[1];
            }
			return out;
		}
		public int[] encodeWords(int words[]) {
		    return encodeWords(words, 8);
		}

		public int[] encodeBytes(byte bytes[]) {
            int len = bytes.length;
            int out[] = new int[len*2];
            int outp = 0;
            for (int i=0 ; i<len ; i++) {
                int res[] = encodeWord(bytes[i]);
                out[outp++] = res[0];
                out[outp++] = res[1];
            }
			return out;
		}

		public int[] encodeStr(String str) {
			return encodeBytes(str.getBytes());
		}
	}




    public static Encoder encoder(int k, int poly1, int poly2) {
        return new Encoder(k, poly1, poly2);
    }

    public static Decoder decoder(int k, int poly1, int poly2, int chunksize) {
        return new Decoder(k, poly1, poly2, chunksize);
    }
    
    public static Decoder decoder(int k, int poly1, int poly2) {
        return decoder(k, poly1, poly2, 8);
    }
    
    public static String toBits(int v) {
        String s = java.lang.Integer.toString(v, 2);
        if (s.length() < 2) s = "0" + s;
        return s;       
    }
    


}
