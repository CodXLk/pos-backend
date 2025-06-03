package com.codX.pos.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {
    SUPER_ADMIN_READ("superAdmin:read"),
    SUPER_ADMIN_UPDATE("superAdmin:update"),
    SUPER_ADMIN_CREATE("superAdmin:create"),
    SUPER_ADMIN_DELETE("superAdmin:delete"),

    COMPANY_ADMIN_READ("companyAdmin:read"),
    COMPANY_ADMIN_UPDATE("companyAdmin:update"),
    COMPANY_ADMIN_CREATE("companyAdmin:create"),
    COMPANY_ADMIN_DELETE("companyAdmin:delete"),

    BRANCH_READ("branch:read"),
    BRANCH_UPDATE("branch:update"),
    BRANCH_CREATE("branch:create"),
    BRANCH_DELETE("branch:delete"),

    BRANCH_ADMIN_READ("branchAdmin:read"),
    BRANCH_ADMIN_UPDATE("branchAdmin:update"),
    BRANCH_ADMIN_CREATE("branchAdmin:create"),
    BRANCH_ADMIN_DELETE("branchAdmin:delete"),

    POS_USER_READ("posUser:read"),
    POS_USER_UPDATE("posUser:update"),
    POS_USER_CREATE("posUser:create"),
    POS_USER_DELETE("posUser:delete"),

    EMPLOYEE_READ("employee:read"),
    EMPLOYEE_UPDATE("employee:update"),
    EMPLOYEE_CREATE("employee:create"),
    EMPLOYEE_DELETE("employee:delete"),

    CUSTOMER_READ("customer:read"),
    CUSTOMER_UPDATE("customer:update"),
    CUSTOMER_CREATE("customer:create"),
    CUSTOMER_DELETE("customer:delete");

    @Getter
    private final String permission;
}
