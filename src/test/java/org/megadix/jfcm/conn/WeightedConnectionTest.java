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

package org.megadix.jfcm.conn;

import static org.junit.Assert.*;

import org.junit.Test;
import org.megadix.jfcm.Concept;
import org.megadix.jfcm.FcmConnection;
import org.megadix.jfcm.act.LinearActivator;
import org.megadix.jfcm.act.SignumActivator;
import org.megadix.jfcm.conn.WeightedConnection;

public class WeightedConnectionTest {

    @Test
    public void test_Constructor() {
        WeightedConnection conn = new WeightedConnection("name", "desc", 1.1);
        assertEquals("name", conn.getName());
        assertEquals("desc", conn.getDescription());
        assertEquals(1.1, conn.getWeight(), 0.0);
    }

    @Test
    public void test_calculateOutput_noDelay() throws Exception {
        Concept c1 = new Concept("c1", null, null, null, -1.0, true);
        Concept c2 = new Concept("c2", null, new SignumActivator(), null, 1.0, false);
        FcmConnection conn = new WeightedConnection("c1-c2", null, 1.0);
        c1.connectOutputTo(conn);
        conn.connectOutputTo(c2);

        assertNull(conn.getOutput());

        conn.calculateOutput();

        assertNotNull(conn.getOutput());
    }

    @Test
    public void test_calculateOutput_delay() throws Exception {
        WeightedConnection delay_0 = new WeightedConnection("delay_0", null, 1.0, 0);
        WeightedConnection delay_1 = new WeightedConnection("delay_1", null, 1.0, 1);
        WeightedConnection delay_10 = new WeightedConnection("delay_10", null, 1.0, 10);

        // source concept
        Concept c1 = new Concept("c1", null, new LinearActivator(), null, 1.0, true);
        // destination concepts
        LinearActivator act = new LinearActivator();

        Concept c2 = new Concept("c2", null, act, null, null, false);
        Concept c3 = new Concept("c3", null, act, null, null, false);
        Concept c4 = new Concept("c4", null, act, null, null, false);

        // wire up everything
        c1.connectOutputTo(delay_0);
        delay_0.connectOutputTo(c2);

        c1.connectOutputTo(delay_1);
        delay_1.connectOutputTo(c3);

        c1.connectOutputTo(delay_10);
        delay_10.connectOutputTo(c4);

        // startup check

        assertNull(delay_0.getOutput());
        assertNull(delay_1.getOutput());
        assertNull(delay_10.getOutput());

        // update #1

        delay_0.calculateOutput();
        delay_1.calculateOutput();
        delay_10.calculateOutput();

        assertNotNull(delay_0.getOutput());
        assertNull(delay_1.getOutput());
        assertNull(delay_10.getOutput());

        // update #2

        delay_0.calculateOutput();
        delay_1.calculateOutput();
        delay_10.calculateOutput();

        assertNotNull(delay_0.getOutput());
        assertNotNull(delay_1.getOutput());
        assertNull(delay_10.getOutput());

        // do 9 more updates
        for (int i = 0; i < 8; i++) {
            delay_0.calculateOutput();
            delay_1.calculateOutput();
            delay_10.calculateOutput();

            assertNotNull(delay_0.getOutput());
            assertNotNull(delay_1.getOutput());
            assertNull(delay_10.getOutput());
        }

        delay_0.calculateOutput();
        delay_1.calculateOutput();
        delay_10.calculateOutput();

        assertNotNull(delay_0.getOutput());
        assertNotNull(delay_1.getOutput());
        assertNotNull(delay_10.getOutput());
    }

    @Test
    public void test_calculateOutput_null() throws Exception {
        Concept c1 = new Concept("c1", null, null, null, null, true);
        Concept c2 = new Concept("c2", null, null, null, null, false);
        FcmConnection conn = new WeightedConnection("c1-c2", null, 1.0);
        c1.connectOutputTo(conn);
        conn.connectOutputTo(c2);

        assertNull(conn.getOutput());

        conn.calculateOutput();

        assertNull(conn.getOutput());
    }

}
