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
 * Logistic function activator
 *
 * @author De Franciscis Dimitri - www.megadix.it
 *
 */
public class SigmoidActivator extends BaseConceptActivator {

    public static final double DEFAULT_K = 1.0;

    private double k = DEFAULT_K;

    public SigmoidActivator() {
        super();
    }

    public SigmoidActivator(double k) {
        super();
        this.k = k;
    }

    public SigmoidActivator(double k, double threshold) {
        super(threshold);
        this.k = k;
    }

    public SigmoidActivator(double threshold, boolean includePreviousOutput, double k) {
        super(threshold, includePreviousOutput);
        this.k = k;
    }

    @Override
    protected double calculateNextOutputImpl(Concept c) {
        double prevOutput = (includePreviousOutput && c.getOutput() != null && !c.getOutput().isNaN()) ? c.getOutput()
                : 0.0;
        double input = prevOutput + c.getInput() + threshold;
        double out = 1.0 / (1.0 + Math.exp(-k * input));
        return out;
    }

    public double getK() {
        return k;
    }

    public void setK(double k) {
        this.k = k;
    }

}
