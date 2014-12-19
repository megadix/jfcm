package org.megadix.jfcm.act;

import org.junit.Test;
import org.megadix.jfcm.act.NaryActivator;

public class NaryActivatorTest extends AbstractConceptActivatorTest {

    @Test
    public void testCalculate() {

        NaryActivator act = new NaryActivator();
        act.setN(3);

        performTest(act, null, -2.0, -1.0);
        performTest(act, null, -1.0, -1.0);
        performTest(act, null, -0.34, -1.0 / 3.0);
        performTest(act, null, -0.32, -1.0 / 3.0);
        performTest(act, null, -0.1, 0.0);
        performTest(act, null, 0.0, 0.0);
        performTest(act, null, 0.1, 0.0);
        performTest(act, null, 0.32, 1.0 / 3.0);
        performTest(act, null, 0.34, 1.0 / 3.0);
        performTest(act, null, 1.0, 1.0);
        performTest(act, null, 2.0, 1.0);

    }
}
