class UserAvatar {
    constructor(containerId) {
        this.container = document.getElementById(containerId);
        this.avatarUrl = null;
        this.init();
    }

    init() {
        this.render();
        this.loadAvatar();
        this.setupEventListeners();
    }

    render() {
        this.container.innerHTML = `
            <div class="avatar-container">
                <div class="avatar-wrapper">
                    <img id="avatar-image" src="/images/default-avatar.png" alt="用户头像" class="avatar-image">
                    <div class="avatar-overlay">
                        <label for="avatar-upload" class="avatar-upload-btn">
                            <i class="fas fa-camera"></i>
                        </label>
                        <input type="file" id="avatar-upload" accept="image/*" style="display: none;">
                    </div>
                </div>
                <div class="avatar-actions">
                    <button id="delete-avatar" class="btn btn-danger btn-sm">
                        <i class="fas fa-trash"></i> 删除头像
                    </button>
                </div>
            </div>
        `;
    }

    setupEventListeners() {
        const uploadInput = this.container.querySelector('#avatar-upload');
        const deleteButton = this.container.querySelector('#delete-avatar');

        uploadInput.addEventListener('change', (e) => this.handleFileUpload(e));
        deleteButton.addEventListener('click', () => this.handleDeleteAvatar());
    }

    async loadAvatar() {
        try {
            const response = await fetch('/api/avatars', {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                }
            });
            const data = await response.json();
            
            if (data.avatarUrl) {
                this.updateAvatarImage(data.avatarUrl);
            }
        } catch (error) {
            console.error('加载头像失败:', error);
        }
    }

    async handleFileUpload(event) {
        const file = event.target.files[0];
        if (!file) return;

        if (!file.type.startsWith('image/')) {
            alert('请选择图片文件');
            return;
        }

        const formData = new FormData();
        formData.append('file', file);

        try {
            const response = await fetch('/api/avatars/upload', {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                },
                body: formData
            });

            const data = await response.json();
            if (data.avatarUrl) {
                this.updateAvatarImage(data.avatarUrl);
            }
        } catch (error) {
            console.error('上传头像失败:', error);
            alert('上传头像失败，请重试');
        }
    }

    async handleDeleteAvatar() {
        if (!confirm('确定要删除头像吗？')) return;

        try {
            const response = await fetch('/api/avatars', {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                }
            });

            const data = await response.json();
            if (data.message) {
                this.updateAvatarImage('/images/default-avatar.png');
            }
        } catch (error) {
            console.error('删除头像失败:', error);
            alert('删除头像失败，请重试');
        }
    }

    updateAvatarImage(url) {
        const avatarImage = this.container.querySelector('#avatar-image');
        avatarImage.src = url;
        this.avatarUrl = url;
    }
} 