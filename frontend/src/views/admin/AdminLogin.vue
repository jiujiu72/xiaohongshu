<template>
  <div class="admin-login-page">
    <div class="admin-login-container">
      <div class="admin-brand">
        <h1>小红书</h1>
        <p>管理后台</p>
      </div>
      <el-card class="admin-login-card">
        <h2>管理员登录</h2>
        <el-form :model="form" :rules="rules" ref="formRef" label-width="0">
          <el-form-item prop="username">
            <el-input v-model="form.username" placeholder="请输入管理员账号" prefix-icon="User" size="large" />
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="form.password" type="password" placeholder="请输入密码" prefix-icon="Lock" show-password size="large" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" class="login-btn" @click="handleLogin" :loading="loading" size="large">登录</el-button>
          </el-form-item>
        </el-form>
        <div class="footer-links">
          <router-link to="/login">返回用户登录</router-link>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../../utils/request'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入管理员账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  await formRef.value.validate()
  loading.value = true
  try {
    const res = await request.post('/admin/login', form)
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('userId', res.data.userId)
    localStorage.setItem('nickname', res.data.nickname)
    localStorage.setItem('role', res.data.role)
    ElMessage.success('登录成功')
    router.push('/admin')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.admin-login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
}

.admin-login-container {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.admin-brand {
  text-align: center;
  margin-bottom: 30px;
}

.admin-brand h1 {
  font-size: 36px;
  color: #fff;
  margin-bottom: 8px;
}

.admin-brand p {
  color: rgba(255, 255, 255, 0.8);
  font-size: 16px;
}

.admin-login-card {
  width: 400px;
  padding: 20px;
  border-radius: 12px;
}

.admin-login-card h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
  font-size: 22px;
}

.login-btn {
  width: 100%;
}

.footer-links {
  text-align: center;
  margin-top: 16px;
  font-size: 14px;
}
</style>
