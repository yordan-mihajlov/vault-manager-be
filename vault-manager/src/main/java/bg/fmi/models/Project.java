package bg.fmi.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "projects",
        uniqueConstraints = {
                @UniqueConstraint(columnNames={"name"})
        })
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @OneToMany(mappedBy="project", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Configuration> configurations;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "project_users",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> users;

    @ManyToOne
    @JoinColumn(name="ownerId",referencedColumnName="id", nullable = false)
    private User owner;
}
