/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dspace.app.xmlui.aspect.administrative.collection;
import org.dspace.app.xmlui.cocoon.AbstractDSpaceTransformer;
import org.dspace.app.xmlui.wing.Message;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.Body;
import org.dspace.app.xmlui.wing.element.Cell;
import org.dspace.app.xmlui.wing.element.CheckBox;
import org.dspace.app.xmlui.wing.element.Division;
import org.dspace.app.xmlui.wing.element.PageMeta;
import org.dspace.app.xmlui.wing.element.Row;
import org.dspace.app.xmlui.wing.element.Table;
import org.dspace.app.xmlui.wing.element.List;
import org.dspace.rate.CostCalculation;
import org.dspace.rate.factory.CostCalculationServiceFactory;
import org.dspace.rate.service.CostCalculationService;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
 import java.math.BigDecimal;
import java.sql.SQLException;


import java.util.UUID;

/**
 *
 * @author Enes
 */
public class CalculateCost extends AbstractDSpaceTransformer{
    private static final Message T_dspace_home =
            message("xmlui.general.dspace_home");
    private static final Message T_title =
            message("xmlui.administrative.registries.CalculateCost.title");
    private static final Message T_rate_registry_trail =
            message(
            "xmlui.administrative.registries.general.rate_registry_trail");
    private static final Message T_head =
            message("xmlui.administrative.registries.CalculateCost.head");
    private static final Message T_para1 =
            message("xmlui.administrative.registries.CalculateCost.para1");
    private static final Message T_column1 =
            message("xmlui.administrative.registries.CalculateCost.column1");
    private static final Message T_column2 =
            message("xmlui.administrative.registries.CalculateCost.column2");
    private static final Message T_column3 =
            message("xmlui.administrative.registries.CalculateCost.column3");
    private static final Message T_column4 =
            message("xmlui.administrative.registries.CalculateCost.column4");
    private static final Message T_column5 =
            message("xmlui.administrative.registries.CalculateCost.column5");
    
    //protected CostCalculation costCalculationService = CostCalculationServiceFactory.getInstance().getCostCalculationService();
    
    public void addPageMeta(PageMeta pageMeta)
            throws WingException
    {
        pageMeta.addMetadata("title").addContent(T_title);
        pageMeta.addTrailLink(contextPath + "/", T_dspace_home);
        pageMeta.addTrailLink(null, T_rate_registry_trail);
    }

    public void addBody(Body body)
            throws WingException, SQLException
    {
        // Get our parameters & state
        /*UUID Id = parameters.getParameters("Id", -1);
        String costTypeId = parameters.getParameter("costTypeId");
          
       java.util.List<CostCalculation> cost = costCalculationService.calculateCost(context, Id, costTypeId);;
        
        */
        // DIVISION: bitstream-format-registry
        Division main = body.addInteractiveDivision("cost-registry",
                contextPath + "/admin/cost-registry", Division.METHOD_POST,
                "primary administrative cost-registry");
        main.setHead(T_head);
        main.addPara(T_para1);
        //main.addPara().addXref( T_new_link);


        Table table = main.addTable("cost-registry", 1// rates.size()
                + 1, 5);

        Row header = table.addRow(Row.ROLE_HEADER);
        header.addCellContent(T_column1);
        header.addCellContent(T_column2);
        header.addCellContent(T_column3);
        header.addCellContent(T_column4);
        header.addCellContent(T_column5);
        
/*
        for (CostCalculation cc : cost)
        {
            
            String kol = String.valueOf(cc.getKol());
            String rateGrade = cc.getRateGrade();
            String calculationUnit = cc.getCalculationUnit();
            String rateDescription = cc.getRateDescription();
            //BigDecimal price = rate.getPrice();
            String price = String.valueOf(cc.getPrice());

            
            
            Row row;
            //if (highlight)
            //{
            //    row = table.addRow(null, null, "highlight");
            //}
            //else
            //{
                row = table.addRow();
            //}

            // Select checkbox
            Cell cell = row.addCell();
            

            
            // Rate Grade
            row.addCell().addContent(rateGrade);
            
             // Rate description
            row.addCell().addContent(rateDescription);
            
            // Calculation unit
            row.addCell().addContent(calculationUnit);
            
            // kol
            row.addCell().addContent(kol);
            
            // Price
            row.addCell().addContent(price);
            
           
            
        }*/


    }
    
    
}
