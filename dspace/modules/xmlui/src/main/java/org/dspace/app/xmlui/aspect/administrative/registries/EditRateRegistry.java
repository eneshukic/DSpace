/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dspace.app.xmlui.aspect.administrative.registries;

import java.sql.SQLException;
import java.util.ArrayList;
import java.math.BigDecimal;

import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.dspace.app.xmlui.cocoon.AbstractDSpaceTransformer;
import org.dspace.app.xmlui.utils.RequestUtils;
import org.dspace.app.xmlui.wing.Message;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.Body;
import org.dspace.app.xmlui.wing.element.CheckBox;
import org.dspace.app.xmlui.wing.element.Division;
import org.dspace.app.xmlui.wing.element.Item;
import org.dspace.app.xmlui.wing.element.List;
import org.dspace.app.xmlui.wing.element.PageMeta;
import org.dspace.app.xmlui.wing.element.Select;
import org.dspace.app.xmlui.wing.element.Text;
import org.dspace.app.xmlui.wing.element.TextArea;
import org.dspace.rate.Rate;
import org.dspace.rate.factory.RateServiceFactory;
import org.dspace.rate.service.RateService;

/**
 *
 * @author Enes
 */
public class EditRateRegistry extends AbstractDSpaceTransformer   {
    /** Language Strings */
	private static final Message T_dspace_home =
		message("xmlui.general.dspace_home");
	private static final Message T_title =
		message("xmlui.administrative.registries.EditRateRegistry.title");
	private static final Message T_rate_registry_trail =
		message("xmlui.administrative.registries.general.rate_registry_trail");
	private static final Message T_trail =
		message("xmlui.administrative.registries.EditRateRegistry.trail");	
	private static final Message T_head1 =
		message("xmlui.administrative.registries.EditRateRegistry.head1");	
	private static final Message T_para1 =
		message("xmlui.administrative.registries.EditRateRegistry.para1");	
	private static final Message T_head2 =
		message("xmlui.administrative.registries.EditRateRegistry.head2");	
	private static final Message T_column1 =
		message("xmlui.administrative.registries.EditRateRegistry.column1");	
	private static final Message T_column2 =
		message("xmlui.administrative.registries.EditRateRegistry.column2");	
	private static final Message T_column3 =
		message("xmlui.administrative.registries.EditRateRegistry.column3");	
	private static final Message T_column4 =
		message("xmlui.administrative.registries.EditRateRegistry.column4");	
	private static final Message T_empty =
		message("xmlui.administrative.registries.EditRateRegistry.empty");	
	private static final Message T_submit_return =
		message("xmlui.general.return");	
	private static final Message T_submit_delete =
		message("xmlui.administrative.registries.EditRateRegistry.submit_delete");		
	private static final Message T_head3 =
		message("xmlui.administrative.registries.EditRateRegistry.head3");	
	private static final Message T_rate_grade =
		message("xmlui.administrative.registries.EditRateRegistry.rateGrade");	
	private static final Message T_calculation_unit =
		message("xmlui.administrative.registries.EditRateRegistry.calculationUnit");	
	private static final Message T_rate_description =
		message("xmlui.administrative.registries.EditRateRegistry.rateDescription");
        private static final Message T_price =
		message("xmlui.administrative.registries.EditRateRegistry.price");
	private static final Message T_submit_add =
		message("xmlui.administrative.registries.EditRateRegistry.submit_add");	
	private static final Message T_head4 =
		message("xmlui.administrative.registries.EditRateRegistry.head4");
	private static final Message T_submit_update =
		message("xmlui.administrative.registries.EditRateRegistry.submit_update");
	private static final Message T_submit_cancel =
		message("xmlui.general.cancel");
	private static final Message T_error =
		message("xmlui.administrative.registries.EditRateRegistry.error");
        private static final Message T_error_rate_grade =
		message("xmlui.administrative.registries.EditRateRegistry.error_rate_grade");
        private static final Message T_error_price =
		message("xmlui.administrative.registries.EditRateRegistry.error_price");
        private static final Message T_error_calculation_unit =
		message("xmlui.administrative.registries.EditRateRegistry.error_calculation_unit");
	private static final Message T_error_duplicate_field =
		message("xmlui.administrative.registries.EditRateRegistry.error_duplicate_field");	
	private static final Message T_error_element_empty =
		message("xmlui.administrative.registries.EditRateRegistry.error_element_empty");	
	private static final Message T_error_element_badchar =
		message("xmlui.administrative.registries.EditRateRegistry.error_element_badchar");	
	private static final Message T_error_element_tolong =
		message("xmlui.administrative.registries.EditRateRegistry.error_element_tolong");	
	private static final Message T_error_qualifier_tolong =
		message("xmlui.administrative.registries.EditRateRegistry.error_qualifier_tolong");	
	private static final Message T_error_qualifier_badchar =
		message("xmlui.administrative.registries.EditRateRegistry.error_qualifier_badchar");
        private static final Message T_submit_save =
		message("xmlui.general.save");
	

