package com.xiaohongshu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaohongshu.dto.CommentRequest;
import com.xiaohongshu.dto.NoteRequest;
import com.xiaohongshu.entity.*;
import com.xiaohongshu.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NoteService {

    @Autowired
    private NoteMapper noteMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private LikeMapper likeMapper;
    @Autowired
    private CollectMapper collectMapper;
    @Autowired
    private CommentMapper commentMapper;

    public Page<Map<String, Object>> getNoteList(int page, int size) {
        Page<Note> notePage = new Page<>(page, size);
        LambdaQueryWrapper<Note> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Note::getCreatedAt);
        noteMapper.selectPage(notePage, wrapper);

        Page<Map<String, Object>> resultPage = new Page<>(page, size, notePage.getTotal());
        List<Map<String, Object>> records = notePage.getRecords().stream().map(note -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", note.getId());
            map.put("title", note.getTitle());
            map.put("coverImage", note.getCoverImage());
            map.put("likeCount", note.getLikeCount());
            map.put("collectCount", note.getCollectCount());
            map.put("createdAt", note.getCreatedAt());
            User author = userMapper.selectById(note.getUserId());
            if (author != null) {
                map.put("authorName", author.getNickname());
                map.put("authorAvatar", author.getAvatar());
            }
            return map;
        }).collect(Collectors.toList());
        resultPage.setRecords(records);
        return resultPage;
    }

    public Map<String, Object> getNoteDetail(Long noteId, Long currentUserId) {
        Note note = noteMapper.selectById(noteId);
        if (note == null) {
            throw new RuntimeException("笔记不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", note.getId());
        result.put("title", note.getTitle());
        result.put("content", note.getContent());
        result.put("coverImage", note.getCoverImage());
        result.put("likeCount", note.getLikeCount());
        result.put("collectCount", note.getCollectCount());
        result.put("commentCount", note.getCommentCount());
        result.put("createdAt", note.getCreatedAt());

        User author = userMapper.selectById(note.getUserId());
        if (author != null) {
            result.put("authorId", author.getId());
            result.put("authorName", author.getNickname());
            result.put("authorAvatar", author.getAvatar());
        }

        if (currentUserId != null) {
            LambdaQueryWrapper<Like> likeWrapper = new LambdaQueryWrapper<>();
            likeWrapper.eq(Like::getUserId, currentUserId).eq(Like::getNoteId, noteId);
            result.put("liked", likeMapper.selectCount(likeWrapper) > 0);

            LambdaQueryWrapper<Collect> collectWrapper = new LambdaQueryWrapper<>();
            collectWrapper.eq(Collect::getUserId, currentUserId).eq(Collect::getNoteId, noteId);
            result.put("collected", collectMapper.selectCount(collectWrapper) > 0);
        } else {
            result.put("liked", false);
            result.put("collected", false);
        }

        return result;
    }

    public Note createNote(Long userId, NoteRequest request) {
        Note note = new Note();
        note.setUserId(userId);
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        note.setCoverImage(request.getCoverImage());
        note.setLikeCount(0);
        note.setCollectCount(0);
        note.setCommentCount(0);
        noteMapper.insert(note);
        return note;
    }

    @Transactional
    public void likeNote(Long userId, Long noteId) {
        LambdaQueryWrapper<Like> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Like::getUserId, userId).eq(Like::getNoteId, noteId);

        if (likeMapper.selectCount(wrapper) > 0) {
            likeMapper.delete(wrapper);
            noteMapper.selectById(noteId);
            Note note = noteMapper.selectById(noteId);
            note.setLikeCount(Math.max(0, note.getLikeCount() - 1));
            noteMapper.updateById(note);
        } else {
            Like like = new Like();
            like.setUserId(userId);
            like.setNoteId(noteId);
            likeMapper.insert(like);
            Note note = noteMapper.selectById(noteId);
            note.setLikeCount(note.getLikeCount() + 1);
            noteMapper.updateById(note);
        }
    }

    @Transactional
    public void collectNote(Long userId, Long noteId) {
        LambdaQueryWrapper<Collect> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Collect::getUserId, userId).eq(Collect::getNoteId, noteId);

        if (collectMapper.selectCount(wrapper) > 0) {
            collectMapper.delete(wrapper);
            Note note = noteMapper.selectById(noteId);
            note.setCollectCount(Math.max(0, note.getCollectCount() - 1));
            noteMapper.updateById(note);
        } else {
            Collect collect = new Collect();
            collect.setUserId(userId);
            collect.setNoteId(noteId);
            collectMapper.insert(collect);
            Note note = noteMapper.selectById(noteId);
            note.setCollectCount(note.getCollectCount() + 1);
            noteMapper.updateById(note);
        }
    }

    @Transactional
    public Comment addComment(Long userId, Long noteId, CommentRequest request) {
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setNoteId(noteId);
        comment.setContent(request.getContent());
        comment.setParentId(request.getParentId());
        commentMapper.insert(comment);

        Note note = noteMapper.selectById(noteId);
        note.setCommentCount(note.getCommentCount() + 1);
        noteMapper.updateById(note);
        return comment;
    }

    public List<Map<String, Object>> getComments(Long noteId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getNoteId, noteId).orderByDesc(Comment::getCreatedAt);
        List<Comment> comments = commentMapper.selectList(wrapper);

        return comments.stream().map(comment -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", comment.getId());
            map.put("content", comment.getContent());
            map.put("parentId", comment.getParentId());
            map.put("createdAt", comment.getCreatedAt());
            User user = userMapper.selectById(comment.getUserId());
            if (user != null) {
                map.put("username", user.getNickname());
                map.put("avatar", user.getAvatar());
            }
            return map;
        }).collect(Collectors.toList());
    }
}
