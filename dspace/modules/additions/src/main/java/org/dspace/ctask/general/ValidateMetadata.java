/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dspace.ctask.general;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

import org.dspace.app.util.DCInput;
import org.dspace.app.util.DCInputSet;
import org.dspace.app.util.DCInputsReader;
import org.dspace.app.util.DCInputsReaderException;
import org.dspace.content.MetadataValue;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.core.Constants;
import org.dspace.curate.AbstractCurationTask;
import org.dspace.curate.Curator;
import org.dspace.curate.Suspendable;

/**
 *
 * @author Enes
 * ValidateMetadata task does these validates:
 * 1. checks if required metadata fields in input-forms.xml have the value
 * 2. checks if dropdown fields in input forms have a value defined in values list
 */

@Suspendable
public class ValidateMetadata extends AbstractCurationTask {
    
    // map of DCInputSets
    protected DCInputsReader reader = null;
    // map of required fields
    protected Map<String, List<String>> reqMap = new HashMap<String, List<String>>();
    
    private static Logger log = Logger.getLogger(ValidateMetadata.class);
    protected List<String> results = null;
    
    @Override 
    public void init(Curator curator, String taskId) throws IOException
    {
        super.init(curator, taskId);
        try
        {
            reader = new DCInputsReader();
        }
        catch (DCInputsReaderException dcrE)
        {
            throw new IOException(dcrE.getMessage(), dcrE);
        }
    }

    /**
     * Perform the curation task upon passed DSO
     *
     * @param dso the DSpace object
     * @throws IOException if IO error
     */
    @Override
    public int perform(DSpaceObject dso) throws IOException
    {
        int count = 0;
        StringBuilder sb = new StringBuilder();
        if (dso.getType() == Constants.ITEM)
        {
            Item item = (Item)dso;
            //int count = 0;
            try
            {
                //StringBuilder sb = new StringBuilder();
                String handle = item.getHandle();
                if (handle == null)
                {
                    // we are still in workflow - no handle assigned
                    handle = "in workflow";
                }
                sb.append("Item: ").append(handle);
                //sb.append("Item: ").append(handle).append("location: ").append(contextPath + "/handle/" + handle);
                //check if requied fields have value
                for (String req : getReqList(item.getOwningCollection().getHandle()))
                {
                    List<MetadataValue> vals = itemService.getMetadataByMetadataString(item, req);
                    if (vals.size() == 0)
                    {
                        sb.append(" missing required field: ").append(req);
                        count++;
                    }
                    else {
                        //check if there is value in the field
                        String myVal = itemService.getMetadata(item, req);
                        if (myVal.equals("")){
                            sb.append(" missing value for required field: ").append(req);
                            count++;
                        }
                    }
                }
                if (count == 0)
                {
                    sb.append(" has all required fields/n");
                }
                //get item's drop down list and make sure that metadata value matches lists value
                for (String ddl : getDropDownList(item.getOwningCollection().getHandle()))
                {
                    List<MetadataValue> vals = itemService.getMetadataByMetadataString(item, ddl);
                    if (vals.size() > 0)
                     {
                        //check if there is value in the field
                        String myVal = itemService.getMetadata(item, ddl);
                        
                        if (myVal.equals("")){
                            sb.append(" missing value for field: ").append(ddl);
                            count++;
                        }
                        else
                        {
                            //create DCInput and get a list of input drop down input values)
                           List<String> myList = getDropDownValues(item.getOwningCollection().getHandle(),ddl);
                           if (!myList.contains(myVal))
                           {
                               sb.append(" incorrect value for field: ").append(ddl).append(" value ").append(myVal);
                               count++;
                           }
                            //sb.append(" value: ").append(myVal);
                        }
                    }
                }
                if (count == 0)
                {
                    sb.append(" has all list of values fields/n");
                }
                //report(sb.toString());
                //setResult(sb.toString());
            }
            catch (DCInputsReaderException dcrE)
            {
                sb.append(dcrE.getMessage());
                throw new IOException(dcrE.getMessage(), dcrE);
            }
            report(sb.toString());
            setResult(sb.toString());
            log.info(sb.toString());
            //results.add(sb.toString());
            //formatResults(item);
            return (count == 0) ? Curator.CURATE_SUCCESS : Curator.CURATE_FAIL;
        }
        else
        {
           //setResult("Object skipped");
           return Curator.CURATE_SKIP;
        }
       // return (count == 0) ? Curator.CURATE_SUCCESS : Curator.CURATE_FAIL;
      
            //setResult(sb.toString());
            //return (count == 0) ? Curator.CURATE_SUCCESS : Curator.CURATE_FAIL;
    }
    
