/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dspace.rate.service;

import org.dspace.rate.Rate;
import org.dspace.core.Context;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Enes
 */
public interface RateService   {
    public Rate create(Context context) throws SQLException;
 
    public Rate read(Context context, int id) throws SQLException;
 
    public void update(Context context, Rate rate) throws SQLException;
 
    public void delete(Context context, Rate rate) throws SQLException;
 
    public List<Rate> findAll(Context context) throws SQLException;
 
    public Rate findByRateGrade(Context context, String rateGrade) throws SQLException;
    
    public List<Object[]> calculateCost(Context context, UUID Id, String  costTypeId) throws SQLException;
}
