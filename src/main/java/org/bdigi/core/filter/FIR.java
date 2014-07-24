package org.bdigi.core.filter;


import org.bdigi.core.Complex;

/**
 * Hardcoded filter for size 13.  Pick 13!
 */
public class FIR {
	
    public interface Filter {
	    public double update(double v);
    }

	
    static class Filter13 implements FIR.Filter {
		double c0,c1,c2,c3,c4,c5,c6,c7,c8,c9,c10,c11,c12;
		double r0,r1,r2,r3,r4,r5,r6,r7,r8,r9,r10,r11,r12;
		double i0,i1,i2,i3,i4,i5,i6,i7,i8,i9,i10,i11,i12;

		public Filter13(double coeffs[]){
			c0=coeffs[0];c1=coeffs[1];c2=coeffs[2];c3=coeffs[3];
			c4=coeffs[4];c5=coeffs[5];c6=coeffs[6];c7=coeffs[7];
			c8=coeffs[8];c9=coeffs[9];c10=coeffs[10];c11=coeffs[11];
			c12=coeffs[12];
		}

		public double update(double v) {
			r12=r11; r11=r10; r10=r9; r9=r8; r8=r7; r7=r6; r6=r5;
			r5=r4; r4=r3; r3=r2; r2=r1; r1=r0; r0=v;

			return c0*r12 + c1*r11 + c2*r10 + c3*r9 + c4*r8 + c5*r7 + c6*r6 +
				c7*r5 + c8*r4 + c9*r3 + c10*r2 + c11*r1 + c12*r0;
		}

		public Complex update(Complex v) {
			r12=r11; r11=r10; r10=r9; r9=r8; r8=r7; r7=r6; r6=r5;
			r5=r4; r4=r3; r3=r2; r2=r1; r1=r0; r0=v.getR();
			i12=i11; i11=i10; i10=i9; i9=i8; i8=i7; i7=i6; i6=i5;
			i5=i4; i4=i3; i3=i2; i2=i1; i1=i0; i0=v.getI();

			return new Complex(
				c0*r12 + c1*r11 + c2*r10 + c3*r9 + c4*r8 + c5*r7 + c6*r6 +
				    c7*r5 + c8*r4 + c9*r3 + c10*r2 + c11*r1 + c12*r0,
				c0*i12 + c1*i11 + c2*i10 + c3*i9 + c4*i8 + c5*i7 + c6*i6 +
				    c7*i5 + c8*i4 + c9*i3 + c10*i2 + c11*i1 + c12*i0
			);
		}

	}



	interface Generator {
		double get(int index);
	}

	public static double[] genCoeffs(int size, Window window, Generator f) {

		if (window == null) window = Window.rectangle;
		double W[] = window.get(size);
		int center = (int)(size * 0.5);
		double sum = 0.0;
		double arr[] = new double[size];
		for (int i=0 ; i<size ; i++) {
		    double v = f.get(i-center) * W[i];
		    sum += v;
		    arr[i] = v;
		}
		for (int i=0 ; i<size ; i++) {
		    arr[i] /= sum;
		}
		return arr;
	}

	static class FIRFilter implements Filter {

		private int size;
		private int sizeless;
		private double coeffs[];
		private double dlr[];
		private double dli[];
		private int dptr;

		public FIRFilter(int size, double coeffs[]) {
			this.size = size;
			sizeless = size - 1;
			dlr = new double[size];
			dli = new double[size];
			dptr = 0;
		}

		public double update(double v) {

			dlr[dptr++] = v;
			dptr %= size;
			int ptr = dptr;
			double sum = 0;
			for (int i=0 ; i < size ; i++) {
				sum += coeffs[i] * dlr[ptr];
				ptr = (ptr+sizeless)%size;
			}
		    return sum;
		}

