package org.auscope.portal.server.web.controllers;

import javax.servlet.http.HttpServletResponse;

import org.auscope.portal.csw.CSWOnlineResource;
import org.auscope.portal.csw.CSWRecord;
import org.auscope.portal.csw.CSWOnlineResource.OnlineResourceType;
import org.auscope.portal.server.util.PortalPropertyPlaceholderConfigurer;
import org.auscope.portal.server.web.service.CSWService;
import org.auscope.portal.server.web.view.CSWRecordResponse;
import org.auscope.portal.server.web.view.ViewCSWRecordFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * @version $Id: CSWController.java 1315 2010-10-18 01:13:39Z mrt $
 */
@Controller
public class CSWController extends CSWRecordResponse {

    private CSWService cswService;
    private ViewCSWRecordFactory viewCSWRecordFactory;

    public CSWController(ViewCSWRecordFactory viewCSWRecordFactory) {
    	this.viewCSWRecordFactory = viewCSWRecordFactory;
    }

    /**
     * Constructor
     * @param
     */
    @Autowired
    public CSWController(CSWService cswService,
                         ViewCSWRecordFactory viewCSWRecordFactory,
                         PortalPropertyPlaceholderConfigurer propertyResolver) {

        this.cswService = cswService;
        this.viewCSWRecordFactory = viewCSWRecordFactory;

        try {
            cswService.updateRecordsInBackground();
        } catch (Exception e) {
            log.error(e);
        }
    }

    /**
     * This controller method returns a representation of each and every CSWRecord from the internal cache
     * @throws Exception
     */
    @RequestMapping("/getCSWRecords.do")           
    public ModelAndView getCSWRecords(@RequestParam("FilteredLayers") final String[] filteredLayers) throws Exception {
    	   	
    	try {
			this.cswService.updateRecordsInBackground();
		} catch (Exception ex) {
			log.error("Error updating cache", ex);
			return generateJSONResponse(false, "Error updating cache", null);
		}

		CSWRecord[] records = null;
		try {
			if(filteredLayers==null || filteredLayers.length==0)			
				records = this.cswService.getAllRecords();
			else {
				CSWOnlineResource.OnlineResourceType[] types 
					= new CSWOnlineResource.OnlineResourceType[filteredLayers.length];
				for(int i=0; i<filteredLayers.length; i++) {
					System.out.println("filteredLayers["+ i +"] = " +filteredLayers[i]);
					if("WMS".equals(filteredLayers[i])) 
						types[i]= OnlineResourceType.WMS;
					else if("WFS".equals(filteredLayers[i]))
						types[i]= OnlineResourceType.WFS;
					else if("WCS".equals(filteredLayers[i]))
						types[i]= OnlineResourceType.WCS;				
				}
				records = this.cswService.getFilteredRecords(types);
			}						
		} catch (Exception e) {
			log.error("error getting data records", e);
			return generateJSONResponse(false, "Error getting data records", null);
		}
		return generateJSONResponse(this.viewCSWRecordFactory, records);
    }
}

