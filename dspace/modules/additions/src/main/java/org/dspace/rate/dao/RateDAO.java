/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dspace.rate.dao;
import org.dspace.rate.Rate;
import org.dspace.core.Context;
import org.dspace.core.GenericDAO;
import org.dspace.eperson.EPerson;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
/**
 *
 * @author Enes
 */
public interface RateDAO extends GenericDAO<Rate> {
    public Rate findRateByRateGrade(Context context, String rateGrade) throws SQLException;
    public List<Object[]> calculateRateCost(Context context, UUID Id, String  costTypeId) throws SQLException;
    
}
