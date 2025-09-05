package com.stamina_inc.academiq_auth.repository;

import com.stamina_inc.academiq_auth.domain.entities.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {

    Optional<Member> findByEmail(String email);

}

