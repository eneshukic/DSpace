/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dspace.rate.factory;

import org.dspace.rate.service.RateService;
import org.dspace.utils.DSpace;

/**
 *
 * @author Enes
 */
public abstract class RateServiceFactory {
    public abstract RateService getRateService();
    
    public static RateServiceFactory getInstance(){
        return new DSpace().getServiceManager().getServiceByName("rateServiceFactory", RateServiceFactory.class);
    }
    
}
