/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dspace.app.xmlui.aspect.administrative.registries;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

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
import org.dspace.rate.Rate;
import org.dspace.rate.factory.RateServiceFactory;
import org.dspace.rate.service.RateService;
/**
 *
 * @author Enes
 */
public class RateRegistryMain extends AbstractDSpaceTransformer {
    private static final Message T_dspace_home =
            message("xmlui.general.dspace_home");
    private static final Message T_title =
            message("xmlui.administrative.registries.RateRegistryMain.title");
    private static final Message T_rate_registry_trail =
            message(
            "xmlui.administrative.registries.general.rate_registry_trail");
    private static final Message T_head =
            message("xmlui.administrative.registries.RateRegistryMain.head");
    private static final Message T_para1 =
            message("xmlui.administrative.registries.RateRegistryMain.para1");
    private static final Message T_new_link =
            message(
            "xmlui.administrative.registries.RateRegistryMain.new_link");
    private static final Message T_column1 =
            message("xmlui.administrative.registries.RateRegistryMain.column1");
    private static final Message T_column2 =
            message("xmlui.administrative.registries.RateRegistryMain.column2");
    private static final Message T_column3 =
            message("xmlui.administrative.registries.RateRegistryMain.column3");
    private static final Message T_column4 =
            message("xmlui.administrative.registries.RateRegistryMain.column4");
    private static final Message T_column5 =
            message("xmlui.administrative.registries.RateRegistryMain.column5");
    private static final Message T_column6 =
            message("xmlui.administrative.registries.RateRegistryMain.column6");
    
    private static final Message T_submit_delete =
            message(
            "xmlui.administrative.registries.RateRegistryMain.submit_delete");
    protected RateService rateService = RateServiceFactory.getInstance().getRateService();
    
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
        int highlightID = parameters.getParameterAsInteger("highlightID", -1);
        List<Rate> rates = rateService.findAll(context);
        String addURL = contextPath
                + "/admin/rate-registry?administrative-continue="
                + knot.getId() + "&submit_add";


        // DIVISION: bitstream-format-registry
        Division main = body.addInteractiveDivision("rate-registry",
                contextPath + "/admin/rate-registry", Division.METHOD_POST,
                "primary administrative rate-registry");
        main.setHead(T_head);
        main.addPara(T_para1);
        main.addPara().addXref(addURL, T_new_link);


        Table table = main.addTable("rate-registry", rates.size()
                + 1, 5);

        Row header = table.addRow(Row.ROLE_HEADER);
        header.addCellContent(T_column1);
        header.addCellContent(T_column2);
        header.addCellContent(T_column3);
        header.addCellContent(T_column4);
        header.addCellContent(T_column5);
        header.addCellContent(T_column6);

        for (Rate rate : rates)
        {
            String id = String.valueOf(rate.getId());
            String rateGrade = rate.getRateGrade();
            String calculationUnit = rate.getCalculationUnit();
            String rateDescription = rate.getRateDescription();
            //BigDecimal price = rate.getPrice();
            String price = String.valueOf(rate.getPrice());

            boolean highlight = false;
            if (rate.getId() == highlightID)
            {
                highlight = true;
            }

            String url = contextPath
                    + "/admin/rate-registry?administrative-continue="
                    + knot.getId() + "&submit_edit&Id=" + id;


            Row row;
            if (highlight)
            {
                row = table.addRow(null, null, "highlight");
            }
            else
            {
                row = table.addRow();
            }

            // Select checkbox
            Cell cell = row.addCell();
            //if (rate.getId() > 1)
            //{
                // Do not allow unknown to be removed.
                CheckBox select = cell.addCheckBox("select_rate");
                select.setLabel(id);
                select.addOption(id);
            //}

            // ID
            row.addCell().addContent(id);

            // Rate Grade
            row.addCell().addXref(url, rateGrade);
            
            // Calculation unit
            row.addCell().addXref(url, calculationUnit);
            
            // Price
            row.addCell().addXref(url, price);
            
            // Rate description
            row.addCell().addXref(url, rateDescription);
            
        }

        main.addPara().addButton("submit_delete").setValue(T_submit_delete);

        main.addHidden("administrative-continue").setValue(knot.getId());

    }
    
}
