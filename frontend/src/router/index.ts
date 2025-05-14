import { createRouter, createWebHistory } from 'vue-router';
import { useUserStore } from '../stores/user';
import DocumentList from '../views/DocumentList.vue';

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'Home',
            component: () => import('../views/Home.vue'),
            meta: {
                title: '首页'
            }
        },
        {
            path: '/login',
            name: 'Login',
            component: () => import('../views/Login.vue'),
            meta: {
                title: '登录'
            }
        },
        {
            path: '/register',
            name: 'Register',
            component: () => import('../views/Register.vue'),
            meta: {
                title: '注册'
            }
        },
        {
            path: '/reset-password',
            name: 'ResetPassword',
            component: () => import('../views/ResetPassword.vue'),
            meta: {
                title: '重置密码'
            }
        },
        {
            path: '/documents',
            name: 'documents',
            component: DocumentList,
            meta: {
                title: '文档管理',
                requiresAuth: true  // 只有文档管理需要登录
            }
        }
    ]
});

// 路由守卫
router.beforeEach((to, from, next) => {
    const userStore = useUserStore();
    
    // 只有需要认证的页面才检查登录状态
    if (to.meta.requiresAuth && !userStore.isLoggedIn) {
        next('/login');
    } else {
        next();
    }
});

export default router; 