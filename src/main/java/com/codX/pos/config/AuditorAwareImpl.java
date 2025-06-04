package com.codX.pos.config;

import com.codX.pos.context.UserContext;
import com.codX.pos.dto.UserContextDto;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component("auditorAwareImpl")
public class AuditorAwareImpl implements AuditorAware<UUID> {

    @Override
    public Optional<UUID> getCurrentAuditor() {
        return Optional.ofNullable(UserContext.getUserContext())
                .map(UserContextDto::userId);
    }
}

