package org.auscope.portal.server.web.controllers;

import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import net.sf.json.JSONArray;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;

import org.apache.log4j.Logger;

import org.auscope.portal.csw.CSWRecord;
import org.auscope.portal.csw.ICSWMethodMaker;
import org.auscope.portal.server.util.PortalPropertyPlaceholderConfigurer;
import org.auscope.portal.server.web.service.CSWService;
import org.auscope.portal.server.web.service.HttpServiceCaller;
import org.auscope.portal.server.web.view.JSONModelAndView;
import org.auscope.portal.vocabs.Concept;
import org.auscope.portal.vocabs.VocabularyServiceResponseHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.InputSource;

/**
 * User: Mathew Wyatt, Michael Stegherr
 * Date: 27/11/2009
 * Time: 11:55:10 AM
 */
@Controller
public class VocabController {

    private Logger logger = Logger.getLogger(getClass());
    private GetMethod method;
    private HttpServiceCaller httpServiceCaller;
    private VocabularyServiceResponseHandler vocabularyServiceResponseHandler;
    private PortalPropertyPlaceholderConfigurer portalPropertyPlaceholderConfigurer;
    private CSWService cswService;
    

    public static void main(String[] args) throws Exception {
        String rdfResponse = new HttpServiceCaller().getMethodResponseAsString(new GetMethod("http://auscope-services-test.arrc.csiro.au/vocab-service/query?repository=3DMM&label=*"), new HttpClient());

        List<Concept> concepts = new VocabularyServiceResponseHandler().getConcepts(rdfResponse);

        for(Concept concept : concepts)
            System.out.println(concept.getPreferredLabel());

    }

    /**
     * Construct
     * @param
     */
    @Autowired
    public VocabController(HttpServiceCaller httpServiceCaller,
                           VocabularyServiceResponseHandler vocabularyServiceResponseHandler,
                           PortalPropertyPlaceholderConfigurer portalPropertyPlaceholderConfigurer,
                           CSWService cswService) {

        this.httpServiceCaller = httpServiceCaller;
        this.vocabularyServiceResponseHandler = vocabularyServiceResponseHandler;
        this.cswService = cswService;
        
        
        String vocabServiceUrl = portalPropertyPlaceholderConfigurer.resolvePlaceholder("HOST.vocabService.url");
        logger.debug("vocab service URL: " + vocabServiceUrl);

        this.method = new GetMethod(vocabServiceUrl + "?repository=3DMM&label=*");

        try {
            this.cswService.updateRecordsInBackground();
        } catch (Exception e) {
            logger.error(e);
        }
        
/*        this.method = new GetMethod(vocabServiceUrl);

        //set all of the parameters
        NameValuePair repo     = new NameValuePair("repository", "commodity_vocab");
        //NameValuePair property = new NameValuePair("property1", "skos:inScheme");
        //NameValuePair value    = new NameValuePair("property_value1", "<urn:cgi:classifierScheme:GA:commodity>");
        NameValuePair property = new NameValuePair("property2", "<urn:cgi:classifierScheme:GA:commodity>");
        
        //attach them to the method
        this.method.setQueryString(new NameValuePair[]{repo, property});*/
        this.portalPropertyPlaceholderConfigurer = portalPropertyPlaceholderConfigurer;
    }
    
    //TODO: optimise this function, loops in loops can't be optimal
    @RequestMapping("/getProducts.do")
    public ModelAndView getProducts() throws Exception {
        //update the records if need be
        cswService.updateRecordsInBackground();

        //query the vocab service
        String vocabResponse = new HttpServiceCaller().getMethodResponseAsString(method, new HttpClient());

        //extract the concepts from the response
        List<Concept> concepts = new VocabularyServiceResponseHandler().getConcepts(vocabResponse);

        logger.debug("Number of data products retrieved: " + concepts.size());

        //get WMS layers from the CSW service
        CSWRecord[] cswRecords = cswService.getWMSRecordsOfOrg("C3DMM");

        logger.debug("Number of WMS layers retrieved: " + cswRecords.length);

        //the main holder for the items
        JSONArray dataItems = new JSONArray();

        for(Concept concept : concepts) {
            JSONArray tableRow = new JSONArray();

            //title
            tableRow.add(concept.getPreferredLabel());

            //description
            tableRow.add(concept.getScopeNotes());

            //add the contact organisation
            /*String org = record.getContactOrganisation();

            //skip if not C3DMM
            if (org.compareTo("C3DMM") != 0)
            	continue;
*/            
            tableRow.add("");

            //wms dont need a proxy url
            tableRow.add("");

            //add the type: wfs or wms
            tableRow.add("wms");

            //TODO: add a proper unique id
            tableRow.add(concept.hashCode());
            
            //an array for the layer names
            JSONArray layerNames = new JSONArray();

            //an array for the service urls
            JSONArray serviceUrls = new JSONArray();

            //find the urls and layer names from the csw service which were tagged with the given product name
            for(CSWRecord cswRecord : cswRecords) {
                if(cswRecord.containsKeyword(concept.getConceptUrn())) {
                    layerNames.add(cswRecord.getOnlineResourceName());
                    serviceUrls.add(cswRecord.getServiceUrl());
                }
            }

            //the layer names
            tableRow.add(layerNames);

            //urls for the services containing this type of product
            tableRow.add(serviceUrls);

            tableRow.element(true);
            tableRow.add("<img src='js/external/extjs/resources/images/default/grid/done.gif'>");

            tableRow.add("<a href='http://portal.auscope.org' id='mylink' target='_blank'><img src='img/picture_link.png'></a>");
            
            tableRow.add("1.0");

            //add to the list
            dataItems.add(tableRow);
        }

        return new JSONModelAndView(dataItems);
    }
    
