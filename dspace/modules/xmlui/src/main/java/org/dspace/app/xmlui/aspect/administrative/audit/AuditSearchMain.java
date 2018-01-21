/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.xmlui.aspect.administrative.audit;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.commons.lang.StringUtils;
//import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
//import org.apache.solr.client.solrj.SolrClient;
//import org.apache.solr.client.solrj.SolrServerException;
//import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.impl.HttpClientUtil;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.util.ClientUtils;

import org.dspace.app.xmlui.cocoon.AbstractDSpaceTransformer;
import org.dspace.app.xmlui.wing.Message;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.Body;
import org.dspace.app.xmlui.wing.element.Cell;
import org.dspace.app.xmlui.wing.element.Division;
import org.dspace.app.xmlui.wing.element.List;
import org.dspace.app.xmlui.wing.element.PageMeta;
import org.dspace.app.xmlui.wing.element.Row;
import org.dspace.app.xmlui.wing.element.Table;
import org.dspace.app.xmlui.wing.element.Text;
import org.dspace.app.xmlui.wing.element.Select;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.BitstreamService;
import org.dspace.content.*;
import org.dspace.eperson.EPerson;
import org.dspace.content.Bitstream;
import org.dspace.eperson.factory.EPersonServiceFactory;
import org.dspace.eperson.service.EPersonService;
import org.dspace.content.DSpaceObject;
import org.dspace.handle.service.HandleService;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Constants;
import org.dspace.handle.factory.HandleServiceFactory;
import org.dspace.statistics.SolrLoggerServiceImpl;
import org.dspace.statistics.content.filter.StatisticsSolrDateFilter;

/**
 * The manage epeople page is the starting point page for managing epeople. From
 * here the user is able to browse or search for epeople, once identified the
 * user can selected them for deletion by selecting the checkboxes and clicking
 * delete or click their name to edit the eperson.
 *
 * @author Enes Hukic
 */
public class AuditSearchMain extends AbstractDSpaceTransformer {

    /**
     * Language Strings
     */
    private static final Message T_title
            = message("xmlui.administrative.eperson.AuditSearch.title");

    private static final Message T_eperson_trail
            = message("xmlui.administrative.eperson.general.epeople_trail");

    private static final Message T_main_head
            = message("xmlui.administrative.eperson.AuditSearch.main_head");

    private static final Message T_actions_head
            = message("xmlui.administrative.eperson.AuditSearch.actions_head");

    private static final Message T_actions_search
            = message("xmlui.administrative.eperson.AuditSearch.actions_search");
    
    private static final Message T_start_date
            = message("xmlui.administrative.eperson.AuditSearch.actions_startdate");
    
    private static final Message T_end_date
            = message("xmlui.administrative.eperson.AuditSearch.actions_enddate");
    
    private static final Message T_email
            = message("xmlui.administrative.eperson.AuditSearch.actions_email");
    
    private static final Message T_handle
            = message("xmlui.administrative.eperson.AuditSearch.actions_handle");

    private static final Message T_search_help
            = message("xmlui.administrative.eperson.AuditSearch.search_help");

    private static final Message T_dspace_home
            = message("xmlui.general.dspace_home");

    private static final Message T_go
            = message("xmlui.general.go");

    private static final Message T_search_head
            = message("xmlui.administrative.eperson.AuditSearch.search_head");

    private static final Message T_search_column1
            = message("xmlui.administrative.eperson.AuditSearch.search_column1");

    private static final Message T_search_column11
            = message("xmlui.administrative.eperson.AuditSearch.search_column11");

    private static final Message T_search_column2
            = message("xmlui.administrative.eperson.AuditSearch.search_column2");

    private static final Message T_search_column3
            = message("xmlui.administrative.eperson.AuditSearch.search_column3");

    private static final Message T_search_column4
            = message("xmlui.administrative.eperson.AuditSearch.search_column4");

    private static final Message T_no_results
            = message("xmlui.administrative.eperson.AuditSearch.no_results");

    private static final Message T_search_terms_head = message("xmlui.statistics.StatisticsSearchTransformer.search-terms.head");

    private static final Message T_time_filter_last_month = message("xmlui.statistics.StatisticsSearchTransformer.time-filter.last-month");
    private static final Message T_time_filter_overall = message("xmlui.statistics.StatisticsSearchTransformer.time-filter.overall");
    private static final Message T_time_filter_last_year = message("xmlui.statistics.StatisticsSearchTransformer.time-filter.last-year");
    private static final Message T_time_filter_last6_months = message("xmlui.statistics.StatisticsSearchTransformer.time-filter.last-6-months");

