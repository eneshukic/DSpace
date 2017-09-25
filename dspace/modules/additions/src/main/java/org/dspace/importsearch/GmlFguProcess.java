/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dspace.importsearch;

import java.io.BufferedReader;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathAPI;
import org.dspace.administer.RegistryImporter;
import org.dspace.authorize.AuthorizeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import org.dspace.core.Context;

import org.xml.sax.SAXException;
/**
 *
 * @author Enes
 */
public class GmlFguProcess {
    /** logging category */
    private static final Logger log = LoggerFactory.getLogger(GmlFguProcess.class);
    
    
    /*
    Read GML and return coordinates in minX minY maxX maxY format
    Handle point, polygon
    String file, File myfile
    */
    public String readGML (File file)throws IOException, TransformerException, ParserConfigurationException, 
    		AuthorizeException, SAXException {
        //addd default coordinates for BiH ???
        String minX = "";
        String minY = "";
        String maxX = "";
        String maxY = "";
        
        // read the XML
        //Document document = RegistryImporter.loadXML(file);
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(file);

       // Get the nodes corresponding to points
       //NodeList pointNodes = XPathAPI.selectNodeList(document, "/dspace-dc-types/dc-schema");
       NodeList pointNodes = document.getElementsByTagName("gml:pos");
       // Add each one as a new format to the registry
        for (int i = 0; i < pointNodes.getLength(); i++)
        {
            Node n = pointNodes.item(i);
            //String point[] = RegistryImporter.getElementData(n, "gml:pos").trim().split(" ");
            if (n !=null){
            //String point[] = n.getNodeValue().trim().split(" ");
            String point[] = n.getTextContent().trim().split(" ");
            //get x, y - should be only one point ignore rest
            if (point.length == 2){
                int decIndex0 = point[0].trim().indexOf(".");
                int decIndex1 = point[1].trim().indexOf(".");
                /*
                String tempX = point[0].trim().substring(0, point[0].trim().indexOf(".")-1);
                String tempY = point[1].trim().substring(0, point[0].trim().indexOf(".")-1);
*/
                String tempX = point[0].trim();
                String tempY = point[1].trim();
                if (decIndex0 > 1)
                {
                    tempX = tempX.substring(0, decIndex0);
                }
                if (decIndex1 > 1)
                {
                    tempY = tempY.substring(0, decIndex1);
                }
                if (tempX.length() > 0) {
                    if (minX.equals("")){
                        minX = tempX;
                    }
                    if (maxX.equals("")){
                        maxX = tempX;
                    }
                    if (Integer.parseInt(minX) > Integer.parseInt(tempX)){
                        minX = tempX;
                    }
                    if (Integer.parseInt(maxX) < Integer.parseInt(tempX)){
                        maxX = tempX;
                    }
                }
                if (tempY.length() > 0) {
                    if (minY.equals("")){
                        minY = tempY;
                    }
                    if (maxY.equals("")){
                        maxY = tempY;
                    }
                    if (Integer.parseInt(minY) > Integer.parseInt(tempY)){
                        minY = tempY;
                    }
                    if (Integer.parseInt(maxY) < Integer.parseInt(tempY)){
                        maxY = tempY;
                    }
                }
            }
            }
        } //end for
        NodeList polNodes = document.getElementsByTagName("gml:posList");
        
        for (int i = 0; i < polNodes.getLength(); i++)
        {
            Node n = polNodes.item(i);
            //String pol[] = RegistryImporter.getElementData(n, "gml:posList").trim().split(" ");
            if (n!=null){
            //String pol[] = polNodes.item(i).getNodeValue().trim().split(" ");
            String pol[] = n.getTextContent().trim().split(" ");
            //get x, y - should have at least one point
            if (pol.length > 1)
            {
                for (int a=0; a < pol.length; a++){
                    //if (a<2) {
                    int decIndex = pol[a].trim().indexOf(".");
                    //String tempP = pol[a].trim().substring(0, pol[a].trim().indexOf(".")-1);
                    String tempP = pol[a].trim();
                    if (decIndex > 0){
                        tempP = tempP.substring(0, decIndex);
                    }
                    if (tempP.length() > 0) {
                        if (a%2 == 0){
                            if (minX.equals("")){
                                minX = tempP;
                            }
                            if (maxX.equals("")){
                                maxX = tempP;
                            }
                            if (Integer.parseInt(minX) > Integer.parseInt(tempP)){
                                minX = tempP;
                            }
                            if (Integer.parseInt(maxX) < Integer.parseInt(tempP)){
                                maxX = tempP;
                            }
                        }
                        else {
                            if (minY.equals("")){
                                minY = tempP;
                            }
                            if (maxY.equals("")){
                                maxY = tempP;
                            }
                            if (Integer.parseInt(minY) > Integer.parseInt(tempP)){
                                minY = tempP;
                            }
                            if (Integer.parseInt(maxY) < Integer.parseInt(tempP)){
                                maxY = tempP;
                            }
                        }
                    }
                }
               // }//end test if
            }
            }
        } //end for
        
        return minX + " " + minY + " " + maxX + " " + maxY;
        
    }
    
    public String readCSV(File f) throws Exception
    {
        
        // Open the CSV file
        BufferedReader input = null;
        String line = "";
        String cvsSplitBy = ",";
        String minX = "";
        String minY = "";
        String maxX = "";
        String maxY = "";
        try
        {
            input = new BufferedReader(new InputStreamReader(new FileInputStream(f),"UTF-8"));
            while ((line = input.readLine()) != null) {

                // use comma as separator
                String[] point = line.split(cvsSplitBy);
                if (point.length == 2){
                    int decIndex0 = point[0].trim().indexOf(".");
                    int decIndex1 = point[1].trim().indexOf(".");

                    String tempX = point[0].trim();
                    String tempY = point[1].trim();
                    if (decIndex0 > 1)
                    {
                        tempX = tempX.substring(0, decIndex0);
                    }
                    if (decIndex1 > 1)
                    {
                        tempY = tempY.substring(0, decIndex1);
                    }
                    if (tempX.length() > 0) {
                        if (minX.equals("")){
                            minX = tempX;
                        }
                        if (maxX.equals("")){
                            maxX = tempX;
                        }
                        if (Integer.parseInt(minX) > Integer.parseInt(tempX)){
                            minX = tempX;
                        }
                        if (Integer.parseInt(maxX) < Integer.parseInt(tempX)){
                            maxX = tempX;
                        }
                    }
                    if (tempY.length() > 0) {
                        if (minY.equals("")){
                            minY = tempY;
                        }
                        if (maxY.equals("")){
                            maxY = tempY;
                        }
                        if (Integer.parseInt(minY) > Integer.parseInt(tempY)){
                            minY = tempY;
                        }
                        if (Integer.parseInt(maxY) < Integer.parseInt(tempY)){
                            maxY = tempY;
                        }
                    }
                }              

            }//while

        } catch (IOException e) {
            e.printStackTrace();
        }
        return minX + " " + minY + " " + maxX + " " + maxY;

    }
    
}
