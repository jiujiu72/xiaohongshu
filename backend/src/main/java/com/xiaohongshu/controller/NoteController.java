package com.xiaohongshu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaohongshu.dto.CommentRequest;
import com.xiaohongshu.dto.NoteRequest;
import com.xiaohongshu.dto.Result;
import com.xiaohongshu.entity.Comment;
import com.xiaohongshu.entity.Note;
import com.xiaohongshu.service.NoteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @GetMapping
    public Result<Page<Map<String, Object>>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(noteService.getNoteList(page, size));
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        try {
            return Result.success(noteService.getNoteDetail(id, userId));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping
    public Result<Note> create(@Valid @RequestBody NoteRequest noteRequest, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Note note = noteService.createNote(userId, noteRequest);
        return Result.success(note);
    }

    @PostMapping("/{id}/like")
    public Result<?> like(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        noteService.likeNote(userId, id);
        return Result.success();
    }

    @PostMapping("/{id}/collect")
    public Result<?> collect(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        noteService.collectNote(userId, id);
        return Result.success();
    }

    @PostMapping("/{id}/comments")
    public Result<Comment> addComment(@PathVariable Long id,
                                      @Valid @RequestBody CommentRequest commentRequest,
                                      HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Comment comment = noteService.addComment(userId, id, commentRequest);
        return Result.success(comment);
    }

    @GetMapping("/{id}/comments")
    public Result<List<Map<String, Object>>> getComments(@PathVariable Long id) {
        return Result.success(noteService.getComments(id));
    }
}
