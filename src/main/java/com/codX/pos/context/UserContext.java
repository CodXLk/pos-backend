package com.codX.pos.context;

import com.codX.pos.dto.UserContextDto;
import org.springframework.stereotype.Component;

@Component
public class UserContext {
    private static final ThreadLocal<UserContextDto> userContextThreadLocal = new ThreadLocal<>();

    public static void setUserContext(UserContextDto userContext) {
        userContextThreadLocal.set(userContext);
    }

    public static UserContextDto getUserContext() {
        return userContextThreadLocal.get();
    }

    public static void clear() {
        userContextThreadLocal.remove();
    }
}
