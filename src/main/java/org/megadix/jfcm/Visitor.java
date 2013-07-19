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

package org.megadix.jfcm;

/**
 * Visitor design pattern.
 */
public interface Visitor {

    /**
     * Visit a {@link CognitiveMap}
     * @param map
     * @return <code>true</code> if visit must continue, <code>false</code> otherwise
     */
    boolean visitCognitiveMap(CognitiveMap map);

    /**
     * Visit a {@link Concept}
     * @param concept
     * @return <code>true</code> if visit must continue, <code>false</code> otherwise
     */
    boolean visitConcept(Concept concept);

    /**
     * Visit a {@link FcmConnection}
     * @param conn
     * @return <code>true</code> if visit must continue, <code>false</code> otherwise
     */
    boolean visitConnection(FcmConnection conn);

    /**
     * Visit a {@link ConceptActivator}
     * @param activator
     * @return <code>true</code> if visit must continue, <code>false</code> otherwise
     */
    boolean visitConceptActivator(ConceptActivator activator);
}
