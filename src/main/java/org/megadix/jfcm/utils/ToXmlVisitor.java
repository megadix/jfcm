package org.megadix.jfcm.utils;

import java.io.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.megadix.jfcm.*;
import org.megadix.jfcm.act.*;
import org.megadix.jfcm.conn.WeightedConnection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class ToXmlVisitor extends BaseVisitor {

    public static final String JFCM_SCHEMA_1_2 = "http://www.megadix.org/standards/JFCM-map-v-1.2.xsd";
    private static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";

    private Document doc;

    private Element xmlElemMaps;

    private Element xmlElemMap;
    private Element xmlElemConcepts;
    private Element xmlElemConcept;
    private Element xmlElemConceptParams;

    private Element xmlElemConnections;

    public ToXmlVisitor() {
    }

    public boolean visitCognitiveMap(CognitiveMap _map) {
        super.visitCognitiveMap(_map);

        try {
            if (xmlElemMaps == null) {
                // first map
                DocumentBuilder documentBuilder = getDocumentBuilder(true);
                doc = documentBuilder.newDocument();

                xmlElemMaps = doc.createElementNS(JFCM_SCHEMA_1_2, "maps");
                xmlElemMaps.setPrefix("jfcm");
                doc.appendChild(xmlElemMaps);
            }

            xmlElemMap = doc.createElement("map");
            xmlElemMap.setAttribute("name", map.getName());
            appendDescription(doc, xmlElemMap, map.getDescription());
            xmlElemMaps.appendChild(xmlElemMap);

            xmlElemConcepts = doc.createElement("concepts");
            xmlElemMap.appendChild(xmlElemConcepts);

            xmlElemConnections = doc.createElement("connections");
            xmlElemMap.appendChild(xmlElemConnections);

        } catch (Exception ex) {
            throw new RuntimeException("Error visiting map", ex);
        }

        return true;
    }

    public boolean visitConcept(Concept _concept) {
        super.visitConcept(_concept);

        try {
            if (concept.getConceptActivator() == null) {
                throw new IllegalStateException("conceptActivator == null, Concept = " + concept.getName());
            }

            xmlElemConcept = doc.createElement("concept");
            xmlElemConcepts.appendChild(xmlElemConcept);
            xmlElemConcept.setAttribute("name", concept.getName());
            appendDescription(doc, xmlElemConcept, concept.getDescription());

            xmlElemConceptParams = doc.createElement("params");
            xmlElemConcept.appendChild(xmlElemConceptParams);

            if (concept.getInput() != null) {
                xmlElemConcept.setAttribute("input", concept.getInput().toString());
            }
            if (concept.getOutput() != null) {
                xmlElemConcept.setAttribute("output", concept.getOutput().toString());
            }
            if (concept.isFixedOutput()) {
                xmlElemConcept.setAttribute("fixed", "true");
            }

            xmlElemConcepts.appendChild(xmlElemConcept);

        } catch (Exception ex) {
            throw new RuntimeException("Error visiting concept", ex);
        }

        return true;
    }

    public boolean visitConceptActivator(ConceptActivator _activator) {
        super.visitConceptActivator(_activator);

        try {
            // append params for ConceptActivator

            if (BaseConceptActivator.class.isAssignableFrom(activator.getClass())) {
                BaseConceptActivator act = (BaseConceptActivator) activator;
                if (act.getThreshold() != BaseConceptActivator.DEFAULT_THRESHOLD) {
                    addXmlParam(xmlElemConceptParams, "threshold", Double.toString(act.getThreshold()));
                }
                if (act.isIncludePreviousOutput() != BaseConceptActivator.DEFAULT_INCLUDE_PREVIOUS_OUTPUT) {
                    addXmlParam(xmlElemConceptParams, "includePreviousOutput",
                            Boolean.toString(act.isIncludePreviousOutput()));
                }
            }

            if (activator instanceof CauchyActivator) {
                xmlElemConcept.setAttribute("act", Constants.ConceptActivatorTypes.CAUCHY.name());

            } else if (activator instanceof GaussianActivator) {
                xmlElemConcept.setAttribute("act", Constants.ConceptActivatorTypes.GAUSS.name());
                GaussianActivator actImpl = (GaussianActivator) activator;
                if (actImpl.getWidth() != GaussianActivator.DEFAULT_WIDTH) {
                    addXmlParam(xmlElemConceptParams, "width", Double.toString(actImpl.getWidth()));
                }

            } else if (activator instanceof IntervalActivator) {
                xmlElemConcept.setAttribute("act", Constants.ConceptActivatorTypes.INTERVAL.name());
                IntervalActivator actImpl = (IntervalActivator) activator;
                if (actImpl.getMode() != IntervalActivator.DEFAULT_MODE) {
                    addXmlParam(xmlElemConceptParams, "mode", actImpl.getMode().name());
                }
                if (actImpl.getZeroValue() != IntervalActivator.DEFAULT_ZERO_VALUE) {
                    addXmlParam(xmlElemConceptParams, "zeroValue", Double.toString(actImpl.getZeroValue()));
                }
                if (actImpl.getAmplitude() != IntervalActivator.DEFAULT_AMPLITUDE) {
                    addXmlParam(xmlElemConceptParams, "amplitude", Double.toString(actImpl.getAmplitude()));
                }

            } else if (activator instanceof LinearActivator) {
                xmlElemConcept.setAttribute("act", Constants.ConceptActivatorTypes.LINEAR.name());
                LinearActivator actImpl = (LinearActivator) activator;

                if (actImpl.getFactor() != LinearActivator.DEFAULT_FACTOR) {
                    addXmlParam(xmlElemConceptParams, "factor", Double.toString(actImpl.getFactor()));
                }
                if (actImpl.getMin() != LinearActivator.DEFAULT_MIN) {
                    addXmlParam(xmlElemConceptParams, "min", Double.toString(actImpl.getMin()));
                }
                if (actImpl.getMax() != LinearActivator.DEFAULT_MAX) {
                    addXmlParam(xmlElemConceptParams, "max", Double.toString(actImpl.getMax()));
                }

            } else if (activator instanceof NaryActivator) {
                xmlElemConcept.setAttribute("act", Constants.ConceptActivatorTypes.NARY.name());
                NaryActivator actImpl = (NaryActivator) activator;
                if (actImpl.getN() != NaryActivator.DEFAULT_N) {
                    addXmlParam(xmlElemConceptParams, "n", Integer.toString(actImpl.getN()));
                }

            } else if (activator instanceof SigmoidActivator) {
                xmlElemConcept.setAttribute("act", Constants.ConceptActivatorTypes.SIGMOID.name());
                SigmoidActivator actImpl = (SigmoidActivator) activator;
                if (actImpl.getK() != SigmoidActivator.DEFAULT_K) {
                    addXmlParam(xmlElemConceptParams, "k", Double.toString(actImpl.getK()));
                }

            } else if (activator instanceof SignumActivator) {
                xmlElemConcept.setAttribute("act", Constants.ConceptActivatorTypes.SIGNUM.name());
                SignumActivator actImpl = (SignumActivator) activator;
                if (actImpl.getMode() != SignumActivator.DEFAULT_MODE) {
                    addXmlParam(xmlElemConceptParams, "mode", actImpl.getMode().name());
                }
                if (actImpl.getZeroValue() != SignumActivator.DEFAULT_ZERO_VALUE) {
                    addXmlParam(xmlElemConceptParams, "zeroValue", Double.toString(actImpl.getZeroValue()));
                }

            } else if (activator instanceof HyperbolicTangentActivator) {
                xmlElemConcept.setAttribute("act", Constants.ConceptActivatorTypes.TANH.name());

            } else {
                throw new IllegalArgumentException("Unsupported ConceptActivator: " + activator.getClass().getName());
            }

        } catch (Exception ex) {
            throw new RuntimeException("Error visiting concept activator", ex);
        }

        return true;
    }

    public boolean visitConnection(FcmConnection _connection) {
        super.visitConnection(_connection);

        try {
            Element xmlElemConnection = doc.createElement("connection");
            xmlElemConnections.appendChild(xmlElemConnection);
            xmlElemConnection.setAttribute("name", connection.getName());

            appendDescription(doc, xmlElemConnection, connection.getDescription());

            if (connection.getFrom() != null) {
                xmlElemConnection.setAttribute("from", connection.getFrom().getName());
            }
            if (connection.getTo() != null) {
                xmlElemConnection.setAttribute("to", connection.getTo().getName());
            }
            if (connection instanceof WeightedConnection) {
                xmlElemConnection.setAttribute("type", "WEIGHTED");

                WeightedConnection wc = (WeightedConnection) connection;

                Element paramsElem = doc.createElement("params");
                xmlElemConnection.appendChild(paramsElem);

                addXmlParam(paramsElem, "weight", Double.toString(wc.getWeight()));
                if (wc.getDelay() != 0) {
                    addXmlParam(paramsElem, "delay", Integer.toString(wc.getDelay()));
                }

            } else {
                throw new UnsupportedOperationException("FcmConnection implementation not supported: "
                        + connection.getClass().getName());
            }

        } catch (Exception ex) {
            throw new RuntimeException("Error visiting connection", ex);
        }

        return true;
    }

    public void saveTo(OutputStream outputStream) {
        saveTo(new OutputStreamWriter(outputStream));
    }

    public void saveTo(Writer writer) {
        Transformer transformer;
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
        } catch (Exception ex) {
            throw new RuntimeException("Error writing XML output", ex);
        } finally {
            try {
                writer.flush();
                writer.close();
            } catch (IOException ioex) {
                throw new RuntimeException("Error closing stream", ioex);
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

    private static void appendDescription(Document doc, Element element, String description) {
        if (StringUtils.isBlank(description)) {
            return;
        }
        Element elemDescription = doc.createElement("description");
        elemDescription.setTextContent(description);
        element.appendChild(elemDescription);
    }

    private static void addXmlParam(Element paramsElem, String name, String value) {
        Element paramElem = paramsElem.getOwnerDocument().createElement("param");
        paramElem.setAttribute("name", name);
        paramElem.setAttribute("value", value);
        paramsElem.appendChild(paramElem);
    }
}
