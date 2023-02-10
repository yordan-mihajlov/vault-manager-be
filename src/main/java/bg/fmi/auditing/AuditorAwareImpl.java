package bg.fmi.auditing;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware {

    @Override
    public Optional getCurrentAuditor() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();

        return authentication != null && !"anonymousUser".equals(authentication.getPrincipal())
                ? Optional.of(((UserDetails) authentication.getPrincipal()).getUsername()) : Optional.of("Anonymous");
    }
}