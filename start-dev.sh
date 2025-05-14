#!/bin/bash

# 停止并删除现有容器
docker-compose down

# 启动所有服务
docker-compose up -d

# 等待服务启动
echo "等待服务启动..."
sleep 10

# 检查服务状态
echo "检查服务状态..."
docker-compose ps

echo "服务启动完成！"
echo "MySQL: localhost:3306"
echo "Neo4j Browser: http://localhost:7474"
echo "MinIO Console: http://localhost:9001" 