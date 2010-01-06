//this runs on DOM load - you can access all the good stuff now.
var theglobalexml;
//var host = "http://localhost:8080";
//Ext.Ajax.timeout = 180000; //3 minute timeout for ajax calls
//Ext.Ajax.timeout = 3600000; //3 minute timeout for ajax calls

Ext.onReady(function() {
    var map;
    /*var formFactory = new FormFactory();

    //-----------Complex Features Panel Configurations

    var complexFeaturesStore = new Ext.data.Store({
        proxy: new Ext.data.HttpProxy(new Ext.data.Connection({url: '/getComplexFeatures.do', timeout:180000})),
        reader: new Ext.data.ArrayReader({}, [
            {   name: 'title'           },
            {   name: 'description'     },
            {   name: 'proxyURL'        },
            {   name: 'serviceType'     },
            {   name: 'id'              },
            {   name: 'typeName'        },
            {   name: 'serviceURLs'     },
            {   name: 'layerVisible'    },
            {   name: 'loadingStatus'   },
            {   name: 'iconImgSrc'      },
            {   name: 'iconUrl'         },
            {   name: 'dataSourceImage' },
        ]),
        sortInfo: {field:'title', direction:'ASC'}
    });

    var complexFeaturesRowExpander = new Ext.grid.RowExpander({
        tpl : new Ext.Template(
                '<p>{description} </p><br>'
                )
    });

    var complexFeaturesPanel = new Ext.grid.GridPanel({
        store: complexFeaturesStore,
        columns: [
            complexFeaturesRowExpander,
            {
                id:'title',
                header: "Title",
                width: 160,
                sortable: true,
                dataIndex: 'title'
            }
        ],
        bbar: [
            {
                text:'Add Layer to Map',
                tooltip:'Add Layer to Map',
                iconCls:'add',
                pressed: true,
                handler: function() {
                    var recordToAdd = complexFeaturesPanel.getSelectionModel().getSelected();

                    //add to active layers
                    activeLayersStore.add(recordToAdd);

                    //invoke this layer as being checked
                    activeLayerCheckHandler(complexFeaturesPanel.getSelectionModel().getSelected(), true);

                    //set this record to selected
                    dataProductsPanel.getSelectionModel().selectRecords([recordToAdd], false);
                }
            }
        ],

        stripeRows: true,
        autoExpandColumn: 'title',
        plugins: [complexFeaturesRowExpander],
        viewConfig: {scrollOffset: 0},

        title: 'Feature Layers',
        region:'north',
        split: true,
        height: 200,
        autoScroll: true
    });*/

    //----------- WMS Layers Panel Configurations

    /*var wmsLayersStore = new Ext.data.Store({
        proxy: new Ext.data.HttpProxy({url: '/getWMSLayers.do'}),
        reader: new Ext.data.ArrayReader({}, [
            {   name: 'title'           },
            {   name: 'description'     },
            {   name: 'proxyURL'        },
            {   name: 'serviceType'     },
            {   name: 'id'              },
            {   name: 'typeName'        },
            {   name: 'serviceURLs'     },
            {   name: 'layerVisible'    },
            {   name: 'loadingStatus'   },
            {   name: 'dataSourceImage' }
        ]),
        sortInfo: {field:'title', direction:'ASC'}
    });

    var wmsLayersRowExpander = new Ext.grid.RowExpander({
        tpl : new Ext.Template(
                '<p>{description}</p><br>'
                )
    });

    var wmsLayersPanel = new Ext.grid.GridPanel({
        store: wmsLayersStore,
        columns: [
            wmsLayersRowExpander,
            {
                id:'title',
                header: "Title",
                width: 160,
                sortable: true,
                dataIndex: 'title'
            }
        ],
        bbar: [
            {
                text:'Add Layer to Map',
                tooltip:'Add Layer to Map',
                iconCls:'add',
                pressed:true,
                handler: function() {
                    var recordToAdd = wmsLayersPanel.getSelectionModel().getSelected();

                    //add to active layers
                    activeLayersStore.add(recordToAdd);

                    //invoke this layer as being checked
                    activeLayerCheckHandler(wmsLayersPanel.getSelectionModel().getSelected(), true);

                    //set this record to selected
                    dataProductsPanel.getSelectionModel().selectRecords([recordToAdd], false);
                }
            }
        ],

        stripeRows: true,
        autoExpandColumn: 'title',
        plugins: [wmsLayersRowExpander],
        viewConfig: {scrollOffset: 0},

        title: 'Map Layers',
        region:'north',
        split: true,
        height: 200,
        autoScroll: true
    });*/

    /*var filterButton = new Ext.Button({
        text:'Apply Filter >>',
        tooltip:'Apply Filter',
        disabled: true,
        //iconCls:'remove',
        handler: function() {
            var selectedRecord = dataProductsPanel.getSelectionModel().getSelected();
            wfsHandler(selectedRecord);
        }
    });

    *//**
     * Used to show extra details for querying services
     *//*
    var filterPanel = new Ext.Panel({
        title: "Filter Properties",
        region: 'south',
        split: true,
        width: '100%',
        layout: 'card',
        activeItem: 0,
        height: 200,
        items: [
            {
                html: '<p style="margin:15px;padding:15px;border:1px dotted #999;color:#555;background: #f9f9f9;"> Filter options will be shown here for special services.</p>'
            }
        ],
        bbar: ['->', filterButton]
    });

    var activeLayersStore = new Ext.data.Store({
        reader: new Ext.data.ArrayReader({}, [
            {   name:'title'            },
            {   name:'description'      },
            {   name:'proxyURL'         },
            {   name: 'serviceType'     },
            {   name: 'id'              },
            {   name: 'typeName'        },
            {   name: 'serviceURLs'     },
            {   name: 'layerVisible'    },
            {   name: 'loadingStatus'   },
            {   name: 'iconImgSrc'      },
            {   name: 'iconUrl'         },
            {   name: 'dataSourceImage' }
        ])
    });

    var activeLayerCheckHandler = function(record, isChecked) {
        //set the record to be selected if checked
        dataProductsPanel.getSelectionModel().selectRecords([record], false);

        if (record.get('loadingStatus') == '<img src="js/external/ext-2.2/resources/images/default/grid/loading.gif">') {
            Ext.MessageBox.show({
                title: 'Please wait',
                msg: "There is an operation in process for this layer. Please wait until it is finished.",
                buttons: Ext.MessageBox.OK,
                animEl: 'mb9',
                icon: Ext.MessageBox.INFO
            });
            return;
        }

        if (isChecked) {
            //if filter panel already exists then show it
            if (record.filterPanel != null) {
                filterPanel.getLayout().setActiveItem(record.get('id'));
                filterButton.enable();
                filterButton.toggle(true);
            } else {
                //create updateCSWRecords filter panel for this record
                record.filterPanel = formFactory.getFilterForm(record);

                //if this type doesnt need updateCSWRecords filter panel then just show the default filter panel
                if (record.filterPanel == null) {
                    filterPanel.getLayout().setActiveItem(0);

                    //show the layer on the map
                    if (record.get('serviceType') == 'wfs') {
                        wfsHandler(record);
                    } else if (record.get('serviceType') == 'wms') {
                        wmsHandler(record);
                    }

                } else {
                    //show the filter panel
                    filterPanel.add(record.filterPanel);
                    filterPanel.getLayout().setActiveItem(record.get('id'));
                    filterButton.enable();
                    filterButton.toggle(true);
                }
            }
        } else {
            if (record.get('serviceType') == 'wfs') {
                if (record.tileOverlay instanceof MarkerManager) record.tileOverlay.clearMarkers();
            } else if (record.get('serviceType') == 'wms') {
                //remove from the map
                map.removeOverlay(record.tileOverlay);
            }

            filterPanel.getLayout().setActiveItem(0);
            filterButton.disable();
        }
    };
    */

    var activeLayerCheckHandler = function(record, isChecked) { };

    /*var wfsHandler = function(selectedRecord) {
        //if there is already updateCSWRecords filter running for this record then don't call another
        if (selectedRecord.get('loadingStatus') == '<img src="js/external/ext-2.2/resources/images/default/grid/loading.gif">') {
            Ext.MessageBox.show({
                title: 'Please wait',
                msg: "There is an operation in process for this layer. Please wait until it is finished.",
                buttons: Ext.MessageBox.OK,
                animEl: 'mb9',
                icon: Ext.MessageBox.INFO
            });
            return;
        }

        if (selectedRecord.tileOverlay instanceof MarkerManager) selectedRecord.tileOverlay.clearMarkers();

        //a response status holder
        selectedRecord.responseTooltip = new ResponseTooltip();

        var serviceURLs = selectedRecord.get('serviceURLs');
        var proxyURL = selectedRecord.get('proxyURL');
        var iconUrl = selectedRecord.get('iconUrl');

        var finishedLoadingCounter = serviceURLs.length;
        // var markerOverlay = new MarkerOverlay();

        var markerManager = new MarkerManager(map);
        selectedRecord.tileOverlay = markerManager;

        //set the status as loading for this record
        selectedRecord.set('loadingStatus', '<img src="js/external/ext-2.2/resources/images/default/grid/loading.gif">');

        var filterParameters = filterPanel.getLayout().activeItem == filterPanel.getComponent(0) ? "&typeName=" + selectedRecord.get('typeName') : filterPanel.getLayout().activeItem.getForm().getValues(true);

        for (var i = 0; i < serviceURLs.length; i++) {
            handleQuery(serviceURLs[i], selectedRecord, proxyURL, iconUrl, markerManager, filterParameters, function() {
                //decrement the counter
                finishedLoadingCounter--;

                //check if we can set the status to finished
                if (finishedLoadingCounter <= 0) {
                    selectedRecord.set('loadingStatus', '<img src="js/external/ext-2.2/resources/images/default/grid/done.gif">');
                }
            });
        }
    };

    var handleQuery = function(serviceUrl, selectedRecord, proxyURL, iconUrl, markerManager, filterParameters, finishedLoadingHandler) {
        selectedRecord.responseTooltip.addResponse(serviceUrl, "Loading...");
        GDownloadUrl(proxyURL + '?' + filterParameters + '&serviceUrl=' + serviceUrl, function(data, responseCode) {
            if (responseCode == 200) {
                var jsonResponse = eval('(' + data + ')');
                if (jsonResponse.success) {
                    var icon = new GIcon(G_DEFAULT_ICON, iconUrl);
                    icon.iconSize = new GSize(32, 32);
                    var markers = new KMLParser(jsonResponse.data.kml).makeMarkers(icon, function(marker) {
                        marker.typeName = selectedRecord.get('typeName');
                        marker.wfsUrl = serviceUrl;
                    });
                    markerManager.addMarkers(markers, 0);
                    markerManager.refresh();

                    //store the gml for later download needs
                    selectedRecord.gml = jsonResponse.data.gml;

                    //store the status
                    selectedRecord.responseTooltip.addResponse(serviceUrl, markers.length + " records retrieved.");
                } else {
                    //store the status
                    selectedRecord.responseTooltip.addResponse(serviceUrl, jsonResponse.msg);
                }
                //markerOverlay.addList(markers);
            } else if (responseCode == -1) {
                //store the status
                selectedRecord.responseTooltip.addResponse(serviceUrl, "Data request timed out. Please try again later.");
            } else {
                //store the status
                selectedRecord.responseTooltip.addResponse(serviceUrl, "Request resulted in error. Please try again later.");
            }

            //we are finito
            finishedLoadingHandler();
        });
    };*/

    var wmsHandler = function(record) {
        var tileLayer = new GWMSTileLayer(map, new GCopyrightCollection(""), 1, 17);
        tileLayer.baseURL = record.get('serviceURLs')[0];
        tileLayer.layers = record.get('typeName');

        //TODO: remove code specific to feature types and styles specific to GSV
        if (record.get('typeName') == 'gsmlGeologicUnit')
            tileLayer.styles = 'ColorByLithology';
        //if (record.get('id') == '7')
        //    tileLayer.styles = '7';

        record.tileOverlay = new GTileLayerOverlay(tileLayer);
        map.addOverlay(record.tileOverlay);
    };

    /*var activeLayerSelectionHandler = function(sm, index, record) {
        //if its not checked then don't do any actions
        if (record.get('layerVisible') == false) {
            filterPanel.getLayout().setActiveItem(0);
            filterButton.disable();
        } else if (record.filterPanel != null) {//if filter panel already exists then show it
            filterPanel.getLayout().setActiveItem(record.get('id'));
            filterButton.enable();
            filterButton.toggle(true);
        } else {//if this type doesnt need updateCSWRecords filter panel then just show the default filter panel
            filterPanel.getLayout().setActiveItem(0);
            filterButton.disable();
        }
    }; */

    var dataProductStore = new Ext.data.Store({
        proxy: new Ext.data.HttpProxy({url: '/getProducts.do'}),
        reader: new Ext.data.ArrayReader({}, [
            {   name:'title'            },
            {   name:'description'      },
            {   name:'layers'           },
            {   name: 'serviceURLs'     }
        ])
    });

    var dataProductSelectionHandler = function(sm, index, record) {

    };

    // custom column plugin example
    var activeLayersPanelCheckColumn = new Ext.grid.CheckColumn({
        header: "Visible",
        dataIndex: 'layerVisible',
        width: 55,
        handler: activeLayerCheckHandler
    });

    var activeLayersPanelExpander = new Ext.grid.RowExpander({
        tpl : new Ext.Template(
                '<p>{description}</p><br>'
                )
    });

    this.dataProductsPanel = new Ext.grid.GridPanel({
        store: dataProductStore,
        columns: [
            activeLayersPanelExpander,
            {
                id:'title',
                header: "Title",
                width: 160,
                sortable: true,
                dataIndex: 'title'
            }
            //activeLayersPanelCheckColumn
            //{header: "Price", width: 75, sortable: true, dataIndex: 'price'},
            //{header: "Change", width: 75, sortable: true, dataIndex: 'change'},
            //{header: "% Change", width: 75, sortable: true, dataIndex: 'pctChange'},
            //{header: "Last Updated", width: 85, sortable: true, dataIndex: 'lastChange'}
        ],
        sm: new Ext.grid.RowSelectionModel({
            singleSelect: true,
            listeners: {
                rowselect: {
                    fn: dataProductSelectionHandler
                }
            }
        }),

        plugins: [activeLayersPanelCheckColumn, activeLayersPanelExpander],

        stripeRows: true,
        autoExpandColumn: 'title',
        viewConfig: {scrollOffset: 0},

        title: 'Available Data Products',
        region:'center',
        split: true,
        height: 200,
        autoScroll: true
    });

    /**
     * Tooltip for the active layers
     */
    var activeLayersToolTip;

    /**
     * Handler for mouse over events on the active layers panel, things like server status, and download buttons
     */
    this.dataProductsPanel.on('mouseover', function(e, t) {
        e.stopEvent();

        var row = e.getTarget('.x-grid3-row');
        var col = e.getTarget('.x-grid3-col');

        //if there is no visible tooltip then create one, if on is visible already we dont want to layer another one on top
        if (col != null && (activeLayersToolTip == null || !activeLayersToolTip.isVisible())) {

            //get the actual data record
            var theRow = dataProductsPanel.getView().findRow(row);
            var record = dataProductsPanel.getStore().getAt(theRow.rowIndex);

            //this is the status icon column
            if (col.cellIndex == '2') {
                var html = 'No status has been recorded.';

                if (record.responseTooltip != null)
                    html = record.responseTooltip.getHtml();

                activeLayersToolTip = new Ext.ToolTip({
                    target: e.target ,
                    title: 'Status Information',
                    autoHide : true,
                    html: html ,
                    anchor: 'bottom',
                    trackMouse: true,
                    showDelay:60,
                    autoHeight:true,
                    autoWidth: true
                });
            }
            //this is the column for download link icons
            else if (col.cellIndex == '5') {
                var serviceType = record.get('serviceType');
                //var html = 'Download layer data.';

                /*if (serviceType == 'wms') { //if a WMS
                    html = 'Click here to view this layers Image in new browser window.';
                } else if (serviceType == 'wfs') {//if a WFS
                    html = 'Click here to view this layers GML in new browser window.';
                }*/

                activeLayersToolTip = new Ext.ToolTip({
                    target: e.target ,
                    //title: 'Status Information',
                    autoHide : true,
                    html: 'Download data for this layer.' ,
                    anchor: 'bottom',
                    trackMouse: true,
                    showDelay:60,
                    autoHeight:true,
                    autoWidth: true
                });
            }
        }
    });

    /**
     * Handler for click events on the active layers panel, used for the
     * new browser window popup which shows the GML or WMS image
     */
    this.dataProductsPanel.on('click', function(e, t) {
        e.stopEvent();

        //alert('clicked');

        var row = e.getTarget('.x-grid3-row');
        var col = e.getTarget('.x-grid3-col');

        // if there is no visible tooltip then create one, if on is
        // visible already we don't want to layer another one on top
        //if (col != null) {

        //get the actual data record
        var theRow = dataProductsPanel.getView().findRow(row);
        var record = dataProductsPanel.getStore().getAt(theRow.rowIndex);

            //this is the column for download link icons
            //if (col.cellIndex == '5') {
                //var serviceType = record.get('serviceType');

                //if (serviceType == 'wms') { //if a WMS, open a new window calling the download controller
        var serviceUrls = record.get('serviceURLs');
        var layers = record.get('layers');

                map.clearOverlays();

                if (serviceUrls.length >= 1) {
                        var urlsParameter = '';

                        //var filterParameters = filterPanel.getLayout().activeItem == filterPanel.getComponent(0) ? "&typeName=" + selectedRecord.get('typeName') : filterPanel.getLayout().activeItem.getForm().getValues(true);
                        var keys = [serviceUrls.length];
                        var values = [serviceUrls.length];

                        for (i = 0; i < serviceUrls.length; i++) {

                            var tileLayer = new GWMSTileLayer(map, new GCopyrightCollection(""), 1, 17);
                            tileLayer.baseURL = serviceUrls[i];
                            tileLayer.layers = layers[i];

                            //TODO: remove code specific to feature types and styles specific to GSV
                            //if (record.get('typeName') == 'gsmlGeologicUnit')
                            //    tileLayer.styles = 'ColorByLithology';
                            //if (record.get('id') == '7')
                            //    tileLayer.styles = '7';

                            record.tileOverlay = new GTileLayerOverlay(tileLayer);
                            map.addOverlay(record.tileOverlay);

                            /*var boundBox = (map.getBounds().getSouthWest().lng() < 0 ? map.getBounds().getSouthWest().lng() + 360.0 : map.getBounds().getSouthWest().lng()) + "," +
                                           map.getBounds().getSouthWest().lat() + "," +
                                           (map.getBounds().getNorthEast().lng() < 0 ? map.getBounds().getNorthEast().lng() + 360.0 : map.getBounds().getNorthEast().lng()) + "," +
                                           map.getBounds().getNorthEast().lat();

                            var url = serviceUrls[i];

                            url += "&REQUEST=GetMap";
                            url += "&SERVICE=WMS";
                            url += "&VERSION=1.1.0";
                            url += "&LAYERS=" + layers[i];
                            *//**//*if (this.styles)
                             url += "&STYLES=" + this.styles;
                             if (this.sld)
                             url += "&SLD=" + this.sld;*//**//*
                            url += "&FORMAT=" + "image/png";
                            url += "&BGCOLOR=0xFFFFFF";
                            url += "&TRANSPARENT=TRUE";
                            url += "&SRS=" + "EPSG:4326";
                            url += "&BBOX=" + boundBox;
                            url += "&WIDTH=" + map.getSize().width;
                            url += "&HEIGHT=" + map.getSize().height;

                            //urlsParameter += "serviceUrls=" + url + '&';
                            keys[i] = 'serviceUrls';
                            values[i] = url;*/
                        }
                        //alert("downloadProxy?" + url);
                        //openWindowWithPost("downloadWMSAsZip.do?", 'WMS_Layer_Download_'+new Date().getTime(), keys, values);
                    }

                /*} else if (serviceType == 'wfs') {//if a WFS open a new window calling the download controller
                    var serviceUrls = record.get('serviceURLs');
                    if (serviceUrls.length >= 1) {
                        var urlsParameter = '';

                        var filterParameters = filterPanel.getLayout().activeItem == filterPanel.getComponent(0) ? "&typeName=" + record.get('typeName') : filterPanel.getLayout().activeItem.getForm().getValues(true);

                        var keys = [serviceUrls.length];
                        var values = [serviceUrls.length];

                        for (i = 0; i < serviceUrls.length; i++) {
                            //urlsParameter += "serviceUrls=" + serviceUrls[i] + filterParameters.replace('&', '%26') + '&';
                            keys[i] = 'serviceUrls';
                            values[i] =  window.location.protocol + "//" + window.location.host + record.get('proxyURL') + "?" + filterParameters + "&serviceUrl=" + serviceUrls[i];
                        }

                        //alert("downloadProxy?" + url);
                        //window.open("downloadAsZip.do?" + urlsParameter, '');
                        openWindowWithPost("downloadGMLAsZip.do?", 'WFS_Layer_Download_'+new Date().getTime(), keys, values);
                    }
                }*/
          //  }
        //}
    });

    /**
     * Opens a new window and submits a POST request to a given URL for given
     * key value pairs. This approach is needed, because it is tricky to send
     * a list of URLs through a GET request URL.
     *
     * @param url
     * @param name
     * @param keys
     * @param values
     */
    var openWindowWithPost = function(url, name, keys, values)
    {
        var newWindow = window.open(url,name);
        if (!newWindow) return false;
        var html = "";
        var html = ""
        html += "<html><head></head><body><form id='formid' method='post' action='" + url + "'>";
        if (keys && values && (keys.length == values.length))
            for (var i = 0; i < keys.length; i++)
                html += "<input type='hidden' name='" + keys[i] + "' value='" + values[i] + "'/>";
        html += "</form><script type='text/javascript'>document.getElementById(\"formid\").submit()</script></body></html>";
        newWindow.document.write(html);
        return newWindow;
    }

    /**
     * Buttons for things like downloading datasets
     */
    /*var buttonsPanel = new Ext.FormPanel({
     region: 'south',
     autoScroll:true,
     width: '100%',
     items: [{border: false}],
     buttons: [{text: "Download Datasets", handler: function() {downloadController(downloadUrls);} }]
     });*/

    // basic tabs 1, built from existing content
    /*var tabsPanel = new Ext.TabPanel({
        //renderTo: 'tabs1',
        //width:450,
        activeTab: 0,
        //title: 'Themes',
        region:'north',
        split: true,
        height: 200,
        autoScroll: true,
        items:[
            complexFeaturesPanel,
            wmsLayersPanel
        ]
    });*/

    /**
     * Used as updateCSWRecords placeholder for the tree and details panel on the left of screen
     */
    var westPanel = {
        layout: 'border',
        region:'west',
        border: false,
        split:true,
        margins: '100 0 0 0',
        width: 400,

        items:[dataProductsPanel]
    };

    /**
     * This center panel will hold the google maps
     */
    var centerPanel = new Ext.Panel({
        region: 'center',
        id: 'center_region',
        margins: '100 0 0 0',
        cmargins:'100 0 0 0'
    });

    /**
     * Add all the panels to the viewport
     */
    var viewport = new Ext.Viewport({
        layout:'border',
        items:[westPanel, centerPanel]
    });

    // Is user's browser suppported by Google Maps?
    if (GBrowserIsCompatible()) {

        map = new GMap2(centerPanel.body.dom);

        map.setUIToDefault();

        //add google earth
        map.addMapType(G_SATELLITE_3D_MAP);

        // Large pan and zoom control
        //map.addControl(new GLargeMapControl(),  new GControlPosition(G_ANCHOR_TOP_LEFT));

        // Toggle between Map, Satellite, and Hybrid types
        map.addControl(new GMapTypeControl());

        var startZoom = 4;
        map.setCenter(new google.maps.LatLng(-26, 133.3), 4);
        map.setMapType(G_SATELLITE_MAP);

        //Thumbnail map
        var Tsize = new GSize(150, 150);
        map.addControl(new GOverviewMapControl(Tsize));

        map.addControl(new DragZoomControl(), new GControlPosition(G_ANCHOR_TOP_RIGHT, new GSize(345, 7)));
    }

    // Fix for IE resize problem
    if(Ext.isIE){
        map.checkResize();
        Ext.get('center_region').on('resize',function(){
            map.checkResize();
        },this);
    }

    //updateCSWRecords dud gloabal for geoxml class
    theglobalexml = new GeoXml("theglobalexml", map, null, null);

    //event handlers and listeners
    //tree.on('click', function(node, event) { treeNodeOnClickController(node, event, viewport, filterPanel); });
    //tree.on('checkchange', function(node, isChecked) { treeCheckChangeController(node, isChecked, map, statusBar, viewport, downloadUrls, filterPanel); });

  /*  //when updateCSWRecords person clicks on updateCSWRecords marker then do something
    GEvent.addListener(map, "click", function(overlay, latlng) {
        gMapClickController(map, overlay, latlng, statusBar, viewport, activeLayersStore);
    });*/

    new Ext.LoadMask(dataProductsPanel.el, {msg: 'Please Wait...', store: dataProductStore});
    //new Ext.LoadMask(complexFeaturesPanel.el, {msg: 'Please Wait...', store: complexFeaturesStore});
    //new Ext.LoadMask(wmsLayersPanel.el, {msg: 'Please Wait...', store: wmsLayersStore});

    //complexFeaturesStore.load();
    //wmsLayersStore.load();
    dataProductStore.load();
});