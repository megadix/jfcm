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

import java.util.Iterator;

import org.junit.Test;
import org.megadix.jfcm.act.SignumActivator;
import org.megadix.jfcm.conn.WeightedConnection;

public class CognitiveMapTest {

    @Test
    public void test_equals_hashCode() {
        CognitiveMap map_1 = new CognitiveMap("1");
        CognitiveMap map_1_bis = new CognitiveMap("1");
        CognitiveMap map_2 = new CognitiveMap("2");

        assertTrue(map_1.equals(map_1_bis));
        assertTrue(map_1.hashCode() == map_1_bis.hashCode());

        assertFalse(map_1.equals(map_2));
        assertFalse(map_1.hashCode() == map_2.hashCode());

    }

    public static CognitiveMap buildTestMap_1() {

        CognitiveMap map = new CognitiveMap("Test map 1");
        ConceptActivator af = new SignumActivator();

        Concept c1 = new Concept("c1", "c1", af, 0.0, 666.0, true);
        map.addConcept(c1);
        assertNotNull(c1.getMap());

        Concept c2 = new Concept("c2", "c2", af, 0.0, 0.0, false);
        map.addConcept(c2);
        assertNotNull(c1.getMap());

        Concept c3 = new Concept("c3", "c3", af, 0.0, 0.0, false);
        map.addConcept(c3);
        assertNotNull(c1.getMap());

        Concept c4 = new Concept("c4", "c4", af, 0.0, 0.0, false);
        map.addConcept(c4);
        assertNotNull(c1.getMap());

        FcmConnection conn_1_2 = new WeightedConnection("FcmConnection 1-2", "FcmConnection 1-2", -0.8);
        map.addConnection(conn_1_2);
        assertNotNull(conn_1_2.getMap());

        FcmConnection conn_2_3 = new WeightedConnection("FcmConnection 2-3", "FcmConnection 2-3", 1.0);
        map.addConnection(conn_2_3);
        assertNotNull(conn_2_3.getMap());

        FcmConnection conn_3_4 = new WeightedConnection("FcmConnection 3-4", "FcmConnection 3-4", 0.9);
        map.addConnection(conn_3_4);
        assertNotNull(conn_3_4.getMap());

        FcmConnection conn_4_1 = new WeightedConnection("FcmConnection 4-1", "FcmConnection 4-1", 1.0);
        map.addConnection(conn_4_1);
        assertNotNull(conn_4_1.getMap());

        // wire concepts and connections

        map.connect("c1", "FcmConnection 1-2", "c2");
        map.connect("c2", "FcmConnection 2-3", "c3");
        map.connect("c3", "FcmConnection 3-4", "c4");
        map.connect("c4", "FcmConnection 4-1", "c1");

        return map;
    }

    @Test
    public void test_copy() {
        CognitiveMap map_1 = buildTestMap_1();
        CognitiveMap copy = map_1.copy();
        checkTestMap_1(copy);
    }

    @Test
    public void test_addConcept() throws Exception {
        CognitiveMap map = new CognitiveMap("Test map 1");

        Concept c1 = new Concept("c1", "c1", null, 0.0, 0.0, false);
        map.addConcept(c1);
        Concept c2 = new Concept("c1", "c1 duplicate name", null, 0.0, 0.0, false);
        try {
            map.addConcept(c2);
            fail("should fail");
        } catch (IllegalArgumentException iae) {
            // OK
        }
        try {
            c2.setName(null);
            map.addConcept(c2);
            fail("should fail");
        } catch (IllegalArgumentException iae) {
            // OK
        }
        try {
            c2.setName("");
            map.addConcept(c2);
            fail("should fail");
        } catch (IllegalArgumentException iae) {
            // OK
        }
    }

    @Test
    public void test_addConnection() throws Exception {
        CognitiveMap map = new CognitiveMap("Test map 1");

        Concept c1 = new Concept("c1", "c1", null, 0.0, 0.0, false);
        map.addConcept(c1);
        Concept c2 = new Concept("c2", "c2", null, 0.0, 0.0, false);
        map.addConcept(c2);
        Concept c3 = new Concept("c3", "c3", null, 0.0, 0.0, false);
        map.addConcept(c3);

        FcmConnection conn_1_2 = new WeightedConnection("1-2", "1-2", 0.0);
        map.addConnection(conn_1_2);

        FcmConnection temp_conn = new WeightedConnection("1-2", "1-2", 0.0);
        try {
            map.addConnection(temp_conn);
            fail("should fail");
        } catch (IllegalArgumentException e) {
            // ok
        }

        try {
            temp_conn.setName(null);
            map.addConnection(temp_conn);
            fail("should fail");
        } catch (IllegalArgumentException e) {
            // ok
        }

        try {
            temp_conn.setName("");
            map.addConnection(temp_conn);
            fail("should fail");
        } catch (IllegalArgumentException e) {
            // ok
        }
    }

