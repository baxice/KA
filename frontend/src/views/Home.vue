<template>
  <div class="home">
    <el-row class="banner">
      <el-col :span="24">
        <h1>知识应用系统</h1>
        <p class="subtitle">高效、智能的知识管理与应用平台</p>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="feature-section">
      <el-col :span="8">
        <el-card class="feature-card">
          <template #header>
            <div class="card-header">
              <el-icon><Document /></el-icon>
              <span>文档管理</span>
            </div>
          </template>
          <div class="card-content">
            <p>支持多种格式文档的上传、预览和管理</p>
            <el-button type="primary" @click="goToDocuments" :disabled="!isLoggedIn">
              {{ isLoggedIn ? '进入文档管理' : '请先登录' }}
            </el-button>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="feature-card">
          <template #header>
            <div class="card-header">
              <el-icon><Connection /></el-icon>
              <span>知识图谱</span>
            </div>
          </template>
          <div class="card-content">
            <p>构建和管理知识之间的关联关系</p>
            <el-button type="primary" @click="goToKnowledgeGraph" :disabled="!isLoggedIn">
              {{ isLoggedIn ? '查看知识图谱' : '请先登录' }}
            </el-button>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="feature-card">
          <template #header>
            <div class="card-header">
              <el-icon><Search /></el-icon>
              <span>智能搜索</span>
            </div>
          </template>
          <div class="card-content">
            <p>快速检索和定位所需的知识内容</p>
            <el-button type="primary" @click="goToSearch" :disabled="!isLoggedIn">
              {{ isLoggedIn ? '开始搜索' : '请先登录' }}
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row class="action-section">
      <el-col :span="24">
        <div class="login-section" v-if="!isLoggedIn">
          <p>立即开始使用系统</p>
          <el-button type="primary" @click="goToLogin">登录</el-button>
          <el-button @click="goToRegister">注册</el-button>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { Document, Connection, Search } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const isLoggedIn = computed(() => userStore.isLoggedIn)

const goToDocuments = () => {
  router.push('/documents')
}

const goToKnowledgeGraph = () => {
  router.push('/knowledge-graph')
}

const goToSearch = () => {
  router.push('/search')
}

const goToLogin = () => {
  router.push('/login')
}

const goToRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
.home {
  padding: 20px;
}

.banner {
  text-align: center;
  padding: 60px 0;
  background: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%);
  color: white;
  margin-bottom: 40px;
  border-radius: 8px;
}

.banner h1 {
  font-size: 2.5em;
  margin-bottom: 20px;
}

.subtitle {
  font-size: 1.2em;
  opacity: 0.9;
}

.feature-section {
  margin-bottom: 40px;
}

.feature-card {
  height: 100%;
  transition: transform 0.3s;
}

.feature-card:hover {
  transform: translateY(-5px);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 1.2em;
}

.card-content {
  text-align: center;
  padding: 20px 0;
}

.card-content p {
  margin-bottom: 20px;
  color: #666;
}

.action-section {
  text-align: center;
  padding: 40px 0;
}

.login-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.login-section p {
  font-size: 1.2em;
  color: #666;
}

:deep(.el-card__header) {
  padding: 15px 20px;
  border-bottom: 1px solid #ebeef5;
  background-color: #f5f7fa;
}
</style> 