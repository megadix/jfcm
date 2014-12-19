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

public class GaussianActivator extends BaseConceptActivator {

    public static final double DEFAULT_WIDTH = 1.0;

    private double width = 1.0;
    private double width2 = DEFAULT_WIDTH;

    public GaussianActivator() {
    }

    public GaussianActivator(double threshold, boolean includePreviousOutput) {
        super(threshold, includePreviousOutput);
    }

    public GaussianActivator(double threshold, boolean includePreviousOutput, double width) {
        this(threshold, includePreviousOutput);
        setWidth(width);
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
        this.width2 = width * width;
    }

    @Override
    protected double calculateNextOutputImpl(Concept c) {
        double prevOutput = (includePreviousOutput && c.getOutput() != null && !c.getOutput().isNaN()) ? c.getOutput()
                : 0.0;
        double input = prevOutput + c.getInput();
        double out = Math.exp(-1.0 * Math.pow((input), 2.0) / 2.0 * width2);
        return out;
    }

}
