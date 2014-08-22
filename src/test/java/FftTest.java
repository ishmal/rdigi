import org.bdigi.core.FFT;
import org.bdigi.core.FFT2;
import org.junit.Test;



/**
 * Used mostly for performance testing of FFT implementations.
 */
public class FftTest {

    @Test
    public void performanceTest() {
        int iterations = 500000;
        FFT fft1 = new FFT(2048);
        FFT2 fft2 = new FFT2(2048);
        double in[] = new double[2048];
        double out[] = new double[1024];
        for (int i=0 ; i<2048 ; i++) {
            in[i] = Math.random();
        }

        long startTime = System.currentTimeMillis();
        for (int i=0 ; i<iterations ; i++) {
            fft1.powerSpectrum(in, out);
        }
        long endTime = System.currentTimeMillis();
        int fft1Time = (int) (endTime - startTime);
        startTime = System.currentTimeMillis();
        for (int i=0 ; i<iterations ; i++) {
            fft2.powerSpectrum(in, out);
        }
        endTime = System.currentTimeMillis();
        int fft2Time = (int) (endTime - startTime);
        System.out.println("fft1: " + fft1Time + "  fft2:" + fft2Time);
    }

}
