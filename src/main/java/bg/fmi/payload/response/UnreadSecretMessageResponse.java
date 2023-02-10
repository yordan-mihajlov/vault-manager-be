package bg.fmi.payload.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UnreadSecretMessageResponse {
    private String header;
    private String uuid;
    private Boolean isOneTime;
    private LocalDateTime expireDate;
}