    @Test
    public void test_connect() throws Exception {
        CognitiveMap map = new CognitiveMap("Test map 1");

        Concept c1 = new Concept("c1", "c1", null, 0.0, 0.0, false);
        map.addConcept(c1);
        Concept c2 = new Concept("c2", "c2", null, 0.0, 0.0, false);
        map.addConcept(c2);

        FcmConnection conn_1_2 = new WeightedConnection("1-2", "FcmConnection 1-2", -0.8);
        map.addConnection(conn_1_2);

        try {
            map.connect("nonexistent", "1-2", "c2");
            fail("should fail");
        } catch (IllegalArgumentException e) {
            // ok
        }

        try {
            map.connect("", "1-2", "c2");
            fail("should fail");
        } catch (IllegalArgumentException e) {
            // ok
        }

        try {
            map.connect(null, "1-2", "c2");
            fail("should fail");
        } catch (IllegalArgumentException e) {
            // ok
        }

        try {
            map.connect("c1", "nonexistent", "c2");
            fail("should fail");
        } catch (IllegalArgumentException e) {
            // ok
        }

        try {
            map.connect("c1", "", "c2");
            fail("should fail");
        } catch (IllegalArgumentException e) {
            // ok
        }

        try {
            map.connect("c1", null, "c2");
            fail("should fail");
        } catch (IllegalArgumentException e) {
            // ok
        }

        try {
            map.connect("c1", "1-2", "nonexistent");
            fail("should fail");
        } catch (IllegalArgumentException e) {
            // ok
        }

        try {
            map.connect("c1", "1-2", "");
            fail("should fail");
        } catch (IllegalArgumentException e) {
            // ok
        }

        try {
            map.connect("c1", "1-2", null);
            fail("should fail");
        } catch (IllegalArgumentException e) {
            // OK
        }
    }

    @Test
    public void test_build() {
        CognitiveMap map = buildTestMap_1();
        checkTestMap_1(map);
    }

    private void checkTestMap_1(CognitiveMap map) {
        assertEquals(4, map.getConcepts().size());
        assertEquals(1, map.getConcept("c1").getInConnections().size());
        assertEquals(4, map.getConnections().size());

        checkConnection(map, "c1", "FcmConnection 1-2", "c2");
        checkConnection(map, "c2", "FcmConnection 2-3", "c3");
        checkConnection(map, "c3", "FcmConnection 3-4", "c4");
    }

    @Test
    public void test_execute_simple() {
        CognitiveMap map = buildTestMap_1();
        map.execute();
    }

    @Test
    public void test_reset() {
        CognitiveMap map = buildTestMap_1();

        map.reset();

        Iterator<Concept> iter = map.getConceptsIterator();
        while (iter.hasNext()) {
            Concept c = iter.next();
            if (!c.isFixedOutput()) {
                assertNull(c.getOutput());
                assertFalse(c.isFixedOutput());
            }
            assertNull(c.getPrevOutput());
        }

        assertEquals(666.0, map.getConcept("c1").getOutput(), 0.0);
    }

    @Test
    public void test_remove() throws Exception {
        CognitiveMap map = buildTestMap_1();
        map.removeConcept(null);
        map.removeConcept("");
        map.removeConcept("c1");
        map.removeConcept("nonexistent");
        assertNull(map.getConnection("FcmConnection 1-2").getFrom());

        map.removeConnection("FcmConnection 3-4");
        map.removeConnection("nonexistent");

        assertEquals(3, map.getConcepts().size());
        assertEquals(0, map.getConcept("c3").getOutConnections().size());
        assertEquals(3, map.getConnections().size());
        assertEquals(0, map.getConcept("c4").getInConnections().size());
        assertNull(map.getConnection("FcmConnection 4-1").getTo());

        checkConnection(map, "c2", "FcmConnection 2-3", "c3");

        FcmConnection con_1_2 = map.getConnection("FcmConnection 1-2");
        con_1_2.setFrom(null);
        con_1_2.setTo(null);
        map.removeConnection("FcmConnection 1-2");
    }

    @Test
    public void test_calculateAverageSquareDelta() throws Exception {
        CognitiveMap map = buildTestMap_1();
        Double delta = map.calculateAverageSquareDelta();
        assertNull(delta);

        map.execute();

        delta = map.calculateAverageSquareDelta();
        assertEquals(0.25, delta, 0.0);
    }

    @Test
    public void test_setFixedOutput() throws Exception {
        CognitiveMap map = buildTestMap_1();
        map.setFixedOutput("c1", 1.23);

        Concept c1 = map.getConcept("c1");
        assertTrue(c1.isFixedOutput());
        assertEquals(1.23, c1.getOutput(), 0.0);
    }

    @Test
    public void test_toString() {
        CognitiveMap map = buildTestMap_1();
        String s = map.toString();
        assertTrue(s != null && s.length() > 0);
    }

    @Test
    public void test_accept() {
        BaseVisitor visitor = new BaseVisitor() {
        };

        CognitiveMap map = buildTestMap_1();
        map.accept(visitor);

        assertNotNull(visitor.map);
    }

    /*
     * private stuff
     */
    private void checkConnection(CognitiveMap map, String fromName, String connName, String toName) {
        Concept from = map.getConcept(fromName);
        FcmConnection conn = map.getConnection(connName);
        Concept to = map.getConcept(toName);

        assertEquals(from.getName(), conn.getFrom().getName());
        assertEquals(to.getName(), conn.getTo().getName());

        boolean check = false;
        for (FcmConnection tempConn : from.getOutConnections()) {
            if (connName.equals(conn.getName())) {
                assertEquals(from.getName(), tempConn.getFrom().getName());
                check = true;
                break;
            }
        }
        assertTrue(check);

        check = false;
        for (FcmConnection tempConn : to.getInConnections()) {
            if (connName.equals(tempConn.getName())) {
                assertEquals(to.getName(), tempConn.getTo().getName());
                check = true;
                break;
            }
        }
    }

}
