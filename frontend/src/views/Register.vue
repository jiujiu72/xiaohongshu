<template>
  <div class="register-page">
    <div class="register-container">
      <div class="register-brand">
        <h1>小红书</h1>
        <p>标记我的生活</p>
      </div>
      <el-card class="register-card">
        <h2>用户注册</h2>
        <el-form :model="form" :rules="rules" ref="formRef" label-width="0">
          <el-form-item prop="username">
            <el-input v-model="form.username" placeholder="请输入用户名" prefix-icon="User" size="large" />
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="form.password" type="password" placeholder="请输入密码" prefix-icon="Lock" show-password size="large" />
          </el-form-item>
          <el-form-item prop="nickname">
            <el-input v-model="form.nickname" placeholder="请输入昵称（选填）" prefix-icon="UserFilled" size="large" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" class="register-btn" @click="handleRegister" :loading="loading" size="large">注册</el-button>
          </el-form-item>
        </el-form>
        <div class="footer-links">
          <router-link to="/login">已有账号？去登录</router-link>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({ username: '', password: '', nickname: '' })
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度3-20位', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度6-20位', trigger: 'blur' }
  ]
}

async function handleRegister() {
  await formRef.value.validate()
  loading.value = true
  try {
    await request.post('/user/register', form)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #ff2442 0%, #ff6b81 100%);
}

.register-container {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.register-brand {
  text-align: center;
  margin-bottom: 30px;
}

.register-brand h1 {
  font-size: 36px;
  color: #fff;
  margin-bottom: 8px;
}

.register-brand p {
  color: rgba(255, 255, 255, 0.8);
  font-size: 16px;
}

.register-card {
  width: 400px;
  padding: 20px;
  border-radius: 12px;
}

.register-card h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
  font-size: 22px;
}

.register-btn {
  width: 100%;
}

.footer-links {
  text-align: center;
  margin-top: 16px;
  font-size: 14px;
}
</style>
