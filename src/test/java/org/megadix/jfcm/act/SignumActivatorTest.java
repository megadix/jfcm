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
import org.megadix.jfcm.act.SignumActivator;
import org.megadix.jfcm.act.SignumActivator.Mode;

public class SignumActivatorTest extends AbstractConceptActivatorTest {

    @Test
    public void test_calculateNextOutputImpl_noPrevOutput() throws Exception {

        // setup
        SignumActivator act = new SignumActivator();
        act.setIncludePreviousOutput(false);

        act.setMode(Mode.BIPOLAR);
        performTest(act, null, 0.0, 0.0);
        performTest(act, null, 0.1, 1.0);
        performTest(act, null, 1.0, 1.0);
        performTest(act, null, -0.1, -1.0);
        performTest(act, null, -1.0, -1.0);
        performTest(act, null, Double.POSITIVE_INFINITY, 1.0);
        performTest(act, null, Double.NEGATIVE_INFINITY, -1.0);

        act.setMode(Mode.BINARY);
        performTest(act, null, 0.0, 0.0);
        performTest(act, null, 0.1, 1.0);
        performTest(act, null, 1.0, 1.0);
        performTest(act, null, -0.1, 0.0);
        performTest(act, null, -1.0, 0.0);
        performTest(act, null, Double.POSITIVE_INFINITY, 1.0);
        performTest(act, null, Double.NEGATIVE_INFINITY, 0.0);

        // test different zeroValue
        act.setZeroValue(0.5);
        performTest(act, null, 0.0, 0.5);

        // restore default zeroValue
        act.setZeroValue(0.0);

        // === threshold - negative ===

        act.setThreshold(-10.0);

        act.setMode(Mode.BIPOLAR);
        performTest(act, null, -10.0, 0.0);
        performTest(act, null, -9.9, 1.0);
        performTest(act, null, 0.0, 1.0);
        performTest(act, null, 1.0, 1.0);
        performTest(act, null, -10.1, -1.0);
        performTest(act, null, -11.0, -1.0);
        performTest(act, null, Double.POSITIVE_INFINITY, 1.0);
        performTest(act, null, Double.NEGATIVE_INFINITY, -1.0);

        act.setMode(Mode.BINARY);
        performTest(act, null, -10.0, 0.0);
        performTest(act, null, -9.9, 1.0);
        performTest(act, null, 0.0, 1.0);
        performTest(act, null, 1.0, 1.0);
        performTest(act, null, -10.1, 0.0);
        performTest(act, null, -11.0, 0.0);
        performTest(act, null, Double.POSITIVE_INFINITY, 1.0);
        performTest(act, null, Double.NEGATIVE_INFINITY, 0.0);
        
        // === threshold - positive ===

        act.setThreshold(10.0);

        act.setMode(Mode.BIPOLAR);
        performTest(act, null, 10.0, 0.0);
        performTest(act, null, 9.9, -1.0);
        performTest(act, null, 0.0, -1.0);
        performTest(act, null, 1.0, -1.0);
        performTest(act, null, 10.1, 1.0);
        performTest(act, null, 11.0, 1.0);
        performTest(act, null, Double.POSITIVE_INFINITY, 1.0);
        performTest(act, null, Double.NEGATIVE_INFINITY, -1.0);

        act.setMode(Mode.BINARY);
        performTest(act, null, 10.0, 0.0);
        performTest(act, null, 9.9, 0.0);
        performTest(act, null, 0.0, 0.0);
        performTest(act, null, 1.0, 0.0);
        performTest(act, null, 10.1, 1.0);
        performTest(act, null, 11.0, 1.0);
        performTest(act, null, Double.POSITIVE_INFINITY, 1.0);
        performTest(act, null, Double.NEGATIVE_INFINITY, 0.0);

    }

    @Test
    public void test_calculateNextOutputImpl_includePrevOutput() throws Exception {

        // setup
        SignumActivator act = new SignumActivator();
        act.setIncludePreviousOutput(true);

        performTest(act, null, 0.0, 0.0);
        performTest(act, null, 1.0, 1.0);
        performTest(act, null, -1.0, -1.0);

        performTest(act, 1.0, 0.0, 1.0);
        performTest(act, 1.0, -1.0, 0.0);
        performTest(act, -1.0, 0.0, -1.0);
        performTest(act, -1.0, 1.0, 0.0);

        act.setZeroValue(0.5);

        performTest(act, 1.0, -1.0, 0.5);
        performTest(act, -1.0, 1.0, 0.5);

    }

}
