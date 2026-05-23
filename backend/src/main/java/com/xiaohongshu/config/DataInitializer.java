package com.xiaohongshu.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiaohongshu.entity.User;
import com.xiaohongshu.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, "admin");
        User existing = userMapper.selectOne(wrapper);

        if (existing == null) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setNickname("管理员");
            admin.setRole(1);
            userMapper.insert(admin);
        } else if (!passwordEncoder.matches("admin123", existing.getPassword())) {
            existing.setPassword(passwordEncoder.encode("admin123"));
            existing.setRole(1);
            userMapper.updateById(existing);
        }
    }
}
