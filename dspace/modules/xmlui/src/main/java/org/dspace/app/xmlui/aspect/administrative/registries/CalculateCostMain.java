/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dspace.app.xmlui.aspect.administrative.registries;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

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
/*import org.dspace.rate.CostCalculation;
import org.dspace.rate.factory.CostCalculationServiceFactory;*/
import org.dspace.rate.factory.RateServiceFactory;
//import org.dspace.rate.service.CostCalculationService;
import org.dspace.rate.service.RateService;
import org.dspace.rate.Rate;

/**
 *
 * @author Enes
 */
public class CalculateCostMain extends AbstractDSpaceTransformer{
    private static final Message T_dspace_home =
            message("xmlui.general.dspace_home");
    private static final Message T_title =
            message("xmlui.administrative.registries.CalculateCostMain.title");
    private static final Message T_calculate_cost_trail =
            message(
            "xmlui.administrative.registries.general.calculate_cost_trail");
    private static final Message T_head =
            message("xmlui.administrative.registries.CalculateCostMain.head");
    private static final Message T_para1 =
            message("xmlui.administrative.registries.CalculateCostMain.para1");
    private static final Message T_column1 =
            message("xmlui.administrative.registries.CalculateCostMain.column1");
    private static final Message T_column2 =
            message("xmlui.administrative.registries.CalculateCostMain.column2");
    private static final Message T_column3 =
            message("xmlui.administrative.registries.CalculateCostMain.column3");
    private static final Message T_column4 =
            message("xmlui.administrative.registries.CalculateCostMain.column4");
    private static final Message T_column5 =
            message("xmlui.administrative.registries.CalculateCostMain.column5");
    
   // protected CostCalculation costCalculationService = CostCalculationServiceFactory.getInstance().getCostCalculationService();
    protected RateService rateService = RateServiceFactory.getInstance().getRateService();
    public void addPageMeta(PageMeta pageMeta)
            throws WingException
    {
        pageMeta.addMetadata("title").addContent(T_title);
        pageMeta.addTrailLink(contextPath + "/", T_dspace_home);
        pageMeta.addTrailLink(null, T_calculate_cost_trail);
    }

    public void addBody(Body body)
            throws WingException, SQLException
    {
        // Get our parameters & state
        UUID Id = UUID.fromString(parameters.getParameter("ID", null));
        String costTypeId = parameters.getParameter("costTypeId",null);
        //UUID Id = UUID.fromString("146d1bfa-72b4-4fbb-a8c9-2899d3627a2e");
        //String costTypeId = "communityID";
         
       //List<CostCalculation> costs = costCalculationService.calculateCost(context, Id, costTypeId);
        List<Object[]> costs = rateService.calculateCost(context, Id, costTypeId);
        // DIVISION: bitstream-format-registry
        Division main = body.addInteractiveDivision("cost-calculation",
                contextPath + "/admin/cost-calculation", Division.METHOD_POST,
                "cost calculation");
        main.setHead(T_head);
        main.addPara(T_para1);
        //main.addPara().addXref( T_new_link);


        Table table = main.addTable("cost_calculation",  costs.size() + 1, 5);

        Row header = table.addRow(Row.ROLE_HEADER);
        header.addCellContent(T_column1);
        header.addCellContent(T_column2);
        header.addCellContent(T_column3);
        header.addCellContent(T_column4);
        header.addCellContent(T_column5);
        

        for (Object[] cc : costs)
        {
            String rateGrade = (String) cc[0];
            String kol = (String) String.valueOf(cc[1]);
            String rateDescription = (String) cc[2];
            String calculationUnit = (String) cc[3];
            String price = (String) String.valueOf(cc[4]);
            //BigDecimal price = rate.getPrice();
            //String price = String.valueOf(cc.getPrice());

            
            
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
            cell.addContent(rateGrade);
            
            // Rate Grade
            //row.addCell().addContent(rateGrade);
            
             // Rate description
            row.addCell().addContent(rateDescription);
            
            // Calculation unit
            row.addCell().addContent(calculationUnit);
            
            // kol
            row.addCell().addContent(kol);
            
            // Price
            row.addCell().addContent(price);       
            
        }


    }
    
    
}
