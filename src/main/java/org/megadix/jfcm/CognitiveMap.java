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

import java.util.*;

import org.megadix.jfcm.conn.WeightedConnection;
import org.megadix.jfcm.utils.StringUtils;

/**
 * Represents a fuzzy cognitive map, has methods to build and execute.
 *
 * @author De Franciscis Dimitri - www.megadix.it
 */
public class CognitiveMap implements Cloneable {

    private String name;
    private String description;
    private Map<String, Concept> concepts = new TreeMap<String, Concept>();
    private Map<String, FcmConnection> connections = new TreeMap<String, FcmConnection>();
    private Double averageSquareDelta = null;

    public CognitiveMap() {
    }

    public CognitiveMap(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CognitiveMap other = (CognitiveMap) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    public CognitiveMap copy() {

        CognitiveMap copy = new CognitiveMap(this.name);

        Iterator<Concept> conceptIter = this.getConceptsIterator();
        while (conceptIter.hasNext()) {
            Concept concept = conceptIter.next();
            Concept conceptClone = new Concept(concept);
            copy.addConcept(conceptClone);
        }

        Iterator<FcmConnection> connIter = this.getConnectionsIterator();
        while (connIter.hasNext()) {
            FcmConnection conn = connIter.next();
            // FIXME: supports only WeightedConnection
            if (!WeightedConnection.class.isAssignableFrom(conn.getClass())) {
                throw new UnsupportedOperationException("Unsupported FcmConnection implementation: "
                        + conn.getClass().getName());
            }
            WeightedConnection wConn = (WeightedConnection) conn;
            WeightedConnection connClone = new WeightedConnection(wConn.getName(), wConn.getDescription(),
                    wConn.getWeight());
            copy.addConnection(connClone);
            // re-establish connections
            copy.connect(conn.getFrom().getName(), conn.getName(), conn.getTo().getName());
        }

        return copy;
    }

    public void accept(Visitor visitor) {
        boolean continueVisit = visitor.visitCognitiveMap(this);
        if (!continueVisit) {
            return;
        }

        Iterator<Concept> conceptIter = getConceptsIterator();
        while (conceptIter.hasNext()) {
            Concept concept = conceptIter.next();
            concept.accept(visitor);
        }

        Iterator<FcmConnection> connIter = getConnectionsIterator();
        while (connIter.hasNext()) {
            FcmConnection conn = connIter.next();
            conn.accept(visitor);
        }

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(512);

        sb.append("concepts = [");
        Iterator<Concept> iter = getConceptsIterator();
        while (iter.hasNext()) {
            Concept c = iter.next();
            sb.append("{");
            sb.append(c.toString());
            sb.append("}");
            if (iter.hasNext()) {
                sb.append(",");
            }
        }
        sb.append("]");

        sb.append(", connections = [");
        Iterator<FcmConnection> connIter = getConnectionsIterator();
        while (connIter.hasNext()) {
            FcmConnection conn = connIter.next();
            sb.append("{");
            sb.append(conn.toString());
            sb.append("}");
            if (iter.hasNext()) {
                sb.append(",");
            }
        }
        sb.append("]");

        return sb.toString();
    }

    public void addConcept(Concept c) {
        if (StringUtils.isBlank(c.getName())) {
            throw new IllegalArgumentException("Empty name");
        }
        if (concepts.containsKey(c.getName())) {
            throw new IllegalArgumentException("Concept name \"" + c.getName() + "\" already used");
        }
        c.setMap(this);
        concepts.put(c.getName(), c);
    }

    public void removeConcept(String conceptName) {
        if (StringUtils.isBlank(conceptName)) {
            return;
        }
        Concept c = getConcept(conceptName);
        if (c == null) {
            return;
        }
        for (FcmConnection conn : c.getOutConnections()) {
            conn.setFrom(null);
        }
        for (FcmConnection conn : c.getInConnections()) {
            conn.setTo(null);
        }
        concepts.remove(conceptName);
    }

    public void addConnection(FcmConnection conn) {
        if (StringUtils.isBlank(conn.getName())) {
            throw new IllegalArgumentException("Empty name");
        }
        if (connections.containsKey(conn.getName())) {
            throw new IllegalArgumentException("FcmConnection name \"" + conn.getName() + "\" already used");
        }
        conn.setMap(this);
        connections.put(conn.getName(), conn);
    }

    public void removeConnection(String connectionName) {
        FcmConnection conn = getConnection(connectionName);
        if (conn == null) {
            return;
        }
        if (conn.getFrom() != null) {
            conn.getFrom().removeOutputConnection(conn);
        }
        if (conn.getTo() != null) {
            conn.getTo().removeInputConnection(conn);
        }
        connections.remove(connectionName);
    }

    public void connect(String fromName, String connectionName, String toName) {
        if (StringUtils.isBlank(fromName) || StringUtils.isBlank(connectionName) || StringUtils.isBlank(toName)) {
            throw new IllegalArgumentException();
        }
        Concept from = getConcept(fromName);
        if (from == null) {
            throw new IllegalArgumentException("Concept \"" + fromName + "\" not found");
        }
        FcmConnection conn = getConnection(connectionName);
        if (conn == null) {
            throw new IllegalArgumentException("FcmConnection \"" + connectionName + "\" not found");
        }

        Concept to = getConcept(toName);
        if (to == null) {
            throw new IllegalArgumentException("Concept \"" + toName + "\" not found");
        }

        from.connectOutputTo(conn);
        conn.connectOutputTo(to);
    }

    /**
     * Resets every {@link Concept}, setting {@link Concept#output} to <code>null</code>
     */
    public void reset() {
        Iterator<Concept> iter = getConceptsIterator();
        while (iter.hasNext()) {
            Concept c = iter.next();
            if (!c.isFixedOutput()) {
                c.setOutput(null);
                c.setFixedOutput(false);
            }
            c.setPrevOutput(null);
        }
    }

    public void execute() {
        Iterator<Concept> iter = getConceptsIterator();
        while (iter.hasNext()) {
            Concept c = iter.next();
            c.startUpdate();
        }

        iter = concepts.values().iterator();
        while (iter.hasNext()) {
            Concept c = iter.next();
            c.commitUpdate();
        }
    }

    /**
     * Calculate average square of previous vs current output of {@link Concept Concepts}
     * and stores it in {@link #getAverageSquareDelta()}
     *
     * @return
     */
    public Double calculateAverageSquareDelta() {
        double delta = 0.0;
        int count = 0;
        Iterator<Concept> iter = getConceptsIterator();
        while (iter.hasNext()) {
            Concept concept = iter.next();
            if (concept.getPrevOutput() != null && concept.getOutput() != null) {
                delta += Math.pow(concept.getOutput() - concept.getPrevOutput(), 2);
                count++;
            }
        }

        if (count == 0) {
            return null;
        }

        this.averageSquareDelta = delta / count;

        return averageSquareDelta;
    }

    public Concept getConcept(String conceptName) {
        return concepts.get(conceptName);
    }

    public FcmConnection getConnection(String connectionName) {
        return connections.get(connectionName);
    }

    public Iterator<Concept> getConceptsIterator() {
        return concepts.values().iterator();
    }

    public Iterator<FcmConnection> getConnectionsIterator() {
        return connections.values().iterator();
    }

    public void setOutput(String conceptName, double value) {
        Concept c = getConcept(conceptName);
        c.setOutput(value);
    }

    public void setFixedOutput(String conceptName, double value) {
        Concept c = getConcept(conceptName);
        c.setFixedOutput(true);
        c.setOutput(value);
    }

    /*
     * simple set/get
     */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Concept> getConcepts() {
        return concepts;
    }

    public void setConcepts(Map<String, Concept> concepts) {
        this.concepts = concepts;
    }

    public Map<String, FcmConnection> getConnections() {
        return connections;
    }

    public void setConnections(Map<String, FcmConnection> connections) {
        this.connections = connections;
    }

    /**
     * Average square variation of Concept output, calculated by {@link #calculateAverageSquareDelta()}
     */
    public Double getAverageSquareDelta() {
        return averageSquareDelta;
    }

}