    /**
     * Performs a query to the vocabulary service on behalf of the client and returns a JSON Map
     * success: Set to either true or false
     * data: The raw XML response
     * scopeNote: The scope note element from the response
     * label: The label element from the response
     * @param repository
     * @param label
     * @return
     */
/*    @RequestMapping("/getScalar.do")
    public ModelAndView getScalarQuery(@RequestParam("repository") final String repository,
    								 @RequestParam("label") final String label) throws Exception {
    	String response = ""; 
    	
    	//Attempt to request and parse our response
    	try {
    		//Do the request
	    	response = httpServiceCaller.getMethodResponseAsString(new ICSWMethodMaker() {
	            public HttpMethodBase makeMethod() {
	                GetMethod method = new GetMethod(portalPropertyPlaceholderConfigurer.resolvePlaceholder("HOST.vocabService.url"));
	
	                //set all of the parameters
	                NameValuePair request = new NameValuePair("repository", repository);
	                NameValuePair elementSet = new NameValuePair("label", label);
	
	                //attach them to the method
	                method.setQueryString(new NameValuePair[]{request, elementSet});
	
	                return method;
	            }
	        }.makeMethod(), httpServiceCaller.getHttpClient());
	    	
	    	//Parse the response
	    	XPath xPath = XPathFactory.newInstance().newXPath();
	    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        InputSource inputSource = new InputSource(new StringReader(response));
            Document doc = builder.parse(inputSource);
            
            String extractLabelExpression = "/RDF/Concept/prefLabel";
            Node tempNode = (Node)xPath.evaluate(extractLabelExpression, doc, XPathConstants.NODE);
    		final String labelString = tempNode != null ? tempNode.getTextContent() : "";
    		
    		String extractScopeExpression = "/RDF/Concept/scopeNote";
    		tempNode = (Node)xPath.evaluate(extractScopeExpression, doc, XPathConstants.NODE);
    		final String scopeNoteString = tempNode != null ? tempNode.getTextContent() : "";
    		
    		return CreateScalarQueryModel(true,response, scopeNoteString, labelString);
    	} catch (Exception ex) {
    		//On error, just return failure JSON (and the response string if any)
    		logger.error("getVocabQuery ERROR: " + ex.getMessage());
    	
    		return CreateScalarQueryModel(false,response, "", "");
    	}
    }
    
    private JSONModelAndView CreateScalarQueryModel(final boolean success, final String data, final String scopeNote, final String label) {
    	ModelMap map = new ModelMap() {{
            put("success", success);
            put("data", data);
            put("scopeNote", scopeNote);
            put("label", label);
        }};
        
        return new JSONModelAndView(map);
    }*/

    /**
     * Get all GA commodity URNs with prefLabels
     * 
     * @param
     */
/*    @RequestMapping("/getCommodities.do")
    public ModelAndView getCommodities() throws Exception {

        logger.debug("vocab service query: " + this.method.getQueryString());

        //query the vocab service
        String vocabResponse = this.httpServiceCaller.getMethodResponseAsString(this.method, new HttpClient());

        //extract the concepts from the response
        List<Concept> concepts = this.vocabularyServiceResponseHandler.getConcepts(vocabResponse);

        //the main holder for the items
        JSONArray dataItems = new JSONArray();

        for(Concept concept : concepts) {

            JSONArray tableRow = new JSONArray();

            //URN
            tableRow.add(concept.getConceptUrn());

            //label
            tableRow.add(concept.getPreferredLabel());

            //add to the list
            dataItems.add(tableRow);
        }

        return new JSONModelAndView(dataItems);
    }*/
    
    
    /**
     * Get all GA commodity URNs with prefLabels
     * 
     * @param
     */        
/*    @RequestMapping("/getAllCommodities.do")
    public ModelAndView getAllCommodities() throws Exception {

        String response = ""; 
        JSONArray dataItems = new JSONArray();
        
        //Attempt to request and parse our response
        try {
            //Do the request
            response = httpServiceCaller.getMethodResponseAsString(new ICSWMethodMaker() {
                public HttpMethodBase makeMethod() {
                    GetMethod method = new GetMethod(portalPropertyPlaceholderConfigurer.resolvePlaceholder("HOST.vocabService.url"));
                        
                    //set all of the parameters
                    NameValuePair request   = new NameValuePair("repository", "commodity_vocab");
                    NameValuePair elementSet = new NameValuePair("property2", "<urn:cgi:classifierScheme:GA:commodity>");
    
                    //attach them to the method
                    method.setQueryString(new NameValuePair[]{request, elementSet});
    
                    return method;
                }
            }.makeMethod(), httpServiceCaller.getHttpClient());

            // Parse the response            
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();            
            InputSource inputSource = new InputSource(new StringReader(response));
            Document doc = builder.parse(inputSource); 
            
            XPath xPath = XPathFactory.newInstance().newXPath();
            NodeList exprResult = (NodeList)xPath.evaluate("/sparql/results/result", doc, XPathConstants.NODESET);            
            
            JSONArray tableRow;

            for (int i=0; i < exprResult.getLength(); i++) {                

                Element result = (Element)exprResult.item(i);

                tableRow = new JSONArray();

                tableRow.add(result.getElementsByTagName("uri").item(0).getTextContent());
                tableRow.add(result.getElementsByTagName("literal").item(0).getTextContent());

                //add to the list
                dataItems.add(tableRow);
            }
            
            return new JSONModelAndView(dataItems);
            
        } catch (Exception ex) {
            //On error, just return failure JSON (and the response string if any)
            logger.error("getAllCommodities Exception: " + ex.getMessage());
        
            return new JSONModelAndView(dataItems);
        }
    }*/
}