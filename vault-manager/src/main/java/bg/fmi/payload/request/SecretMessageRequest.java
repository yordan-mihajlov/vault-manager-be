package bg.fmi.payload.request;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
public class SecretMessageRequest {
    @NotBlank
    private String header;
    @NotBlank
    private String content;

    @NotNull
    @Range(min = 1)
    private Integer expireDays;

    @NotNull
    private Boolean isOneTime;

    private List<String> toUsers;
}
