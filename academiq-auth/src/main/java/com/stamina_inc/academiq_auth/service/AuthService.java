package com.stamina_inc.academiq_auth.service;

import com.stamina_inc.academiq_auth.domain.dto.MemberDTO;
import com.stamina_inc.academiq_auth.domain.dto.MemberPayload;
import com.stamina_inc.academiq_auth.domain.entities.Member;
import com.stamina_inc.academiq_auth.mapper.MemberMapper;
import com.stamina_inc.academiq_auth.messaging.MemberProducer;
import com.stamina_inc.academiq_auth.repository.MemberRepository;
import com.stamina_inc.academiq_auth.security.CustomUserDetails;
import com.stamina_inc.academiq_auth.utils.JwtUtils;
import com.stamina_inc.academiq_auth.utils.PasswordUtils;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final MemberRepository memberRepository;

    private final MemberMapper memberMapper;

    private final JwtUtils jwtUtils;

    private final MemberProducer memberProducer;

    public AuthService(
            MemberRepository memberRepository,
            MemberMapper memberMapper,
            JwtUtils jwtUtils,
            MemberProducer memberProducer
    ) {
        this.memberRepository = memberRepository;
        this.memberMapper = memberMapper;
        this.jwtUtils = jwtUtils;
        this.memberProducer = memberProducer;
    }

    /**
     * Register a new student with the given email. Send him a temporary password.
     *
     * @param email used for registration of the student (university email).
     * @return true if the student was registered, false if the email is already in use.
     */
    public MemberDTO register(String email, String password) throws IllegalArgumentException {
        Optional<Member> member = memberRepository.findByEmail(email);

        if (member.isPresent()) {
            throw new IllegalArgumentException("Email is already in use");
        }

        Member toRegister = new Member();
        toRegister.setEmail(email);
        toRegister.setUsername(email);
        toRegister.setPassword(PasswordUtils.hashPassword(password));
        toRegister.setActive(false);

        Member savedMember = memberRepository.save(toRegister);

        memberProducer.publicRegisteredMember(new MemberPayload(email, savedMember.getUsername()));

        return memberMapper.toDTO(savedMember);
    }

    /**
     * Extract the member from the given JWT token
     *
     * @param token JWT token
     * @return CustomUserDetails of the member
     * @throws IllegalArgumentException if the member is not found
     */
    public CustomUserDetails getMemberFromToken(String token) throws IllegalArgumentException {
        String plainToken = jwtUtils.extractToken(token);
        Claims claims = jwtUtils.getClaimsFromToken(plainToken);
        Optional<Member> member = memberRepository.findByEmail(claims.getSubject());

        if (member.isEmpty()) {
            throw new IllegalArgumentException("Member not found");
        }

        return new CustomUserDetails(member.get());
    }

    /**
     * Login a member with the given email and password. Returns a JWT token if successful
     *
     * @param email    of the member
     * @param password of the member
     * @return JWT token
     * @throws IllegalArgumentException
     */
    public String login(String email, String password) throws IllegalArgumentException {
        Optional<Member> member = memberRepository.findByEmail(email);

        if (member.isEmpty()) {
            throw new IllegalArgumentException("Member not found");
        }

        Member currentMember = member.get();

        if (!PasswordUtils.verifyPassword(password, currentMember.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        return jwtUtils.generateToken(new CustomUserDetails(currentMember));
    }

}
