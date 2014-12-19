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

/**
 * Connects two concepts, forwarding output from one concept to the other.
 *
 * @author De Franciscis Dimitri - www.megadix.it
 *
 */
public abstract class FcmConnection {

    protected CognitiveMap map;
    protected String name;
    protected String description;
    protected Concept from;
    protected Concept to;
    protected Double output;

    public FcmConnection() {
    }

    public FcmConnection(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void accept(Visitor visitor) {
        visitor.visitConnection(this);
    }

    public void connectOutputTo(Concept c) {
        c.addInputConnection(this);
        this.to = c;
    }

    /**
     * Implementations should calculate output and put the result in the
     * {@link #output} property. Clients interested only in current output
     * should call {@link #getOutput()} instead.
     *
     * @return
     */
    public abstract Double calculateOutput();

    /**
     * Returns the current calculated output, set by {@link #calculateOutput()}
     *
     * @return
     */
    public Double getOutput() {
        return this.output;
    }

    /*
     * simple set/get
     */

    public CognitiveMap getMap() {
        return map;
    }

    public void setMap(CognitiveMap map) {
        this.map = map;
    }

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

    public Concept getFrom() {
        return from;
    }

    public void setFrom(Concept from) {
        this.from = from;
    }

    public Concept getTo() {
        return to;
    }

    public void setTo(Concept to) {
        this.to = to;
    }
}
