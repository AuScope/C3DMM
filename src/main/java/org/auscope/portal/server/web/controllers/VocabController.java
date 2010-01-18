package org.auscope.portal.server.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.beans.factory.annotation.Autowired;
import org.auscope.portal.server.web.service.HttpServiceCaller;
import org.auscope.portal.server.web.service.CSWService;
import org.auscope.portal.server.web.view.JSONModelAndView;
import org.auscope.portal.server.util.PortalPropertyPlaceholderConfigurer;
import org.auscope.portal.vocabs.VocabularyServiceResponseHandler;
import org.auscope.portal.vocabs.Concept;
import org.auscope.portal.csw.CSWRecord;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.HttpClient;
import org.apache.log4j.Logger;

import java.net.URL;
import java.util.List;
import java.util.ArrayList;

import net.sf.json.JSONArray;


/**
 * User: Mathew Wyatt
 * Date: 27/11/2009
 * Time: 11:55:10 AM
 */
@Controller
public class VocabController {

    private Logger logger = Logger.getLogger(getClass());
    private PortalPropertyPlaceholderConfigurer portalPropertyPlaceholderConfigurer;
    private GetMethod method;
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
    public VocabController(PortalPropertyPlaceholderConfigurer portalPropertyPlaceholderConfigurer,
                           CSWService cswService) {

        this.cswService = cswService;
        this.portalPropertyPlaceholderConfigurer = portalPropertyPlaceholderConfigurer;

        String vocabServiceUrl = portalPropertyPlaceholderConfigurer.resolvePlaceholder("HOST.vocabService.url");
        logger.debug("vocabService: " + vocabServiceUrl);

        method = new GetMethod(vocabServiceUrl + "?repository=3DMM&label=*");

        try {
            cswService.updateRecordsInBackground();
        } catch (Exception e) {
            logger.error(e);
        }
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

        //get WMS layers from the CSW service
        CSWRecord[] cswRecords = cswService.getWMSRecords();

        //the main holder for the items
        JSONArray dataItems = new JSONArray();

        for(Concept concept : concepts) {
            //Add the mineral occurrence
            JSONArray tableRow = new JSONArray();

            //title
            tableRow.add(concept.getPreferredLabel());

            //description
            tableRow.add(concept.getScopeNotes());

            //an array for the layer names
            JSONArray layerNames = new JSONArray();

            //an array for the service urls
            JSONArray serviceUrls = new JSONArray();

            //find the urls and layer names from the csw service which were tagged with the given product name
            for(CSWRecord cswRecord : cswRecords) {
                if(cswRecord.containsKeyword(concept.getRDFAbout())) {
                    layerNames.add(cswRecord.getOnlineResourceName());
                    serviceUrls.add(cswRecord.getServiceUrl());
                }
            }

            //the layer names
            tableRow.add(layerNames);

            //urls for the services containing this type of product
            tableRow.add(serviceUrls);

            //add to the list
            dataItems.add(tableRow);
        }

        return new JSONModelAndView(dataItems);
    }
}
