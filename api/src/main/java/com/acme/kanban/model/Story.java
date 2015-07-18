package com.acme.kanban.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "stories")
@NamedQueries({
        @NamedQuery(
                name = "Story.findAllByProject",
                query = "SELECT t FROM Story t WHERE t.project.id = :project_id"
        )
})
@Getter
@Setter
@EqualsAndHashCode(of={"id", "title", "description"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Story {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="title")
    private String title;

    @Column(name="description", columnDefinition="TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="project_id")
    @JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
    @JsonIdentityReference(alwaysAsId=true)
    private Project project;

    @ManyToOne
    @JoinColumn(name="step_id")
    @JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
    @JsonIdentityReference(alwaysAsId=true)
    private KanbanStep step;
}
