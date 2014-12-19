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
import org.megadix.jfcm.act.LinearActivator;

public class LinearConceptActivatorTest extends AbstractConceptActivatorTest {

    @Test
    public void test_calculateNextOutputImpl() throws Exception {
        LinearActivator act = new LinearActivator();

        double[] input = { -10.0, -1.0, 0.0, 1.0, 10.0 };
        double[] expected_noPrevOutput = { -10.0, -1.0, 0.0, 1.0, 10.0 };
        double[] expected_withPrevOutput = { 0.0, 9.0, 10.0, 11.0, 20.0 };

        checkValues(input, null, expected_noPrevOutput, act);
        checkValues(input, 10.0, expected_withPrevOutput, act);

        performTest(act, null, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
        performTest(act, null, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

        performTest(act, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
        performTest(act, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NaN);

        performTest(act, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        performTest(act, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NaN);

    }

}
