/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dspace.ctask.general;

import com.hp.hpl.jena.sparql.lib.Metadata;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.awt.image.*;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.Iterator;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormatImpl;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import static jdk.nashorn.tools.ShellFunctions.input;
import org.apache.commons.io.FilenameUtils;

import org.dspace.app.util.DCInput;
import org.dspace.app.util.DCInputSet;
import org.dspace.app.util.DCInputsReader;
import org.dspace.app.util.DCInputsReaderException;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Bitstream;
import org.dspace.content.Bundle;
import org.dspace.content.MetadataValue;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.BitstreamService;
import org.dspace.core.Constants;
import org.dspace.curate.AbstractCurationTask;
import org.dspace.curate.Curator;
import org.dspace.curate.Suspendable;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
import org.dspace.core.Utils;
import org.w3c.dom.NodeList;

/**
 *
 * @author Enes Calculate MBR from tfw file and add to Item record
 * dc.spatial.coverage if ti does not exists
 */
public class ExtractMBRfromTFW extends AbstractCurationTask {

    // map of DCInputSets
    protected DCInputsReader reader = null;
    // map of required fields
    protected Map<String, List<String>> reqMap = new HashMap<String, List<String>>();

    static String fieldMBR = "dc.coverage.spatial";
    // The status of this item
    protected int status = Curator.CURATE_UNSET;
    
    private static final BitstreamService bitstreamService = ContentServiceFactory.getInstance().getBitstreamService();

    @Override
    public void init(Curator curator, String taskId) throws IOException {
        super.init(curator, taskId);
        try {
            reader = new DCInputsReader();
        } catch (DCInputsReaderException dcrE) {
            throw new IOException(dcrE.getMessage(), dcrE);
        }
    }

