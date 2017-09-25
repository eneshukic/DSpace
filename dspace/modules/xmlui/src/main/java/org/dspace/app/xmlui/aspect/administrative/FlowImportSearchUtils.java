/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dspace.app.xmlui.aspect.administrative;

import java.io.*;
import java.sql.*;
import java.util.*;
import org.apache.cocoon.environment.*;
import org.apache.cocoon.servlet.multipart.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.*;
import org.dspace.importsearch.*;
import org.dspace.app.xmlui.cocoon.servlet.multipart.*;
import org.dspace.app.xmlui.wing.*;
import org.dspace.authorize.*;
//import org.dspace.core.Context;
import org.dspace.core.LogManager;

/**
 *
 * @author Enes
 */
public class FlowImportSearchUtils {
    /** Language Strings */
	
    private static final Message T_upload_failed = new Message("default", "xmlui.administrative.metadataimport.flow.upload_failed");
    private static final Message T_import_failed = new Message("default", "xmlui.administrative.metadataimport.flow.import_failed");

    // Other variables
    
    private static Logger log = Logger.getLogger(FlowImportSearchUtils.class);
    
    public static FlowResult processUpload(Request request) throws SQLException, AuthorizeException, IOException, Exception
{
    FlowResult result = new FlowResult();
    result.setContinue(false);

            Object object = null;

            if(request.get("file") != null) {
                object = request.get("file");
            }

            Part filePart = null;
            File file = null;

            if (object instanceof Part)
            {
                    filePart = (Part) object;
                    file = ((DSpacePartOnDisk)filePart).getFile();
            }

            if (filePart != null && filePart.getSize() > 0)
            {
                    String name = filePart.getUploadName();

                    while (name.indexOf('/') > -1)
                    {
                            name = name.substring(name.indexOf('/') + 1);
                    }

                    while (name.indexOf('\\') > -1)
                    {
                            name = name.substring(name.indexOf('\\') + 1);
                    }

                    //log.info(LogManager.getHeader("searchimport", "loading file"));

                    // Process CSV without import
                    //DSpaceCSV csv = new DSpaceCSV(file, context);
                    /*
                    if (!file.delete())
                    {
                        log.error("Unable to delete  file");
                    }
                    */
                    String myCoordinates;
                    String fileExtension = FilenameUtils.getExtension(name);

                    //MetadataImport mImport = new MetadataImport(context, csv);
                    GmlFguProcess myGmlProcess = new GmlFguProcess(); 
                    if (fileExtension.equals("gml") || fileExtension.equals("GML") || fileExtension.equals("xml") || fileExtension.equals("XML")) {
                        myCoordinates = myGmlProcess.readGML(file);
                    }
                    else if (fileExtension.equals("csv") || fileExtension.equals("CSV")){
                        myCoordinates = myGmlProcess.readCSV(file);
                    }
                    else {
                        myCoordinates = null;
                    }
                    
                    
                    // Success!
                    // Set session and request attributes

                    request.setAttribute("searchspatial", myCoordinates);
                    request.getSession().setAttribute("gml", myGmlProcess);
                    result.setContinue(true);
                    result.setOutcome(true);
                   //result.setMessage(T_upload_successful);
                                               
                                       
            }
           

            return result;
        }
    
}
