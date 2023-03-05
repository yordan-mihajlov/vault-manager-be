package bg.fmi.models;

import bg.fmi.auditing.AuditableEntity;
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
@Table(name = "system_configurations",
        uniqueConstraints = {
                @UniqueConstraint(columnNames={"name"})
        })
public class SystemConfiguration extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @OneToMany(mappedBy="systemConfiguration", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Configuration> configurations;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "config_users",
            joinColumns = @JoinColumn(name = "config_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> users;

    @ManyToOne
    @JoinColumn(name="ownerId",referencedColumnName="id", nullable = false)
    private User owner;
}
