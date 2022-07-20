package bg.fmi.payload.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ProjectRequest {
    @NotBlank
    private String name;
}
