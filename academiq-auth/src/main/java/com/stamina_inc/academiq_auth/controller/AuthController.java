package com.stamina_inc.academiq_auth.controller;

import com.stamina_inc.academiq_auth.domain.dto.MemberDTO;
import com.stamina_inc.academiq_auth.domain.dto.RegisterQuery;
import com.stamina_inc.academiq_auth.domain.resources.MemberResource;
import com.stamina_inc.academiq_auth.service.AuthService;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody RegisterQuery query) {
        try {
            MemberDTO memberDTO = authService.register(
                    query.email(),
                    query.password()
            );

            EntityModel<MemberResource> model = EntityModel.of(
                    new MemberResource(
                            memberDTO.id(),
                            memberDTO.username(),
                            memberDTO.email()
                    ),
                    Link.of("/register").withSelfRel()
            );

            return ResponseEntity
                    .created(URI.create("/members/" + memberDTO.id()))
                    .body(model);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
