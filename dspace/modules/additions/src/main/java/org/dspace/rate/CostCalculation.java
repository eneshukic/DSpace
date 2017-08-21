/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dspace.rate;

import java.math.BigDecimal;
import javax.persistence.*;
/**
 *
 * @author Enes
 */
public class CostCalculation {
    private String rateGrade;
    private Integer kol;
    private String rateDescription;
    private String calculationUnit;
    private BigDecimal price;
    
    protected CostCalculation (){
        
    }

    public String getRateGrade() {
        return rateGrade;
    }

    public void setRateGrade(String rateGrade) {
        this.rateGrade = rateGrade;
    }

    public Integer getKol() {
        return kol;
    }

    public void setKol(Integer kol) {
        this.kol = kol;
    }

    public String getRateDescription() {
        return rateDescription;
    }

    public void setRateDescription(String rateDescription) {
        this.rateDescription = rateDescription;
    }

    public String getCalculationUnit() {
        return calculationUnit;
    }

    public void setCalculationUnit(String calculationUnit) {
        this.calculationUnit = calculationUnit;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
}
