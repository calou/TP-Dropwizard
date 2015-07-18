package com.acme.kanban.repository;


import com.acme.kanban.model.Project;
import com.acme.kanban.model.Story;
import com.google.common.base.Optional;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.*;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class StoryRepository extends AbstractDAO<Story> {
    public StoryRepository(SessionFactory factory) {
        super(factory);
    }

    public List<Story> findAllByProjectId(Long projectId) {
        Query query = currentSession().getNamedQuery("Story.findAllByProject").setLong("project_id", projectId);
        final List<Story> stories = list(query);
        // Si la liste des stories est vide, on vérifie que le project existe.
        // Si le project n'existe pas alors on lève une exception
        if(stories.isEmpty()){
            checkProjectExist(projectId);
        }
        return stories;
    }

    public Optional<Story> findById(Long id) {
        return Optional.fromNullable(get(id));
    }

    public Story create(Story story) {
        // On vérifie que le project existe
        checkProjectExist(story.getProject().getId());
        return persist(story);
    }

    public Optional<Story> update(Story story) {
        // On vérifie que l'entité existe, si ce n'est pas le cas
        // alors on retourne Optional.absent
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
        return RepositoryHelper.checkEntityExist(this.currentSession(), Story.class, id);
    }

    private void checkProjectExist(Long projectId) {
        if (!RepositoryHelper.checkEntityExist(this.currentSession(), Project.class, projectId)) {
            throw new ObjectNotFoundException(projectId, "Project");
        }
    }
}
