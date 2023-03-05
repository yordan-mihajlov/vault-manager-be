package bg.fmi.payload.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class ConfigResponse {
    private String name;

    private String description;

    private List<String> usernames;

    private List<String> systemnames;

    private Map<String, String> configurations;
}
