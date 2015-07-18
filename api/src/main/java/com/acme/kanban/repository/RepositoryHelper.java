package com.acme.kanban.repository;

import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public final class RepositoryHelper {
    /**
     * Vérifie que l'entité existe
     * @param session
     * @param klass
     * @param id
     * @return
     */
    public static boolean checkEntityExist(Session session, Class klass, Long id) {
        return session.createCriteria(klass)
                .add(Restrictions.eq("id", id))
                .setProjection(Projections.property("id"))
                .uniqueResult() == null ? false : true;
    }
}
