/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.xmlui.aspect.administrative;

import org.apache.cocoon.environment.Request;
import org.dspace.app.xmlui.utils.RequestUtils;
import org.dspace.app.xmlui.utils.UIException;
import org.dspace.app.xmlui.wing.Message;
import org.dspace.authorize.AuthorizeException;
import org.dspace.rate.Rate;
import org.dspace.content.NonUniqueMetadataException;
import org.dspace.rate.factory.RateServiceFactory;
import org.dspace.rate.service.RateService;
import org.dspace.core.Constants;
import org.dspace.core.Context;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.util.Date;
import org.dspace.eperson.EPerson;

/**
 * 
 */
public class FlowRateRegistryUtils 
{

	/** Language Strings */
	
	private static final Message T_edit_rate_success_notice =
		new Message("default","xmlui.administrative.FlowRateRegistryUtils.edit_rate_success_notice");
	private static final Message T_delete_rate_success_notice =
		new Message("default","xmlui.administrative.FlowRateRegistryUtils.delete_rate_success_notice");

	protected static final RateService rateService = RateServiceFactory.getInstance().getRateService();
	
	/**
	 * Edit rate. If the ID is -1 then a new rate is created.
	 * The ID of the new format is added as a parameter to the results object.
	 * 
	 * FIXME: the reason we accept a request object is so that we can use the 
	 * RequestUtils.getFieldvalues() to get the multivalue field values.
	 * 
	 * @param context The dspace context
	 * @param formatID The id of the format being updated.
	 * @param request The request object, for all the field entries.
	 * @return A results object
     * @throws java.sql.SQLException passed through.
     * @throws org.dspace.authorize.AuthorizeException passed through.
	 */
	public static FlowResult processEditRate(Context context, int Id, Request request) throws SQLException, AuthorizeException
	{
            FlowResult result = new FlowResult();
            result.setContinue(false);
		
		// Get the values
             String rateGrade = request.getParameter("rate_grade");
             String calculationUnit = request.getParameter("calculation_unit");
             String rateDescription = request.getParameter("rate_description");
             BigDecimal price = request.getParameter("price")!= null ? new BigDecimal (request.getParameter("price")) : BigDecimal.ZERO;
             EPerson modifiedBy = context.getCurrentUser();
             Date dateModified = new Date();
            
            // The format must have rate grade, price and calculation unit.
            if (rateGrade == null || rateGrade.length() == 0)
            {
                    result.addError("rate_grade");
                    return result;
            }
            if  (calculationUnit == null || calculationUnit.length() == 0)
            {
                    result.addError("calculation_unit");
                    return result;
            }

           

            // Get or create the rate
            Rate rate;
            if (Id >= 0)
            {
                rate = rateService.read(context, Id);
            }
            else
            {
                rate = rateService.create(context);
            }

            // Update values
            rate.setRateGrade(rateGrade);
            rate.setCalculationUnit(calculationUnit);
            rate.setRateDescription(rateDescription);
            rate.setPrice(price);
            rate.setModifiedBy(modifiedBy);
            rate.setDateModified(dateModified);
            	
            // Commit the change
            rateService.update(context, rate);

		// Return status
            result.setContinue(true);
            result.setOutcome(true);
            result.setMessage(T_edit_rate_success_notice);
            result.setParameter("Id",rate.getId());
        
            return result;
	}
	
	/**
	 * Delete the specified rates.
	 * 
	 * @param context The DSpace context
	 * @param IDs The rates-to-be-deleted.
	 * @return A results object.
     * @throws java.sql.SQLException passed through.
     * @throws org.dspace.authorize.AuthorizeException passed through.
	 */
	public static FlowResult processDeleteRates(Context context, String[] IDs)
            throws NumberFormatException, SQLException, AuthorizeException
	{
            FlowResult result = new FlowResult();
		
            int count = 0;
            for (String id : IDs) 
            {
		Rate rate = rateService.read(context,Integer.valueOf(id));
		rateService.delete(context, rate);
	        count++;
            }
		
            if (count > 0)
            {
                result.setContinue(true);
                result.setOutcome(true);
                result.setMessage(T_delete_rate_success_notice);
            }

            return result;
	}
	
}
