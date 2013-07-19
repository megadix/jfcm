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

import static org.junit.Assert.*;

import java.io.*;
import java.text.ParseException;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;

import org.apache.commons.beanutils.BeanUtils;
import org.junit.Before;
import org.junit.Test;
import org.megadix.jfcm.*;
import org.megadix.jfcm.act.*;
import org.megadix.jfcm.act.SignumActivator.Mode;
import org.megadix.jfcm.conn.WeightedConnection;
import org.w3c.dom.*;

public class FcmIOTest {

    CognitiveMap map;

    @Before
    public void setUp() throws Exception {

        map = new CognitiveMap("Test Map");
        map.setDescription("This is a test map");

        Concept c1 = new Concept("c1", "c1 description <>&\"", new SignumActivator(1.1, false, Mode.BINARY, 0.0), 1.0,
                1.1, false);
        map.addConcept(c1);
        Concept c2 = new Concept("c2", "c2 description", new SigmoidActivator(2.2), null, 3.3, false);
        map.addConcept(c2);
        Concept c3 = new Concept("c3", "c3 description", new HyperbolicTangentActivator(3.3), 3.0, 1.23, true);
        map.addConcept(c3);
        Concept c4 = new Concept("c4", "c4 description", new LinearActivator(1.4, 1.2, -2.0, 2.0), 3.0, 1.23, true);
        map.addConcept(c4);

        FcmConnection con_1_2 = new WeightedConnection("c1-c2", "c1-c2 description <>&\"", 1.1, 5);
        map.addConnection(con_1_2);
        FcmConnection con_1_3 = new WeightedConnection("c1-c3", "c1-c3 description", 2.2);
        map.addConnection(con_1_3);
        FcmConnection con_2_3 = new WeightedConnection("c2-c3", "c2-c3 description", 3.3);
        map.addConnection(con_2_3);
        FcmConnection con_3_1 = new WeightedConnection("c3-c1", "c3-c1 description", 4.4);
        map.addConnection(con_3_1);

        map.connect("c1", "c1-c2", "c2");
        map.connect("c1", "c1-c3", "c3");
        map.connect("c2", "c2-c3", "c3");
        map.connect("c3", "c3-c1", "c1");
    }

    @Test
    public void test_saveAsXml_in_memory() throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // save
        FcmIO.saveAsXml(map, baos);

        // check results
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        Document doc = db.parse(is);
        XPath xpath = XPathFactory.newInstance().newXPath();

        Element mapElem = (Element) xpath.evaluate("/maps/map[position() = 1]", doc, XPathConstants.NODE);

        assertEquals("Test Map", xpath.evaluate("/maps/map[@name='Test Map']/@name", doc));
        checkXmlDescription(xpath, mapElem, "This is a test map");

        NodeList nodelist;
        Node node;

        Map<String, String> expectedParams = new HashMap<String, String>();

        // check <concepts>

        nodelist = (NodeList) xpath.evaluate("/maps/map[@name='Test Map']/concepts/concept", doc,
                XPathConstants.NODESET);
        assertEquals(4, nodelist.getLength());

        // -----

        node = (Node) xpath.evaluate("/maps/map[@name='Test Map']/concepts/concept[@name='c1']", doc,
                XPathConstants.NODE);
        expectedParams.clear();
        expectedParams.put("threshold", "1.1");
        expectedParams.put("includePreviousOutput", "false");
        expectedParams.put("mode", "BINARY");

        checkConceptXml(xpath, (Element) node, "c1", "c1 description <>&\"", "SIGNUM", "1.0", "1.1", null,
                expectedParams);

        // -----

        node = (Node) xpath.evaluate("/maps/map[@name='Test Map']/concepts/concept[@name='c2']", doc,
                XPathConstants.NODE);
        expectedParams.clear();
        expectedParams.put("k", "2.2");
        checkConceptXml(xpath, (Element) node, "c2", "c2 description", "SIGMOID", null, "3.3", null, expectedParams);

        node = (Node) xpath.evaluate("/maps/map[@name='Test Map']/concepts/concept[@name='c3']", doc,
                XPathConstants.NODE);
        expectedParams.clear();
        expectedParams.put("threshold", "3.3");

        checkConceptXml(xpath, (Element) node, "c3", "c3 description", "TANH", "3.0", "1.23", "true", expectedParams);

