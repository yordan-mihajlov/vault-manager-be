package bg.fmi.payload.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SecretMessageResponse {
    private String header;
    private String content;
    private boolean isActive;
}
