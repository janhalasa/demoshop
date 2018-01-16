package com.halasa.demoshop.app.security.jwt;

import com.halasa.demoshop.app.security.JwtAuthenticationToken;
import com.halasa.demoshop.app.security.Roles;
import com.halasa.demoshop.service.CustomerService;
import com.halasa.demoshop.service.domain.Customer;
import com.halasa.demoshop.service.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class JwtAuthSuccessHandler {

    private static final Pattern FULL_NAME_PATTERN = Pattern.compile("^([^ ]+)[ ]+(.*)$");

    private final CustomerRepository customerRepository;
    private final CustomerService customerService;

    @Autowired
    public JwtAuthSuccessHandler(
            CustomerRepository customerRepository,
            CustomerService customerService) {
        this.customerRepository = customerRepository;
        this.customerService = customerService;
    }

    /**
     * Creates a new Customer if the given token contains the CUSTOMER role.
     */
    public void handle(JwtAuthenticationToken authenticationToken) {
        if (! authenticationToken.getAuthorities().contains(Roles.CUSTOMER)) {
            return;
        }

        Optional<Customer> optionalCustomer = this.customerRepository.getByEmail(authenticationToken.getPrincipal().getEmail());
        if (! optionalCustomer.isPresent()) {
            Pair<String, String> names = this.splitNames(authenticationToken.getPrincipal().getName());

            final Customer customer = new Customer(
                    names.getFirst(),
                    names.getSecond(),
                    null,
                    authenticationToken.getPrincipal().getEmail(),
                    null);
            this.customerService.save(customer, Optional.empty());
        }
    }

    Pair<String, String> splitNames(String fullName) {
        final String trimmed = fullName.trim();
        final Matcher matcher = FULL_NAME_PATTERN.matcher(trimmed);
        if (! matcher.matches()) {
            return Pair.of(trimmed, trimmed);
        }
        return Pair.of(matcher.group(1), matcher.group(2));
    }
}
