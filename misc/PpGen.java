


public class PpGen {

    private int size;
    private double w[]; //window

    public PpGen(int size) {
        this.size = size;
        makeWindow(size);
    }


    private void makeWindow(int size) {
        w = new int[size];
        for (int i=0 ; i<size ; i++) {
            //Hann window
		    w[i] = 0.5 - 0.5 * Math.cos(2.0 * Math.PI * i / (size - 1));
		}
    }
    
    private void p(String s) {
        System.out.print(s);
    }
    
    
    public void output(int size; double coeffs[]) {
        p("//###############################\n");
        p("//### decimation : " + size + "\n");
        p("//###############################\n");
    
    }


    public void generate(int decimation) {

         double omega = Math.PI * 2.0 / decimation;
         int half = size>>1;
         double coeffs[] = new double[size];
         for (int idx=0 ; idx < size ; idx++) {
             int i = idx - half;
             double coeff = (i == 0) ? omega / Math.PI : Math.sin(omega * i) / (Math.PI * i);
             coeffs[idx] = coeff * w[idx];
         } 
         output(size, coeffs);   
    }
    
    public void generate(int maxDecimation) {
    
        for (int decim=0 ; decim<maxDecimation ; decim++) {
            generate(decim);
        }
    }



    public static void main(String argv[]) {
        PpGen ppg = new PpGen(13);
        ppg.generate(11);
    }





}