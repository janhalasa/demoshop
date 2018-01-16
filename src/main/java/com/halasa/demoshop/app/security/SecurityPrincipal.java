package com.halasa.demoshop.app.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.StringUtils;

import java.util.Collection;

public class SecurityPrincipal extends User {

    private final String name;

    public SecurityPrincipal(String email, String name, Collection<? extends GrantedAuthority> authorities) {
        super(email, "", true, true, true, true, authorities);
        if (! StringUtils.hasText(name)) {
            throw new IllegalArgumentException("User must have a name");
        }
        this.name = name.trim();
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return getUsername();
    }
}
