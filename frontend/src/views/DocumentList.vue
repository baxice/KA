<template>
  <div class="document-list">
    <div class="header">
      <h1>文档管理</h1>
      <el-button type="primary" @click="showUploadDialog">上传文档</el-button>
    </div>

    <el-card class="document-container">
      <el-table :data="documents" style="width: 100%">
        <el-table-column prop="name" label="文档名称" />
        <el-table-column prop="type" label="类型" width="120" />
        <el-table-column prop="size" label="大小" width="120">
          <template #default="scope">
            {{ formatFileSize(scope.row.size) }}
          </template>
        </el-table-column>
        <el-table-column prop="uploadTime" label="上传时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button type="primary" link @click="previewDocument(scope.row)">预览</el-button>
            <el-button type="primary" link @click="downloadDocument(scope.row)">下载</el-button>
            <el-button type="danger" link @click="deleteDocument(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 上传对话框 -->
    <el-dialog v-model="uploadDialogVisible" title="上传文档" width="500px">
      <el-upload
        class="upload-demo"
        drag
        action="/api/documents/upload"
        :on-success="handleUploadSuccess"
        :on-error="handleUploadError"
        :before-upload="beforeUpload"
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">
          将文件拖到此处，或<em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            支持 PDF、Word、Excel 等格式文件
          </div>
        </template>
      </el-upload>
    </el-dialog>

    <!-- 预览对话框 -->
    <el-dialog v-model="previewDialogVisible" title="文档预览" width="80%">
      <div class="preview-container">
        <iframe :src="previewUrl" frameborder="0" width="100%" height="600px"></iframe>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import type { Document } from '@/types/document'

const documents = ref<Document[]>([])
const uploadDialogVisible = ref(false)
const previewDialogVisible = ref(false)
const previewUrl = ref('')

// 获取文档列表
const fetchDocuments = async () => {
  try {
    const response = await fetch('/api/documents')
    const data = await response.json()
    documents.value = data
  } catch (error) {
    ElMessage.error('获取文档列表失败')
  }
}

// 显示上传对话框
const showUploadDialog = () => {
  uploadDialogVisible.value = true
}

// 上传前检查
const beforeUpload = (file: File) => {
  const allowedTypes = ['application/pdf', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document']
  if (!allowedTypes.includes(file.type)) {
    ElMessage.error('只支持 PDF 和 Word 文档！')
    return false
  }
  return true
}

// 上传成功处理
const handleUploadSuccess = (response: any) => {
  ElMessage.success('上传成功')
  uploadDialogVisible.value = false
  fetchDocuments()
}

// 上传失败处理
const handleUploadError = () => {
  ElMessage.error('上传失败')
}

// 预览文档
const previewDocument = (document: Document) => {
  previewUrl.value = `/api/documents/${document.id}/preview`
  previewDialogVisible.value = true
}

// 下载文档
const downloadDocument = async (document: Document) => {
  try {
    const response = await fetch(`/api/documents/${document.id}/download`)
    const blob = await response.blob()
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = document.name
    document.body.appendChild(a)
    a.click()
    window.URL.revokeObjectURL(url)
    document.body.removeChild(a)
  } catch (error) {
    ElMessage.error('下载失败')
  }
}

// 删除文档
const deleteDocument = async (document: Document) => {
  try {
    await fetch(`/api/documents/${document.id}`, {
      method: 'DELETE'
    })
    ElMessage.success('删除成功')
    fetchDocuments()
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

// 格式化文件大小
const formatFileSize = (size: number) => {
  if (size < 1024) {
    return size + ' B'
  } else if (size < 1024 * 1024) {
    return (size / 1024).toFixed(2) + ' KB'
  } else {
    return (size / (1024 * 1024)).toFixed(2) + ' MB'
  }
}

onMounted(() => {
  fetchDocuments()
})
</script>

<style scoped>
.document-list {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.document-container {
  margin-top: 20px;
}

.preview-container {
  width: 100%;
  height: 600px;
  background: #f5f5f5;
}

:deep(.el-upload-dragger) {
  width: 100%;
}
</style> 