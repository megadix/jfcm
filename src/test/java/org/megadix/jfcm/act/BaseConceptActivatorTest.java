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

import static org.junit.Assert.*;

import org.junit.Test;
import org.megadix.jfcm.*;
import org.megadix.jfcm.act.BaseConceptActivator;
import org.megadix.jfcm.conn.WeightedConnection;

public class BaseConceptActivatorTest {

    static class DummyConceptActivator extends BaseConceptActivator {
        @Override
        protected double calculateNextOutputImpl(Concept c) {
            return c.getInput();
        }
    }

    @Test
    public void testActivate() {

        // setup
        BaseConceptActivator act = new DummyConceptActivator();
        FcmConnection con_1_3, con_2_3;

        // threshold = 0.0 (default)
        Concept c1 = new Concept("c1", null, null, null, 0.0, true);
        Concept c2 = new Concept("c2", null, null, null, 0.0, true);
        Concept c3 = new Concept("c3", null, act, null, 0.0, false);

        c1.setOutput(1.0);
        c2.setOutput(3.0);
        c3.startUpdate();

        assertNull(c3.getNextOutput());

        con_1_3 = new WeightedConnection("1-3", null, 1.0);
        c1.connectOutputTo(con_1_3);
        con_1_3.connectOutputTo(c3);

        con_2_3 = new WeightedConnection("2-3", null, 1.0);
        c2.connectOutputTo(con_2_3);
        con_2_3.connectOutputTo(c3);

        c3.startUpdate();

        assertNotNull(c3.getNextOutput());
        assertEquals(4.0, c3.getNextOutput(), 0.0);

        c3.setFixedOutput(true);
        c3.setOutput(null);
        c3.startUpdate();

        assertNull(c3.getNextOutput());
        assertNull(c3.getOutput());

        c3.setOutput(1.123);
        c3.startUpdate();

        assertEquals(1.123, c3.getNextOutput(), 0.0);
        assertEquals(1.123, c3.getOutput(), 0.0);

        // try to overwrite...
        c3.setNextOutput(100.0);
        c3.startUpdate();

        // ... with no luck :)
        assertEquals(1.123, c3.getNextOutput(), 0.0);
        assertEquals(1.123, c3.getOutput(), 0.0);

    }

    @Test
    public void testActivate_delay() {

        // setup
        BaseConceptActivator act = new DummyConceptActivator();
        FcmConnection con_1_2;

        // threshold = 0.0 (default)
        Concept c1 = new Concept("c1", null, null, null, 1.0, true);
        Concept c2 = new Concept("c2", null, act, null, 0.0, false);

        con_1_2 = new WeightedConnection("1-2", null, 1.0, 1);
        c1.connectOutputTo(con_1_2);
        con_1_2.connectOutputTo(c2);

        // null test

        c2.startUpdate();
        assertNull(c2.getNextOutput());

        // NaN test
        c1.setOutput(Double.NaN);
        c2.startUpdate();
        assertNull(c2.getNextOutput());

    }

    @Test
    public void testActivate_extremeConditions() {
        // setup
        BaseConceptActivator act = new DummyConceptActivator();

        Concept c1 = new Concept("c1", null, null, null, 1.0, true);
        Concept c2 = new Concept("c2", null, act, null, 1.0, true);
        Concept c3 = new Concept("c2", null, act, null, 0.0, false);

        WeightedConnection con_1_3 = new WeightedConnection("1-3", null, 1.0);
        c1.connectOutputTo(con_1_3);
        con_1_3.connectOutputTo(c3);

        WeightedConnection con_2_3 = new WeightedConnection("2-3", null, 1.0);
        c2.connectOutputTo(con_2_3);
        con_2_3.connectOutputTo(c3);

        // Test: c1 output = null
        c1.setOutput(null);
        c3.startUpdate();
        assertEquals(1.0, c3.getNextOutput().doubleValue(), 0.0);

        // Test: c1 output = NaN
        c1.setOutput(Double.NaN);
        c3.startUpdate();
        assertEquals(1.0, c3.getNextOutput().doubleValue(), 0.0);

        // Test: c1 output = positive infinity
        c1.setOutput(Double.POSITIVE_INFINITY);
        c3.startUpdate();
        assertTrue(c3.getNextOutput().isNaN());

        // Test: c1 output = negative infinity
        c1.setOutput(Double.NEGATIVE_INFINITY);
        c3.startUpdate();
        assertTrue(c3.getNextOutput().isNaN());

    }

    @Test
    public void test_setThreshold() {
        BaseConceptActivator act = new DummyConceptActivator();

        try {
            act.setThreshold(Double.POSITIVE_INFINITY);
            fail("should fail!");
        } catch (IllegalArgumentException iaex) {
            // OK
        }
        try {
            act.setThreshold(Double.NEGATIVE_INFINITY);
            fail("should fail!");
        } catch (IllegalArgumentException iaex) {
            // OK
        }
        try {
            act.setThreshold(Double.NaN);
            fail("should fail!");
        } catch (IllegalArgumentException iaex) {
            // OK
        }
    }

    @Test
    public void test_accept() {

        class TestVisitor extends BaseVisitor {
            // expose variable just for testing
            ConceptActivator getActivator() {
                return activator;
            }
        }

        TestVisitor visitor = new TestVisitor();

        BaseConceptActivator act = new BaseConceptActivator() {
            @Override
            protected double calculateNextOutputImpl(Concept c) {
                return 0;
            }
        };

        act.accept(visitor);

        assertNotNull(visitor.getActivator());
    }
}
