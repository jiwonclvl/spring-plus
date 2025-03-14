package org.example.expert.domain.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SigninResponse;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.auth.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 회원 인증 처리
 *
 * Security 설정에서 "/auth/**" 경로는 인증 없이 접근할 수 있도록 허용
 */
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    //회원가입
    @PostMapping("/auth/signup")
    public SignupResponse signup(@Valid @RequestBody SignupRequest signupRequest) {
        return authService.signup(signupRequest);
    }

    //로그인
    @PostMapping("/auth/signin")
    public SigninResponse signin(@Valid @RequestBody SigninRequest signinRequest) {
        return authService.signin(signinRequest);
    }
}
