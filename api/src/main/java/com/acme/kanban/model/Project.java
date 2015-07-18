package com.acme.kanban.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "projects")
@NamedQueries({
        @NamedQuery(
                name = "Project.findAll",
                query = "SELECT t FROM Project t"
        ),
        @NamedQuery(
                name = "Project.findAllWithStoriesAndSteps",
                query = "SELECT p FROM Project p LEFT JOIN FETCH p.stories LEFT JOIN FETCH p.steps WHERE p.id = :project_id"
        )
})
@Getter
@Setter
@EqualsAndHashCode(of={"id", "title", "description"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="title")
    private String title;

    @Column(name="description", columnDefinition="TEXT")
    private String description;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "project", cascade = CascadeType.ALL)
    @OrderBy("step_order")
    @JsonDeserialize(as=LinkedHashSet.class)
    private Set<Step> steps;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "project", cascade = CascadeType.ALL)
    @OrderBy("id")
    @JsonDeserialize(as=LinkedHashSet.class)
    private Set<Story> stories;
}
