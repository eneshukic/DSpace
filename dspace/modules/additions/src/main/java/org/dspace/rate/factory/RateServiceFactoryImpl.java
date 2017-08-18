/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dspace.rate.factory;
import org.dspace.rate.service.RateService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Enes
 */
public class RateServiceFactoryImpl extends RateServiceFactory {
    @Autowired(required = true)
    private RateService rateService;
 
    @Override
    public RateService getRateService() {
        return rateService;
    }
}
