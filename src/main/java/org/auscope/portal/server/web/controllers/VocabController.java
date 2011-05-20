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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.auscope.portal.csw.CSWOnlineResource;
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
 * Controller that enables access to vocabulary services.
 */
@Controller
public class VocabController {
    protected final Log log = LogFactory.getLog(getClass());
    private GetMethod method;
    private HttpServiceCaller httpServiceCaller;
    private VocabularyServiceResponseHandler vocabularyServiceResponseHandler;
    private PortalPropertyPlaceholderConfigurer portalPropertyPlaceholderConfigurer;
    private CSWService cswService;
    
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
        this.portalPropertyPlaceholderConfigurer = portalPropertyPlaceholderConfigurer;
        this.cswService = cswService;
        
        String vocabServiceUrl = portalPropertyPlaceholderConfigurer.resolvePlaceholder("HOST.vocabService.url");
        log.debug("vocab service URL: " + vocabServiceUrl);

        this.method = new GetMethod(vocabServiceUrl + "/rdf/getConceptByLabel?C3DMM/*");
    }

    //TODO: optimise this function, loops in loops can't be optimal
    @RequestMapping("/getProducts.do")        
    public ModelAndView getProducts() throws Exception {
            	
    	//update the records if need be
        //cswService.updateRecordsInBackground();

        //query the vocab service
        String vocabResponse = httpServiceCaller.getMethodResponseAsString(method, httpServiceCaller.getHttpClient());

        //extract the concepts from the response9
        List<Concept> concepts = new VocabularyServiceResponseHandler().getConcepts(vocabResponse);

        log.debug("Number of data products retrieved: " + concepts.size());

        //get WMS layers from the CSW service
        CSWRecord[] cswRecords = cswService.getWMSRecordsOfOrg("C3DMM");

        log.debug("Number of WMS layers retrieved: " + cswRecords.length);

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

            //an array for the service URLs
            JSONArray serviceUrls = new JSONArray();

            //find the URLs and layer names from the CSW service which were tagged with the given product name
            for(CSWRecord cswRecord : cswRecords) {
            	if(cswRecord.containsKeyword(concept.getConceptUrn())) {
            		CSWOnlineResource[] resources = cswRecord.getOnlineResources();           	
            		for(CSWOnlineResource resource : resources ) {     			
            			 layerNames.add(resource.getName());
                         serviceUrls.add(resource.getLinkage());
            		}
        		}	
            }            
            
            //the layer names
            tableRow.add(layerNames);

            //URLs for the services containing this type of product
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
    @RequestMapping("/getScalar.do")
    public ModelAndView getScalarQuery( @RequestParam("repository") final String repository,
                                        @RequestParam("label") final String label) throws Exception {
    	String response = ""; 
    	
    	//Attempt to request and parse our response
    	try {    		
    		//Do the request
	    	response = httpServiceCaller.getMethodResponseAsString(new ICSWMethodMaker() {
	            public HttpMethodBase makeMethod() {
//	                GetMethod method = new GetMethod(portalPropertyPlaceholderConfigurer.resolvePlaceholder("HOST.vocabService.url.nvclScalars"));	
	                GetMethod method = new GetMethod(portalPropertyPlaceholderConfigurer.resolvePlaceholder("HOST.vocabService.url")+"/getConceptByLabel");	
                    method.setQueryString(repository+"/"+label);	                
	                
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
    		
    		String extractDefinitionExpression = "/RDF/Concept/definition";
            tempNode = (Node)xPath.evaluate(extractDefinitionExpression, doc, XPathConstants.NODE);
            final String definitionString = tempNode != null ? tempNode.getTextContent() : "";
    		
    		return CreateScalarQueryModel(true,response, scopeNoteString, labelString, definitionString);
    	} catch (Exception ex) {
    		//On error, just return failure JSON (and the response string if any)
    		log.error("getVocabQuery ERROR: " + ex.getMessage());
    	
    		return CreateScalarQueryModel(false,response, "", "", "");
    	}
    }
    
    private JSONModelAndView CreateScalarQueryModel
            (final boolean success, final String data, final String scopeNote, final String label, final String definition) {
    	ModelMap map = new ModelMap();
    	map.put("success", success);
    	map.put("data", data);
    	map.put("scopeNote", scopeNote);
    	map.put("definition", definition);
    	map.put("label", label);
        
        return new JSONModelAndView(map);
    }
    
    /**
     * Get all GA commodity URNs with prefLabels
     * 
     * @param
     */        
    @RequestMapping("getAllCommodities.do")
    public ModelAndView getAllCommodities() throws Exception {

        String response = ""; 
        JSONArray dataItems = new JSONArray();
        
        //Attempt to request and parse our response
        try {
            //Do the request
            response = httpServiceCaller.getMethodResponseAsString(new ICSWMethodMaker() {
                public HttpMethodBase makeMethod() {
                    GetMethod method = new GetMethod(portalPropertyPlaceholderConfigurer.resolvePlaceholder("HOST.vocabService.url")+"/getCommodity");
                    method.setQueryString("commodity_vocab/urn:cgi:classifierScheme:GA:commodity");
                    
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
            log.error("getAllCommodities Exception: " + ex.getMessage());
        
            return new JSONModelAndView(dataItems);
        }
    }    
}
