package com.acme.kanban.repository;


import com.acme.kanban.model.Story;
import com.google.common.base.Optional;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class StoryRepository extends AbstractDAO<Story> {
    public StoryRepository(SessionFactory factory) {
        super(factory);
    }

    public List<Story> findAllByProjectId(Long projectId) {
        Query query = currentSession().getNamedQuery("Story.findAllByProject").setLong("project_id", projectId);
        return list(query);
    }

    public Optional<Story> findById(Long id) {
        return Optional.fromNullable(get(id));
    }

    public Story create(Story story) {
        return persist(story);
    }

    public Optional<Story> update(Story story) {
        // On teste si l'entité existe
        if (!checkEntityExists(story.getId())) {
            return Optional.absent();
        }
        Story merged = (Story) this.currentSession().merge(story);
        Optional<Story> optional = Optional.fromNullable(merged);
        this.currentSession().saveOrUpdate(optional.get());
        return optional;
    }

    public Optional<Story> delete(Long id) {
        if (!checkEntityExists(id)) {
            return Optional.absent();
        }
        Optional<Story> optional = Optional.fromNullable(get(id));
        this.currentSession().delete(optional.get());
        return optional;
    }

    public final boolean checkEntityExists(Long id) throws HibernateException {
        return this.currentSession().createCriteria(Story.class)
                .add(Restrictions.eq("id", id))
                .setProjection(Projections.property("id"))
                .uniqueResult() == null ? false : true;
    }
}
