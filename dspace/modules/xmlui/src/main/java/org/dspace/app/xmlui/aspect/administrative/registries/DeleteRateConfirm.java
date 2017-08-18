/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dspace.app.xmlui.aspect.administrative.registries;
import java.sql.SQLException;
import java.util.ArrayList;
import java.math.BigDecimal;
import org.dspace.app.xmlui.cocoon.AbstractDSpaceTransformer;
import org.dspace.app.xmlui.wing.Message;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.Body;
import org.dspace.app.xmlui.wing.element.Division;
import org.dspace.app.xmlui.wing.element.PageMeta;
import org.dspace.app.xmlui.wing.element.Para;
import org.dspace.app.xmlui.wing.element.Row;
import org.dspace.app.xmlui.wing.element.Table;
import org.dspace.authorize.AuthorizeException;
import org.dspace.rate.Rate;
import org.dspace.rate.factory.RateServiceFactory;
import org.dspace.rate.service.RateService;
/**
 *
 * @author Enes
 */
public class DeleteRateConfirm extends AbstractDSpaceTransformer {
    /** Language Strings */
	private static final Message T_dspace_home =
		message("xmlui.general.dspace_home");
	private static final Message T_submit_delete =
		message("xmlui.general.delete");
	private static final Message T_submit_cancel =
		message("xmlui.general.cancel");
	private static final Message T_title =
		message("xmlui.administrative.registries.DeleteRateConfirm.title");
	private static final Message T_format_registry_trail =
		message("xmlui.administrative.registries.general.rate_registry_trail");
	private static final Message T_trail =
		message("xmlui.administrative.registries.DeleteRateConfirm.trail");
	private static final Message T_head =
		message("xmlui.administrative.registries.DeleteRateConfirm.head");
	private static final Message T_para1 =
		message("xmlui.administrative.registries.DeleteRateConfirm.para1");
	private static final Message T_column1 =
		message("xmlui.administrative.registries.DeleteRateConfirm.column1");
	private static final Message T_column2 =
		message("xmlui.administrative.registries.DeleteRateConfirm.column2");
        
	protected RateService rateService = RateServiceFactory.getInstance().getRateService();
        
        public void addPageMeta(PageMeta pageMeta) throws WingException
        {
            pageMeta.addMetadata("title").addContent(T_title);
            pageMeta.addTrailLink(contextPath + "/", T_dspace_home);
            pageMeta.addTrailLink(contextPath + "/admin/rate-registry",T_format_registry_trail);
            pageMeta.addTrail().addContent(T_trail);
        }
        
        public void addBody(Body body) throws WingException, SQLException, AuthorizeException
	{
		// Get all our parameters
		String idsString = parameters.getParameter("IDs", null);
		
		ArrayList<Rate> rates = new ArrayList<Rate>();
		for (String id : idsString.split(","))
		{
			Rate rate = rateService.read(context,Integer.valueOf(id));
                        //Rate rate = rateService.read(context,Integer.valueOf(21));
			rates.add(rate);
		}
 
		// DIVISION: bitstream-format-confirm-delete
            Division deleted = body.addInteractiveDivision("rate-confirm-delete",contextPath+"/admin/rate-registry",Division.METHOD_POST,"primary administrative rate-registry");
            deleted.setHead(T_head);
            deleted.addPara(T_para1);

            Table table = deleted.addTable("rate-confirm-delete",rates.size() + 1, 3);
            Row header = table.addRow(Row.ROLE_HEADER);
            header.addCell().addContent(T_column1);
            header.addCell().addContent(T_column2);
            

            for (Rate rate : rates) 
            {
                if (rate == null)
                {
                    continue;
                }

                String Id = String.valueOf(rate.getId());
                String rateGrade = rate.getRateGrade();
                
                Row row = table.addRow();
                row.addCell().addContent(Id);
                row.addCell().addContent(rateGrade);
            }
            Para buttons = deleted.addPara();
            buttons.addButton("submit_confirm").setValue(T_submit_delete);
            buttons.addButton("submit_cancel").setValue(T_submit_cancel);

            deleted.addHidden("administrative-continue").setValue(knot.getId());
    }
    
}
