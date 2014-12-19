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

import org.megadix.jfcm.*;

/**
 * Basic implementation of {@link ConceptActivator}, accumulates outputs of
 * incoming connections.
 *
 * @author De Franciscis Dimitri - www.megadix.it
 *
 */
public abstract class BaseConceptActivator implements ConceptActivator {

    public static final double DEFAULT_THRESHOLD = 0.0;
    public static final boolean DEFAULT_INCLUDE_PREVIOUS_OUTPUT = true;

    protected double threshold = DEFAULT_THRESHOLD;
    protected boolean includePreviousOutput = DEFAULT_INCLUDE_PREVIOUS_OUTPUT;

    public BaseConceptActivator() {
    }

    public BaseConceptActivator(double threshold) {
        super();
        this.threshold = threshold;
    }

    public BaseConceptActivator(double threshold, boolean includePreviousOutput) {
        super();
        this.threshold = threshold;
        this.includePreviousOutput = includePreviousOutput;
    }

    public final double getThreshold() {
        return threshold;
    }

    public final void setThreshold(double threshold) {
        if (Double.isInfinite(threshold) || Double.isNaN(threshold)) {
            throw new IllegalArgumentException("Illegal threshold : " + threshold);
        }
        this.threshold = threshold;
    }

    public final boolean isIncludePreviousOutput() {
        return includePreviousOutput;
    }

    /**
     * If set to <code>true</code> calculations should include previous output value,
     * if <code>false</code> just input values, default is <code>true</code>.
     *
     * @param includePreviousOutput
     */
    public final void setIncludePreviousOutput(boolean includePreviousOutput) {
        this.includePreviousOutput = includePreviousOutput;
    }

    /**
     * Accumulates outputs of incoming connections.
     */
    public final void calculateNextOutput(Concept c) {

        c.setPrevOutput(c.getOutput());

        if (!c.isFixedOutput() && c.getInConnections().size() == 0) {
            c.setNextOutput(null);
            return;
        }

        if (c.isFixedOutput()) {
            c.setNextOutput(c.getOutput());
            return;
        }

        double temp = 0.0;
        int count = 0;

        for (FcmConnection conn : c.getInConnections()) {
            conn.calculateOutput();
            Concept from = conn.getFrom();
            // if any of the inputs is not defined...
            if (from.getOutput() == null || from.getOutput().isNaN()) {
                // skip
                continue;

            } else if (from.getOutput().isInfinite()) {
                // set output tu NaN and return
                c.setNextOutput(Double.NaN);
                return;
            }

            Double connOutput = conn.getOutput();
            if (connOutput != null) {
                temp += connOutput;
                count++;
            }
        }

        // Concept input = sum
        if (count == 0) {
            c.setInput(null);
        } else {
            c.setInput(temp);
        }

        if (c.getInput() == null) {
            // set output to null and return
            c.setNextOutput(null);
            return;
        }

        if (c.getInput().isNaN()) {
            // set output tu NaN and return
            c.setNextOutput(Double.NaN);
            return;
        }

        c.setNextOutput(calculateNextOutputImpl(c));
    }

    /**
     * Calculate next Concept output, returning it as double.
     * Concept input can be rtetrieved using {@link Concept#getInput()}, and code
     * SHOULD NOT call {@link Concept#setNextOutput(Double)} to set Concept output.
     * Also note that at this point all edge conditions have been already checked:
     * fixedOutput, zero connections, infinite/null/NaN input configurations, etc.
     * This method is called by {@link #calculateNextOutput(Concept)}.
     *
     * @param c concept to activate
     * @return new value passed to {@link Concept#setNextOutput(Double)}
     */
    protected abstract double calculateNextOutputImpl(Concept c);

    /**
     * Default implementation, calls <code>visitor.visitConceptActivator(this);</code>
     */
    public void accept(Visitor visitor) {
        visitor.visitConceptActivator(this);
    }
}
