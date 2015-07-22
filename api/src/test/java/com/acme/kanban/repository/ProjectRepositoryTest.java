package com.acme.kanban.repository;

import com.acme.kanban.model.Project;
import com.acme.kanban.model.Step;
import com.acme.kanban.model.Story;
import com.google.common.base.Optional;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ProjectRepositoryTest extends BaseRepositoryTest {

    ProjectRepository repository;

    @Override
    public void onSetup() {
        repository = new ProjectRepository(sessionFactory);
    }

    @Test
    public void list(){
        List<Project> projects = repository.findAll();
        assertThat(projects).hasSize(4);
    }

    @Test
    public void findById(){
        Optional<Project> optional = repository.findById(1235l);
        assertThat(optional.isPresent()).isEqualTo(true);
        Project project = optional.get();
        assertThat(project.getTitle()).isEqualTo("Project F2");
    }

    @Test
    public void findByIdWithStepsAndStories(){
        Optional<Project> optional = repository.findByIdWithStepsAndStories(1235l);
        Project project = optional.get();

        // On force la fermeture de la transaction pour
        // s'assurer que les stories et steps ont effectivement
        // été remontées
        tx.commit();

        org.hibernate.Transaction tx1 = getSession().beginTransaction();
        Step s1 = (Step) getSession().get(Step.class, 20l);
        Step s2 = (Step) getSession().get(Step.class, 21l);
        Step s3 = (Step) getSession().get(Step.class, 22l);

        Story story1 = (Story)  getSession().get(Story.class, 200l);
        Story story2 = (Story)  getSession().get(Story.class, 201l);
        Story story3 = (Story)  getSession().get(Story.class, 202l);
        tx1.commit();

        // Les steps sont ordonnées d'après la colonne order,
        // L'ordre des ids attendu est 21, 20, 22
        assertThat(project.getSteps()).containsExactly(s2,s1,s3);
        assertThat(project.getStories()).containsExactly(story1,story2,story3);
    }

    @Test
    public void create(){
        Project newProject = Project.builder().title("Awesome new project").description("...").build();
        repository.create(newProject);

        // On ferme la transaction pour s'assurer que le projet est bien créé
        tx.commit();

        org.hibernate.Transaction tx1 = getSession().beginTransaction();
        Long count =  (Long) getSession().createQuery("SELECT COUNT(*) FROM Project").uniqueResult();
        assertThat(count).isEqualTo(5l);

        Project lastProject = getLastProject();
        assertThat(lastProject).isEqualToComparingOnlyGivenFields(newProject, "title", "description");
        tx1.commit();
    }

    @Test
    public void createShouldCreateDefaultSteps(){
        Project newProject = Project.builder().title("New project with steps").description("...").build();
        repository.create(newProject);

        // On ferme la transaction pour s'assurer que le projet est bien créé
        tx.commit();

        org.hibernate.Transaction tx1 = getSession().beginTransaction();
        Project lastProject = getLastProject();
        assertThat(lastProject.getSteps()).extracting("title").containsExactly("TODO", "DEVELOP", "DONE");
        tx1.commit();
    }

    private Project getLastProject() {
        // On récupère le plus grand id (le dernier) de la table "projects"
        Long lastId =  (Long) getSession().createQuery("SELECT MAX(id) FROM Project").uniqueResult();
        return (Project) getSession().get(Project.class, lastId);
    }

    @Test
    public void findByIdNonInexisting(){
        Optional<Project> optional = repository.findById(9999l);
        assertThat(optional.isPresent()).isEqualTo(false);
    }

    @Test
    public void update(){
        Project originalProject = repository.findById(1235l).get();
        originalProject.setTitle("this is the new title");
        Project wish = repository.update(originalProject).get();
        assertThat(wish.getTitle()).isEqualTo(originalProject.getTitle());
    }

    @Test
    public void updateWithNonExisting() {
        Project nonExistingProject = Project.builder( ).id(9999l).title("title").build();
        Optional<Project> optional = repository.update(nonExistingProject);
        assertThat(optional.isPresent()).isEqualTo(false);
    }

    @Test
    public void delete(){
        Optional<Project> optional = repository.delete(1235l);
        assertThat(optional.isPresent()).isEqualTo(true);
        Project wish = optional.get();
        assertThat(wish.getTitle()).isEqualTo("Project F2");
        List<Project> remainingProjects = repository.findAll();
        assertThat(remainingProjects).hasSize(3);
    }

    @Test
    public void deleteNonInexisting(){
        Optional<Project> optional = repository.delete(9999l);
        assertThat(optional.isPresent()).isEqualTo(false);
    }
}