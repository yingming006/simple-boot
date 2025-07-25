package com.example.simple.framework.security.principal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class SecurityPrincipal implements UserDetails, Serializable {
    private Long id;
    private String username;

    @JsonIgnore
    private String password;

    // 存储从数据库查询出的权限标识符，如 'user:create', 'ROLE_ADMIN'
    private Collection<String> permissions;

    public SecurityPrincipal(Long id, String username, String password, Collection<String> permissions) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.permissions = permissions;
    }

    @Override
    @JsonIgnore // 在序列化LoginUser对象时，忽略 authorities 字段
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (CollectionUtils.isEmpty(this.permissions)) {
            return Collections.emptyList();
        }
        return this.permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}