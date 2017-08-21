/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dspace.rate.dao.impl;

import org.dspace.core.AbstractHibernateDAO;
import org.dspace.rate.CostCalculation;
import org.dspace.rate.dao.CostCalculationDAO;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
 
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
/**
 *
 * @author Enes
 */
public class CostCalculationDAOImpl extends AbstractHibernateDAO<CostCalculation> implements CostCalculationDAO {
    @Override
    public List<CostCalculation> calculateRateCost(Context context, UUID Id, String  costTypeId) throws SQLException {
        String strQuery = "select mv.text_value, count (mv.text_value) kol, r.rate_description, sum(r.price) pricet " +
"from metadatavalue mv " +
"inner join metadatafieldregistry mfr on mfr.metadata_field_id = mv.metadata_field_id " +
"inner join rate r on r.rate_grade = mv.text_value " +
"where mfr.element = 'tarrif' " +
"and dspace_object_id in (select i.uuid " +
"                        from item i " +
"                        inner join collection2item c2i on c2i.item_id = i.uuid " +
"inner join collection c on c.uuid = c2i.collection_id " +
                " inner join community2collection c2c on c2c.collection_id = c.uuid where ) ";
        switch (costTypeId) {
            case ("communityID"):
                strQuery.concat("c2c.commnity_id = :community_id ");
                break;
            case ("collectionID"):
                strQuery.concat("c.uuid = :collection_id ");
                break;
            case ("itemID"):
                strQuery.concat("i.uuid = :item_id ");
                break;
            default:
                strQuery.concat("1=2 ");
                break;
        }
        strQuery.concat("group by mv.text_value, r.rate_description");
        Query query = createQuery(context, strQuery);
        switch (costTypeId) {
            case ("communityID"):
                query.setParameter("community_id", Id);
                break;
            case ("collectionID"):
                query.setParameter("collection_id", Id);
                break;
            case ("itemID"):
                query.setParameter("item_id", Id);
                break;
            default:
                break;
        }
        //return list<CostCalculation>(query,CostCalculation.class);
        return list(query);
    }
}
