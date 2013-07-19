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
 * Base abstract implementation of {@link Visitor} design pattern, that simply
 * stores an instance of each element visited. Remember to call super.visitXYZ()
 * when overriding methods!
 */
public abstract class BaseVisitor implements Visitor {

    protected CognitiveMap map;
    protected Concept concept;
    protected FcmConnection connection;
    protected ConceptActivator activator;

    public boolean visitCognitiveMap(CognitiveMap _map) {
        this.map = _map;
        return true;
    }

    public boolean visitConcept(Concept _concept) {
        this.concept = _concept;
        return true;
    }

    public boolean visitConnection(FcmConnection _connection) {
        this.connection = _connection;
        return true;
    }

    public boolean visitConceptActivator(ConceptActivator _activator) {
        this.activator = _activator;
        return true;
    }
}
