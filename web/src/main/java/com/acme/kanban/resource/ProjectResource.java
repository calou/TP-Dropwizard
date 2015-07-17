package com.acme.kanban.resource;

import com.acme.kanban.model.Project;
import com.acme.kanban.repository.ProjectRepository;
import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/projects")
@Produces(MediaType.APPLICATION_JSON)
public class ProjectResource {

    private final ProjectRepository repository;

    public ProjectResource(ProjectRepository repo){
        this.repository = repo;
    }

    @GET
    @Timed
    @UnitOfWork
    public List<Project> list(){
        return repository.findAll();
    }


    @POST
    @UnitOfWork
    public Project create(Project todo){
        return repository.create(todo);
    }

    @GET
    @Path("/{id}")
    @Timed
    @UnitOfWork
    public Project get(@PathParam("id") Long id){
        return findSafely(id);
    }

    @PUT
    @Path("/{id}")
    @Timed
    @UnitOfWork
    @Consumes(MediaType.APPLICATION_JSON)
    public Project update(@PathParam("id") Long id, Project project){
        project.setId(id);
        final Optional<Project> optional = repository.update(project);
        throwExceptionIfAbsent(optional, project.getId());
        return optional.get();
    }

    @DELETE
    @Path("/{id}")
    @Timed
    @UnitOfWork
    public Project delete(@PathParam("id") Long id){
        final Optional<Project> optional = repository.delete(id);
        throwExceptionIfAbsent(optional, id);
        return optional.get();
    }

    private Project findSafely(@PathParam("id") Long id) {
        Optional<Project> optional = repository.findByIdWithStepsAndStories(id);
        throwExceptionIfAbsent(optional, id);
        Project todo = optional.get();
        return todo;
    }

    private void throwExceptionIfAbsent(Optional<Project> optional, Long id) {
        if(!optional.isPresent()){
            throw new NotFoundException("Todo with id " + id);
        }
    }
}
