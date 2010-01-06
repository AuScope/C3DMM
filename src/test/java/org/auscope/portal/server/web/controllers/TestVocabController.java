package org.auscope.portal.server.web.controllers;

import org.junit.Test;
import org.junit.Assert;
import org.auscope.portal.server.web.KnownFeatureTypeDefinition;
import org.auscope.portal.csw.CSWRecord;
import org.jmock.Expectations;
import org.springframework.web.servlet.ModelAndView;

import java.util.Iterator;
import java.io.StringWriter;
import java.io.PrintWriter;

/**
 * User: Mathew Wyatt
 * Date: 04/12/2009
 * Time: 1:43:52 PM
 */
public class TestVocabController {

    /**
     * Test with valid records
     * @throws Exception
     */
    @Test
    public void testGetProducts() throws Exception {
       /* final KnownFeatureTypeDefinition def = new KnownFeatureTypeDefinition("0", "1", "2", "3", "4");
        final String expectedJSONResponse = "[[\"1\",\"2 Institutions: , \",\"3\",\"wfs\","+def.hashCode()+",\"0\",[\"\"],\"true\",\"<img src='js/external/ext-2.2/resources/images/default/grid/done.gif'>\",\"<img width='16' heigh='16' src='4'>\",\"4\",\"<a href='http://portal.auscope.org' id='mylink' target='_blank'><img src='img/page_code.png'><\\/a>\"]]";
        final Iterator mockIterator = context.mock(Iterator.class);
        final StringWriter actualJSONResponse = new StringWriter();
        final CSWRecord mockRecord = context.mock(CSWRecord.class);

        context.checking(new Expectations() {{
            oneOf(cswService).updateRecordsInBackground();
            oneOf(knownTypes).iterator();will(returnValue(mockIterator));
            oneOf(mockIterator).hasNext();will(returnValue(true));
            oneOf(mockIterator).next();will(returnValue(def));
            oneOf(cswService).getWFSRecordsForTypename(def.getFeatureTypeName());will(returnValue(new CSWRecord[]{mockRecord}));

            oneOf(mockRecord).getServiceUrl();
            oneOf(mockRecord).getContactOrganisation();

            oneOf(mockIterator).hasNext();will(returnValue(false));

            //check that the correct response is getting output
            oneOf (mockHttpResponse).setContentType(with(any(String.class)));
            oneOf (mockHttpResponse).getWriter(); will(returnValue(new PrintWriter(actualJSONResponse)));
        }});

        ModelAndView modelAndView = cswController.getComplexFeatures();

        //check that our JSON response has been nicely populated
        //calling the renderer will write the JSON to our mocks
        modelAndView.getView().render(modelAndView.getModel(), mockHttpRequest, mockHttpResponse);

        System.out.println(expectedJSONResponse);
        System.out.println(actualJSONResponse.getBuffer().toString());

        //check that the actual is the expected
        if(expectedJSONResponse.equals(actualJSONResponse.getBuffer().toString()))
            Assert.assertTrue(true);
        else
            Assert.assertFalse(true);*/
    }

}
