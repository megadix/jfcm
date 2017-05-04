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

package org.megadix.jfcm.conn;

import java.util.Deque;
import java.util.LinkedList;

import org.megadix.jfcm.FcmConnection;

/**
 * Simple {@link FcmConnection} implementation that applies a weight and a threshold
 * to the input {@link org.megadix.jfcm.Concept}. Final output is calculated as follows:
 * <pre>
 * result = (from.getOutput() - from.getOutputThreshold()) * weight
 * </pre>
 *
 * @author De Franciscis Dimitri - www.megadix.it
 *
 */
public class WeightedConnection extends FcmConnection {

    private double weight = 1.0;
    private int delay = 0;
    private Deque<Double> buffer = null;

    public WeightedConnection() {
    }

    public WeightedConnection(String name, String description, double weight, int delay) {
        super(name, description);
        this.weight = weight;
        this.delay = delay;
        if (delay > 0) {
            buffer = new LinkedList<>();
        }
    }

    public WeightedConnection(String name, String description, double weight) {
        super(name, description);
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "[" + name + "] weight=" + weight + ", delay=" + delay;
    }

    @Override
    public Double calculateOutput() {
        if (from.getOutput() == null) {
            this.output = null;
        } else {
            this.output = from.getOutput() * weight;
        }

        if (delay > 0) {
            // insert result into buffer
            buffer.addFirst(output);
            // if buffer is big enough
            if (buffer.size() == delay + 1) {
                // return delayed result
                this.output = buffer.removeLast();
            } else {
                // else return null (signal not yet at the end of delay queue)
                this.output = null;
            }
        }

        return this.output;
    }

    /*
     * simple set / get
     */

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
        if (delay > 0) {
            buffer = new LinkedList<>();
        } else {
            buffer = null;
        }
    }
}
