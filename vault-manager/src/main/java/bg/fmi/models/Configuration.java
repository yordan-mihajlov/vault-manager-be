package bg.fmi.models;

import bg.fmi.security.AttributeEncryptor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "configurations")
public class Configuration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @Convert(converter = AttributeEncryptor.class)
    @NotBlank
    private String value;

    @ManyToOne
    @JoinColumn(name="projectId", referencedColumnName="id", nullable = false)
    private Project project;
}
