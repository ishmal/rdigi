import org.bdigi.core.Complex;
import org.bdigi.core.Digi;
import org.bdigi.core.Property;
import org.bdigi.core.mode.Mode;
import org.bdigi.core.mode.Props;
import org.junit.Test;

/**
 * This is simply a place to test audio output
 */
public class OutputTest {

    class TestMode extends Mode {

        public TestMode(Digi par) {
            super(par,
            new Property("psk", "phase shift keying").
            radio("pitch", "frequency", "500.0", "500.0", "1000.0", "1500.0"),
            1000);
            setRate(31.25);
        }

        @Override
        public void radioControl(String name, String value) {
            double d = Double.parseDouble(value);
            if ("pitch".equals(name)) {
                setPitch(d);
            }
        }

        private void setPitch(double v) {

        }

        //###################
        //# transmit
        //###################

        @Override
        public Complex[] transmit() {

            return new Complex[0];


        }
    }


    @Test
    public void guiTest() {
    }
}
