import request from './request';

export interface LoginRequest {
    email: string;
    password: string;
}

export interface RegisterRequest {
    email: string;
    username: string;
    password: string;
    confirmPassword: string;
}

export interface LoginResponse {
    token: string;
    email: string;
    username: string;
}

export const userApi = {
    login(data: LoginRequest) {
        return request.post<LoginResponse>('/auth/login', data);
    },

    register(data: RegisterRequest) {
        return request.post<LoginResponse>('/auth/register', data);
    },

    sendVerificationCode(email: string) {
        return request.post('/auth/send-verification-code', null, {
            params: { email }
        });
    },

    verifyEmail(email: string, code: string) {
        return request.post('/auth/verify-email', { email, code });
    },

    resetPassword(data: {
        email: string;
        code: string;
        newPassword: string;
        confirmPassword: string;
    }) {
        return request.post('/auth/reset-password', data);
    }
}; 