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

package org.megadix.jfcm.utils;

import org.megadix.jfcm.CognitiveMap;

public class SimpleFcmRunner implements FcmRunner {

    private CognitiveMap map;
    private double maxDelta;
    private int maxEpochs;

    public SimpleFcmRunner() {
    }

    public SimpleFcmRunner(double maxDelta, int maxEpochs) {
        super();
        this.maxDelta = maxDelta;
        this.maxEpochs = maxEpochs;
    }

    public SimpleFcmRunner(CognitiveMap map, double maxDelta, int maxEpochs) {
        super();
        this.map = map;
        this.maxDelta = maxDelta;
        this.maxEpochs = maxEpochs;
    }

    public void setMap(CognitiveMap map) {
        this.map = map;
    }

    public void setMaxDelta(double maxDelta) {
        this.maxDelta = maxDelta;
    }

    public void setMaxEpochs(int maxEpochs) {
        this.maxEpochs = maxEpochs;
    }

    public boolean converge() {
        Double delta = map.calculateAverageSquareDelta();
        int i = 0;
        while ((delta == null || Double.isNaN(delta) || Double.isInfinite(delta) || delta > maxDelta) && i < maxEpochs) {
            map.execute();
            delta = map.calculateAverageSquareDelta();
            i++;
        }

        return (delta != null && delta.doubleValue() <= maxDelta);
    }
}
