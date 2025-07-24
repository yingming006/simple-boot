package com.example.simple.modules.auth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.simple.interceptor.LoginUser;
import com.example.simple.modules.user.entity.UserEntity;
import com.example.simple.modules.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEntity::getUsername, username);
        UserEntity user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            throw new UsernameNotFoundException("用户 '" + username + "' 不存在");
        }

        return new LoginUser(user.getId(), user.getUsername(), user.getPassword(), user.getRole());
    }
}