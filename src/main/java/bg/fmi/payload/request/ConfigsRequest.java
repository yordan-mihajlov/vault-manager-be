package bg.fmi.payload.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Map;

@Data
public class ConfigsRequest {
    @NotBlank
    private String projectName;
    private Map<String, String> configs;
}
