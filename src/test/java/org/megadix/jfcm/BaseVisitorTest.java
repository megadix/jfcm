package org.megadix.jfcm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class BaseVisitorTest {

    public static class DummyVisitor extends BaseVisitor {
        List<Concept> concepts = new ArrayList<Concept>();
        List<FcmConnection> connections = new ArrayList<FcmConnection>();
        List<ConceptActivator> activators = new ArrayList<ConceptActivator>();

        @Override
        public boolean visitConcept(Concept concept) {
            super.visitConcept(concept);
            concepts.add(concept);
            return true;
        }

        @Override
        public boolean visitConnection(FcmConnection connection) {
            super.visitConnection(connection);
            connections.add(connection);
            return true;
        }

        @Override
        public boolean visitConceptActivator(ConceptActivator activator) {
            super.visitConceptActivator(activator);
            activators.add(activator);
            return true;
        }

    }

    CognitiveMap map = CognitiveMapTest.buildTestMap_1();

    @Test
    public void test_visit() {
        DummyVisitor visitor = new DummyVisitor();
        map.accept(visitor);

        assertNotNull(visitor.map);

        assertNotNull(visitor.concept);
        assertEquals(4, visitor.concepts.size());

        assertNotNull(visitor.connection);
        assertEquals(4, visitor.connections.size());

        assertNotNull(visitor.activator);
        assertEquals(4, visitor.activators.size());
    }

}