    /**
     * The total number of entries to show on a page
     */
    private static final int PAGE_SIZE = 15;

    protected EPersonService ePersonService = EPersonServiceFactory.getInstance().getEPersonService();
    protected BitstreamService bitstreamService = ContentServiceFactory.getInstance().getBitstreamService();
    protected HandleService handleService = HandleServiceFactory.getInstance().getHandleService();

    public void addPageMeta(PageMeta pageMeta) throws WingException {
        pageMeta.addMetadata("title").addContent(T_title);
        pageMeta.addTrailLink(contextPath + "/", T_dspace_home);
        pageMeta.addTrailLink(null, T_eperson_trail);
    }

    public void addBody(Body body) throws WingException, SQLException {
        /* Get and setup our parameters */
        int page = parameters.getParameterAsInteger("page", 0);
        String highlightID = parameters.getParameter("highlightID", null);
        String query = decodeFromURL(parameters.getParameter("query", null));
        String baseURL = contextPath + "/admin/audit?administrative-continue=" + knot.getId();
        Request request = ObjectModelHelper.getRequest(objectModel);
        //String selectedTimeFilter = request.getParameter("time_filter");
        String dStart = parameters.getParameter("dStart", null);
        String dEnd = parameters.getParameter("dEnd", null);
        String pepersonID = "";
        String epersonEmail = parameters.getParameter("epersonID", "");
        String objHandle = parameters.getParameter("handle","");
        Date startDate = null;
        Date endDate = null;
        String dateRange = "";
        DateFormat format = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat formatter = new SimpleDateFormat(SolrLoggerServiceImpl.DATE_FORMAT_8601);
        EPerson epersonsearch = null;
        DSpaceObject dso = null;
        String objID = "";
        String objType = "";
        try {
            startDate = format.parse(dStart);
            endDate = format.parse(dEnd);
            dateRange = " time:[" + formatter.format(startDate) + " TO " + formatter.format(endDate) + "]";
        } catch (Exception e) {
            startDate = null;
            endDate = null;
        }
        if (!epersonEmail.equals("")) {
            try {
                epersonsearch = ePersonService.findByEmail(context,epersonEmail);
                pepersonID = " epersonid:" + epersonsearch.getID().toString();
            } catch (Exception e) {
                pepersonID = "";
            }
        }
        if (objHandle != ""){
            try {
                dso = handleService.resolveToObject(context, objHandle);
                objID =dso.getID().toString();
                //make sure id is item, collection, or community
                switch (dso.getType()){
                    case Constants.ITEM:
                        objType = " owningItem:" + objID;
                        break;
                    case Constants.COLLECTION:
                        objType = " owningColl:" + objID;
                        break;
                    case Constants.COMMUNITY:
                        objType = " owningComm:" + objID;
                        break;
                    default:
                        //reset objid
                        objID = "";
                        break;
                }
            }catch (Exception e){
                objID = "";
            }
        }
        //Retrieve the optional time filter
        //StatisticsSolrDateFilter dateFilter = getDateFilter(selectedTimeFilter);
        //pepersonID = "";
        //String myquery = "type:0 AND (bundleName:[* TO *]-bundleName:ORIGINAL)";
        String myquery = "type:0 bundleName:ORIGINAL";
        
        /*if(dateFilter != null){
                myquery = myquery + selectedTimeFilter.toString();
            }*/
        // run solr query to get eperson id, time accessed and URl
        HttpSolrServer solrServer = new HttpSolrServer(ConfigurationManager.getProperty("solr-statistics.server"));
        SolrQuery solrQuery = new SolrQuery();
        //solrQuery.setFields( "epersonid","id","time","referrer");
        if (!(startDate == null || endDate == null)) {
            myquery = myquery + dateRange;
        }
        if (!pepersonID.equals("")){
            myquery = myquery + pepersonID;
        }
        if (!objType.equals("")){
            myquery = myquery + objType;
        }

        //solrQuery.set("fq", myquery);
        /* try {
             //myquery = URLEncoder.encode(myquery.trim(),"UTF-8");
             pepersonID = URLEncoder.encode("7960956b-42ca-4e26-9194-01b3256a3bd6","UTF-8");
            // myquery = myquery + " epersonid:" + pepersonID;
         }
         catch (UnsupportedEncodingException e) {
             //just return default
             myquery = "type:0";
         }*/
        solrQuery.set("q", myquery);

        //solrQuery.set("fq", myquery);
        //solrQuery.set("fq", "bundleName:ORIGINAL");
        /*solrQuery.set("fq", myquery);
        if (pepersonID != null){
            solrQuery.set("fq", pepersonID);
        }*/
        solrQuery.set("fl", "*");
        solrQuery.addSort("time", SolrQuery.ORDER.desc);

        //solrQuery.addFilterQuery("epersonid:7960956b-42ca-4e26-9194-01b3256a3bd6");
       
        //solrQuery.setParam("facet.field", "epersonid");
        QueryRequest qReq = new QueryRequest(solrQuery);
        QueryResponse qRes = null;
        try {
            qRes = qReq.process(solrServer);
        } catch (SolrServerException e) {
            e.printStackTrace();
        }
        //QueryResponse response = solrServer.query(solrQuery);
        SolrDocumentList docList = qRes.getResults();

        int resultCount = (int) docList.getNumFound();
        
        // DIVISION: audit-main
        Division main = body.addInteractiveDivision("audit-main", contextPath
                + "/admin/audit", Division.METHOD_POST,
                "primary audit");
        main.setHead(T_main_head);

        //Add the time filter box
        // Division searchTermsDivision = main.addDivision("search-terms");
        //searchTermsDivision.setHead(T_search_terms_head);
        //addTimeFilter(searchTermsDivision);
        // DIVISION: eperson-actions
        Division actions = main.addDivision("audit-actions");
        actions.setHead(T_actions_head);

        List actionsList = actions.addList("actions", List.TYPE_FORM);

        //actionsList.addLabel(T_actions_search);
       
        
        //Text queryField = actionItem.addText("query");
        //queryField.setAutofocus("autofocus");
       
        //Text dateStart = actionItem.addText("dStart");
        Text dateStart = actionsList.addItem().addText("dStart");
        dateStart.setLabel(T_start_date);
        dateStart.setSize(30);
    
        //Text dateEnd = actionItem.addText("dEnd");
        Text dateEnd = actionsList.addItem().addText("dEnd");
        dateEnd.setLabel(T_end_date);
        dateEnd.setSize(30);
       
        //Text tepersonMail = actionItem.addText("epersonID");
        Text tepersonMail = actionsList.addItem().addText("epersonID");
        tepersonMail.setLabel(T_email);
        tepersonMail.setSize(100);
        
        Text tHandle = actionsList.addItem().addText("handle");
        tHandle.setLabel(T_handle);
        tHandle.setSize(100);
        
        //keep values after submit
        if (startDate != null) {
            dateStart.setValue(dStart);
        }
        if (endDate !=null){
            dateEnd.setValue(dEnd);
        }
        if (!epersonEmail.equals("")){
            tepersonMail.setValue(epersonEmail);
        }
        if (!objHandle.equals("")){
            tHandle.setValue(objHandle);
        }

        /* if (query != null)
        {
            queryField.setValue(query);
        }
        queryField.setHelp(T_search_help);*/
 /*Calendar cal = Calendar.getInstance();
                Date dateEnd = cal.getTime();

                //Roll back to Jan 1 0:00.000 five years ago.
                cal.roll(Calendar.YEAR, -5);
                cal.set(Calendar.MONTH, 0);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY,0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                Date dateStart = cal.getTime();*/
 org.dspace.app.xmlui.wing.element.Item actionItem = actionsList.addItem();
        actionItem.addButton("submit_search").setValue(T_go);

        // DIVISION: eperson-search
        Division search = main.addDivision("eperson-search");
        search.setHead(T_search_head);
        // If there are more than 10 results the paginate the division.
        if (resultCount > PAGE_SIZE) {
            // If there are enough results then paginate the results
            int firstIndex = page * PAGE_SIZE + 1;
            //int lastIndex = page*PAGE_SIZE + resultCount;
            int lastIndex = page*PAGE_SIZE + PAGE_SIZE;
            if (lastIndex > resultCount){
                resultCount = lastIndex;
            }
            

            String nextURL = null, prevURL = null;
            if (page < (resultCount / PAGE_SIZE)) {
                nextURL = baseURL + "&page=" + (page + 1);
            }
            if (page > 0) {
                prevURL = baseURL + "&page=" + (page - 1);
            }

            search.setSimplePagination(resultCount, firstIndex, lastIndex, prevURL, nextURL);
        }

        Table table = search.addTable("audit-search-table", resultCount + 1, 1);
        Row header = table.addRow(Row.ROLE_HEADER);
        header.addCell().addContent(T_search_column1);
        header.addCell().addContent(T_search_column11);
        header.addCell().addContent(T_search_column2);
        header.addCell().addContent(T_search_column3);
        header.addCell().addContent(T_search_column4);

        for (SolrDocument doc : docList) {
            //Map<String,String> myMap = 
            String epersonID = String.valueOf(doc.getFieldValue("epersonid"));
            String epersonName = "";
            String epersoneMail = "";
            EPerson eperson = null;

            if (epersonID.length() > 0) {
                try {
                    eperson = ePersonService.find(context, UUID.fromString(epersonID));
                    epersonName = eperson.getFullName();
                    epersoneMail = eperson.getEmail();
                } catch (Exception e) {
                    epersonName = epersonID;
                }

            }
            //Object epersonID =doc.getFieldValue("epersonid");
            //String personName = ePersonService.getName(epersonID);
            //String urlVisited = String.valueOf(doc.getFieldValue("uid"));
            String objId = String.valueOf(doc.getFieldValue("id"));
            Bitstream bs = null;
            String bsName = "";
            if (objId.length() > 0) {
                try {
                    bs = bitstreamService.find(context, UUID.fromString(objId));
                    bsName = bs.getName();
                } catch (Exception e) {
                    bsName = objId;
                }
            }
            String timeVisited = String.valueOf(doc.getFieldValue("time"));
            String refere = String.valueOf(doc.getFieldValue("referrer"));
            /* epersonID = "";
            for (String fn :doc.getFieldNames())
            {
                epersonID = epersonID + " " + fn;
            }*/
            //String fullName = person.getFullName();
            //String email = person.getEmail();
            //String url = baseURL+"&submit_edit&epersonID="+epersonID;

            Row row;
            /*if (epersonID.equals(highlightID))
            {
                // This is a highlighted eperson
                row = table.addRow(null, null, "highlight");
            }
            else
            {
                row = table.addRow();
            }*/

            row = table.addRow();

            row.addCellContent(epersonName);
            row.addCellContent(epersoneMail);
            //row.addCellContent(urlVisited);
            row.addCellContent(bsName);
            row.addCellContent(timeVisited);
            //row.addCellContent(refere);
            row.addCell().addXref(refere,refere);
        }

        if (resultCount <= 0) {
            Cell cell = table.addRow().addCell(1, 4);
            cell.addHighlight("italic").addContent(T_no_results);
        }

        //faced count
        /* Table table = search.addTable("audit-search-table", resultCount + 1, 1);
        Row header = table.addRow(Row.ROLE_HEADER);
        header.addCell().addContent(T_search_column1);
        header.addCell().addContent(T_search_column2);
        List<FacetField> facet = qRes.getFacetFields();
        for (FacetField facet :facet){
            
        }*/
        main.addHidden("administrative-continue").setValue(knot.getId());

    }

    protected void addTimeFilter(Division mainDivision) throws WingException {
        Request request = ObjectModelHelper.getRequest(objectModel);
        String selectedTimeFilter = request.getParameter("time_filter");

        Select timeFilter = mainDivision.addPara().addSelect("time_filter");
        timeFilter.addOption(StringUtils.equals(selectedTimeFilter, "-1"), "-1", T_time_filter_last_month);
        timeFilter.addOption(StringUtils.equals(selectedTimeFilter, "-6"), "-6", T_time_filter_last6_months);
        timeFilter.addOption(StringUtils.equals(selectedTimeFilter, "-12"), "-12", T_time_filter_last_year);
        timeFilter.addOption(StringUtils.isBlank(selectedTimeFilter), "", T_time_filter_overall);
    }

    protected StatisticsSolrDateFilter getDateFilter(String timeFilter) {
        if (StringUtils.isNotEmpty(timeFilter)) {
            StatisticsSolrDateFilter dateFilter = new StatisticsSolrDateFilter();
            dateFilter.setStartStr(timeFilter);
            dateFilter.setEndStr("0");
            dateFilter.setTypeStr("month");
            return dateFilter;
        } else {
            return null;
        }
    }
}
