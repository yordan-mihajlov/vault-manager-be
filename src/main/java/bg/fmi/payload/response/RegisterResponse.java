package bg.fmi.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponse {
	private Long id;
	private String username;
	private String firstname;
	private String lastname;
	private String email;
	private List<String> roles;
}
