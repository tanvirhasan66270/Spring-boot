package com.example.SCM.security;

import com.example.SCM.entity.Customer;
import com.example.SCM.repository.CustomerRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component("customerSecurity")
@RequiredArgsConstructor
public class CustomerSecurity {

    private final CustomerRepository customerRepository;

    public boolean isSelf(Long requestedId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || requestedId == null) {
            return false;
        }

        String loginIdentifier = authentication.getName();

        return customerRepository.findById(requestedId)
                .map(customer -> matches(customer, loginIdentifier))
                .orElse(false);
    }

    private boolean matches(Customer customer, String loginIdentifier) {
        // TODO: same decision as DriverSecurity — pick the field that actually
        // matches your login credential. Uncomment the correct one:

        // return loginIdentifier.equalsIgnoreCase(customer.getEmail());
        // return loginIdentifier.equals(customer.getPhone());
        // return customer.getUser() != null
        //         && loginIdentifier.equalsIgnoreCase(customer.getUser().getUsername());

        return false; // safe default until wired up — denies self-access rather than guessing wrong
    }
}