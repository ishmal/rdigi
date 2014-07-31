


public class PpGen {


    private double w[]; //Hann window
    
    private void p(String s) {
        System.out.print(s);
    }
    
    
    public void output(int size; double coeffs[]) {
        p("//###############################\n");
        p("//### decimation : " + size + "\n");
        p("//###############################\n");
    
    }


    public void generate(int size, int decimation) {

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
    
    
    




    public void generate(int size) {
    
        w = new int[size];
        for (int i=0 ; i<size ; i++) {
		   w[i] = 0.5 - 0.5 * Math.cos( 2.0 * Math.PI * i / (size - 1));
		}
        for (int decim=0 ; decim<11 ; decim++) {
            generate(size, decim);
        }
    }



    public static void main(String argv[]) {
        PpGen ppg = new PpGen();
        ppg.generate(13);
    }





}