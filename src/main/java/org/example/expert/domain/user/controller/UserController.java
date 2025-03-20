package org.example.expert.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.annotation.Auth;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.request.UserImageUrlRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.dto.response.UserWithImageResponse;
import org.example.expert.domain.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //유저 조회
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    /**
     * 유저 비밀번호 변경
     * @param authUser (@AuthenticationPrincipal를 통해 인증된 유저 정보 가져오기)
     */
    @PutMapping("/users/password")
    public void changePassword(@AuthenticationPrincipal AuthUser authUser, @RequestBody UserChangePasswordRequest userChangePasswordRequest) {
        userService.changePassword(authUser.getId(), userChangePasswordRequest);
    }

    /**
     * 유저 프로필 이미지 등록
     * @param authUser (@AuthenticationPrincipal를 통해 인증된 유저 정보 가져오기)ㅁ
     */
    @PatchMapping("/users/profile-image")
    public ResponseEntity<UserWithImageResponse> UserProfileImage(
        @AuthenticationPrincipal AuthUser authUser,
        @RequestBody UserImageUrlRequest userImageUrlRequest
    ) {
        return ResponseEntity.ok(userService.saveUserProfileImage(authUser.getId(), userImageUrlRequest));
    }
}
