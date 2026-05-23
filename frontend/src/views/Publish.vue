<template>
  <div class="publish-container">
    <el-card class="publish-card">
      <h2>发布笔记</h2>
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入笔记标题" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="封面图片URL（选填）">
          <el-input v-model="form.coverImage" placeholder="请输入图片链接" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="10" placeholder="分享你的生活..." maxlength="5000" show-word-limit />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handlePublish" :loading="loading">发布</el-button>
        </el-form-item>
      </el-form>
    </el-card>
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

const form = reactive({ title: '', content: '', coverImage: '' })
const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

async function handlePublish() {
  await formRef.value.validate()
  loading.value = true
  try {
    const res = await request.post('/notes', form)
    ElMessage.success('发布成功')
    router.push(`/note/${res.data.id}`)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.publish-container {
  max-width: 700px;
  margin: 0 auto;
}

.publish-card h2 {
  margin-bottom: 20px;
  color: #ff2442;
}
</style>
