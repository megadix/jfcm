# JFCM Java Fuzzy Cognitive Maps

## About the project

JFCM is a Java implementation of Fuzzy Cognitive Maps. It's LGPL licence, so you
can use it in any type of project (open and closed source) but modifications to
the library itself must be distributed under a license compatible with LGPL.

## Documentation & Sample code

You can find documentation, examples, etc. on JFCM website:
http://jfcm.megadix.it/

## About the author

My name is Dimitri De Franciscis, I live and work in Italy (Milan area) as freelance consultant. I work mostly on enterprise Java projects, both on the front-end and back-end. I also write for programming-related magazines (online and offline).

You can find more about me on my website:

http://www.megadix.it/

## CHANGELOG

### Release 1.4.2

* fixed XSD:
  * moved CAUCHY, GAUSS, INTERVAL, LINEAR, NARY activator enum to JFCM-map-v-1.2.xsd
* FcmRunner:
  * added method to FcmRunner interface: void run();
  * introduced BaseFcmRunner to ease development of subclasses;
  * added CSV output functionality to SimpleFcmRunner
* changed format of README from Textile to Markdown

### Release 1.4.1

* fixed bug in FcmIO.saveAsXml(): wrong schema version in XML output, uses 1.1 but should be 1.2
* Refactored FcmIO.saveAsXml() to ToXmlVisitor.java

### Release: 1.4.0

* moved ConceptActivator and FcmConncetion implementations in sub-packages:
  * org.megadix.jfcm.act : ConceptActivator implementations;
  * org.megadix.jfcm.conn : FcmConnection implementations;
* implemented Visitor design pattern;
* added missing LGPL headers;
* code cleanup, following Checkstyle indications;
* pom.xml:
  * added project.build.sourceEncoding property;
  * added version to plugins, so Maven doesn't complain about it :)

### Release 1.3.3

* CognitiveMap.reset(): do not set output or fixedOutput if fixedOutput == true
* Concept.startUpdate(): bugfix if conceptActivator == null;
* FcmIO.loadXml(String filename);
* fixed comment in BaseConceptActivator
* Implemented CognitiveMap.description:
  * CognitiveMap: added description property;
  *  FcmIO: load / save
* Concept activator:
  * bug fixes and enhancements;
  * many new ConceptActivator implementations:
    * CauchyActivator;
    * GaussianActivator;
    * IntervalActivator;
    * NaryActivator;
  * updated XSD and namespace: http://www.megadix.org/standards/JFCM-map-v-1.2.xsd
* pull up checkValues() method to AbstractConceptActivatorTest;
  * Concept.setFixedOutput(): changed Boolean (Object, may be null) to boolean (native, never null)
* better toString() methods: CognitiveMap, Concept, WeightedConnection;

### Release 1.3.2

* bugfix to BaseConceptActivator.calculateNextOutput() on null or NaN inputs;
* SignumActivator:
  * added SignumActivator.Mode enum: BIPOLAR (default) and BINARY;
  * added zeroValue property, to let users customize value at zero input;
  * update FcmIO to load and save SignumActivator.mode;
* Concept: implemented equals() / hashCode();
* small fix to SimpleFcmRunner
* BaseConceptActivator: check threshold parameter;
* simpler and easier tests for concept activators.

### Release 1.3.1

* FcmIO: added support for "includePreviousOutput" as concept parameter;
* tests enhancements

### Release 1.3

* BaseConceptActivator.includePreviousOutput;
* change in output calculations: HyperbolicTangentActivator, LinearActivator,
SigmoidActivator, SignumActivator include by default (includePreviousOutput =
true) previous output in calculations;
* Remove Jakarta Commons dependency, added StringUtils as substitute.

### Release 1.2

* map.execute() should really have two phases
* Add method CognitiveMap.reset() that resets every Concept to null
* Create a deep copy constructor on CognitiveMap
* XML output from FcmIO.saveAsXml() should include namespace
* Backport LinearConceptActivator from 2.0-SNAPSHOT
* Backport time delay to WeightedConnection from 2.0-SNAPSHOT

### Release 1.1.0

* moved threshold to BaseConceptActivator;
* bugfix: threshold was not used in transfer functions!
* added LinearConceptActivator;
* modification in XML format:
  * <param> elements now enclosed in <params> element;
  * XSD namespace fix
    * version 1.0 became http://www.megadix.org/standards/JFCM-map-v-1.0.xsd
    (added .xsd suffix)
    * version 1.1 (current):
    http://www.megadix.org/standards/JFCM-map-v-1.1.xsd
  * modification of FcmIO;
  * many new JUnit tests, both for modifications and bugfixes
