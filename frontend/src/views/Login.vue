<template>
    <div class="login-container">
        <el-card class="login-card">
            <template #header>
                <h2>登录</h2>
            </template>
            <el-form
                ref="formRef"
                :model="form"
                :rules="rules"
                label-width="80px"
                @submit.prevent="handleSubmit"
            >
                <el-form-item label="邮箱" prop="email">
                    <el-input v-model="form.email" placeholder="请输入邮箱" />
                </el-form-item>
                <el-form-item label="密码" prop="password">
                    <el-input
                        v-model="form.password"
                        type="password"
                        placeholder="请输入密码"
                        show-password
                    />
                </el-form-item>
                <el-form-item>
                    <div class="forgot-password">
                        <router-link to="/reset-password">忘记密码？</router-link>
                    </div>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" native-type="submit" :loading="loading">
                        登录
                    </el-button>
                    <el-button @click="$router.push('/register')">注册</el-button>
                </el-form-item>
            </el-form>
        </el-card>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import type { FormInstance } from 'element-plus';
import { useUserStore } from '../stores/user';

const router = useRouter();
const userStore = useUserStore();
const formRef = ref<FormInstance>();
const loading = ref(false);

const form = reactive({
    email: '',
    password: ''
});

const rules = {
    email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
    ]
};

const handleSubmit = async () => {
    if (!formRef.value) return;
    
    await formRef.value.validate(async (valid) => {
        if (valid) {
            try {
                loading.value = true;
                await userStore.login(form);
                ElMessage.success('登录成功');
                router.push('/');
            } catch (error: any) {
                ElMessage.error(error.message || '登录失败');
            } finally {
                loading.value = false;
            }
        }
    });
};
</script>

<style scoped>
.login-container {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    background-color: #f5f7fa;
}

.login-card {
    width: 400px;
}

.login-card :deep(.el-card__header) {
    text-align: center;
}

.login-card h2 {
    margin: 0;
    color: #303133;
}

.forgot-password {
    text-align: right;
    margin-top: -20px;
    margin-bottom: 20px;
}

.forgot-password a {
    color: #409EFF;
    text-decoration: none;
}

.forgot-password a:hover {
    color: #66b1ff;
}
</style> 