    protected List<String> getReqList(String handle) throws DCInputsReaderException
    {
        List<String> reqList = reqMap.get(handle);
        if (reqList == null)
        {
            reqList = reqMap.get("default");
        }
        if (reqList == null)
        {
            reqList = new ArrayList<String>();
            DCInputSet inputs = reader.getInputs(handle);
            for (int i = 0; i < inputs.getNumberPages(); i++)
            {
                for (DCInput input : inputs.getPageRows(i, true, true))
                {
                    if (input.isRequired())
                    {
                        StringBuilder sb = new StringBuilder();
                        sb.append(input.getSchema()).append(".");
                        sb.append(input.getElement()).append(".");
                        String qual = input.getQualifier();
                        if (qual == null)
                        {
                            qual = "";
                        }
                        sb.append(qual);
                        reqList.add(sb.toString());
                    }
                }
            }
            reqMap.put(inputs.getFormName(), reqList);
        }
        return reqList;
    }
    
    /*
    Gets list of items dropdown and qual-drop lists
    */
    protected List<String> getDropDownList(String handle) throws DCInputsReaderException
    {
        List<String> reqList = reqMap.get(handle);
        if (reqList == null)
        {
            reqList = reqMap.get("default");
        }
        if (reqList == null)
        {
            reqList = new ArrayList<String>();
            DCInputSet inputs = reader.getInputs(handle);
            for (int i = 0; i < inputs.getNumberPages(); i++)
            {
                for (DCInput input : inputs.getPageRows(i, true, true))
                {
                    if (input.getInputType().equals("dropdown") || input.getInputType().equals("qualdrop_value") || 
                            input.getInputType().equals("list"))
                    {
                        StringBuilder sb = new StringBuilder();
                        sb.append(input.getSchema()).append(".");
                        sb.append(input.getElement()).append(".");
                        String qual = input.getQualifier();
                        if (qual == null)
                        {
                            qual = "";
                        }
                        sb.append(qual);
                        reqList.add(sb.toString());
                    }
                }
            }
            reqMap.put(inputs.getFormName(), reqList);
        }
        return reqList;
    }
    
    protected List<String> getDropDownValues(String handle, String ddlName) throws DCInputsReaderException
    {
        List<String> ddlVals = new ArrayList<String>() ;
        DCInputSet inputs = reader.getInputs(handle);
            for (int i = 0; i < inputs.getNumberPages(); i++)
            {
                for (DCInput input : inputs.getPageRows(i, true, true))
                {
                    if (input.getInputType().equals("dropdown") || input.getInputType().equals("qualdrop_value") || 
                            input.getInputType().equals("list"))
                    {
                        StringBuilder sb = new StringBuilder();
                        sb.append(input.getSchema()).append(".");
                        sb.append(input.getElement()).append(".");
                        String qual = input.getQualifier();
                        if (qual == null)
                        {
                            qual = "";
                        }
                        sb.append(qual);
                        if (sb.toString().equals(ddlName))
                        {
                            ddlVals = input.getPairs();
                        }
                    }
                }
            }
        
        return ddlVals;
    }
    
    protected void formatResults(Item item) throws IOException
    {
        StringBuilder sb = new StringBuilder();
        //sb.append("Item: ").append(getItemHandle(item)).append(" ");
        int count = 0;
            for (String validateresult : results)
            {
                sb.append("\n").append(validateresult).append("\n");
                count++;
            }
        /*if (status == Curator.CURATE_FAIL)
        {
            sb.append(INFECTED_MESSAGE);
            int count = 0;
            for (String scanresult : results)
            {
                sb.append("\n").append(scanresult).append("\n");
                count++;
            }
            sb.append(count).append(" virus(es) found. ")
                            .append(" failfast: ").append(failfast);
        }
        else
        {
            sb.append(CLEAN_MESSAGE);
        }*/
        setResult(sb.toString());
    }
    
}
