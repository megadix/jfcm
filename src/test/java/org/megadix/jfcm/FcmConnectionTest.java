package org.megadix.jfcm;

import static org.junit.Assert.*;

import org.junit.Test;

public class FcmConnectionTest {

    @Test
    public void test_accept() {

        BaseVisitor visitor = new BaseVisitor() {
        };

        FcmConnection conn = new FcmConnection() {
            @Override
            public Double calculateOutput() {
                return null;
            }
        };

        conn.accept(visitor);

        assertNotNull(visitor.connection);
    }
}
