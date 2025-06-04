package com.codX.pos.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public enum Role {
    SUPER_ADMIN(
            Set.of(
                    Permission.SUPER_ADMIN_READ,
                    Permission.SUPER_ADMIN_UPDATE,
                    Permission.SUPER_ADMIN_DELETE,
                    Permission.SUPER_ADMIN_CREATE,
                    Permission.COMPANY_ADMIN_CREATE,
                    Permission.COMPANY_ADMIN_READ,
                    Permission.COMPANY_ADMIN_UPDATE,
                    Permission.COMPANY_ADMIN_DELETE
            )
    ),
    COMPANY_ADMIN(
            Set.of(
                    Permission.COMPANY_ADMIN_READ,
                    Permission.COMPANY_ADMIN_UPDATE,
                    Permission.BRANCH_CREATE,
                    Permission.BRANCH_READ,
                    Permission.BRANCH_UPDATE,
                    Permission.BRANCH_DELETE,
                    Permission.BRANCH_ADMIN_CREATE,
                    Permission.BRANCH_ADMIN_READ,
                    Permission.BRANCH_ADMIN_UPDATE,
                    Permission.BRANCH_ADMIN_DELETE
            )
    ),
    BRANCH_ADMIN(
            Set.of(
                    Permission.BRANCH_ADMIN_READ,
                    Permission.BRANCH_ADMIN_UPDATE,
                    Permission.POS_USER_CREATE,
                    Permission.POS_USER_READ,
                    Permission.POS_USER_UPDATE,
                    Permission.POS_USER_DELETE,
                    Permission.EMPLOYEE_CREATE,
                    Permission.EMPLOYEE_READ,
                    Permission.EMPLOYEE_UPDATE,
                    Permission.EMPLOYEE_DELETE
            )
    ),
    POS_USER(
            Set.of(
                    Permission.POS_USER_READ,
                    Permission.POS_USER_UPDATE,
                    Permission.CUSTOMER_CREATE,
                    Permission.CUSTOMER_READ,
                    Permission.CUSTOMER_UPDATE,
                    Permission.CUSTOMER_DELETE
            )
    ),
    EMPLOYEE(
            Set.of(
                    Permission.EMPLOYEE_READ,
                    Permission.EMPLOYEE_UPDATE
            )
    ),
    CUSTOMER(
            Set.of(
                    Permission.CUSTOMER_READ
            )
    );

    @Getter
    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
