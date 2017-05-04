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

/**
 * Represents a single concept in the cognitive map.
 *
 * @author De Franciscis Dimitri - www.megadix.it
 *
 */
public class Concept {

    protected String name;
    protected String description;
    protected ConceptActivator conceptActivator;
    protected Double input;
    protected Double prevOutput;
    protected Double output;
    protected Double nextOutput;
    protected boolean fixedOutput = false;

    protected CognitiveMap map;
    protected Set<FcmConnection> outConnections = new HashSet<>();
    protected Set<FcmConnection> inConnections = new HashSet<>();

    public Concept() {
    }

    public Concept(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Concept(String name, String description, ConceptActivator conceptActivator, Double input, Double output,
            boolean fixedOutput) {
        this(name, description);
        this.conceptActivator = conceptActivator;
        this.input = input;
        this.output = output;
        this.fixedOutput = fixedOutput;
    }

    public void accept(Visitor visitor) {
        boolean continueVisit = visitor.visitConcept(this);
        if (! continueVisit) {
            return;
        }

        if (conceptActivator != null) {
            conceptActivator.accept(visitor);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(256);
        sb.append("[");
        sb.append(name);
        sb.append("] act = ");
        sb.append(conceptActivator != null ? conceptActivator.getClass().getName() : "null");
        sb.append(", input = ");
        sb.append(input != null ? input : "null");
        sb.append(", output = ");
        sb.append(output != null ? output : "null");
        sb.append(", fixed = ");
        sb.append(Boolean.toString(fixedOutput));

        return sb.toString();
    }

    /**
     * Shallow copy constructor, without copying {@link #getMap()},
     * {@link #getOutConnections()}, {@link #getInConnections()}
     * @param c concept to copy properties from
     */
    public Concept(Concept c) {
        this.name = c.name;
        this.description = c.description;
        this.conceptActivator = c.conceptActivator;
        this.input = c.input;
        this.prevOutput = c.prevOutput;
        this.output = c.output;
        this.nextOutput = c.nextOutput;
        this.fixedOutput = c.fixedOutput;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        Concept other = (Concept) obj;

        return name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * Phase 1 of update: calculate {@link #nextOutput} invoking {@link ConceptActivator#calculateNextOutput(Concept)}
     */
    public void startUpdate() {
        if (conceptActivator == null) {
            prevOutput = output;
            nextOutput = output;
            return;
        }
        conceptActivator.calculateNextOutput(this);
    }

    /**
     * Phase 2 of update: copy {@link #nextOutput} to {@link #output}
     */
    public void commitUpdate() {
        output = nextOutput;
    }

    public void addInputConnection(FcmConnection conn) {
        this.inConnections.add(conn);
    }

    public void removeInputConnection(FcmConnection conn) {
        this.inConnections.remove(conn);
    }

    public void connectOutputTo(FcmConnection conn) {
        if (conn.getFrom() != null) {
            conn.getFrom().removeOutputConnection(conn);
        }
        conn.setFrom(this);
        this.outConnections.add(conn);
    }

    public void removeOutputConnection(FcmConnection conn) {
        this.outConnections.remove(conn);
    }

    public Set<FcmConnection> getOutConnections() {
        return Collections.unmodifiableSet(outConnections);
    }

    public Set<FcmConnection> getInConnections() {
        return Collections.unmodifiableSet(inConnections);
    }

    /*
     * simple set / get
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

    public ConceptActivator getConceptActivator() {
        return conceptActivator;
    }

    public void setConceptActivator(ConceptActivator conceptActivator) {
        this.conceptActivator = conceptActivator;
    }

    public CognitiveMap getMap() {
        return map;
    }

    public void setMap(CognitiveMap map) {
        this.map = map;
    }

    public Double getInput() {
        return input;
    }

    public void setInput(Double input) {
        this.input = input;
    }

    public Double getPrevOutput() {
        return prevOutput;
    }

    public void setPrevOutput(Double prevOutput) {
        this.prevOutput = prevOutput;
    }

    public Double getOutput() {
        return output;
    }

    public void setOutput(Double output) {
        this.output = output;
    }

    public Double getNextOutput() {
        return nextOutput;
    }

    public void setNextOutput(Double nextOutput) {
        this.nextOutput = nextOutput;
    }

    public boolean isFixedOutput() {
        return fixedOutput;
    }

    public void setFixedOutput(boolean fixedOutput) {
        this.fixedOutput = fixedOutput;
    }
}
