<template>
    <div class="reset-password-container">
        <el-card class="reset-password-card">
            <template #header>
                <h2>重置密码</h2>
            </template>
            <el-form
                ref="formRef"
                :model="form"
                :rules="rules"
                label-width="100px"
                @submit.prevent="handleSubmit"
            >
                <el-form-item label="邮箱" prop="email">
                    <el-input v-model="form.email" placeholder="请输入邮箱">
                        <template #append>
                            <el-button 
                                :disabled="!!countdown" 
                                @click="handleSendCode"
                            >
                                {{ countdown ? `${countdown}秒后重试` : '获取验证码' }}
                            </el-button>
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item label="验证码" prop="code">
                    <el-input v-model="form.code" placeholder="请输入验证码" />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                    <el-input
                        v-model="form.newPassword"
                        type="password"
                        placeholder="请输入新密码"
                        show-password
                    />
                </el-form-item>
                <el-form-item label="确认密码" prop="confirmPassword">
                    <el-input
                        v-model="form.confirmPassword"
                        type="password"
                        placeholder="请再次输入新密码"
                        show-password
                    />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" native-type="submit" :loading="loading">
                        重置密码
                    </el-button>
                    <el-button @click="$router.push('/login')">返回登录</el-button>
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
import { userApi } from '../api/user';

const router = useRouter();
const formRef = ref<FormInstance>();
const loading = ref(false);
const countdown = ref(0);

const form = reactive({
    email: '',
    code: '',
    newPassword: '',
    confirmPassword: ''
});

const validatePass = (rule: any, value: string, callback: any) => {
    if (value === '') {
        callback(new Error('请输入新密码'));
    } else {
        if (form.confirmPassword !== '') {
            formRef.value?.validateField('confirmPassword');
        }
        callback();
    }
};

const validatePass2 = (rule: any, value: string, callback: any) => {
    if (value === '') {
        callback(new Error('请再次输入新密码'));
    } else if (value !== form.newPassword) {
        callback(new Error('两次输入密码不一致!'));
    } else {
        callback();
    }
};

const rules = {
    email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
    ],
    code: [
        { required: true, message: '请输入验证码', trigger: 'blur' },
        { len: 6, message: '验证码长度应为6位', trigger: 'blur' }
    ],
    newPassword: [
        { required: true, validator: validatePass, trigger: 'blur' },
        { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
    ],
    confirmPassword: [
        { required: true, validator: validatePass2, trigger: 'blur' }
    ]
};

const startCountdown = () => {
    countdown.value = 60;
    const timer = setInterval(() => {
        countdown.value--;
        if (countdown.value <= 0) {
            clearInterval(timer);
        }
    }, 1000);
};

const handleSendCode = async () => {
    if (!form.email) {
        ElMessage.warning('请先输入邮箱');
        return;
    }

    try {
        await userApi.sendVerificationCode(form.email);
        ElMessage.success('验证码已发送');
        startCountdown();
    } catch (error: any) {
        ElMessage.error(error.message || '发送验证码失败');
    }
};

const handleSubmit = async () => {
    if (!formRef.value) return;
    
    await formRef.value.validate(async (valid) => {
        if (valid) {
            try {
                loading.value = true;
                await userApi.resetPassword(form);
                ElMessage.success('密码重置成功');
                router.push('/login');
            } catch (error: any) {
                ElMessage.error(error.message || '密码重置失败');
            } finally {
                loading.value = false;
            }
        }
    });
};
</script>

<style scoped>
.reset-password-container {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    background-color: #f5f7fa;
}

.reset-password-card {
    width: 400px;
}

.reset-password-card :deep(.el-card__header) {
    text-align: center;
}

.reset-password-card h2 {
    margin: 0;
    color: #303133;
}
</style> 