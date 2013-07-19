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
 *
 *
 * @author De Franciscis Dimitri - www.megadix.it
 *
 */
public class HyperbolicTangentActivator extends BaseConceptActivator {

    public HyperbolicTangentActivator() {
        super();
    }

    public HyperbolicTangentActivator(double threshold) {
        super(threshold);
    }

    public HyperbolicTangentActivator(double threshold, boolean includePreviousOutput) {
        super(threshold, includePreviousOutput);
    }

    @Override
    protected double calculateNextOutputImpl(Concept c) {

        double prevOutput = (includePreviousOutput && c.getOutput() != null && !c.getOutput().isNaN()) ? c.getOutput()
                : 0.0;
        double input = prevOutput + c.getInput() + threshold;

        return Math.tanh(input);
    }
}
