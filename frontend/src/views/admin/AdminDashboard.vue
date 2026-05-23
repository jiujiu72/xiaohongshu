<template>
  <div class="admin-dashboard">
    <el-card class="admin-header-card">
      <div class="admin-header">
        <h2>管理后台</h2>
        <div class="admin-actions">
          <span class="admin-name">{{ nickname }}</span>
          <el-button type="danger" @click="logout">退出登录</el-button>
        </div>
      </div>
    </el-card>

    <div class="stats-grid">
      <el-card class="stat-card">
        <div class="stat-icon user-icon">
          <el-icon size="32"><User /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.userCount }}</div>
          <div class="stat-label">注册用户数</div>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-icon note-icon">
          <el-icon size="32"><Document /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.noteCount }}</div>
          <div class="stat-label">笔记总数</div>
        </div>
      </el-card>
    </div>

    <el-card class="welcome-card">
      <h3>欢迎回来，{{ nickname }}</h3>
      <p>这里是小红书管理后台，您可以在此管理平台的用户和内容。</p>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import request from '../../utils/request'

const router = useRouter()
const nickname = computed(() => localStorage.getItem('nickname') || '管理员')
const stats = reactive({ userCount: 0, noteCount: 0 })

async function fetchDashboard() {
  try {
    const res = await request.get('/admin/dashboard')
    stats.userCount = res.data.userCount
    stats.noteCount = res.data.noteCount
  } catch (e) {
    // ignored
  }
}

function logout() {
  localStorage.removeItem('token')
  localStorage.removeItem('userId')
  localStorage.removeItem('nickname')
  localStorage.removeItem('role')
  router.push('/admin/login')
}

onMounted(() => fetchDashboard())
</script>

<style scoped>
.admin-dashboard {
  max-width: 900px;
  margin: 0 auto;
}

.admin-header-card {
  margin-bottom: 20px;
}

.admin-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.admin-header h2 {
  color: #409eff;
  margin: 0;
}

.admin-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.admin-name {
  color: #666;
  font-size: 14px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
}

.stat-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
  gap: 16px;
  width: 100%;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.user-icon {
  background: linear-gradient(135deg, #409eff, #66b1ff);
}

.note-icon {
  background: linear-gradient(135deg, #67c23a, #95d475);
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #999;
  margin-top: 4px;
}

.welcome-card h3 {
  margin-bottom: 12px;
  color: #333;
}

.welcome-card p {
  color: #666;
  font-size: 14px;
}
</style>
