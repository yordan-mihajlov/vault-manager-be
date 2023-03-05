package bg.fmi.payload.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class MarkUsersAsAdminsRequest {
    private List<String> usernames;
}
