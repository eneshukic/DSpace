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

import org.dspace.content.Collection;
import org.dspace.content.MetadataValue;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.core.Constants;
import org.dspace.curate.AbstractCurationTask;
import org.dspace.curate.Curator;
import org.dspace.curate.Suspendable;
import org.dspace.curate.Distributive;

/**
 *
 * @author Enes
 */
public class InheritCollectionPermissions extends AbstractCurationTask {

   
    @Override
    public void init(Curator curator, String taskId) throws IOException {
        super.init(curator, taskId);
        
    }

    /**
     * Perform the curation task upon passed DSO
     *
     * @param dso the DSpace object
     * @throws IOException if IO error
     */
    @Override
    public int perform(DSpaceObject dso) throws IOException {
        int count = 0;
        StringBuilder sb = new StringBuilder();
        if (dso.getType() == Constants.ITEM) {
            Item item = (Item) dso;
            //int count = 0;
            try {
               
                performItem(item);
            /*} catch (DCInputsReaderException dcrE) {
                sb.append(dcrE.getMessage());
                throw new IOException(dcrE.getMessage(), dcrE);
            }*/
            }catch (SQLException sqlE) {
    		throw new IOException(sqlE.getMessage(), sqlE);
            }
            report(sb.toString());
            setResult(sb.toString());
            return (count == 0) ? Curator.CURATE_SUCCESS : Curator.CURATE_FAIL;
        } else {
            setResult("Object skipped");
            return Curator.CURATE_SKIP;
        }
        // return (count == 0) ? Curator.CURATE_SUCCESS : Curator.CURATE_FAIL;

    }

    @Override
    protected void performItem(Item item) throws SQLException, IOException {

        // get the owning collection
        Collection owningColl = item.getOwningCollection();

        // inherit the policies for this item from its owning collection
        try {
           itemService.adjustItemPolicies(Curator.curationContext(), item, owningColl);
           // addResult(item, "success", "Inherited policies from owning collection");
            return;

        } catch (Exception e) {

            //addResult(item, "error", "Unable to inherit policies from owning collection");
            return;
        }
    }

    /*private void addResult(Item item, String status, String message) {
        results.add(item.getHandle() + " (" + status + ") " + message);
    }

    private void formatResults() {
        StringBuilder outputResult = new StringBuilder();
        for (String result : results) {
            outputResult.append(result).append("\n");
        }
        setResult(outputResult.toString());
    }*/

}
