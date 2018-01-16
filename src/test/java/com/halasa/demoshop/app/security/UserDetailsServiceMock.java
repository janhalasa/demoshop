package com.halasa.demoshop.app.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserDetailsServiceMock implements UserDetailsService {

    public static final String ADMIN = "admin";
    public static final String CUSTOMER = "customer";

    private final Map<String, SecurityPrincipal> users = new HashMap<>();

    public UserDetailsServiceMock() {
        users.put(ADMIN, new SecurityPrincipal(
                "pedro@ved.ro",
                "Pedro Vedro",
                Arrays.asList(new SimpleGrantedAuthority(Roles.ADMIN))));

        users.put(CUSTOMER, new SecurityPrincipal(
                "customer@ved.ro",
                "Customer Vedro",
                Arrays.asList(new SimpleGrantedAuthority(Roles.CUSTOMER))));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final SecurityPrincipal user = this.users.get(username);
        if (user == null) {
            throw new UsernameNotFoundException("No such user: " + username);
        }
        return user;
    }
}
