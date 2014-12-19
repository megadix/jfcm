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

public class LinearActivator extends BaseConceptActivator {

    public static final double DEFAULT_FACTOR = 1.0;
    public static final double DEFAULT_MIN = Double.NEGATIVE_INFINITY;
    public static final double DEFAULT_MAX = Double.POSITIVE_INFINITY;

    private double factor = DEFAULT_FACTOR;
    private double min = DEFAULT_MIN;
    private double max = DEFAULT_MAX;

    public LinearActivator() {
        super();
    }

    public LinearActivator(double threshold, double factor, double min, double max) {
        super(threshold);
        this.factor = factor;
        this.min = min;
        this.max = max;
    }

    public LinearActivator(double threshold, boolean includePreviousOutput, double factor, double min, double max) {
        super(threshold, includePreviousOutput);
        this.factor = factor;
        this.min = min;
        this.max = max;
    }

    public double getFactor() {
        return factor;
    }

    public void setFactor(double factor) {
        this.factor = factor;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    @Override
    protected double calculateNextOutputImpl(Concept c) {
        double prevOutput = (includePreviousOutput && c.getOutput() != null && !c.getOutput().isNaN()) ? c.getOutput()
                : 0.0;
        double input = prevOutput + c.getInput() + threshold;
        double result = input * factor;
        result = Math.max(result, min);
        result = Math.min(result, max);
        return result;
    }

}
