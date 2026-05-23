<template>
  <div id="app">
    <el-container>
      <el-header class="app-header">
        <div class="header-content">
          <h1 class="logo" @click="$router.push('/')">小红书</h1>
          <div class="header-right">
            <template v-if="isLoggedIn">
              <el-button type="primary" @click="$router.push('/publish')">发布笔记</el-button>
              <span class="username">{{ nickname }}</span>
              <el-button link @click="logout">退出</el-button>
            </template>
            <template v-else>
              <el-button @click="$router.push('/login')">登录</el-button>
              <el-button type="primary" @click="$router.push('/register')">注册</el-button>
            </template>
          </div>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const isLoggedIn = computed(() => !!localStorage.getItem('token'))
const nickname = computed(() => localStorage.getItem('nickname') || '')

function logout() {
  localStorage.removeItem('token')
  localStorage.removeItem('userId')
  localStorage.removeItem('nickname')
  router.push('/login')
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  background-color: #f5f5f5;
}

.app-header {
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
}

.logo {
  color: #ff2442;
  cursor: pointer;
  font-size: 24px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.username {
  color: #333;
  font-size: 14px;
}

.el-main {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}
</style>
