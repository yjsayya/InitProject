package init.project.global.security.model;

import init.project.domain.users.model.UserAccount;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Builder
@ToString
public class UserDetailsImpl implements UserDetails {

    private Long userId;
    private String email;
    private String password;
    private String username;
    private String userRole;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.userRole));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override public boolean isAccountNonExpired() { return UserDetails.super.isAccountNonExpired(); }
    @Override public boolean isAccountNonLocked() { return UserDetails.super.isAccountNonLocked(); }
    @Override public boolean isCredentialsNonExpired() { return UserDetails.super.isCredentialsNonExpired(); }
    @Override public boolean isEnabled() { return UserDetails.super.isEnabled(); }

    public static UserDetailsImpl from(UserAccount dto) {
        return UserDetailsImpl.builder()
                .userId(dto.getUserId())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .username(dto.getUsername())
                .userRole(dto.getUserRole())
                .build();
    }

}