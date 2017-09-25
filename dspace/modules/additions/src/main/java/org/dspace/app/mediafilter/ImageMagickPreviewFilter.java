/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.mediafilter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.imageio.ImageIO;

import org.dspace.content.Bitstream;
import org.dspace.content.Bundle;
import org.dspace.content.Item;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.process.ProcessStarter;

import org.dspace.core.ConfigurationManager;

/**
 * Filter image bitstreams, scaling the image to be within the bounds of
 * thumbnail.maxwidth, thumbnail.maxheight, the size we want our thumbnail to be
 * no bigger than. Creates only JPEGs.
 */
public  class ImageMagickPreviewFilter  extends MediaFilter //implements SelfRegisterInputFormats
{
	/*protected static int width = 1800;
    protected static int height = 1200;*/
	private static boolean flatten = true;
	static String bitstreamDescription = "IM Preview";
	static final String defaultPattern = "Generated Preview";
	static Pattern replaceRegex = Pattern.compile(defaultPattern);
    protected final ItemService itemService = ContentServiceFactory.getInstance().getItemService();
	
	static {
		String pre = ImageMagickPreviewFilter.class.getName();
		String s = ConfigurationManager.getProperty(pre + ".ProcessStarter");
		ProcessStarter.setGlobalSearchPath(s);
		/*width = ConfigurationManager.getIntProperty("preview.maxwidth", width);
		height = ConfigurationManager.getIntProperty("preview.maxheight", height);*/
                flatten = ConfigurationManager.getBooleanProperty(pre + ".flatten", flatten);
		String description = ConfigurationManager.getProperty(pre + ".bitstreamDescription");
		if (description != null) {
			bitstreamDescription = description;
		}
		try {
			String patt = ConfigurationManager.getProperty(pre + ".replaceRegex");
		    replaceRegex = Pattern.compile(patt == null ? defaultPattern : patt);
		} catch(PatternSyntaxException e) {
			System.err.println("Invalid preview replacement pattern: "+e.getMessage());
		}
	}
	
	public ImageMagickPreviewFilter() {
	}
	
	
    @Override
    public String getFilteredName(String oldFilename)
    {
        return oldFilename + "_preview.jpg";
    }

    /**
     * @return String bundle name
     *  
     */
    @Override
    public String getBundleName()
    {
        return "BRANDED_PREVIEW";
    }

    /**
     * @return String bitstreamformat
     */
    @Override
    public String getFormatString()
    {
        return "JPEG";
    }

    /**
     * @return String bitstreamDescription
     */
    @Override
    public String getDescription()
    {
        return bitstreamDescription;
    }

    public File inputStreamToTempFile(InputStream source, String prefix, String suffix) throws IOException {
		File f = File.createTempFile(prefix, suffix);
		f.deleteOnExit();
    	FileOutputStream fos = new FileOutputStream(f);
    	
    	byte[] buffer = new byte[1024];
    	int len = source.read(buffer);
    	while (len != -1) {
    		fos.write(buffer, 0, len);
    		len = source.read(buffer);
    	}
    	fos.close();
		return f;
    }
    
    public File getPreviewFile(File f, boolean verbose) throws IOException, InterruptedException, IM4JavaException {
    	File f2 = new File(f.getParentFile(), f.getName() + "_preview.jpg");
    	f2.deleteOnExit();
    	ConvertCmd cmd = new ConvertCmd();
		IMOperation op = new IMOperation();
		op.addImage(f.getAbsolutePath());
                op.quality(10.0);
                op.gravity("SouthEast");
                op.pointsize(60).draw("text 10,10 FGU");
                //op.watermark(10.0);
                //op.draw("image Over     50,50 300,300 '"+watermarks.get(site.getPrefix())+"'");
		op.addImage(f2.getAbsolutePath());
        if (verbose) {
		    System.out.println("IM Preview Param: "+op);
        }
		cmd.run(op);
		return f2;
    }
    
    public File getImageFile(File f, int page, boolean verbose) throws IOException, InterruptedException, IM4JavaException {
    	File f2 = new File(f.getParentFile(), f.getName() + ".jpg");
    	f2.deleteOnExit();
    	ConvertCmd cmd = new ConvertCmd();
		IMOperation op = new IMOperation();
		String s = "[" + page + "]";
		op.addImage(f.getAbsolutePath()+s);
                if (flatten)
                {
                    op.flatten();
                }
		op.addImage(f2.getAbsolutePath());
        if (verbose) {
		    System.out.println("IM Image Param: "+op);
        }
		cmd.run(op);
		return f2;
    }
    
    @Override
    public boolean preProcessBitstream(Context c, Item item, Bitstream source, boolean verbose)
            throws Exception
    {
    	String nsrc = source.getName();
    	for(Bundle b: itemService.getBundles(item, "BRANDED_PREVIEW")) {
    		for(Bitstream bit: b.getBitstreams()) {
                String n = bit.getName();
    			if (n != null) {
    				if (nsrc != null) {
    					if (!n.startsWith(nsrc)) continue;
    				}
    			}
     	    	String description = bit.getDescription();
    	    	//If anything other than a generated thumbnail is found, halt processing
    	    	if (description != null) {
        	    	if (replaceRegex.matcher(description).matches()) {
        	            if (verbose) {
        	    		    System.out.println(description + " " + nsrc + " matches pattern and is replacable.");
        	            }
        	    		continue;
        	    	}
        	    	if (description.equals(bitstreamDescription)) {
        	            if (verbose) {
        	    		    System.out.println(bitstreamDescription + " " + nsrc + " is replacable.");
        	            }
        	    		continue;    	    		
        	    	}
    	    	}
    			System.out.println("Custom Preview exists for " + nsrc + " for item " + item.getHandle() + ".  Preview will not be generated. ");
    			return false;
    		}
    	}
    	
    	
        return true; //assume that the thumbnail is a custom one
    }
    
    /**
     * @param currentItem item
     * @param source  source input stream
     * @param verbose verbose mode
     * 
     * @return InputStream the resulting input stream
     * @throws Exception if error
     */
    @Override
    public InputStream getDestinationStream(Item currentItem, InputStream source, boolean verbose)
            throws Exception
    {
		File f = inputStreamToTempFile(source, "imtprev", ".tmp");
    	File f2 = null;
	    try
	    {
		    f2 = getPreviewFile(f, verbose);
		    byte[] bytes = Files.readAllBytes(f2.toPath());
		    return new ByteArrayInputStream(bytes);
	    }
	    finally
	    {
		    //noinspection ResultOfMethodCallIgnored
		    f.delete();
		    if (f2 != null)
		    {
			    //noinspection ResultOfMethodCallIgnored
			    f2.delete();
		    }
	    }
	}
/*
     @Override
     public String[] getInputMIMETypes()
    {
        return ImageIO.getReaderMIMETypes();
    }

    @Override
    public String[] getInputDescriptions()
    {
        return null;
    }

    @Override
    public String[] getInputExtensions()
    {
        return ImageIO.getReaderFileSuffixes();
    }*/
}
