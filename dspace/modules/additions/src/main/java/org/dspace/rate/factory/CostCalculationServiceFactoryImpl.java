/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dspace.rate.factory;
import org.dspace.rate.service.CostCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
/**
 *
 * @author Enes
 */
public class CostCalculationServiceFactoryImpl extends CostCalculationServiceFactory {
    @Autowired(required = true)
    private CostCalculationService costCalculationService;
 
    @Override
    public CostCalculationService getCostCalculationService() {
        return costCalculationService;
    }
    
}
