/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dspace.rate.factory;

import org.dspace.rate.service.CostCalculationService;
import org.dspace.utils.DSpace;

/**
 *
 * @author Enes
 */
public abstract class CostCalculationServiceFactory {
    public abstract CostCalculationService getCostCalculationService();
    
    public static CostCalculationServiceFactory getInstance(){
        return new DSpace().getServiceManager().getServiceByName("costCalculationServiceFactory", CostCalculationServiceFactory.class);
    }
}
