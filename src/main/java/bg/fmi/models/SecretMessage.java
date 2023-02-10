package bg.fmi.models;


import bg.fmi.auditing.AuditableEntity;
import bg.fmi.security.AttributeEncryptor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "secret_messages")
public class SecretMessage extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String uuid;

    @NotBlank
    private String header;

    @NotBlank
    @Convert(converter = AttributeEncryptor.class)
    private String content;

    @NotNull
    private LocalDateTime initDate;

    @NotNull
    @Range(min = 1)
    private Integer expireDays;

    @NotNull
    private Boolean isOneTime;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "secret_massage_read_by_users",
            joinColumns = @JoinColumn(name = "secret_massage_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> readBy;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "secret_massage_users",
            joinColumns = @JoinColumn(name = "secret_massage_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> toUsers;

    @NotNull
    private Boolean isPublic;
}
