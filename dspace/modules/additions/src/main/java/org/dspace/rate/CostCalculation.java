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
//@Entity
//@Table("calc_report")
public class CostCalculation {
    //@Id
    //@Column(name = "id", unique = true, nullable = false, length = 50)
    private String rateGrade;
    //@Column(name="kol")
    private Integer kol;
    //@Column(name="rate_description", length=1000)
    private String rateDescription;
    //@Column(name="calculation_unit",  length=50)
    private String calculationUnit;
    //@Column(name="price", precision=7, scale=2)
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
