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
    
}
