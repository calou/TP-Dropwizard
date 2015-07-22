package com.acme.kanban.resource;

import com.acme.kanban.model.Story;
import com.acme.kanban.repository.StoryRepository;
import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/stories")
@Produces(MediaType.APPLICATION_JSON)
public class StoryResource {

    private final StoryRepository repository;

    public StoryResource(StoryRepository repo){
        this.repository = repo;
    }

    @POST
    @UnitOfWork
    public Story create(Story story){
        return repository.create(story);
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
    public Story update(@PathParam("id") Long id, Story story){
        final Optional<Story> optional = repository.update(story);
        throwExceptionIfAbsent(optional, story.getId());
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
        return optional.get();
    }

    private void throwExceptionIfAbsent(Optional<Story> optional, Long id) {
        if(!optional.isPresent()){
            throw new NotFoundException("Story with id " + id);
        }
    }
}
