package com.xiaohongshu.controller;

import com.xiaohongshu.dto.LoginRequest;
import com.xiaohongshu.dto.Result;
import com.xiaohongshu.mapper.NoteMapper;
import com.xiaohongshu.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private NoteMapper noteMapper;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        try {
            Map<String, Object> data = userService.adminLogin(request);
            return Result.success(data);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard() {
        Map<String, Object> data = new HashMap<>();
        data.put("userCount", userService.countUsers());
        data.put("noteCount", noteMapper.selectCount(null));
        return Result.success(data);
    }
}