	protected RateService rateService = RateServiceFactory.getInstance().getRateService();
        
        @Override
	public void addPageMeta(PageMeta pageMeta) throws WingException
        {
            pageMeta.addMetadata("title").addContent(T_title);
            pageMeta.addTrailLink(contextPath + "/", T_dspace_home);
            pageMeta.addTrailLink(contextPath + "/admin/rate-registry", T_rate_registry_trail);
            pageMeta.addTrail().addContent(T_trail);
        }
        
        public void addBody(Body body) throws WingException, SQLException 
	{
            // Get our parameters & state
            int Id = parameters.getParameterAsInteger("Id",-1);
            Rate rate = null;

            if (Id >= 0)
            {
                rate = rateService.read(context, Id);
            }
	
            String errorString = parameters.getParameter("errors",null);
            ArrayList<String> errors = new ArrayList<String>();
            if (errorString != null)
            {
                for (String error : errorString.split(",")) {
                    errors.add(error);
                }
            }
		
	    Request request = ObjectModelHelper.getRequest(objectModel);
            String rateGrade = request.getParameter("rate_grade");
            String calculationUnit = request.getParameter("calculation_unit");
            String rateDescription = request.getParameter("rate_description");
            BigDecimal price = request.getParameter("price")!= null ? new BigDecimal (request.getParameter("price")) : BigDecimal.ZERO;
            
            /*String supportLevelValue = request.getParameter("support_level");
            String internalValue = request.getParameter("internal");
            java.util.List<String> extensionValues = RequestUtils.getFieldValues(request, "extensions");*/
           
            
            if (rate != null) {
                if (rateGrade == null) {
                    rateGrade = rate.getRateGrade();
                }
                if (calculationUnit == null) {
                    calculationUnit = rate.getCalculationUnit();
                }
                if (rateDescription == null) {
                    rateDescription = rate.getRateDescription();
                }
                if (price == null) {
                    price = rate.getPrice();
                }
            }
		     
        
            // DIVISION: edit-rate
            Division main = body.addInteractiveDivision("edit-rate",contextPath+"/admin/rate-registry",Division.METHOD_POST,"primary administrative rate-registry");
            if (Id == -1)
            {
                main.setHead(T_head1);
            }
            else
            {
                main.setHead(T_head2.parameterize(rateGrade));
            }
            main.addPara(T_para1);

            List form = main.addList("edit-rate",List.TYPE_FORM);
            
            Text tRateGrade = form.addItem().addText("rate_grade");
            tRateGrade.setRequired();
            tRateGrade.setLabel(T_rate_grade);
            //name.setHelp(T_name_help);
            tRateGrade.setValue(rateGrade);
            tRateGrade.setSize(35);
            if (errors.contains("grade"))
            {
                tRateGrade.addError(T_error_rate_grade);
            }

            Text tPrice = form.addItem().addText("price");
            tPrice.setRequired();
            tPrice.setLabel(T_price);
            //name.setHelp(T_name_help);
            tPrice.setValue(price.toString());
            tPrice.setSize(35);
            if (errors.contains("price"))
            {
                tPrice.addError(T_error_price);
            }
		
            Text tCalculationUnit = form.addItem().addText("calculation_unit");
            tCalculationUnit.setRequired();
            tCalculationUnit.setLabel(T_calculation_unit);
            //mimeType.setHelp(T_mimetype_help);
            tCalculationUnit.setValue(calculationUnit);
            tCalculationUnit.setSize(35);
            if (errors.contains("calculation"))
            {
                tCalculationUnit.addError(T_error_calculation_unit);
            }

            TextArea Tdescription = form.addItem().addTextArea("rate_description");
            Tdescription.setLabel(T_rate_description);
            Tdescription.setValue(rateDescription);
            Tdescription.setSize(3, 35);

            

            Item actions = form.addItem();
            actions.addButton("submit_save").setValue(T_submit_save);
            actions.addButton("submit_cancel").setValue(T_submit_cancel);


            main.addHidden("administrative-continue").setValue(knot.getId());
        
   }
    
}
