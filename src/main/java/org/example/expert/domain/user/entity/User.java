package org.example.expert.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.entity.Timestamped;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User extends Timestamped {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //User nickname 추가
    private String nickname;

    //User imageUrl
    private String imageUrl;

    @Column(unique = true)
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    public User(String nickname, String email, String password, UserRole userRole) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.userRole = userRole;
    }

    private User(Long id, String email, UserRole userRole) {
        this.id = id;
        this.email = email;
        this.userRole = userRole;
    }

    public static User fromAuthUser(AuthUser authUser) {
        //GrantedAuthority타입의 유저 권한 변환
        GrantedAuthority authorities = authUser.getAuthorities().iterator().next();
        return new User(authUser.getId(), authUser.getEmail(), UserRole.of(authorities.getAuthority()));
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public void updateRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public void updateImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
