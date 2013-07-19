package org.megadix.jfcm.act;

import org.junit.Test;
import org.megadix.jfcm.act.IntervalActivator;
import org.megadix.jfcm.act.IntervalActivator.Mode;

public class IntervalActivatorTest extends AbstractConceptActivatorTest {

    @Test
    public void testCalculate() {
        IntervalActivator act = new IntervalActivator();

        double[] input = { -2.0, -1.0, -0.9, -0.1, 0.0, 0.1, 0.9, 1.0, 2.0 };
        double[] expected_BINARY = { 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0 };
        double[] expected_BIPOLAR = { -1.0, -1.0, 1.0, 1.0, 1.0, 1.0, 1.0, -1.0, -1.0 };

        act.setMode(Mode.BINARY);
        checkValues(input, null, expected_BINARY, act);

        act.setMode(Mode.BIPOLAR);
        checkValues(input, null, expected_BIPOLAR, act);

        // change amplitutde
        act.setAmplitude(2.0);

        input = new double[] { -3.0, -2.0, -1.9, -1.0, 0.0, 1.0, 1.9, 2.0, 3.0 };
        expected_BINARY = new double[] { 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0 };
        expected_BIPOLAR = new double[] { -1.0, -1.0, 1.0, 1.0, 1.0, 1.0, 1.0, -1.0, -1.0 };

        act.setMode(Mode.BINARY);
        checkValues(input, null, expected_BINARY, act);

        act.setMode(Mode.BIPOLAR);
        checkValues(input, null, expected_BIPOLAR, act);

        // TODO threshold
    }

}
