package bg.fmi.payload.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class ProjectResponse {
    private String name;

    private List<String> username;

    private Map<String, String> configurations;
}
