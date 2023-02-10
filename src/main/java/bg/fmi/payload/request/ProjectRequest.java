package bg.fmi.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class ProjectRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String description;
}