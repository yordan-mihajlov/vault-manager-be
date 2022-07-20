package bg.fmi;

import bg.fmi.vaultmanagerclient.annotation.EnableVaultManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableVaultManager
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
