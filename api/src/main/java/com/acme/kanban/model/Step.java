package com.acme.kanban.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "steps")
@NamedQueries({
        @NamedQuery(
                name = "Step.findAllByProject",
                query = "SELECT t FROM Step t WHERE t.project.id = :project_id"
        )
})
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "title"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Step extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "step_order")
    private Integer order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    @JsonIgnore
    private Project project;
}
