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

import java.io.*;
import java.text.ParseException;
import java.util.*;

import javax.xml.parsers.*;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.*;

import org.megadix.jfcm.*;
import org.megadix.jfcm.act.*;
import org.megadix.jfcm.act.SignumActivator.Mode;
import org.megadix.jfcm.conn.WeightedConnection;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class FcmIO {

    public static final String JFCM_SCHEMA_1_1 = "http://www.megadix.org/standards/JFCM-map-v-1.1.xsd";
    public static final String JFCM_SCHEMA_1_2 = "http://www.megadix.org/standards/JFCM-map-v-1.2.xsd";
    private static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";

    private static final Map<String, BaseConceptActivatorBuilder> actBuilders;

    static {
        actBuilders = new HashMap<>();

        BaseConceptActivatorBuilder actBuilder;

        // CAUCHY
        actBuilder = new BaseConceptActivatorBuilder(CauchyActivator.class) {
            @Override
            public BaseConceptActivator build(Map<String, String> params) throws InstantiationException,
                    IllegalAccessException {
                CauchyActivator act = (CauchyActivator) super.build(params);
                return act;
            }
        };
        actBuilders.put(Constants.ConceptActivatorTypes.CAUCHY.name(), actBuilder);

        // GAUSS
        actBuilder = new BaseConceptActivatorBuilder(GaussianActivator.class) {
            @Override
            public BaseConceptActivator build(Map<String, String> params) throws InstantiationException,
                    IllegalAccessException {
                GaussianActivator act = (GaussianActivator) super.build(params);
                if (params.containsKey("width")) {
                    act.setWidth(Double.parseDouble(params.get("width")));
                }
                return act;
            }
        };
        actBuilders.put(Constants.ConceptActivatorTypes.GAUSS.name(), actBuilder);

        // INTERVAL
        actBuilder = new BaseConceptActivatorBuilder(IntervalActivator.class) {
            @Override
            public BaseConceptActivator build(Map<String, String> params) throws InstantiationException,
                    IllegalAccessException {
                IntervalActivator act = (IntervalActivator) super.build(params);
                if (params.containsKey("mode")) {
                    act.setMode(org.megadix.jfcm.act.IntervalActivator.Mode.valueOf(params.get("mode")));
                }
                if (params.containsKey("zeroValue")) {
                    act.setZeroValue(Double.parseDouble(params.get("zeroValue")));
                }
                if (params.containsKey("amplitude")) {
                    act.setAmplitude((Double.parseDouble(params.get("amplitude"))));
                }
                return act;
            }
        };
        actBuilders.put(Constants.ConceptActivatorTypes.INTERVAL.name(), actBuilder);

        // LINEAR
        actBuilder = new BaseConceptActivatorBuilder(LinearActivator.class) {
            @Override
            public BaseConceptActivator build(Map<String, String> params) throws InstantiationException,
                    IllegalAccessException {
                LinearActivator act = (LinearActivator) super.build(params);
                if (params.containsKey("factor")) {
                    act.setFactor(Double.parseDouble(params.get("factor")));
                }
                if (params.containsKey("min")) {
                    act.setMin(Double.parseDouble(params.get("min")));
                }
                if (params.containsKey("max")) {
                    act.setMax(Double.parseDouble(params.get("max")));
                }
                return act;
            }
        };
        actBuilders.put(Constants.ConceptActivatorTypes.LINEAR.name(), actBuilder);

        // NARY
        actBuilder = new BaseConceptActivatorBuilder(NaryActivator.class) {
            @Override
            public BaseConceptActivator build(Map<String, String> params) throws InstantiationException,
                    IllegalAccessException {
                NaryActivator act = (NaryActivator) super.build(params);
                if (params.containsKey("n")) {
                    act.setN(Integer.parseInt(params.get("n")));
                }
                return act;
            }
        };
        actBuilders.put(Constants.ConceptActivatorTypes.NARY.name(), actBuilder);

        // SIGMOID
        actBuilder = new BaseConceptActivatorBuilder(SigmoidActivator.class) {
            @Override
            public BaseConceptActivator build(Map<String, String> params) throws InstantiationException,
                    IllegalAccessException {
                SigmoidActivator act = (SigmoidActivator) super.build(params);
                if (params.containsKey("k")) {
                    act.setK(Double.parseDouble(params.get("k")));
                }
                return act;
            }
        };
        actBuilders.put(Constants.ConceptActivatorTypes.SIGMOID.name(), actBuilder);

        // SIGNUM
        actBuilder = new BaseConceptActivatorBuilder(SignumActivator.class) {
            @Override
            public BaseConceptActivator build(Map<String, String> params) throws InstantiationException,
                    IllegalAccessException {
                SignumActivator act = (SignumActivator) super.build(params);
                if (params.containsKey("mode")) {
                    act.setMode(Mode.valueOf(params.get("mode")));
                }
                if (params.containsKey("zeroValue")) {
                    act.setZeroValue(Double.parseDouble(params.get("zeroValue")));
                }

                return act;
            }
        };
        actBuilders.put(Constants.ConceptActivatorTypes.SIGNUM.name(), actBuilder);

        // TANH
        actBuilder = new BaseConceptActivatorBuilder(HyperbolicTangentActivator.class) {
            @Override
            public BaseConceptActivator build(Map<String, String> params) throws InstantiationException,
                    IllegalAccessException {
                HyperbolicTangentActivator act = (HyperbolicTangentActivator) super.build(params);
                return act;
            }
        };
        actBuilders.put(Constants.ConceptActivatorTypes.TANH.name(), actBuilder);
    }

    public static void saveAsXml(CognitiveMap map, OutputStream outputStream) {
        List<CognitiveMap> maps = new ArrayList<>(1);
        maps.add(map);
        saveAsXml(maps, outputStream);
    }

    public static void saveAsXml(CognitiveMap map, String filename) throws FileNotFoundException {
        saveAsXml(map, new FileOutputStream(filename));
    }

    public static void saveAsXml(List<CognitiveMap> maps, OutputStream outputStream) {
        ToXmlVisitor xmlVisitor = new ToXmlVisitor();

        for (CognitiveMap map : maps) {
            map.accept(xmlVisitor);
        }

        xmlVisitor.saveTo(outputStream);
    }

    public static void saveAsXml(CognitiveMap map, Writer writer) {
        ToXmlVisitor xmlVisitor = new ToXmlVisitor();
        map.accept(xmlVisitor);
        xmlVisitor.saveTo(writer);
    }

    public static List<CognitiveMap> loadXml(String filename) throws ParseException, FileNotFoundException {
        return loadXml(new FileInputStream(filename));
    }

    public static List<CognitiveMap> loadXml(InputStream inputStream) throws ParseException, FileNotFoundException {
        return loadXml(new InputStreamReader(inputStream));
    }

    public static List<CognitiveMap> loadXml(Reader reader) throws ParseException {
        try {
            DocumentBuilder documentBuilder = getDocumentBuilder(true);
            Document doc = documentBuilder.parse(new InputSource(reader));
            XPath xpath = XPathFactory.newInstance().newXPath();
            List<CognitiveMap> maps = new ArrayList<>();
            NodeList nodelist = (NodeList) xpath.evaluate("/maps/map", doc, XPathConstants.NODESET);
            for (int i = 0; i < nodelist.getLength(); i++) {
                Element mapElem = (Element) nodelist.item(i);
                maps.add(parseMap(xpath, mapElem));
            }

            return maps;

        } catch (ParseException pex) {
            throw pex;
        } catch (Exception ex) {
            throw new RuntimeException("Error loading XML file", ex);
        } finally {
            try {
                reader.close();
            } catch (IOException ioex) {
                throw new RuntimeException("Error closing reader", ioex);
            }
        }
    }

    /*
     * private stuff
     */

    private static DocumentBuilder getDocumentBuilder(boolean strict) throws SAXException, ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        if (strict) {
            Source schemaSource = new StreamSource(FcmIO.class.getResourceAsStream("/JFCM-map-v-1.2.xsd"));
            Schema schema = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema").newSchema(schemaSource);
            documentBuilderFactory.setAttribute(JAXP_SCHEMA_SOURCE, schema);
        }
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        return documentBuilder;
    }

    private static CognitiveMap parseMap(XPath xpath, Element mapElem) throws Exception {
        NodeList nodelist;
        CognitiveMap map = new CognitiveMap();

        map.setName(xpath.evaluate("@name", mapElem));
        String description = xpath.evaluate("description/text()", mapElem);
        if (StringUtils.isNotBlank(description)) {
            map.setDescription(description);
        }

        nodelist = (NodeList) xpath.evaluate("concepts/concept", mapElem, XPathConstants.NODESET);
        for (int i = 0; i < nodelist.getLength(); i++) {
            Element conceptElem = (Element) nodelist.item(i);
            map.addConcept(parseConcept(xpath, conceptElem));
        }

        nodelist = (NodeList) xpath.evaluate("connections/connection", mapElem, XPathConstants.NODESET);
        for (int i = 0; i < nodelist.getLength(); i++) {
            Element connElem = (Element) nodelist.item(i);
            map.addConnection(parseConnection(map, xpath, connElem));
        }

        // TODO optimize
        Iterator<FcmConnection> iter = map.getConnectionsIterator();
        while (iter.hasNext()) {
            FcmConnection conn = iter.next();
            map.connect(conn.getFrom().getName(), conn.getName(), conn.getTo().getName());
        }

        return map;
    }

    private static Concept parseConcept(XPath xpath, Element conceptElem) {
        try {
            Concept c = new Concept();
            c.setName(xpath.evaluate("@name", conceptElem));

            String description = xpath.evaluate("description/text()", conceptElem);
            if (StringUtils.isNotBlank(description)) {
                c.setDescription(description);
            }

            String actAttr = conceptElem.getAttribute("act");

            Map<String, String> params = parseParams(conceptElem, xpath);

            if (!actBuilders.containsKey(actAttr)) {
                throw new ParseException("ConceptActivator not supported: \"" + StringUtils.defaultString(actAttr)
                        + "\"", 0);
            }

            BaseConceptActivatorBuilder actBuilder = actBuilders.get(actAttr);
            c.setConceptActivator(actBuilder.build(params));

            String s;
            s = xpath.evaluate("@input", conceptElem);
            if (StringUtils.isNotBlank(s)) {
                c.setInput(Double.parseDouble(s));
            }
            s = xpath.evaluate("@output", conceptElem);
            if (StringUtils.isNotBlank(s)) {
                c.setOutput(Double.parseDouble(s));
            }
            s = xpath.evaluate("@fixed", conceptElem);
            if (StringUtils.isNotBlank(s)) {
                c.setFixedOutput(Boolean.parseBoolean(s));
            }

            return c;

        } catch (Exception ex) {
            throw new RuntimeException("Error parsing concept", ex);
        }
    }

    private static Map<String, String> parseParams(Element elem, XPath xpath) throws XPathExpressionException {
        Map<String, String> params = new HashMap<>();
        Element paramsElem = (Element) xpath.evaluate("params", elem, XPathConstants.NODE);
        if (paramsElem != null) {
            NodeList paramsList = (NodeList) xpath.evaluate("param", paramsElem, XPathConstants.NODESET);
            for (int i = 0; i < paramsList.getLength(); i++) {
                Element param = (Element) paramsList.item(i);
                params.put(param.getAttribute("name"), param.getAttribute("value"));
            }
        }

        return params;
    }

    private static FcmConnection parseConnection(CognitiveMap map, XPath xpath, Element connElem) throws Exception {

        FcmConnection conn;
        String s;
        String name = xpath.evaluate("@name", connElem);
        if (StringUtils.isBlank(name)) {
            throw new Exception("Missing connection name");
        }

        Map<String, String> params = parseParams(connElem, xpath);

        String type = xpath.evaluate("@type", connElem);

        if ("WEIGHTED".equalsIgnoreCase(type)) {
            WeightedConnection wConn = new WeightedConnection();
            if (params.containsKey("weight")) {
                wConn.setWeight(Double.parseDouble(params.get("weight")));
            }
            if (params.containsKey("delay")) {
                wConn.setDelay(Integer.parseInt(params.get("delay")));
            }
            conn = wConn;

        } else {
            throw new ParseException("Connection type not supported: \"" + type + "\"", 0);
        }

        conn.setName(name);

        String description = xpath.evaluate("description/text()", connElem);
        if (StringUtils.isNotBlank(description)) {
            conn.setDescription(description);
        }

        Concept c;

        s = xpath.evaluate("@from", connElem);
        if (StringUtils.isBlank(s)) {
            throw new Exception("Missing \"from\" reference in connection \"" + conn.getName() + "\"");
        }
        c = map.getConcept(s);
        if (c == null) {
            throw new Exception("Missing \"from\" reference in connection \"" + conn.getName() + "\"");
        }
        conn.setFrom(c);

        s = xpath.evaluate("@to", connElem);
        if (StringUtils.isBlank(s)) {
            throw new Exception("Missing \"to\" reference in connection \"" + conn.getName() + "\"");
        }
        c = map.getConcept(s);
        if (c == null) {
            throw new Exception("Missing \"to\" reference in connection \"" + conn.getName() + "\"");
        }
        conn.setTo(c);

        return conn;
    }

}

abstract class BaseConceptActivatorBuilder {

    Class<? extends BaseConceptActivator> clazz;

    public BaseConceptActivatorBuilder(Class<? extends BaseConceptActivator> clazz) {
        super();
        this.clazz = clazz;
    }

    public BaseConceptActivator build(Map<String, String> params) throws InstantiationException, IllegalAccessException {

        BaseConceptActivator act = clazz.newInstance();
        if (params.containsKey("threshold")) {
            act.setThreshold(Double.parseDouble(params.get("threshold")));
        }

        if (params.containsKey("includePreviousOutput")) {
            act.setIncludePreviousOutput(Boolean.parseBoolean(params.get("includePreviousOutput")));
        } else {
            act.setIncludePreviousOutput(true);
        }

        return act;
    }

}
