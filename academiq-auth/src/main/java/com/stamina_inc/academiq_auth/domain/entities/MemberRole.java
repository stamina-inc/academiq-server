package com.stamina_inc.academiq_auth.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

@Entity
public class MemberRole {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Getter
    private String name;

    @ManyToMany
    private List<Member> members;

}
