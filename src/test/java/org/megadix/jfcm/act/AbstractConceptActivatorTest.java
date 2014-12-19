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

import static org.junit.Assert.assertEquals;

import org.megadix.jfcm.Concept;
import org.megadix.jfcm.act.BaseConceptActivator;

/**
 * Base abstract class for testing {@link BaseConceptActivator} implementations.
 */
public abstract class AbstractConceptActivatorTest {

    protected void performTest(BaseConceptActivator act, Double prevOutput, Double input, Double expected) {
        Concept c = new Concept();
        c.setConceptActivator(act);
        c.setOutput(prevOutput);
        c.setInput(input);

        double result = act.calculateNextOutputImpl(c);
        assertEquals(expected.doubleValue(), result, 0.0001);
    }

    protected void checkValues(double[] input, Double prevOutput, double[] expected, BaseConceptActivator act) {
        for (int i = 0; i < input.length; i++) {
            performTest(act, prevOutput, input[i], expected[i]);
        }
    }
}
