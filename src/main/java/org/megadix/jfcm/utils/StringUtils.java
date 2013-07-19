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

/**
 * Subset of functionality provided by similar utilities like StringUtils from
 * Jakarta Common Lang
 */
public final class StringUtils {

    private StringUtils() {
        // prevents calls
        throw new UnsupportedOperationException();
    }

    public static boolean isBlank(CharSequence cs) {
        if (cs == null || cs.length() == 0) {
            return true;
        }

        int l = cs.length();
        for (int i = 0; i < l; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                // break loop
                return false;
            }
        }

        return true;
    }

    public static boolean isNotBlank(CharSequence cs) {
        return !isBlank(cs);
    }

    public static String defaultString(String s) {
        return s == null ? "" : s;
    }

}
