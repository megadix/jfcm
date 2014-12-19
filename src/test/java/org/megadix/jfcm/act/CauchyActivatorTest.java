package org.megadix.jfcm.act;

import org.junit.Test;
import org.megadix.jfcm.act.CauchyActivator;

public class CauchyActivatorTest extends AbstractConceptActivatorTest {

    @Test
    public void testCalculate() {

        CauchyActivator act = new CauchyActivator();
        act.setIncludePreviousOutput(false);

        // test values

        double[] input = { -4.0, -3.5, -3.0, -2.5, -2.0, -1.5, -1.0, -0.5, -0.0, 0.5, 1, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0 };
        double[] expected = { 0.01872, 0.02402, 0.03183, 0.04390, 0.06366, 0.09794, 0.15915, 0.25465, 0.31831, 0.25465,
                0.15915, 0.09794, 0.06366, 0.04390, 0.03183, 0.02402, 0.01872 };

        checkValues(input, null, expected, act);

        performTest(act, null, Double.NEGATIVE_INFINITY, 0.0);
        performTest(act, null, Double.POSITIVE_INFINITY, 0.0);

        // TODO threshold, gain, invert
    }

}
