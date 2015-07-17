package com.acme.kanban.repository;


import com.acme.kanban.model.KanbanStep;
import com.acme.kanban.model.Project;
import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import java.util.List;
import java.util.Set;

public class ProjectRepository extends AbstractDAO<Project> {
    public ProjectRepository(SessionFactory factory) {
        super(factory);
    }

    public List<Project> findAll() {
        return list(namedQuery("Project.findAll"));
    }

    public Optional<Project> findById(Long id) {
        return Optional.fromNullable(get(id));
    }

    public Optional<Project> findByIdWithStepsAndStories(Long id) {
        Query query = currentSession().getNamedQuery("Project.findAllWithStoriesAndSteps").setLong("project_id", id);
        return Optional.fromNullable(uniqueResult(query));
    }

    public Project create(Project project) {
        Set<KanbanStep> steps = project.getSteps();
        if(steps == null || steps.isEmpty()){
            KanbanStep todoStep = KanbanStep.builder().order(1).title("TODO").project(project).build();
            KanbanStep developStep = KanbanStep.builder().order(2).title("DEVELOP").project(project).build();
            KanbanStep doneStep = KanbanStep.builder().order(3).title("DONE").project(project).build();
            project.setSteps(Sets.newHashSet(todoStep, developStep,doneStep));
        }
        return persist(project);
    }

    public Optional<Project> update(Project project) {
        // On teste si l'entité existe
        if (!checkEntityExists(project.getId())) {
            return Optional.absent();
        }
        Project merged = (Project) this.currentSession().merge(project);
        Optional<Project> optional = Optional.fromNullable(merged);
        this.currentSession().saveOrUpdate(optional.get());
        return optional;
    }

    public Optional<Project> delete(Long id) {
        if (!checkEntityExists(id)) {
            return Optional.absent();
        }
        Optional<Project> optional = Optional.fromNullable(get(id));
        this.currentSession().delete(optional.get());
        return optional;
    }

    public final boolean checkEntityExists(Long id) throws HibernateException {
        return this.currentSession().createCriteria(Project.class)
                .add(Restrictions.eq("id", id))
                .setProjection(Projections.property("id"))
                .uniqueResult() == null ? false : true;
    }
}
