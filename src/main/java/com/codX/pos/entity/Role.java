package com.codX.pos.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
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
                    Permission.SUPER_ADMIN_CREATE
            )
    ),
    COMPANY_ADMIN(
            Set.of(
                    Permission.COMPANY_ADMIN_READ,
                    Permission.COMPANY_ADMIN_UPDATE,
                    Permission.COMPANY_ADMIN_DELETE,
                    Permission.COMPANY_ADMIN_CREATE
            )
    ),
    PROPERTY_ADMIN(
            Set.of(
                    Permission.PROPERTY_ADMIN_READ,
                    Permission.PROPERTY_ADMIN_UPDATE,
                    Permission.PROPERTY_ADMIN_DELETE,
                    Permission.PROPERTY_ADMIN_CREATE
            )
    ),
    POS_USER(
            Set.of(
                    Permission.POS_USER_READ,
                    Permission.POS_USER_UPDATE,
                    Permission.POS_USER_CREATE,
                    Permission.POS_USER_DELETE
            )
    ),
    EMPLOYEE(
            Set.of(
                    Permission.EMPLOYEE_READ,
                    Permission.EMPLOYEE_UPDATE,
                    Permission.EMPLOYEE_CREATE,
                    Permission.EMPLOYEE_DELETE
            )
    ),
    CUSTOMER(
            Set.of(
                    Permission.CUSTOMER_READ,
                    Permission.CUSTOMER_UPDATE,
                    Permission.CUSTOMER_CREATE,
                    Permission.CUSTOMER_DELETE
            )
    )
    ;

    @Getter
    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities(){
        var authorities= getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());

        authorities.add(new SimpleGrantedAuthority("ROLE_"+ this.name()));
        return authorities;
    }
}