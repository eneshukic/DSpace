/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dspace.rate;

import java.math.BigDecimal;
import org.dspace.eperson.EPerson;

import org.dspace.core.Constants;
import org.dspace.core.Context;
import javax.persistence.*;
import java.util.Date;
//import org.dspace.content.DSpaceObject;
//import org.dspace.content.DSpaceObjectLegacySupport;

@Entity
@Table(name="rate")
/**
 *
 * @author Enes
 */
public class Rate  //implements Serializable , ReloadableEntity<Integer>
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE ,generator="rate_seq")
    @SequenceGenerator(name="rate_seq", sequenceName="rate_seq", allocationSize = 1)
    @Column(name = "rate_id", unique = true, nullable = false, insertable = true, updatable = false)
    private int id;
    @Column(name="rate_grade",  unique=true,  length=50)
    private String rateGrade;
    @Column(name="calculation_unit",  length=50)
    private String calculationUnit;
    @Column(name="rate_description", length=1000)
    private String rateDescription;
    @Column(name="date_modified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateModified = new Date();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modified_by")
    private EPerson modifiedBy;
    @Column(name="price", precision=7, scale=2)
    private BigDecimal price;
    
    protected Rate(){
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRateGrade() {
        return rateGrade;
    }

    public void setRateGrade(String rateGrade) {
        this.rateGrade = rateGrade;
    }

    public String getCalculationUnit() {
        return calculationUnit;
    }

    public void setCalculationUnit(String calculationUnit) {
        this.calculationUnit = calculationUnit;
    }

    public String getRateDescription() {
        return rateDescription;
    }

    public void setRateDescription(String rateDescription) {
        this.rateDescription = rateDescription;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public EPerson getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(EPerson modifiedBy) {
        this.modifiedBy = modifiedBy;
         //setModified();
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
}