        // -----

        node = (Node) xpath.evaluate("/maps/map[@name='Test Map']/concepts/concept[@name='c4']", doc,
                XPathConstants.NODE);
        expectedParams.clear();
        expectedParams.put("threshold", "1.4");
        expectedParams.put("factor", "1.2");
        expectedParams.put("min", "-2.0");
        expectedParams.put("max", "2.0");

        checkConceptXml(xpath, (Element) node, "c4", "c4 description", "LINEAR", "3.0", "1.23", "true", expectedParams);

        // check <connections>

        nodelist = (NodeList) xpath.evaluate("/maps/map[@name='Test Map']/connections/connection", doc,
                XPathConstants.NODESET);
        assertEquals(4, nodelist.getLength());

        // -----

        expectedParams.clear();
        expectedParams.put("weight", "1.1");
        expectedParams.put("delay", "5");

        checkConnectionXml(xpath, (Element) nodelist.item(0), "c1-c2", "c1-c2 description <>&\"", "c1", "c2",
                "WEIGHTED", expectedParams);

        // -----

        expectedParams.clear();
        expectedParams.put("weight", "2.2");

        checkConnectionXml(xpath, (Element) nodelist.item(1), "c1-c3", "c1-c3 description", "c1", "c3", "WEIGHTED",
                expectedParams);

        // -----

        expectedParams.clear();
        expectedParams.put("weight", "3.3");

        checkConnectionXml(xpath, (Element) nodelist.item(2), "c2-c3", "c2-c3 description", "c2", "c3", "WEIGHTED",
                expectedParams);

        // -----

        expectedParams.clear();
        expectedParams.put("weight", "4.4");

