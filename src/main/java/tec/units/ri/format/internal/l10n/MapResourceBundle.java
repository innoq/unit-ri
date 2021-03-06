/**
 * Unit-API - Units of Measurement API for Java
 * Copyright (c) 2005-2015, Jean-Marie Dautelle, Werner Keil, V2COM.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of JSR-363 nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package tec.units.ri.format.internal.l10n;

import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.measure.MeasurementException;

/**
 * A Java ME compliant backport of {@link java.util.ResourceBundle}
 * @author Werner
 * @version 0.3
 */ 
public abstract class MapResourceBundle implements L10nBundle {
    protected MapResourceBundle parent;
 
    /**
     * Returns a resource bundle using the specified base name and target
     * locale.
     */
    public static MapResourceBundle getBundle(String baseName, Locale targetLocale) {
        MapResourceBundle parentBundle = null;
        MapResourceBundle bundle = null;
        // Get a list of candidate locales for which resource bundles are
        // searched
        Vector candidateLocales = getCandidateLocales(targetLocale);
        // Go through every candidate locale and try to instantiate a
        // MapResourceBundle using the base name and the candidate locale
        for (int i = candidateLocales.size() - 1; i >= 0; i--) {
            Locale locale = (Locale)candidateLocales.elementAt(i);
            // Bundle name consists of the base name plus an underscore and
            // locale if there is a locale. Otherwise, it will consist only of
            // the base name.
            String bundleName = baseName +
                ((locale.toString().equals("")) ? "" : ("_" + locale));
            try {
                // Try to instantiate a resource bundle using the name
                // constructed above
                Class bundleClass = Class.forName("tec.units.ri.format.internal.l10n." + bundleName); // TODO try use generics here
                bundle = (MapResourceBundle)bundleClass.newInstance();
                // Set the parent bundle for this bundle. For the base bundle
                // (the one with the root locale, Locale.ROOT), the parent is
                // null.
                bundle.setParent(parentBundle);
                parentBundle = bundle;
            } catch (Exception ex) {
                // No need to do anything, just continue to the next bundle
            }
        }
        // If bundle is null even here, no resource bundle could be found.
        // This is an error situation.
        if (bundle == null) {
            throw new MeasurementException(
                "Can't find resource bundle for base name " + baseName + ".");
        }
        return bundle;
    }
 
    /**
     * Returns a list of Locales as candidate locales used in bundle
     * instantiation.
     * @param locale the locale for which a resource bundle is desired
     */
    private static Vector getCandidateLocales(Locale locale) {
        String language = locale.getLanguage();
        String country = locale.getCountry();
 
        Vector locales = new Vector(3);
        if (!country.equals("")) {
            locales.addElement(locale);
        }
        if (!language.equals("")) {
            locales.addElement(
                (locales.size() == 0) ? locale : new Locale(language, ""));
        }
        locales.addElement(Locale.ROOT);
        return locales;
    }
 
    protected void setParent(MapResourceBundle parent) {
        this.parent = parent;
    }
 
    public final String getString(String key) {
        String string = handleGetString(key);
        if (string == null) {
            if (parent != null) {
                string = parent.getString(key);
            }
            if (string == null) {
                throw new MeasurementException("Can't find resource for bundle " +
                    this.getClass().getName() + " and key " + key + ".");
            }
        }
        return string;
    }
    
    /**
     * Returns an <code>Enumeration</code> of the keys contained in
     * this <code>ResourceBundle</code> and its parent bundles.
     *
     * @return an <code>Enumeration</code> of the keys contained in
     *         this <code>ResourceBundle</code> and its parent bundles.
     * @see #keySet()
     */
    public abstract Enumeration<String> getKeys();
 
    /**
     * Gets a string for the given key from this resource bundle. Returns null
     * if this resource bundle does not contain a string for the given key.
     */
    protected abstract String handleGetString(String key);
}
