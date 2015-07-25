package com.acme.kanban.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class Story extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="title")
    private String title;

    @Column(name="description", columnDefinition="TEXT")
    private String description;

    @Column(name="points")
    private Integer points;

    @Column(name="number")
    private Integer number;

    @Column(name="priority")
    private Integer priority;

    @Column(name="bg_color")
    private String bgColor;

    @Column(name="fg_color")
    private String fgColor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="project_id")
    @JsonIgnoreProperties({"title", "description", "steps", "stories" })
    private Project project;

    @ManyToOne
    @JoinColumn(name="step_id")
    @JsonIgnoreProperties({"title", "project", "order" })
    private Step step;
}
