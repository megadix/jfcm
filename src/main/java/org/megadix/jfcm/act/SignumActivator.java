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
public class SignumActivator extends BaseConceptActivator {

    public static final double DEFAULT_ZERO_VALUE = 0.0;
    public static final Mode DEFAULT_MODE = Mode.BIPOLAR;

    public enum Mode {
        /** Default mode, values = [-1, zeroValue, +1] */
        BIPOLAR,
        /** Values = [0, zeroValue, 1] */
        BINARY
    }

    private Mode mode = DEFAULT_MODE;
    private double zeroValue = DEFAULT_ZERO_VALUE;

    public SignumActivator() {
        super();
    }

    public SignumActivator(double threshold) {
        super(threshold);
    }

    public SignumActivator(double threshold, boolean includePreviousOutput, Mode mode, double zeroValue) {
        super(threshold, includePreviousOutput);
        this.mode = mode;
        this.zeroValue = zeroValue;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public double getZeroValue() {
        return zeroValue;
    }

    public void setZeroValue(double zeroValue) {
        this.zeroValue = zeroValue;
    }

    @Override
    protected double calculateNextOutputImpl(Concept c) {
        double result = zeroValue;

        double prevOutput = (includePreviousOutput && c.getOutput() != null && !c.getOutput().isNaN()) ? c.getOutput()
                : 0.0;
        double input = prevOutput + c.getInput();

        // first, calculate as in BIPOLAR mode

        if (input < threshold) {
            result = -1.0;
        } else if (input > threshold) {
            result = 1.0;
        }

        // switch to BINARY if necessary
        if (mode == Mode.BINARY && result < 0.0) {
            result = zeroValue;
        }

        return result;
    }
}
