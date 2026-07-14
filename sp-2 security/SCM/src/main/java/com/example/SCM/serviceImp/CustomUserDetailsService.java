package com.example.SCM.serviceImp;

import com.example.SCM.entity.User;
import com.example.SCM.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

       @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

           User user=userRepository.findByEmail(username)
                   .orElseThrow(()->new UsernameNotFoundException(
                           "User Not found with Email"+username
                   ));

           // Role stored as "MANAGER" → Spring Security needs "ROLE_MANAGER"

           String roleAuthority="ROLE_"+user.getRole().name();

           if (!user.isActive()){

               throw new DisabledException(
                       "User Account is inactive please contact Manager"
               );
           }



           return new org.springframework.security.core.userdetails.User(

                   user.getEmail(),
                   user.getPassword(),
                   List.of(new SimpleGrantedAuthority(roleAuthority))

           );
    }
}
