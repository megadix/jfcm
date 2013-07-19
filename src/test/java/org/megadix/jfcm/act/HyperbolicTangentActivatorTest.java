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
import org.megadix.jfcm.act.HyperbolicTangentActivator;

public class HyperbolicTangentActivatorTest extends AbstractConceptActivatorTest {

    @Test
    public void test_calculateNextOutputImpl() throws Exception {

        // setup
        HyperbolicTangentActivator act = new HyperbolicTangentActivator();

        // test values

        double[] input = { -2.00, -1.00, -0.50, 0.00, 0.50, 1.00, 2.00 };
        double[] expected_noPrevOutput = { -0.9640, -0.7616, -0.4621, 0.0000, 0.4621, 0.7616, 0.9640 };
        double[] expected_withPrevOutput = { -0.7616, 0.0000, 0.4621, 0.7616, 0.9051, 0.9640, 0.9951 };

        checkValues(input, null, expected_noPrevOutput, act);
        checkValues(input, 1.0, expected_withPrevOutput, act);

        performTest(act, null, Double.NEGATIVE_INFINITY, -1.0);
        performTest(act, -1.0, Double.NEGATIVE_INFINITY, -1.0);
        performTest(act, null, Double.POSITIVE_INFINITY, 1.0);
        performTest(act, 1.0, Double.POSITIVE_INFINITY, 1.0);
    }

}
