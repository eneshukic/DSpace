/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dspace.app.xmlui.aspect.administrative.importsearch;

import org.dspace.app.xmlui.cocoon.AbstractDSpaceTransformer;
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
 * Class to import GML/CSV search parameters
 */
public class ImportSearchMain extends AbstractDSpaceTransformer {
    
    private static final Message T_dspace_home = message("xmlui.general.dspace_home");
    private static final Message T_title = message("xmlui.administrative.importsearch.general.title");
	private static final Message T_head1 = message("xmlui.administrative.importsearch.general.head1");
        private static final Message T_submit_upload = message("xmlui.administrative.importsearch.ImportSearchMain.submit_upload");
        private static final Message T_trail = message("xmlui.administrative.importsearch.general.trail");
	
	public void addPageMeta(PageMeta pageMeta) throws WingException  
	{
		pageMeta.addMetadata("title").addContent(T_title);
		
		pageMeta.addTrailLink(contextPath + "/", T_dspace_home);
		pageMeta.addTrail().addContent(T_trail);
	}

	
	public void addBody(Body body) throws SAXException, WingException
	{
                String hid = parameters.getParameter("hid",null);

		// DIVISION: metadata-import
		Division div = body.addInteractiveDivision("import-search",contextPath + "/admin/import-search", Division.METHOD_MULTIPART,"primary administrative");
		div.setHead(T_head1);

                Para file = div.addPara();
                file.addFile("file");
                file.addHidden("hid").setValue(hid);

                Para actions = div.addPara();
                Button button = actions.addButton("submit_upload");
                button.setValue(T_submit_upload);

		div.addHidden("administrative-continue").setValue(knot.getId());
	}
    
}