        checkConnectionXml(xpath, (Element) nodelist.item(3), "c3-c1", "c3-c1 description", "c3", "c1", "WEIGHTED",
                expectedParams);

    }

    @Test
    public void test_saveAsXml_checkErrors() throws Exception {
        CognitiveMap testMap = new CognitiveMap("Test Map");
        Concept c = new Concept("c1", "c1 description");
        testMap.addConcept(c);

        try {
            // should fail, because Concept.conceptActivator == null
            FcmIO.saveAsXml(testMap, new ByteArrayOutputStream());
            fail("Should fail");
        } catch (Exception e) {
            // OK
        }

        // TODO other cases
    }

    @Test
    public void test_saveAsXml_file() throws Exception {
        File tempDir = new File("target/temp");
        tempDir.mkdirs();
        File outFile = new File(tempDir, "FcmIoTest_test_saveAsXml_file.xml");
        FileOutputStream fos = new FileOutputStream(outFile);

        FcmIO.saveAsXml(map, fos);

        List<CognitiveMap> maps = FcmIO.loadXml(new FileInputStream(outFile));
        CognitiveMap testMap = maps.get(0);
        assertNotNull(testMap);
    }

    @Test
    public void test_loadXml() throws Exception {
        List<CognitiveMap> maps = FcmIO.loadXml(getClass().getResourceAsStream("FcmIOTest_1.fcm.xml"));
        assertEquals(1, maps.size());
        CognitiveMap map = maps.get(0);

        checkTestMap(map);
    }

    @Test
    public void test_loadXml_no_namespace() throws Exception {
        List<CognitiveMap> maps = FcmIO.loadXml(getClass().getResourceAsStream("FcmIOTest_1_no_namespace.fcm.xml"));
        assertEquals(1, maps.size());
        CognitiveMap map = maps.get(0);

        checkTestMap(map);
    }

    /**
     * Check that saving unsupported types gives an error
     * 
     * @throws Exception
     */
    @Test
    public void test_save_errorUnsupported() throws Exception {
        CognitiveMap map = new CognitiveMap("Test Map");

        ConceptActivator customActivator = new ConceptActivator() {
            public void calculateNextOutput(Concept c) {
                // dummy
            }

            public void accept(Visitor visitor) {
                visitor.visitConceptActivator(this);
            }
        };

        Concept c1 = new Concept("c1", "c1 description <>&\"", customActivator, 1.0, 1.1, false);
        map.addConcept(c1);

        try {
            File tempDir = new File("target/temp");
            tempDir.mkdirs();
            File outFile = new File(tempDir, "FcmIoTest_test_save_errorUnsupported.xml");
            FileOutputStream fos = new FileOutputStream(outFile);

            FcmIO.saveAsXml(map, fos);

            fail("Should fail");

        } catch (Exception ex) {
            assertTrue(ex.getCause() instanceof IllegalArgumentException);
            // OK
        }

        // TODO other cases
    }

    /**
     * Checks that loading unsupported activation types fails
     * 
     * @throws Exception
     */
    @Test
    public void test_load_error_unsupported_activation() throws Exception {
        try {
            FcmIO.loadXml(getClass().getResourceAsStream("FcmIOTest_error_unsupported_activation.fcm.xml"));
            fail("should fail");
        } catch (Exception pex) {
            // OK
        }
    }

    /**
     * Checks that loading unsupported connection types fails
     * 
     * @throws Exception
     */
    @Test
    public void test_load_error_unsupported_connectionType() throws Exception {
        try {
            FcmIO.loadXml(getClass().getResourceAsStream("FcmIOTest_error_unsupported_connectionType.fcm.xml"));
            fail("should fail");
        } catch (ParseException pex) {
            // OK
        }
    }

    @Test
    public void test_saveActivators() throws Exception {

        // create test map

        CognitiveMap map = new CognitiveMap();
        Concept c;

        c = new Concept("c1", null);
        c.setConceptActivator(new CauchyActivator(1.1, true));
        map.addConcept(c);

        c = new Concept("c2", null);
        c.setConceptActivator(new GaussianActivator(1.2, false, 2.0));
        map.addConcept(c);

        c = new Concept("c3", null);
        c.setConceptActivator(new IntervalActivator(1.3, true, org.megadix.jfcm.act.IntervalActivator.Mode.BINARY,
                -0.5, 2.0));
        map.addConcept(c);

        c = new Concept("c4", null);
        c.setConceptActivator(new LinearActivator(1.4, false, 2.0, 0.1, 0.2));
        map.addConcept(c);

        c = new Concept("c5", null);
        c.setConceptActivator(new NaryActivator(1.5, true, 3));
        map.addConcept(c);

        c = new Concept("c6", null);
        c.setConceptActivator(new SigmoidActivator(1.6, false, 1.1));
        map.addConcept(c);

        c = new Concept("c7", null);
        c.setConceptActivator(new SignumActivator(1.7, true, Mode.BINARY, 0.1));
        map.addConcept(c);

        c = new Concept("c8", null);
        c.setConceptActivator(new HyperbolicTangentActivator(1.8, false));
        map.addConcept(c);

        // save

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FcmIO.saveAsXml(map, baos);

        // check results

        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        Document doc = db.parse(is);
        XPath xpath = XPathFactory.newInstance().newXPath();

        NodeList nodelist;
        Node node;

        Map<String, String> expectedParams = new HashMap<String, String>();

        // check <concepts>

        nodelist = (NodeList) xpath.evaluate("/maps/map/concepts/concept", doc, XPathConstants.NODESET);
        assertEquals(8, nodelist.getLength());

        // -----

        node = (Node) xpath.evaluate("/maps/map/concepts/concept[@name='c1']", doc, XPathConstants.NODE);
        expectedParams.clear();
        expectedParams.put("threshold", "1.1");

        checkConceptXml(xpath, (Element) node, "c1", null, "CAUCHY", null, null, null, expectedParams);

        // -----

        node = (Node) xpath.evaluate("/maps/map/concepts/concept[@name='c2']", doc, XPathConstants.NODE);
        expectedParams.clear();
        expectedParams.put("threshold", "1.2");
        expectedParams.put("includePreviousOutput", "false");
        expectedParams.put("width", "2.0");

        checkConceptXml(xpath, (Element) node, "c2", null, "GAUSS", null, null, null, expectedParams);

        // -----

        node = (Node) xpath.evaluate("/maps/map/concepts/concept[@name='c3']", doc, XPathConstants.NODE);
        expectedParams.clear();
        expectedParams.put("threshold", "1.3");
        expectedParams.put("mode", "BINARY");
        expectedParams.put("zeroValue", "-0.5");
        expectedParams.put("amplitude", "2.0");

        checkConceptXml(xpath, (Element) node, "c3", null, "INTERVAL", null, null, null, expectedParams);

        // -----

        node = (Node) xpath.evaluate("/maps/map/concepts/concept[@name='c4']", doc, XPathConstants.NODE);
        expectedParams.clear();
        expectedParams.put("threshold", "1.4");
        expectedParams.put("includePreviousOutput", "false");
        expectedParams.put("factor", "2.0");
        expectedParams.put("min", "0.1");
        expectedParams.put("max", "0.2");

        checkConceptXml(xpath, (Element) node, "c4", null, "LINEAR", null, null, null, expectedParams);

        // -----

        node = (Node) xpath.evaluate("/maps/map/concepts/concept[@name='c5']", doc, XPathConstants.NODE);
        expectedParams.clear();
        expectedParams.put("threshold", "1.5");
        expectedParams.put("n", "3");

        checkConceptXml(xpath, (Element) node, "c5", null, "NARY", null, null, null, expectedParams);

        // -----

        node = (Node) xpath.evaluate("/maps/map/concepts/concept[@name='c6']", doc, XPathConstants.NODE);
        expectedParams.clear();
        expectedParams.put("threshold", "1.6");
        expectedParams.put("includePreviousOutput", "false");
        expectedParams.put("k", "1.1");

        checkConceptXml(xpath, (Element) node, "c6", null, "SIGMOID", null, null, null, expectedParams);

        // -----

        node = (Node) xpath.evaluate("/maps/map/concepts/concept[@name='c7']", doc, XPathConstants.NODE);
        expectedParams.clear();
        expectedParams.put("threshold", "1.7");
        expectedParams.put("mode", "BINARY");
        expectedParams.put("zeroValue", "0.1");

        checkConceptXml(xpath, (Element) node, "c7", null, "SIGNUM", null, null, null, expectedParams);

        // -----

        node = (Node) xpath.evaluate("/maps/map/concepts/concept[@name='c8']", doc, XPathConstants.NODE);
        expectedParams.clear();
        expectedParams.put("threshold", "1.8");
        expectedParams.put("includePreviousOutput", "false");

        checkConceptXml(xpath, (Element) node, "c8", null, "TANH", null, null, null, expectedParams);

    }

    @Test
    public void test_readActivators() throws Exception {
        CognitiveMap map = FcmIO.loadXml(getClass().getResourceAsStream("FcmIOTest_activators.fcm.xml")).get(0);

        Map<String, Object> expectedParams = new HashMap<String, Object>();

        // -----

        expectedParams.clear();
        expectedParams.put("threshold", 1.1);
        expectedParams.put("includePreviousOutput", true);

        checkConcept(map, "c1", null, CauchyActivator.class, null, null, false, expectedParams, null, null);

        // -----

        expectedParams.clear();
        expectedParams.put("threshold", 1.2);
        expectedParams.put("includePreviousOutput", false);
        expectedParams.put("width", "2.0");

        checkConcept(map, "c2", null, GaussianActivator.class, null, null, false, expectedParams, null, null);

        // -----

        expectedParams.clear();
        expectedParams.put("threshold", 1.3);
        expectedParams.put("includePreviousOutput", true);
        expectedParams.put("mode", "BINARY");
        expectedParams.put("zeroValue", "-0.5");
        expectedParams.put("amplitude", "2.0");

        checkConcept(map, "c3", null, IntervalActivator.class, null, null, false, expectedParams, null, null);

        // -----

        expectedParams.clear();
        expectedParams.put("threshold", 1.4);
        expectedParams.put("includePreviousOutput", false);
        expectedParams.put("factor", "2.0");
        expectedParams.put("min", "0.1");
        expectedParams.put("max", "0.2");

        checkConcept(map, "c4", null, LinearActivator.class, null, null, false, expectedParams, null, null);

        // -----

        expectedParams.clear();
        expectedParams.put("threshold", 1.5);
        expectedParams.put("includePreviousOutput", true);
        expectedParams.put("n", "3");

        checkConcept(map, "c5", null, NaryActivator.class, null, null, false, expectedParams, null, null);

        // -----

        expectedParams.clear();
        expectedParams.put("threshold", 1.6);
        expectedParams.put("includePreviousOutput", false);
        expectedParams.put("k", "1.1");

        checkConcept(map, "c6", null, SigmoidActivator.class, null, null, false, expectedParams, null, null);

        // -----

        expectedParams.clear();
        expectedParams.put("threshold", 1.7);
        expectedParams.put("includePreviousOutput", true);
        expectedParams.put("mode", "BINARY");
        expectedParams.put("zeroValue", "0.1");

        checkConcept(map, "c7", null, SignumActivator.class, null, null, false, expectedParams, null, null);

        // -----

        expectedParams.clear();
        expectedParams.put("threshold", 1.8);
        expectedParams.put("includePreviousOutput", false);

        checkConcept(map, "c8", null, HyperbolicTangentActivator.class, null, null, false, expectedParams, null, null);

    }

    /*
     * private stuff
     */

    private void checkTestMap(CognitiveMap map) throws Exception {

        Map<String, Object> expectedParams = new HashMap<String, Object>();

        assertEquals("Test Map", map.getName());
        assertEquals("This is a test map", map.getDescription());
        assertEquals(4, map.getConcepts().size());

        Set<String> inConnections = new HashSet<String>();
        Set<String> outConnections = new HashSet<String>();

        // -----

        inConnections.add("c3-c1");
        outConnections = new HashSet<String>();
        outConnections.add("c1-c2");
        outConnections.add("c1-c3");

        expectedParams.clear();
        expectedParams.put("threshold", 1.1);
        expectedParams.put("includePreviousOutput", false);
        expectedParams.put("mode", Mode.BIPOLAR);

        checkConcept(map, "c1", "c1 description <>&\"", SignumActivator.class, 1.0, 1.1, false, expectedParams,
                inConnections, outConnections);

        // -----

        inConnections.clear();
        inConnections.add("c1-c2");
        outConnections.clear();
        outConnections.add("c2-c3");

        expectedParams.clear();
        expectedParams.put("threshold", 1.2);
        expectedParams.put("includePreviousOutput", true);

        checkConcept(map, "c2", "c2 description", SigmoidActivator.class, null, 3.3, false, expectedParams,
                inConnections, outConnections);

        // -----

        inConnections.clear();
        inConnections.add("c1-c3");
        inConnections.add("c2-c3");
        outConnections.clear();
        outConnections.add("c3-c1");

        expectedParams.clear();
        expectedParams.put("threshold", 1.3);
        expectedParams.put("includePreviousOutput", true);

        checkConcept(map, "c3", "c3 description", HyperbolicTangentActivator.class, 3.0, 1.23, true, expectedParams,
                inConnections, outConnections);

        // -----

        inConnections.clear();
        outConnections.clear();

        expectedParams.clear();
        expectedParams.put("threshold", 1.4);
        expectedParams.put("includePreviousOutput", true);

        checkConcept(map, "c4", "c4 description", LinearActivator.class, 3.0, 1.23, true, expectedParams,
                inConnections, outConnections);

        // -----

        assertEquals(4, map.getConnections().size());
        checkConnection(map, "c1-c2", "c1-c2 description <>&\"", "c1", "c2", 1.1, 0.1, 5);
        checkConnection(map, "c1-c3", "c1-c3 description", "c1", "c3", 2.2, 0.2, 0);
        checkConnection(map, "c2-c3", "c2-c3 description", "c2", "c3", 3.3, 0.3, 0);
        checkConnection(map, "c3-c1", "c3-c1 description", "c3", "c1", 4.4, 0.4, 0);
    }

    private void checkConceptXml(XPath xpath, Element elem, String name, String description, String act, String input,
            String output, String fixed, Map<String, String> expectedParams) throws XPathExpressionException {

        assertEquals(name, elem.getAttribute("name"));

        if (description != null) {
            checkXmlDescription(xpath, elem, description);
        }

        checkXmlAttribute(elem, "act", act);
        checkXmlAttribute(elem, "input", input);
        checkXmlAttribute(elem, "output", output);
        checkXmlAttribute(elem, "fixed", fixed);

        Element paramsElem = (Element) xpath.evaluate("params", elem, XPathConstants.NODE);
        assertNotNull(paramsElem);

        NodeList paramsList = paramsElem.getElementsByTagName("param");

        for (int i = 0; i < paramsList.getLength(); i++) {
            Element param = (Element) paramsList.item(i);
            String paramName = param.getAttribute("name");
            assertTrue(StringUtils.isNotBlank(paramName));
            String paramValue = param.getAttribute("value");
            assertTrue(StringUtils.isNotBlank(paramValue));

            assertTrue("Parameter not found: " + paramName, expectedParams.containsKey(paramName));
            assertEquals(paramValue, expectedParams.get(paramName));
            expectedParams.remove(paramName);
        }

        assertEquals("Parameter not found: " + expectedParams.keySet().toString(), 0, expectedParams.size());
    }

    private void checkConnectionXml(XPath xpath, Element elem, String name, String description, String from, String to,
            String type, Map<String, String> expectedParams) throws XPathExpressionException {

        assertEquals(name, elem.getAttribute("name"));

        checkXmlDescription(xpath, elem, description);

        checkXmlAttribute(elem, "from", from);
        checkXmlAttribute(elem, "to", to);
        checkXmlAttribute(elem, "type", type);

        Element paramsElem = (Element) xpath.evaluate("params", elem, XPathConstants.NODE);
        assertNotNull(paramsElem);

        NodeList paramsList = paramsElem.getElementsByTagName("param");

        for (int i = 0; i < paramsList.getLength(); i++) {
            Element param = (Element) paramsList.item(i);
            String paramName = param.getAttribute("name");
            assertTrue(StringUtils.isNotBlank(paramName));
            String paramValue = param.getAttribute("value");
            assertTrue(StringUtils.isNotBlank(paramValue));

            assertTrue("Parameter not found: " + paramName, expectedParams.containsKey(paramName));
            assertEquals(paramValue, expectedParams.get(paramName));
            expectedParams.remove(paramName);
        }

        assertEquals(0, expectedParams.size());
    }

    private void checkXmlAttribute(Element elem, String name, String value) {
        if (value == null) {
            assertFalse("Unexpected attribute: " + name, elem.hasAttribute(name));
        } else {
            assertEquals(value, elem.getAttribute(name));
        }
    }

    private void checkXmlDescription(XPath xpath, Element elem, String value) throws XPathExpressionException {
        Element descrElem = (Element) xpath.evaluate("description", elem, XPathConstants.NODE);
        assertNotNull(descrElem);
        assertEquals(value, descrElem.getTextContent());
    }

    private static void checkConcept(CognitiveMap map, String name, String description,
            Class<? extends ConceptActivator> activatorClass, Double input, Double output, boolean fixed,
            Map<String, Object> expectedParams, Set<String> inConnections, Set<String> outConnections) throws Exception {

        Concept c = map.getConcept(name);
        assertNotNull(c);
        assertEquals(name, c.getName());
        assertEquals(description, c.getDescription());
        assertNotNull(c.getConceptActivator());

        assertTrue(activatorClass.isAssignableFrom(c.getConceptActivator().getClass()));

        for (Map.Entry<String, Object> paramEntry : expectedParams.entrySet()) {
            Object expected = paramEntry.getValue();
            Object actual = BeanUtils.getProperty(c.getConceptActivator(), paramEntry.getKey());
            assertEquals(paramEntry.getKey(), expected.toString(), actual);
        }

        assertEquals(input, c.getInput());
        assertEquals(output, c.getOutput());
        assertEquals(fixed, c.isFixedOutput());

        Set<String> check;

        if (inConnections != null) {
            assertEquals(inConnections.size(), c.getInConnections().size());
            check = new HashSet<String>(inConnections);
            for (FcmConnection conn : c.getInConnections()) {
                check.remove(conn.getName());
            }
            assertEquals(0, check.size());
        }

        if (outConnections != null) {
            assertEquals(outConnections.size(), c.getOutConnections().size());
            check = new HashSet<String>(outConnections);
            for (FcmConnection conn : c.getOutConnections()) {
                check.remove(conn.getName());
            }
            assertEquals(0, check.size());
        }

    }

    private void checkConnection(CognitiveMap map, String name, String description, String from, String to,
            double weight, double threshold, int delay) {

        // As for now we only have WeightedConnection
        // TODO check for other FcmConnection implementations
        WeightedConnection conn = (WeightedConnection) map.getConnection(name);
        assertNotNull(conn);
        assertEquals(name, conn.getName());
        assertEquals(description, conn.getDescription());
        assertEquals(from, conn.getFrom().getName());
        assertEquals(to, conn.getTo().getName());
        assertEquals(weight, conn.getWeight(), 0.0);
        assertEquals(delay, conn.getDelay());
    }

}
