/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dspace.app.xmlui.aspect.administrative.validatemetadata;

import com.google.common.collect.Iterators;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;

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
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.core.Constants;
import org.dspace.handle.factory.HandleServiceFactory;
import org.dspace.ctask.general.ValidateMetadata;
import org.dspace.handle.service.HandleService;
import org.dspace.content.service.ItemService;
import org.dspace.content.service.CommunityService;
import org.dspace.curate.Curator;

/**
 *
 * @author Enes
 */
public class ValidateMetadataMain extends AbstractDSpaceTransformer {

    private static final Message T_dspace_home
            = message("xmlui.general.dspace_home");
    private static final Message T_title
            = message("xmlui.administrative.validatemetadata.ValidateMetadataMain.title");
    private static final Message T_validate_metadata_trail
            = message(
                    "xmlui.administrative.validatemetadata.general.validate_metadata_trail");
    private static final Message T_head
            = message("xmlui.administrative.validatemetadata.ValidateMetadataMain.head");
    private static final Message T_para1
            = message("xmlui.administrative.validatemetadata.ValidateMetadataMain.para1");
    private static final Message T_column1
            = message("xmlui.administrative.validatemetadata.ValidateMetadataMain.column1");
    private static final Message T_column2
            = message("xmlui.administrative.validatemetadata.ValidateMetadataMain.column2");
    private static final Message T_column3
            = message("xmlui.administrative.validatemetadata.ValidateMetadataMain.column3");

    private static final Message T_totalitems
            = message("xmlui.administrative.validatemetadata.ValidateMetadataMain.totalitems");

    protected HandleService handleService = HandleServiceFactory.getInstance().getHandleService();
    protected ItemService itemService = ContentServiceFactory.getInstance().getItemService();
    protected CommunityService communityService = ContentServiceFactory.getInstance().getCommunityService();

    public void addPageMeta(PageMeta pageMeta)
            throws WingException {
        pageMeta.addMetadata("title").addContent(T_title);
        pageMeta.addTrailLink(contextPath + "/", T_dspace_home);
        pageMeta.addTrailLink(null, T_validate_metadata_trail);
    }

    public void addBody(Body body)
            throws WingException, SQLException {
        // Get our parameters & state
        //id and type id used in case of requirement where user is not sys admin
        UUID Id = UUID.fromString(parameters.getParameter("ID", null));
        String TypeId = parameters.getParameter("TypeId", null);
        String objHandle = parameters.getParameter("handle", "");

        DSpaceObject dso = null;
        //Request request = ObjectModelHelper.getRequest(objectModel);

        Iterator<Item> items = null;
        
        int tabSize = 15;
        int invalidItems = 0;
        boolean isItem = false;

        if (objHandle != "") {
            //try {
            dso = handleService.resolveToObject(context, objHandle);
            //make sure id is item, collection, or community
            switch (dso.getType()) {
                case Constants.COMMUNITY:
                    Community community = (Community) dso;
                    // get all the collections in the community
                    List<Collection> collections = communityService.getAllCollections(context, community);
                    for (Collection collection : collections) {
                        // get all the items in each collection
                        Iterator<Item> itemscol = itemService.findByCollection(context, collection);
                        if (items == null) {
                            items = itemscol;
                        } else {
                            items = Iterators.concat(items, itemscol);
                        }
                    }
                    //tabSize = Iterators.size(items);
                    break;
                case Constants.COLLECTION:
                    Collection collection = (Collection) dso;
                    //Iterator<Item> iitems = itemService.findByCollection(context, collection);
                    items = itemService.findByCollection(context, collection);
                    //tabSize = Iterators.size(items);
                    break;
                case Constants.ITEM:
                    tabSize = 1;
                    isItem = true;
                    break;
                default:
                    //reset objid
                    break;
            }
            // DIVISION: bitstream-format-registry
            Division main = body.addInteractiveDivision("validate-metadata",
                    contextPath + "/admin/validate-metadata", Division.METHOD_POST,
                    "validate metadata");
            main.setHead(T_head);
            main.addPara(T_para1);
            Table table = main.addTable("validate_metadata", tabSize, 3);

            Row header = table.addRow(Row.ROLE_HEADER);
            header.addCellContent(T_column1);
            header.addCellContent(T_column2);
            header.addCellContent(T_column3);
            //main.addPara().addXref( T_new_link);
            if (isItem) {
                Row row;
                row = table.addRow();

                try {
                    Item item = (Item) dso;
                    Curator curator = new Curator();
                    curator.addTask("validatemetadata").curate(item);
                    int status = curator.getStatus("validatemetadata");
                    String result = curator.getResult("validatemetadata");
                    String itemURL = itemService.getMetadata(item, "dc.identifier.uri");
                    if (status == Curator.CURATE_FAIL) {
                        invalidItems = 1;
                    }
                   

                    row.addCell().addContent(Integer.toString(status));
                    row.addCell().addContent(result);
                    row.addCell().addXref(itemURL, itemURL);
                } catch (IOException IOE) {
                    row.addCell().addContent("IOException");
                    row.addCell().addContent(IOE.getMessage());
                    row.addCell().addContent("");
                }
            } else {
               
                while (items.hasNext()) 
                {
                    Item item = items.next();
                    
                    try {
                        Curator curator = new Curator();
                        curator.addTask("validatemetadata").curate(item);
                        int status = curator.getStatus("validatemetadata");
                        String result = curator.getResult("validatemetadata");
                        if (status == Curator.CURATE_FAIL) {
                            invalidItems = invalidItems + 1;
                            String itemURL = itemService.getMetadata(item, "dc.identifier.uri");
                            Row row;
                            row = table.addRow();
                            row.addCell().addContent(Integer.toString(status));
                            row.addCell().addContent(result);
                            row.addCell().addXref(itemURL, itemURL);
                        }
                        tabSize = tabSize + 1;
                    } catch (IOException IOE) {
                        Row row;
                        row = table.addRow();
                        row.addCell().addContent("IOException");
                        row.addCell().addContent(IOE.getMessage());
                        row.addCell().addContent("");
                    }

                }//loop
            }
            Row footer = table.addRow(Row.ROLE_HEADER);
            footer.addCellContent(T_totalitems);
            footer.addCellContent("");
            footer.addCellContent("");
            footer.addCellContent(String.valueOf(invalidItems) + "/"+ String.valueOf(tabSize));

        }
    }

}