    /**
     * Perform the curation task upon passed DSO
     *
     * @param dso the DSpace object
     * @throws IOException if IO error
     */
    @Override
    public int perform(DSpaceObject dso) throws IOException {
        StringBuilder sb = new StringBuilder();
        int count = 0;
        if (dso.getType() == Constants.ITEM) {
            Item item = (Item) dso;
            //int count = 0;
            try {

                String handle = item.getHandle();
                if (handle == null) {
                    // we are still in workflow - no handle assigned
                    handle = "in workflow";
                }
                sb.append("Item: ").append(handle);

                //first check if item has fieldMBR.  If not delete it
                List<MetadataValue> vals = itemService.getMetadataByMetadataString(item, fieldMBR);
                if (vals.size() == 0) {
                    //get bitstreams
                    boolean changed = false;
                    //to replace existing uncomment line bellow
                    //itemService.clearMetadata(Curator.curationContext(), item, "dc", "coverage", "spatial", Item.ANY);
                    for (Bundle bundle : item.getBundles()) {
                        if ("ORIGINAL".equals(bundle.getName())) {
                            //Bitstream primBitstream = bundle.getPrimaryBitstream();
                            //get first tiff with mbr
                            Bitstream primBitstream = null;
                            String tifName = "";
                            String tfwName = "";
                            for (Bitstream bitstream : bundle.getBitstreams()) {
                                String primExt = FilenameUtils.getExtension(bitstream.getName()); ;
                                 sb.append(" file name " + bitstream.getName());
                                 sb.append(" file ext " + primExt);
                                if (primExt.equalsIgnoreCase("tiff") || primExt.equalsIgnoreCase("tif")) {
                                    primBitstream = bitstream;
                                    tifName = primBitstream.getName();
                                    tfwName = primBitstream.getName().replace("." + primExt, ".tfw");
                                    sb.append(" bitstream process " + tifName);
                                    //search for tfw
                                    for (Bitstream bitstream2 : bundle.getBitstreams()) {

                                        if (bitstream2.getName().equalsIgnoreCase(tfwName)) {
                                            //extract values from tfw and find MBR
                                            sb.append(" tfw found extracting data " + tfwName);
                                            sb.append(" source " + bitstream2.getSource());
                                                                                                                                    
                                            InputStream is = bitstreamService.retrieve(Curator.curationContext(), bitstream2);
                                            BufferedReader input = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                                            
                                            String line = "";
                                            String[] strtfw = new String[6];
                                            int i = 0;
                                            while ((line = input.readLine()) != null || i < 6) {
                                                strtfw[i] = line.trim();
                                                 sb.append(" tfw line " + line.trim());
                                                i++;
                                            }
                                            if (strtfw.length == 6) {
                                                //read tiff hight and width metadata
                                                sb.append(" Tiff name " + primBitstream.getName());
                                                int bsheight = 0;
                                                int bswidth = 0;
                                                //InputStream isp = bitstreamService.retrieve(Curator.curationContext(), primBitstream);
                                                
                                                
                                                //BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream (primBitstream.getName())));
                                               // try (ImageInputStream in = ImageIO.createImageInputStream(primBitstream)) {
                                                //try (ImageInputStream in = ImageIO.createImageInputStream(isp)) {
                                                 try (InputStream in = bitstreamService.retrieve(Curator.curationContext(), primBitstream)) {
                                                    sb.append(" iterators ");
                                                    ImageInputStream iis = ImageIO.createImageInputStream(in);
                                                    Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
                                                    sb.append(" readers ");
                                                    /*String[] extList = ImageIO.getReaderFormatNames();
                                                    for (String s: extList) {           
                                                    //Do your stuff here
                                                    System.out.println(s); 
                                                        }*/
                                                   
                                                    if (readers.hasNext()) {
                                                        sb.append(" has next ");
                                                        //ImageReader reader = readers.next();
                                                       
                                                        ImageReader reader = readers.next();
                                                        try {
                                                            sb.append(" readming tiff ");
                                                            reader.setInput(iis);
                                    
                                                            bswidth = reader.getWidth(0);
                                                            bsheight = reader.getHeight(0);
                                                            sb.append(" width " + Integer.toString(bswidth)+ " height " +Integer.toString(bsheight));
                                                        } catch (Exception e) {
                                                            throw new Exception (e.getMessage());
                                                        }
                                                        
                                                        finally {
                                                            reader.dispose();
                                                        }
                                                    }
                                                }
                                               /*
                                                InputStream isp = bitstreamService.retrieve(Curator.curationContext(), primBitstream);
                                                ImageInputStream iisn = ImageIO.createImageInputStream(isp);
                                                BufferedImage bimg = ImageIO.read(iisn);
                                                
                                                bswidth = bimg.getWidth(null);
                                                bsheight = bimg.getHeight(null);
                                               */
                                                //3rd option
                                                /*InputStream isp = bitstreamService.retrieve(Curator.curationContext(), primBitstream);
                                                 sb.append(" isp ");
                                                
                                                ImageInputStream iisn = ImageIO.createImageInputStream(isp);
                                                 sb.append(" iisn ");
                                                BufferedImage bimg = ImageIO.read(isp);
                                                if (bimg.equals(null)) {
                                                    sb.append(" bimg is null ");
                                                }
                                                 sb.append(" bimg");
                                                
                                                bswidth = Math.round((float) bimg.getWidth(null));
                                                bsheight = Math.round((float) bimg.getHeight(null));
                                                isp.close();*/
                                               /* ImageReader reader = ImageIO.getImageReaders(isp).next();
                                                reader.setInput(isp);
                                                sb.append(" set input ");
                                                bswidth = reader.getWidth(reader.getMinIndex());
                                                bsheight = reader.getHeight(reader.getMinIndex());*/

                                                
                                                sb.append(" width " + Integer.toString(bswidth)+ " height " +Integer.toString(bsheight));
                                                String myMBR = this.getMBR(strtfw, bswidth, bsheight);
                                                if (!myMBR.equals("")) {
                                                    sb.append("adding mbr ");
                                                    itemService.addMetadata(Curator.curationContext(), item, "dc", "coverage", "spatial", Item.ANY, myMBR);
                                                    //count++;
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                    }//for
                                }
                            }                            
                        }
                        if (changed) {
                        //itemService.update(Curator.curationContext(), item);
                        status = Curator.CURATE_SUCCESS;
                    }
                    } //for

                    
                }

            } /*catch (DCInputsReaderException dcrE)
            {
                throw new IOException(dcrE.getMessage(), dcrE);
            } */ catch (SQLException sqlE) {
                status = Curator.CURATE_FAIL;
                throw new IOException(sqlE.getMessage(), sqlE);
            }
             catch (Exception E) {
                status = Curator.CURATE_FAIL;
                report(sb.toString());
                throw new IOException(E.getMessage(), E);
            }
            /* catch (AuthorizeException authE) {
    		throw new IOException(authE.getMessage(), authE);
            }*/
            //return (count == 0) ? Curator.CURATE_SUCCESS : Curator.CURATE_FAIL;
        } else {
            setResult("Object skipped");
            return Curator.CURATE_SKIP;
        }
        report(sb.toString());
        setResult(sb.toString());
        return status;
    }

//get mbr
//use data to calculate mbr then remove any decimal and format it into minx miny maxX max y
    protected String getMBR(String[] myTfw, int iwidth, int iheight) 
    {
            String mbr = "";

            double xcord = Double.parseDouble(myTfw[4]);
            double ycord = Double.parseDouble(myTfw[5]);
            double xpixsize = Double.parseDouble(myTfw[0]);
            double ypixsize = Double.parseDouble(myTfw[3]);
            double r1 = Double.parseDouble(myTfw[1]);
            double r2 = Double.parseDouble(myTfw[2]);
            double xmin = 0.0;
            double ymin = 0.0;
            double xmax = 0.0;
            double ymax = 0.0;

            xmin = xpixsize + r1 * iheight + xcord;
            ymin = -xpixsize * iheight + r2 + ycord;

            xmax = xpixsize * iwidth + r1 + xcord;
            ymax = -xpixsize + r2 * iwidth + ycord;

            String xmins = String.valueOf(xmin);
            xmins = xmins.substring(0, xmins.indexOf("."));

            String ymins = String.valueOf(ymin);
            ymins = ymins.substring(0, ymins.indexOf("."));

            String xmaxs = String.valueOf(xmax);
            xmaxs = xmaxs.substring(0, xmaxs.indexOf("."));

            String ymaxs = String.valueOf(ymax);
            ymaxs = ymaxs.substring(0, ymaxs.indexOf("."));

            mbr = xmins + " " + ymins + " " + xmaxs + " " + ymaxs;
            return mbr;
    }
    private static float getPixelSizeMM(final IIOMetadataNode dimension, final String elementName) {
        // NOTE: The standard metadata format has defined dimension to pixels per millimeters, not DPI...
        NodeList pixelSizes = dimension.getElementsByTagName(elementName);
        IIOMetadataNode pixelSize = pixelSizes.getLength() > 0 ? (IIOMetadataNode) pixelSizes.item(0) : null;
        return pixelSize != null ? Float.parseFloat(pixelSize.getAttribute("value")) : -1;
    }
    
}
