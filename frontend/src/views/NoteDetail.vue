<template>
  <div class="note-detail" v-if="note">
    <el-card class="detail-card">
      <div class="detail-header">
        <div class="author-info">
          <el-avatar :size="40">{{ note.authorName?.charAt(0) }}</el-avatar>
          <span class="author-name">{{ note.authorName }}</span>
        </div>
        <span class="date">{{ formatDate(note.createdAt) }}</span>
      </div>

      <h1 class="detail-title">{{ note.title }}</h1>

      <div v-if="note.coverImage" class="detail-cover">
        <img :src="note.coverImage" alt="" />
      </div>

      <div class="detail-content">{{ note.content }}</div>

      <div class="detail-actions">
        <el-button :type="note.liked ? 'danger' : 'default'" @click="handleLike">
          <el-icon><Star /></el-icon>
          点赞 {{ note.likeCount }}
        </el-button>
        <el-button :type="note.collected ? 'warning' : 'default'" @click="handleCollect">
          <el-icon><Collection /></el-icon>
          收藏 {{ note.collectCount }}
        </el-button>
      </div>
    </el-card>

    <el-card class="comment-card">
      <h3>评论 ({{ note.commentCount }})</h3>
      <div class="comment-input" v-if="isLoggedIn">
        <el-input v-model="commentText" type="textarea" :rows="3" placeholder="写评论..." />
        <el-button type="primary" @click="submitComment" :disabled="!commentText.trim()">发表</el-button>
      </div>
      <div class="comment-list">
        <div class="comment-item" v-for="c in comments" :key="c.id">
          <el-avatar :size="32">{{ c.username?.charAt(0) }}</el-avatar>
          <div class="comment-body">
            <span class="comment-author">{{ c.username }}</span>
            <p class="comment-text">{{ c.content }}</p>
            <span class="comment-time">{{ formatDate(c.createdAt) }}</span>
          </div>
        </div>
        <el-empty v-if="comments.length === 0" description="暂无评论" :image-size="60" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const route = useRoute()
const note = ref(null)
const comments = ref([])
const commentText = ref('')
const isLoggedIn = computed(() => !!localStorage.getItem('token'))

async function fetchNote() {
  const res = await request.get(`/notes/${route.params.id}`)
  note.value = res.data
}

async function fetchComments() {
  const res = await request.get(`/notes/${route.params.id}/comments`)
  comments.value = res.data || []
}

async function handleLike() {
  if (!isLoggedIn.value) { ElMessage.warning('请先登录'); return }
  await request.post(`/notes/${route.params.id}/like`)
  await fetchNote()
}

async function handleCollect() {
  if (!isLoggedIn.value) { ElMessage.warning('请先登录'); return }
  await request.post(`/notes/${route.params.id}/collect`)
  await fetchNote()
}

async function submitComment() {
  if (!commentText.value.trim()) return
  await request.post(`/notes/${route.params.id}/comments`, { content: commentText.value })
  commentText.value = ''
  ElMessage.success('评论成功')
  await fetchNote()
  await fetchComments()
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString('zh-CN')
}

onMounted(() => {
  fetchNote()
  fetchComments()
})
</script>

<style scoped>
.detail-card {
  margin-bottom: 20px;
}

.detail-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.author-name {
  font-weight: 500;
  font-size: 15px;
}

.date {
  color: #999;
  font-size: 13px;
}

.detail-title {
  font-size: 22px;
  margin-bottom: 16px;
  color: #333;
}

.detail-cover {
  margin-bottom: 16px;
  border-radius: 8px;
  overflow: hidden;
}

.detail-cover img {
  width: 100%;
  max-height: 400px;
  object-fit: cover;
}

.detail-content {
  font-size: 15px;
  line-height: 1.8;
  color: #333;
  white-space: pre-wrap;
  margin-bottom: 20px;
}

.detail-actions {
  display: flex;
  gap: 12px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.comment-card h3 {
  margin-bottom: 16px;
}

.comment-input {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 20px;
}

.comment-input .el-button {
  align-self: flex-end;
}

.comment-item {
  display: flex;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
}

.comment-body {
  flex: 1;
}

.comment-author {
  font-weight: 500;
  font-size: 13px;
  color: #333;
}

.comment-text {
  margin: 4px 0;
  font-size: 14px;
  color: #555;
}

.comment-time {
  font-size: 12px;
  color: #999;
}
</style>