		public Complex update(Complex v) {
		    dlr[dptr]   = v.getR();
			dli[dptr++] = v.getI();
			dptr %= size;
			int ptr = dptr;
			double sumr = 0;
			double sumi = 0;
			for (int i=0 ; i < size ; i++) {
				sumr += coeffs[i] * dlr[ptr];
				sumi += coeffs[i] * dli[ptr];
				ptr = (ptr+sizeless)%size;
			}
			return new Complex(sumr, sumi);
		}

	}


	public static Filter average(int size, Window window) {
		final double omega = 1.0 / size;
		double coeffs[] = genCoeffs(size, window, new Generator() { public double get(int i) {  return omega; }});
		return (size==13) ? new Filter13(coeffs) : new FIRFilter(size, coeffs);
	}

	public static Filter boxcar(int size, Window window) {
		double coeffs[] = genCoeffs(size, window, new Generator() { public double get(int i) {  return 1.0; }});
		return (size==13) ? new Filter13(coeffs) : new FIRFilter(size, coeffs);
	}

	public static Filter lowpass(int size, double cutoffFreq, double sampleRate, Window window) {
		final double omega = 2.0 * Math.PI * cutoffFreq / sampleRate;
		double coeffs[] = genCoeffs(size, window, new Generator() {
			public double get(int i) {  return (i == 0) ? omega / Math.PI : Math.sin(omega * i) / (Math.PI * i); }});
		return (size==13) ? new Filter13(coeffs) : new FIRFilter(size, coeffs);
	}

	public static Filter highpass(int size, double cutoffFreq, double sampleRate, Window window) {
		final double omega = 2.0 * Math.PI * cutoffFreq / sampleRate;
		double coeffs[] = genCoeffs(size, window, new Generator() {
			public double get(int i) {  return (i == 0) ? 1.0 - omega / Math.PI : -Math.sin(omega * i) / (Math.PI * i); }});
		return (size==13) ? new Filter13(coeffs) : new FIRFilter(size, coeffs);
	}

	public static Filter bandpass(int size, double loCutoffFreq, double hiCutoffFreq, double sampleRate, Window window) {
		final double omega1 = 2.0 * Math.PI * hiCutoffFreq / sampleRate;
		final double omega2 = 2.0 * Math.PI * loCutoffFreq / sampleRate;
		double coeffs[] = genCoeffs(size, window, new Generator() {
			public double get(int i) {  return (i == 0) ? (omega2 - omega1) / Math.PI :
					(Math.sin(omega2 * i) - Math.sin(omega1 * i)) / (Math.PI * i); }});
		return (size==13) ? new Filter13(coeffs) : new FIRFilter(size, coeffs);
	}

	public static Filter bandreject(int size, double loCutoffFreq, double hiCutoffFreq, double sampleRate, Window window) {
		final double omega1 = 2.0 * Math.PI * hiCutoffFreq / sampleRate;
		final double omega2 = 2.0 * Math.PI * loCutoffFreq / sampleRate;
		double coeffs[] = genCoeffs(size, window, new Generator() {
			public double get(int i) {  return (i == 0) ? 1.0 - (omega2 - omega1) / Math.PI :
					(Math.sin(omega1 * i) - Math.sin(omega2 * i)) / (Math.PI * i); }});
		return (size==13) ? new Filter13(coeffs) : new FIRFilter(size, coeffs);
	}

	public static Filter raisedcosine(int size, double rolloff, double symbolFreq, double sampleRate, Window window) {
		final double T  = sampleRate / symbolFreq;
		final double a = rolloff;
		double coeffs[] = genCoeffs(size, window, new Generator() {
			public double get(int i) {
				double nT = i / T;
				double anT = a * nT;
				double c = 0;
				if (i == 0)
					c = 1.0;
				else if (anT == 0.5 || anT == -0.5)//look at denominator below
					c = Math.sin(Math.PI*nT)/(Math.PI*nT) * Math.PI / 4.0;
				else
					c = Math.sin(Math.PI*nT)/(Math.PI*nT) * Math.cos(Math.PI * anT) /
							(1.0 - 4.0 * anT * anT);
				return c;
			}
		});

		return (size==13) ? new Filter13(coeffs) : new FIRFilter(size, coeffs);
	}


}
