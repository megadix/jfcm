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

import org.megadix.jfcm.Concept;

/**
 * n-ary activator, similar to {@link LinearActivator}, but approximates output
 * using <em>n</em> values, equally distributed between -1.0 and + 1.0
 */
public class NaryActivator extends BaseConceptActivator {

    public static final int DEFAULT_N = 2;

    private int n = DEFAULT_N;

    public NaryActivator() {
    }

    public NaryActivator(double threshold, boolean includePreviousOutput, int n) {
        super(threshold, includePreviousOutput);
        this.n = n;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    @Override
    protected double calculateNextOutputImpl(Concept c) {

        double prevOutput = (includePreviousOutput && c.getOutput() != null && !c.getOutput().isNaN()) ? c.getOutput()
                : 0.0;
        double input = prevOutput + c.getInput() + threshold;

        double result = Math.max(input, -1.0);
        result = Math.min(result, 1.0);
        result = Math.round(result * n);
        return result / n;
    }

}
