package org.auscope.portal.vocabs;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;

/**
 * User: Michael Stegherr
 * Date: 07/09/2009
 * Time: 2:02:06 PM
 */
public class Concept {

    private Node conceptNode;
    private XPath xPath;

    public Concept(Node conceptNode) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
        this.conceptNode = conceptNode;

        XPathFactory factory = XPathFactory.newInstance();
        xPath = factory.newXPath();
        xPath.setNamespaceContext(new VocabularyServiceNamespaceContext());
    }

    public String getPreferredLabel() {
        try {
            XPathExpression expr = xPath.compile("skos:prefLabel");
            Node result = (Node)expr.evaluate(conceptNode, XPathConstants.NODE);
            return result.getTextContent();
        } catch (Exception e) {
            return "";
        }
    }

    public String getSchemeUrn() {
        try {
            XPathExpression expr = xPath.compile("skos:inScheme ");
            Node result = (Node)expr.evaluate(conceptNode, XPathConstants.NODE);
            return result.getAttributes().getNamedItem("rdf:resource").getTextContent();
        } catch (Exception e) {
            return "";
        }
    }

    public String getRDFAbout() {
         try {
             return conceptNode.getAttributes().getNamedItem("rdf:about").getTextContent();
        } catch (Exception e) {
            return "";
        }
    }

    //TODO: this is a temp function, find better place somewhere else
    public String getScopeNotes() {
        try {
            XPathExpression expr = xPath.compile("skos:scopeNote");
            NodeList result = (NodeList)expr.evaluate(conceptNode, XPathConstants.NODESET);

            String scopeNotes = "";

            for(int i = 0; i < result.getLength(); i++) {
                if(result.item(i).getAttributes().getNamedItem("rdf:datatype").getTextContent().equals("http://c3dmm.csiro.au/classifier/hyperspectral_products/scope_note#base_algorithm")) {
                    scopeNotes += " Base Algorithm: " + result.item(i).getTextContent();
                }
                if(result.item(i).getAttributes().getNamedItem("rdf:datatype").getTextContent().equals("http://c3dmm.csiro.au/classifier/hyperspectral_products/scope_note#filters")) {
                    scopeNotes += " Filters: " + result.item(i).getTextContent();
                }
                if(result.item(i).getAttributes().getNamedItem("rdf:datatype").getTextContent().equals("http://c3dmm.csiro.au/classifier/hyperspectral_products/scope_note#lower_stretch_limit")) {
                    scopeNotes += " Lower Stretch Limit: " + result.item(i).getTextContent();
                }
                if(result.item(i).getAttributes().getNamedItem("rdf:datatype").getTextContent().equals("http://c3dmm.csiro.au/classifier/hyperspectral_products/scope_note#upper_stretch_limit")) {
                    scopeNotes += " Upper Stretch Limit: " + result.item(i).getTextContent();
                }
                if(result.item(i).getAttributes().getNamedItem("rdf:datatype").getTextContent().equals("http://c3dmm.csiro.au/classifier/hyperspectral_products/scope_note#stretch_type_and_colour_chart")) {
                    scopeNotes += " Stretch Type and Colour: " + result.item(i).getTextContent();
                }
                if(result.item(i).getAttributes().getNamedItem("rdf:datatype").getTextContent().equals("http://c3dmm.csiro.au/classifier/hyperspectral_products/scope_note#accuracy")) {
                    scopeNotes += " Accuracy: " + result.item(i).getTextContent();
                }
            }

            return scopeNotes;
        } catch (Exception e) {
            return "";
        }
    }
}
