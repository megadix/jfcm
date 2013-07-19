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

package org.megadix.jfcm.utils;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.megadix.jfcm.*;
import org.megadix.jfcm.act.SignumActivator;
import org.megadix.jfcm.conn.WeightedConnection;

public class FcmRunnerTest {

    public static CognitiveMap buildTestMap_1() {

        CognitiveMap map = new CognitiveMap("Test map 1");
        ConceptActivator af = new SignumActivator();

        Concept c1 = new Concept("c1", "c1", af, null, null, true);
        map.addConcept(c1);
        assertNotNull(c1.getMap());

        Concept c2 = new Concept("c2", "c2", af, 0.0, 0.0, false);
        map.addConcept(c2);
        assertNotNull(c1.getMap());

        Concept c3 = new Concept("c3", "c3", af, 0.0, 0.0, false);
        map.addConcept(c3);
        assertNotNull(c1.getMap());

        FcmConnection conn_1_2 = new WeightedConnection("c1-c2", "c1-c2", -1.0);
        map.addConnection(conn_1_2);
        assertNotNull(conn_1_2.getMap());

        FcmConnection conn_1_3 = new WeightedConnection("c1-c3", "c1-c3", 0.5);
        map.addConnection(conn_1_3);
        assertNotNull(conn_1_3.getMap());

        FcmConnection conn_2_3 = new WeightedConnection("c2-c3", "c2-c3", -1.0);
        map.addConnection(conn_2_3);
        assertNotNull(conn_2_3.getMap());

        // wire concepts and connections

        map.connect("c1", "c1-c2", "c2");
        map.connect("c1", "c1-c3", "c3");
        map.connect("c2", "c2-c3", "c3");

        // set values

        c1.setOutput(1.0);

        return map;
    }

    @Test
    public void test_converge() {
        CognitiveMap map = buildTestMap_1();
        FcmRunner runner = new SimpleFcmRunner(map, 0.1, 1000);
        runner.converge();

        assertTrue(map.calculateAverageSquareDelta() <= 0.1);
    }

}
