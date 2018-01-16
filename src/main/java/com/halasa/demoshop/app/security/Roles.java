package com.halasa.demoshop.app.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public interface Roles {
    String CUSTOMER = "ROLE_CUSTOMER";
    String ADMIN = "ROLE_ADMIN";
    GrantedAuthority ADMIN_GA = new SimpleGrantedAuthority(ADMIN);
    String TOKEN = "ROLE_TOKEN";

    String IS_ADMIN = "hasRole('ADMIN')";
}
