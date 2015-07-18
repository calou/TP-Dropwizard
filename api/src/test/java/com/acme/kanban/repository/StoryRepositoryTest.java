package com.acme.kanban.repository;

import com.acme.kanban.model.Step;
import com.acme.kanban.model.Project;
import com.acme.kanban.model.Story;
import com.google.common.base.Optional;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class StoryRepositoryTest extends BaseRepositoryTest {

    StoryRepository repository;

    @Override
    public void onSetup() {
        repository = new StoryRepository(sessionFactory);
    }

    @Test
    public void findAllByProjectId(){
        List<Story> stories = repository.findAllByProjectId(1234l);

        Story story1 = (Story)  getSession().get(Story.class, 100l);
        Story story2 = (Story)  getSession().get(Story.class, 101l);
        Story story3 = (Story)  getSession().get(Story.class, 102l);
        Story story4 = (Story)  getSession().get(Story.class, 103l);

        assertThat(stories).hasSize(4).containsExactly(story1, story2, story3, story4);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void findAllByProjectIdWithUnavailableProject(){
        repository.findAllByProjectId(9999l);
    }

    @Test
    public void create(){
        Project project = (Project) getSession().get(Project.class, 1234l);
        Step step = project.getSteps().iterator().next();

        Story newStory = Story.builder().title("Awesome new story").description("...").project(project).step(step).build();
        repository.create(newStory);

        // On ferme la transaction pour s'assurer que le projet est bien créé
        tx.commit();

        final long expected = 8l;

        org.hibernate.Transaction tx1 = beginTransaction();
        assertStoryCountIs(expected);

        Story lastStory = getLastStory();
        assertThat(lastStory).isEqualToIgnoringNullFields(newStory);
        tx1.commit();
    }

    @Test
    public void createShouldSetNumberIfNotProvided(){
        Project project = (Project) getSession().get(Project.class, 1234l);
        Step step = project.getSteps().iterator().next();

        Story newStory = Story.builder().title("Awesome new story").description("...").project(project).step(step).build();
        Story savedStory = repository.create(newStory);

        assertThat(savedStory.getNumber()).isEqualTo(5);
    }

    @Test
    public void createShouldSetNumberIfNotProvidedEvenNoneStoryAlreadyExist(){
        // On supprime toutes les stories
        getSession().createQuery("DELETE FROM Story").executeUpdate();
        Project project = (Project) getSession().get(Project.class, 1234l);
        Step step = project.getSteps().iterator().next();

        Story newStory = Story.builder().title("Awesome new story").description("...").project(project).step(step).build();
        Story savedStory = repository.create(newStory);

        assertThat(savedStory.getNumber()).isEqualTo(1);
    }

    @Test
    public void createShouldNotChangeNumberIfProvided(){
        Project project = (Project) getSession().get(Project.class, 1234l);
        Step step = project.getSteps().iterator().next();

        Story newStory = Story.builder().number(12).title("Awesome new story").description("...").project(project).step(step).build();
        Story savedStory = repository.create(newStory);

        assertThat(savedStory.getNumber()).isEqualTo(12);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void createWithNonExistingProject(){
        Project project = Project.builder().id(9999l).build();

        Story newStory = Story.builder().title("Story with non-existing project").description("...").project(project).build();
        repository.create(newStory);
    }

    private void assertStoryCountIs( long expected) {
        Long count =  (Long) getSession().createQuery("SELECT COUNT(*) FROM Story").uniqueResult();
        assertThat(count).isEqualTo(expected);
    }

    private Story getLastStory() {
        // On récupère le plus grand id (le dernier) de la table "projects"
        Long lastId =  (Long) getSession().createQuery("SELECT MAX(id) FROM Story").uniqueResult();
        return (Story) getSession().get(Story.class, lastId);
    }

    @Test
    public void findByIdNonInexisting(){
        Optional<Story> optional = repository.findById(9999l);
        assertThat(optional.isPresent()).isEqualTo(false);
    }

    @Test
    public void update(){
        Step step = (Step) getSession().get(Step.class, 22l);

        Story originalStory = repository.findById(200l).get();
        originalStory.setTitle("this is the new title");
        originalStory.setStep(step);

        tx.commit();

        org.hibernate.Transaction tx1 = getSession().beginTransaction();
        Story updatedStory = (Story) getSession().get(Story.class, originalStory.getId());
        assertThat(updatedStory.getTitle()).isEqualTo(originalStory.getTitle());
        assertThat(updatedStory.getStep()).isEqualTo(originalStory.getStep());
        tx1.commit();
    }

    @Test
    public void updateWithNonExisting() {
        Story nonExistingStory = Story.builder( ).id(9999l).title("title").build();
        Optional<Story> optional = repository.update(nonExistingStory);
        assertThat(optional.isPresent()).isEqualTo(false);
    }

    @Test
    public void delete(){
        Optional<Story> optional = repository.delete(200l);
        assertThat(optional.isPresent()).isEqualTo(true);
        Story wish = optional.get();
        assertThat(wish.getTitle()).isEqualTo("Todo feature");

        tx.commit();

        Session session = getSession();
        org.hibernate.Transaction tx1 = beginTransaction();
        assertStoryCountIs(6l);
        tx1.commit();
    }

    @Test
    public void deleteNonInexisting(){
        Optional<Story> optional = repository.delete(9999l);
        assertThat(optional.isPresent()).isEqualTo(false);
    }
}