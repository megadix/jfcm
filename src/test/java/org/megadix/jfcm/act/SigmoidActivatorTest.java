/*
JFCM (Java Fuzzy Congnitive Maps)
Copyright (C) De Franciscis Dimitri - www.megadix.it

This library is free software; you can redistribute it and/or modify it under
the terms of the GNU Lesser General Public License as published by the Free
Software Foundation; either version 2.1 of the License, or (at your option) any
later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along
with this library; if not, write to the Free Software Foundation, Inc., 59
Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package org.megadix.jfcm.act;

import org.junit.Test;
import org.megadix.jfcm.act.SigmoidActivator;

public class SigmoidActivatorTest extends AbstractConceptActivatorTest {

    @Test
    public void test_calculateNextOutputImpl() throws Exception {

        // setup
        SigmoidActivator act = new SigmoidActivator();

        // test values

        double[] input = { -2.00, -1.00, 0.00, 1.00, 2.00 };
        double[] k = { 1.00, 2.00, 3.00 };
        double[] expected_noPrevOutput = { 0.1192, 0.2689, 0.5000, 0.7311, 0.8808, 0.0180, 0.1192, 0.5000, 0.8808,
                0.9820, 0.0025, 0.0474, 0.5000, 0.9526, 0.9975 };
        double[] expected_withPrevOutput = { 0.2689, 0.5000, 0.7311, 0.8808, 0.9526, 0.1192, 0.5000, 0.8808, 0.9820,
                0.9975, 0.0474, 0.5000, 0.9526, 0.9975, 0.9999 };

        checkValues(input, null, k, expected_noPrevOutput, act);
        checkValues(input, 1.0, k, expected_withPrevOutput, act);

        checkValues(input, 1.0, k, expected_withPrevOutput, act);
        
        performTest(act, null, Double.NEGATIVE_INFINITY, 0.0);
        performTest(act, -1.0, Double.NEGATIVE_INFINITY, 0.0);
        performTest(act, null, Double.POSITIVE_INFINITY, 1.0);
        performTest(act, 1.0, Double.POSITIVE_INFINITY, 1.0);

    }

    private void checkValues(double[] input, Double prevOutput, double[] k, double[] expected, SigmoidActivator act) {
        for (int i = 0; i < k.length; i++) {
            for (int j = 0; j < input.length; j++) {
                int idx = j + input.length * i;
                act.setK(k[i]);
                performTest(act, prevOutput, input[j], expected[idx]);
            }
        }
    }

}
