package bg.fmi.payload.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UnreadSecretMessagesCountResponse {
    private Integer count;
}
