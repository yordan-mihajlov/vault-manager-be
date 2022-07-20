package bg.fmi.payload.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class UsersRequest {
    @NotBlank
    private String projectName;
    private List<String> userNames;
}
