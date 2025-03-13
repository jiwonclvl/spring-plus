package org.example.expert.domain.common.dto;

import lombok.Getter;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

@Getter
public class AuthUser {

    private final Long id;
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities; //여러 권한을 가질 수 있기 때문에 컬랙션으로 선언

    public AuthUser(Long id, String email, UserRole userRole) {
        this.id = id;
        this.email = email;
        this.authorities = List.of(new SimpleGrantedAuthority(userRole.name())); //인증된 유저의 권환을 Security에서 사용할 수 있는 권한 객체로 변환
    }

}
