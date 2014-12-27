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

public interface FcmRunner {

    /**
     * Set the map to run,
     * @param map
     */
    void setMap(CognitiveMap map);

    /**
     * Set the number of epochs to run before stopping.
     * @param maxEpochs
     */
    public void setMaxEpochs(int maxEpochs);

    /**
     * Try to repeatedly execute the map until convergence. Each implementation
     * must provide its own convergence test.
     * @return
     */
    boolean converge();

    /**
     * Repeatedly execute the map, without trying to converge.
     */
    void run();
}
