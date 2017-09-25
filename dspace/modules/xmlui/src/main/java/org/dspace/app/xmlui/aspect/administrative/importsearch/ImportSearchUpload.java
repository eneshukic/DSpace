/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dspace.app.xmlui.aspect.administrative.importsearch;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import javax.servlet.http.HttpServletResponse;
import org.dspace.app.xmlui.cocoon.AbstractDSpaceTransformer;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.http.HttpEnvironment;
import org.dspace.app.xmlui.wing.Message;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.Body;
import org.dspace.app.xmlui.wing.element.Button;
import org.dspace.app.xmlui.wing.element.Division;
import org.dspace.app.xmlui.wing.element.PageMeta;
import org.dspace.app.xmlui.wing.element.Para;
import org.xml.sax.SAXException;

/**
 *
 * @author Enes
 */
public class ImportSearchUpload extends AbstractDSpaceTransformer {
    
    private static final Message T_dspace_home = message("xmlui.general.dspace_home");
	private static final Message T_submit_return = message("xmlui.general.return");
	private static final Message T_trail = message("xmlui.administrative.importsearch.general.trail");
	private static final Message T_title = message("xmlui.administrative.importsearch.general.title");
	private static final Message T_head1 = message("xmlui.administrative.importsearch.general.head1");
	private static final Message T_submit_confirm = message("xmlui.administrative.importsearch.ImportSearchUpload.submit_confirm");
        
        public void addPageMeta(PageMeta pageMeta) throws WingException  
	{
		pageMeta.addMetadata("title").addContent(T_title);
		
		pageMeta.addTrailLink(contextPath + "/", T_dspace_home);
		pageMeta.addTrail().addContent(T_trail);
	}
        
        public void addBody(Body body) throws WingException,  SAXException
	{
		Request request = ObjectModelHelper.getRequest(objectModel);
                String hid = parameters.getParameter("hid",null);
                String returnURI = contextPath + "/handle/" + hid;
                
                // DIVISION: import-search
                    Division div = body.addInteractiveDivision("import-search",contextPath + "/admin/import-search", Division.METHOD_MULTIPART,"primary administrative");
                    div.setHead(T_head1);
                    Para para = div.addPara();
                
                 if(request.getAttribute("searchspatial") != null) {
                     
                    //para.addContent(T_success);
                    //para.addContent(request.getAttribute("searchspatial").toString());
                    //para.addContent(hid);
                    //para.addContent(T_changes);
                    //Para actions = div.addPara();
                    //Button continuesearch = actions.addButton("submit_confirm");
                    //continuesearch.setValue(T_submit_confirm);
                    String sq = "";
                    try {
                        sq = URLEncoder.encode(request.getAttribute("searchspatial").toString(),"UTF-8");
                        HttpServletResponse httpResponse = (HttpServletResponse) 
            		objectModel.get(HttpEnvironment.HTTP_RESPONSE_OBJECT);
                        returnURI = returnURI + "?sq=" + sq;
                        try {
                        httpResponse.sendRedirect(returnURI);
                        }
                        catch (IOException e) {
                        para.addXref(returnURI + "?sq=" + sq,T_submit_confirm);
                        }
                    }
                    catch (UnsupportedEncodingException e) {
                        para.addContent("Unsupported encoding exception: " + e.getMessage());
                    }
                    
                    
                    //Button cancel = actions.addButton("submit_return");
                    //cancel.setValue(T_submit_return);
                    
                    div.addHidden("administrative-continue").setValue(knot.getId());
                     
                 }
                 else {
                     para.addXref(returnURI,T_submit_confirm);
                 }
                 
		
    }
    
}
