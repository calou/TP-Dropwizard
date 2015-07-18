package com.acme.kanban.repository;

import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public final class RepositoryHelper {
    public static boolean checkEntityExist(Session session, Class klass, Long id) {
        return session.createCriteria(klass)
                .add(Restrictions.eq("id", id))
                .setProjection(Projections.property("id"))
                .uniqueResult() == null ? false : true;
    }
}
