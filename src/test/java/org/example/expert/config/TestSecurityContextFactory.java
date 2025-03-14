package org.example.expert.config;

import org.example.expert.domain.common.dto.AuthUser;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

/**
 * 테스트 환경에서 SecurityContext를 설정하는 클래스
 *  WithMockAuthUser 어노테이션을 기반으로 가짜 인증 정보를 생성한다.
 */
public class TestSecurityContextFactory implements WithSecurityContextFactory<WithMockAuthUser> {

    /**
     * WithMockAuthUser 어노테이션을 통해 받은 유저 정보를 바탕으로 SecurityContext 생성
     *
     * @param customUser WithMockAuthUser를 통해 받아온 유저 정보(userId, email, role)
     * @return SecurityContext 가짜 유저 인증 정보를 담은 context
     */
    @Override
    public SecurityContext createSecurityContext(WithMockAuthUser customUser) {
        //비어있는 새로운 SecurityContext 객체 생성
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        //유저 객체 생성
        AuthUser authUser = new AuthUser(customUser.userId(), customUser.email(), customUser.role());
        JwtAuthenticationToken authentication = new JwtAuthenticationToken(authUser);

        //SecurityContext에 유저 인증 정보 저장
        context.setAuthentication(authentication);
        return context;
    }
}
