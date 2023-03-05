package bg.fmi.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@Builder
public class SystemConfigurationRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String description;
}