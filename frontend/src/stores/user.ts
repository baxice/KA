import { defineStore } from 'pinia';
import { userApi, type LoginRequest, type RegisterRequest } from '../api/user';

export const useUserStore = defineStore('user', {
    state: () => ({
        token: localStorage.getItem('token') || '',
        email: '',
        username: ''
    }),

    getters: {
        isLoggedIn: (state) => !!state.token
    },

    actions: {
        async login(loginRequest: LoginRequest) {
            try {
                const response = await userApi.login(loginRequest);
                this.setUserInfo(response);
                return response;
            } catch (error) {
                throw error;
            }
        },

        async register(registerRequest: RegisterRequest) {
            try {
                const response = await userApi.register(registerRequest);
                this.setUserInfo(response);
                return response;
            } catch (error) {
                throw error;
            }
        },

        setUserInfo(data: { token: string; email: string; username: string }) {
            this.token = data.token;
            this.email = data.email;
            this.username = data.username;
            localStorage.setItem('token', data.token);
        },

        logout() {
            this.token = '';
            this.email = '';
            this.username = '';
            localStorage.removeItem('token');
        }
    }
}); 