import axios from 'axios';
import { ElMessage } from 'element-plus';

const request = axios.create({
    baseURL: 'http://localhost:8080',
    timeout: 5000,
    headers: {
        'Content-Type': 'application/json'
    }
});

// 请求拦截器
request.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    error => {
        ElMessage.error('请求配置错误');
        return Promise.reject(error);
    }
);

// 响应拦截器
request.interceptors.response.use(
    response => {
        return response.data;
    },
    error => {
        if (error.response) {
            const { data, status } = error.response;
            switch (status) {
                case 401:
                    ElMessage.error('未授权，请重新登录');
                    // 可以在这里处理登出逻辑
                    break;
                case 403:
                    ElMessage.error('拒绝访问');
                    break;
                case 404:
                    ElMessage.error('请求的资源不存在');
                    break;
                case 500:
                    ElMessage.error('服务器错误');
                    break;
                default:
                    if (data && data.message) {
                        ElMessage.error(data.message);
                    } else if (data && typeof data === 'object') {
                        const errorMessages = Object.values(data).join('\n');
                        ElMessage.error(errorMessages);
                    } else {
                        ElMessage.error(`请求失败: ${status}`);
                    }
            }
        } else if (error.request) {
            ElMessage.error('服务器无响应，请检查网络连接');
        } else {
            ElMessage.error('请求配置错误');
        }
        return Promise.reject(error);
    }
);

export default request; 