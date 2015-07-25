package com.acme.kanban.resource;

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
    public static boolean checkEntityExists(Session session, Class klass, Long id) {
        final Object result = session.createCriteria(klass)
                .add(Restrictions.eq("id", id))
                .setProjection(Projections.property("id"))
                .uniqueResult();
        return result != null;
    }
}
