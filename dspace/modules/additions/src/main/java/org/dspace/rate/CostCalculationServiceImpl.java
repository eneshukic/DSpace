/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dspace.rate;
import org.dspace.rate.dao.CostCalculationDAO;
import org.dspace.rate.service.CostCalculationService;
import org.dspace.core.Context;
import org.springframework.beans.factory.annotation.Autowired;
 
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
/**
 *
 * @author Enes
 */
public class CostCalculationServiceImpl implements CostCalculationService {
    @Autowired(required = true)
    protected CostCalculationDAO costCalculationDAO;
 
    protected CostCalculationServiceImpl() {
    }
     @Override
    public List<CostCalculation> calculateCost(Context context, UUID Id, String  costTypeId) throws SQLException {
        return costCalculationDAO.calculateRateCost(context, Id, costTypeId);
    }
}
