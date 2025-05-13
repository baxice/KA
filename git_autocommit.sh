#!/bin/bash

# Git 自动提交脚本（带版本号递增）

# 检查是否在 Git 仓库中
if ! git rev-parse --is-inside-work-tree > /dev/null 2>&1; then
    echo "❌ 错误：当前目录不是 Git 仓库！"
    exit 1
fi

# 版本文件路径
VERSION_FILE=".version"

# 初始化版本号（如果不存在）
if [ ! -f "$VERSION_FILE" ]; then
    echo "V1.0.0" > "$VERSION_FILE"
    echo "🆕 初始化版本号: V1.0.0"
fi

# 读取当前版本号
CURRENT_VERSION=$(cat "$VERSION_FILE")
echo "🏷️ 当前版本: $CURRENT_VERSION"

# 提取版本号数字部分
if [[ $CURRENT_VERSION =~ ^V([0-9]+)\.([0-9]+)\.([0-9]+)$ ]]; then
    MAJOR=${BASH_REMATCH[1]}
    MINOR=${BASH_REMATCH[2]}
    PATCH=${BASH_REMATCH[3]}
else
    echo "❌ 版本号格式错误，应为 Vx.y.z"
    exit 1
fi

# 递增版本号（第二位 +1）
NEW_MINOR=$((MINOR + 2))
NEW_VERSION="V${MAJOR}.${NEW_MINOR}.0"

# 更新版本文件
echo "$NEW_VERSION" > "$VERSION_FILE"
echo "🆙 新版本号: $NEW_VERSION"

# 添加所有更改（包括版本文件）
git add -A

# 生成提交信息
COMMIT_MSG="release: $NEW_VERSION"
echo "📝 自动提交信息: $COMMIT_MSG"

# 提交更改
git commit -m "$COMMIT_MSG"

# 获取当前分支
CURRENT_BRANCH=$(git branch --show-current)

# 询问是否推送到远程
read -p "🚀 是否推送到远程分支 '$CURRENT_BRANCH'? [y/N] " PUSH_CHOICE
if [[ $PUSH_CHOICE =~ ^[Yy]$ ]]; then
    git push origin "$CURRENT_BRANCH"
fi

echo "🎉 提交完成！版本已更新至 $NEW_VERSION"
