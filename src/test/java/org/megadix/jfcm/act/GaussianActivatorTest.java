package org.megadix.jfcm.act;

import org.junit.Test;
import org.megadix.jfcm.act.GaussianActivator;

public class GaussianActivatorTest extends AbstractConceptActivatorTest {

    @Test
    public void testCalculate() {

        GaussianActivator act = new GaussianActivator();
        act.setIncludePreviousOutput(false);

        // test values

        double[] input = { -4.0, -3.5, -3.0, -2.5, -2.0, -1.5, -1.0, -0.5, -0.0, 0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0 };
        double[] expected = { 0.0003, 0.0022, 0.0111, 0.0439, 0.1353, 0.3247, 0.6065, 0.8825, 1.0000, 0.8825, 0.6065,
                0.3247, 0.1353, 0.0439, 0.0111, 0.0022, 0.0003 };

        checkValues(input, null, expected, act);

        performTest(act, null, Double.NEGATIVE_INFINITY, 0.0);
        performTest(act, null, Double.POSITIVE_INFINITY, 0.0);

        // TODO threshold, gain, invert

    }
}
