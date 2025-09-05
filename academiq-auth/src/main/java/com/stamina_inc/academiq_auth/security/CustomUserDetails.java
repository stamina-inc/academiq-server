package com.stamina_inc.academiq_auth.security;

import com.stamina_inc.academiq_auth.domain.entities.Member;
import lombok.Getter;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails, CredentialsContainer {

    @Getter
    private final List<GrantedAuthority> authorities;
    @Getter
    private String id;
    @Getter
    private String email;
    @Getter
    private String username;
    @Getter
    private String password;

    public CustomUserDetails(Member member) {
        this.username = member.getUsername();
        this.password = member.getPassword();
        this.id = member.getId();

        if (member.getRoles() == null) {
            this.authorities = List.of();
            return;
        }

        this.authorities = member.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public void eraseCredentials() {
        password = null;
    }

}
