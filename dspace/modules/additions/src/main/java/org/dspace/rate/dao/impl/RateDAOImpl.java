/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dspace.rate.dao.impl;

import org.dspace.rate.Rate;
import org.dspace.rate.dao.RateDAO;
import org.dspace.core.AbstractHibernateDAO;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
//import org.hibernate.transform.Transformers;
//import javax.persistence.TypedQuery;


/**
 *
 * @author Enes
 */
public class RateDAOImpl extends AbstractHibernateDAO<Rate> implements RateDAO {
    
    @Override
    public Rate findRateByRateGrade(Context context, String rateGrade) throws SQLException{
        Criteria criteria = createCriteria(context,Rate.class);
        //criteria.add(Restrictions.and(Restrictions.eq("rate_grade",rateGrade)));
        criteria.add(Restrictions.eq("rate_grade",rateGrade));
        return uniqueResult(criteria);
    }
    @Override
    public List<Object[]> calculateRateCost(Context context, UUID Id, String  costTypeId) throws SQLException {
       
       String strQuery = "select   mv.text_value, count (mv.text_value)as kol, r.rate_description, r.calculation_unit, r.price unit_price, sum(r.price) as price\n" +
"from metadatavalue mv\n " +
"inner join metadatafieldregistry mfr on mfr.metadata_field_id = mv.metadata_field_id\n" +
"inner join rate r on r.rate_grade = mv.text_value\n" +
"inner join item i on i.uuid = mv.dspace_object_id\n" +
"inner join collection2item c2i on c2i.item_id = i.uuid\n" +
"inner join collection c on c.uuid = c2i.collection_id\n" +
"inner join community2collection c2c on c2c.collection_id = c.uuid\n" +
"where mfr.element = 'tarrif' \n ";
        switch (costTypeId) {
            case ("communityID"):
                strQuery += "and c2c.community_id = :community_id \n";
                break;
            case ("collectionID"):
                strQuery+=" and c.uuid = :collection_id \n";
                break;
            case ("itemID"):
                strQuery+="and i.uuid = :item_id \n";
                break;
            default:
                //do not want any data
                strQuery+="and 1=2 ";
                break;
        }
        strQuery +="group by  mv.text_value,r.rate_description, r.calculation_unit, r.price order by mv.text_value";

       Query query = getHibernateSession(context).createSQLQuery(strQuery);
        //Query query = createQuery(context, strQuery);
        //query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        
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

        return query.list();
    }
    
}
