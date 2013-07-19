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

package org.megadix.jfcm;

import static org.junit.Assert.*;

import org.junit.Test;
import org.megadix.jfcm.act.SignumActivator;
import org.megadix.jfcm.conn.WeightedConnection;

public class ConceptTest {

    @Test
    public void test_Concept() throws Exception {
        Concept c = new Concept("name", "desc", new SignumActivator(), 1.0, 2.0, true);
        assertEquals("name", c.getName());
        assertEquals("desc", c.getDescription());
        assertTrue(c.getConceptActivator() instanceof SignumActivator);
        assertEquals(1.0, c.getInput().doubleValue(), 0.0);
        assertEquals(2.0, c.getOutput().doubleValue(), 0.0);
        assertTrue(c.isFixedOutput());
    }

    @Test
    public void test_CopyConstructor() throws Exception {
        ConceptActivator act = new SignumActivator();

        Concept c = new Concept();
        c.setName("name");
        c.setDescription("description");
        c.setConceptActivator(act);
        c.setInput(-666.0);
        c.setFixedOutput(true);
        c.setOutput(1.1);
        c.setNextOutput(2.2);
        c.setPrevOutput(3.3);

        Concept c2 = new Concept(c);

        assertEquals("name", c2.getName());
        assertEquals("description", c2.getDescription());
        assertSame(act, c2.getConceptActivator());
        assertEquals(-666.0, c2.getInput().doubleValue(), 0.0);
        assertTrue(c2.isFixedOutput());
        assertEquals(1.1, c2.getOutput().doubleValue(), 0.0);
        assertEquals(2.2, c2.getNextOutput().doubleValue(), 0.0);
        assertEquals(3.3, c2.getPrevOutput().doubleValue(), 0.0);
    }

    @Test
    public void test_equals_hashCode() {
        Concept cA = new Concept("A", null);
        Concept cA_bis = new Concept("A", null);
        Concept cB = new Concept("B", null);

        assertTrue(cA.equals(cA_bis));
        assertTrue(cA.hashCode() == cA_bis.hashCode());

        assertFalse(cA.equals(cB));
        assertFalse(cA.hashCode() == cB.hashCode());
    }

    @Test
    public void test_update() {
        Concept c1 = new Concept("c1", null, new SignumActivator(), null, 1.0, false);
        Concept c2 = new Concept("c2", null, new SignumActivator(), null, null, false);

        FcmConnection conn_1_2 = new WeightedConnection("c1-c2", null, 1.0);
        c1.connectOutputTo(conn_1_2);
        conn_1_2.connectOutputTo(c2);

        FcmConnection conn_2_1 = new WeightedConnection("c2-c1", null, 1.0);
        c2.connectOutputTo(conn_2_1);
        conn_2_1.connectOutputTo(c1);

        c2.startUpdate();

        assertEquals(1.0, c1.getOutput().doubleValue(), 0.0);
        assertEquals(1.0, c2.getNextOutput().doubleValue(), 0.0);
        assertNull(c2.getOutput());

        c2.commitUpdate();

        assertEquals(1.0, c1.getOutput().doubleValue(), 0.0);
        assertEquals(1.0, c2.getNextOutput().doubleValue(), 0.0);
        assertEquals(1.0, c2.getOutput().doubleValue(), 0.0);

        // check retroaction
        c1.setOutput(null);

        c1.startUpdate();
        assertNull(c1.getOutput());

        c1.commitUpdate();
        assertEquals(1.0, c1.getOutput().doubleValue(), 0.0);
        assertEquals(1.0, c2.getOutput().doubleValue(), 0.0);
    }

    @Test
    public void test_update_fixed() {
        Concept c1 = new Concept("c1", null, new SignumActivator(), null, 1.0, false);
        Concept c2 = new Concept("c2", null, new SignumActivator(), null, 1.1, true);
        FcmConnection conn_1_2 = new WeightedConnection("c1-c2", null, 1.0);
        c1.connectOutputTo(conn_1_2);
        conn_1_2.connectOutputTo(c2);

        FcmConnection conn_2_1 = new WeightedConnection("c2-c1", null, 1.0);
        c2.connectOutputTo(conn_2_1);
        conn_2_1.connectOutputTo(c1);

        c2.startUpdate();
        assertEquals(1.1, c2.getNextOutput().doubleValue(), 0.0);
        assertEquals(1.1, c2.getOutput().doubleValue(), 0.0);

        c2.commitUpdate();
        assertEquals(1.1, c2.getNextOutput().doubleValue(), 0.0);
        assertEquals(1.1, c2.getOutput().doubleValue(), 0.0);
    }

    @Test
    public void test_prevOutput() {
        Concept c1 = new Concept("c1", null, null, null, -1.0, true);
        Concept c2 = new Concept("c2", null, new SignumActivator(), null, 1.0, false);
        FcmConnection conn = new WeightedConnection("c1-c2", null, 1.0);
        c1.connectOutputTo(conn);
        conn.connectOutputTo(c2);

        c2.startUpdate();
        c2.commitUpdate();

        assertEquals(0.0, c2.getOutput().doubleValue(), 0.0);
        assertEquals(1.0, c2.getPrevOutput().doubleValue(), 0.0);

        c2.startUpdate();
        c2.commitUpdate();

        assertEquals(-1.0, c2.getOutput().doubleValue(), 0.0);
        assertEquals(0.0, c2.getPrevOutput().doubleValue(), 0.0);

    }

    @Test
    public void test_startUpdate_no_activator() {
        Concept c = new Concept();
        c.setOutput(111.0);
        c.setPrevOutput(222.0);
        c.setNextOutput(222.0);

        c.startUpdate();

        assertEquals(111.0, c.getPrevOutput(), 0.0);
        assertEquals(111.0, c.getOutput(), 0.0);
        assertEquals(111.0, c.getNextOutput(), 0.0);
        assertNull(c.getInput());
    }

    @Test
    public void test_toString() throws Exception {
        Concept c = new Concept("c1", null, new SignumActivator(), -123.0, 456.0, true);
        assertEquals(
                "[c1] act = " + SignumActivator.class.getName() + ", input = -123.0, output = 456.0, fixed = true",
                c.toString());
    }

    @Test
    public void test_accept() {

        BaseVisitor visitor = new BaseVisitor() {
        };

        Concept concept = new Concept();
        concept.setConceptActivator(new SignumActivator());

        concept.accept(visitor);

        assertNotNull(visitor.concept);
        assertNotNull(visitor.activator);
    }
}
