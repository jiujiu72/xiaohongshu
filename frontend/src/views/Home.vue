<template>
  <div class="home">
    <div class="note-grid">
      <div class="note-card" v-for="note in notes" :key="note.id" @click="goDetail(note.id)">
        <div class="note-cover">
          <img v-if="note.coverImage" :src="note.coverImage" alt="" />
          <div v-else class="cover-placeholder">{{ note.title.charAt(0) }}</div>
        </div>
        <div class="note-info">
          <h3 class="note-title">{{ note.title }}</h3>
          <div class="note-meta">
            <span class="author">{{ note.authorName || '匿名' }}</span>
            <span class="like-count">
              <el-icon><Star /></el-icon>
              {{ note.likeCount }}
            </span>
          </div>
        </div>
      </div>
    </div>
    <div class="load-more" v-if="hasMore">
      <el-button @click="loadMore" :loading="loading">加载更多</el-button>
    </div>
    <el-empty v-if="!loading && notes.length === 0" description="暂无笔记" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '../utils/request'

const router = useRouter()
const notes = ref([])
const page = ref(1)
const hasMore = ref(true)
const loading = ref(false)

async function fetchNotes() {
  loading.value = true
  try {
    const res = await request.get('/notes', { params: { page: page.value, size: 12 } })
    const records = res.data.records || []
    notes.value.push(...records)
    hasMore.value = notes.value.length < res.data.total
  } finally {
    loading.value = false
  }
}

function loadMore() {
  page.value++
  fetchNotes()
}

function goDetail(id) {
  router.push(`/note/${id}`)
}

onMounted(() => fetchNotes())
</script>

<style scoped>
.note-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 20px;
}

.note-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.note-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.note-cover {
  width: 100%;
  height: 200px;
  overflow: hidden;
}

.note-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #ff2442, #ff6b81);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48px;
  color: #fff;
  font-weight: bold;
}

.note-info {
  padding: 12px;
}

.note-title {
  font-size: 14px;
  color: #333;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.note-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #999;
}

.like-count {
  display: flex;
  align-items: center;
  gap: 4px;
}

.load-more {
  text-align: center;
  margin-top: 30px;
}
</style>
