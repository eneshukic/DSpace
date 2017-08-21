/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dspace.rate;

import org.dspace.rate.dao.RateDAO;
import org.dspace.rate.service.RateService;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.springframework.beans.factory.annotation.Autowired;
 
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Enes
 */
public class RateServiceImpl implements RateService {
    @Autowired(required = true)
    protected RateDAO rateDAO;
 
    protected RateServiceImpl() {
    }
    
    @Override
    public Rate create(Context context) throws SQLException {
        return rateDAO.create(context, new Rate());
    }
 
    @Override
    public Rate read(Context context, int id) throws SQLException {
        return rateDAO.findByID(context, Rate.class, id);
    }
 
    @Override
     public void update(Context context, Rate rate) throws SQLException {
        if(context.getCurrentUser().equals(rate.getModifiedBy())){
            rateDAO.save(context, rate);
        }
     }
 
    @Override
    public void delete(Context context, Rate rate) throws SQLException {
        if(context.getCurrentUser().equals(rate.getModifiedBy())) {
            rateDAO.delete(context, rate);
        }
    }
 
    @Override
    public List<Rate> findAll(Context context) throws SQLException {
        return rateDAO.findAll(context, Rate.class);
    }
 
    @Override
    public Rate findByRateGrade(Context context, String rateGrade) throws SQLException {
        return rateDAO.findRateByRateGrade(context,rateGrade);
    }
    
   /* @Override
    public List<Object[]> calculateCost(Context context, UUID Id, String  costTypeId) throws SQLException {
        return rateDAO.calculateRateCost(context, Id, costTypeId);
    }*/
 
 
}
