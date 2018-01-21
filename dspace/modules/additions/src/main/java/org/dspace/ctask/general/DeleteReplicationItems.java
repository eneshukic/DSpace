/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dspace.ctask.general;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dspace.app.util.DCInput;
import org.dspace.app.util.DCInputSet;
import org.dspace.app.util.DCInputsReader;
import org.dspace.app.util.DCInputsReaderException;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.MetadataValue;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.core.Constants;
import org.dspace.curate.AbstractCurationTask;
import org.dspace.curate.Curator;
import org.dspace.curate.Suspendable;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
/**
 *
 * @author Enes
 * DeleteReplicationItems searches collection for items that do not have metadata da.replication.mco = appropriate value
 * and deletes those items.  This task is used to eliminate those itams that were harvested from the central repository
 */
public class DeleteReplicationItems extends AbstractCurationTask 
{
    // map of DCInputSets
    protected DCInputsReader reader = null;
    // map of required fields
    protected Map<String, List<String>> reqMap = new HashMap<String, List<String>>();
    
    //municipality that we should keep
    static String mco = ConfigurationManager.getProperty("mco");
    static String fieldMCO = "da.replication.mco";
    
    /** The Context */
   // Context c;
    
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
        if (dso.getType() == Constants.ITEM && !mco.equals(""))
        {
            Item item = (Item)dso;
            int count = 0;
            try
            {
                StringBuilder sb = new StringBuilder();
                String handle = item.getHandle();
                if (handle == null)
                {
                    // we are still in workflow - no handle assigned
                    handle = "in workflow";
                }
                sb.append("Item: ").append(handle);
               
                //first check if item has fieldMCO.  If not delete it
                List<MetadataValue> vals = itemService.getMetadataByMetadataString(item, fieldMCO);
                if (vals.size() == 0)
                {
                    sb.append(" missing mco field. deleting item. ");
                    //Context context = curationContext();
                    itemService.delete(Curator.curationContext(), item);
                    count++;
                }
                else {
                    //check if there is value in the list of fields
                    //can't use lambda expressions
                    //if (vals.stream().anyMatch(val -> vals.getValue()==mco)
                    boolean contains = false;
                    for (MetadataValue val : vals){
                        if (val.getValue().equals(mco))
                        {
                            contains = true;
                            break;
                        }
                    }
                    if (!contains)
                    {
                        sb.append(" item does not belong here. Deleting item ");
                        itemService.delete(Curator.curationContext(), item);
                        count++;
                    }
                    /*
                    String myVal = itemService.getMetadata(item, fieldMCO);
                    if (!myVal.equals(mco)){
                        sb.append(" item does not belong here. Deleting item ");
                        itemService.delete(Curator.curationContext(), item);
                        count++;
                     }
                    */
                    
                            
                }
               
                if (count == 0)
                {
                    sb.append(" has all required fields");
                }
                report(sb.toString());
                setResult(sb.toString());
            }
            /*catch (DCInputsReaderException dcrE)
            {
                throw new IOException(dcrE.getMessage(), dcrE);
            }*/
            catch (SQLException sqlE) {
    		throw new IOException(sqlE.getMessage(), sqlE);
            }
            catch (AuthorizeException authE) {
    		throw new IOException(authE.getMessage(), authE);
            }
            return (count == 0) ? Curator.CURATE_SUCCESS : Curator.CURATE_FAIL;
        }
        else
        {
           setResult("Object skipped");
           return Curator.CURATE_SKIP;
        }
    }
    
    /*protected List<String> getMCOList(String handle) throws DCInputsReaderException
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
    }*/
    
}
