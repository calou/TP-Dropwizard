package com.acme.kanban.resource;

import com.acme.kanban.model.Story;
import com.acme.kanban.repository.ProjectRepository;
import com.acme.kanban.repository.StoryRepository;
import com.acme.kanban.repository.StoryRepository;
import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/projects/{projectId}/stories")
@Produces(MediaType.APPLICATION_JSON)
public class StoryResource {

    private final StoryRepository repository;
    private final ProjectRepository projectRepository;

    public StoryResource(StoryRepository repo, ProjectRepository projectRepository){
        this.repository = repo;
        this.projectRepository = projectRepository;
    }

    @GET
    @Timed
    @UnitOfWork
    public List<Story> list(@PathParam("projectId") Long projectId){
        return repository.findAllByProjectId(projectId);
    }


    @POST
    @UnitOfWork
    public Story create(@PathParam("projectId") Long projectId, Story todo){
        return repository.create(todo);
    }

    @GET
    @Path("/{id}")
    @Timed
    @UnitOfWork
    public Story get(@PathParam("id") Long id){
        return findSafely(id);
    }

    @PUT
    @Path("/{id}")
    @Timed
    @UnitOfWork
    @Consumes(MediaType.APPLICATION_JSON)
    public Story update(@PathParam("id") Long id, Story project){
        project.setId(id);
        final Optional<Story> optional = repository.update(project);
        throwExceptionIfAbsent(optional, project.getId());
        return optional.get();
    }

    @DELETE
    @Path("/{id}")
    @Timed
    @UnitOfWork
    public Story delete(@PathParam("id") Long id){
        final Optional<Story> optional = repository.delete(id);
        throwExceptionIfAbsent(optional, id);
        return optional.get();
    }

    private Story findSafely(Long id) {
        Optional<Story> optional = repository.findById(id);
        throwExceptionIfAbsent(optional, id);
        Story todo = optional.get();
        return todo;
    }

    private void throwExceptionIfAbsent(Optional<Story> optional, Long id) {
        if(!optional.isPresent()){
            throw new NotFoundException("Todo with id " + id);
        }
    }
}
