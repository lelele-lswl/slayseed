#!/bin/bash
# ============================================
#  Slay Seed - 构建 + 推送 + 服务器部署 (一键脚本)
#  使用方法：
#    bash deploy-docker.sh <服务器IP> [镜像标签]
#  示例：
#    bash deploy-docker.sh 116.62.47.66 v1.0.0
# ============================================

set -e

SERVER=${1:?"请提供服务器IP，用法: bash deploy-docker.sh <服务器IP> [镜像标签]"}
IMAGE_TAG=${2:-latest}
USER="root"
IMAGE_REGISTRY=${IMAGE_REGISTRY:-"registry.cn-hangzhou.aliyuncs.com"}
NAMESPACE="slayseed"
DEPLOY_DIR="/opt/slayseed"

echo "=========================================="
echo "  Slay Seed Docker 一键部署"
echo "  目标服务器: ${USER}@${SERVER}"
echo "  镜像标签:   ${IMAGE_TAG}"
echo "=========================================="

echo ""
read -p "请输入镜像仓库用户名: " REGISTRY_USER
read -s -p "请输入镜像仓库密码: " REGISTRY_PASS
echo ""

echo "[1/8] 登录镜像仓库..."
docker login ${IMAGE_REGISTRY} -u ${REGISTRY_USER} -p ${REGISTRY_PASS}
echo "登录成功"

echo ""
echo "[2/8] 打包 Spring Boot 后端..."
cd "$(dirname "$0")/.."
mvn clean package -DskipTests -q
echo "后端打包完成"

echo ""
echo "[3/8] 构建后端 Docker 镜像..."
docker build -f deploy/Dockerfile.backend -t ${IMAGE_REGISTRY}/${NAMESPACE}/backend:${IMAGE_TAG} .
echo "后端镜像构建完成"

echo ""
echo "[4/8] 构建前端 Docker 镜像..."
docker build -f deploy/Dockerfile.frontend -t ${IMAGE_REGISTRY}/${NAMESPACE}/frontend:${IMAGE_TAG} .
echo "前端镜像构建完成"

echo ""
echo "[5/8] 推送后端镜像..."
docker push ${IMAGE_REGISTRY}/${NAMESPACE}/backend:${IMAGE_TAG}
echo "后端镜像推送完成"

echo ""
echo "[5.2/8] 推送前端镜像..."
docker push ${IMAGE_REGISTRY}/${NAMESPACE}/frontend:${IMAGE_TAG}
echo "前端镜像推送完成"

echo ""
echo "[6/8] 上传部署文件到服务器..."
ssh ${USER}@${SERVER} "mkdir -p ${DEPLOY_DIR}"
scp docker-compose-prod.yml ${USER}@${SERVER}:${DEPLOY_DIR}/docker-compose.yml

if [ -f .env ]; then
  scp .env ${USER}@${SERVER}:${DEPLOY_DIR}/.env
  echo "已上传本地 .env 文件"
else
  echo "⚠️  未找到本地 .env 文件，将生成默认配置"
  cat > /tmp/slayseed-env <<EOF
IMAGE_REGISTRY=${IMAGE_REGISTRY}
IMAGE_TAG=${IMAGE_TAG}
MYSQL_PASSWORD=your-strong-password-here
JASYPT_ENCRYPTOR_KEY=your-encryptor-key-here
EOF
  scp /tmp/slayseed-env ${USER}@${SERVER}:${DEPLOY_DIR}/.env
  rm -f /tmp/slayseed-env
  echo "⚠️  请登录服务器修改 ${DEPLOY_DIR}/.env 中的密码和密钥！"
fi
echo "文件上传完成"

echo ""
echo "[7/8] 在服务器上登录镜像仓库..."
ssh ${USER}@${SERVER} "docker login ${IMAGE_REGISTRY} -u ${REGISTRY_USER} -p ${REGISTRY_PASS}"
echo "服务器登录成功"

echo ""
echo "[8/8] 在服务器上拉取镜像并启动服务..."
ssh ${USER}@${SERVER} "cd ${DEPLOY_DIR} && docker compose pull && docker compose up -d"
echo "服务启动完成"

echo ""
echo "[可选] 清理本地镜像..."
docker rmi ${IMAGE_REGISTRY}/${NAMESPACE}/backend:${IMAGE_TAG} 2>/dev/null || true
docker rmi ${IMAGE_REGISTRY}/${NAMESPACE}/frontend:${IMAGE_TAG} 2>/dev/null || true
echo "本地镜像清理完成"

echo ""
echo "=========================================="
echo "  部署完成！"
echo "  访问地址: http://${SERVER}"
echo ""
echo "  服务器操作:"
echo "    查看状态: ssh ${USER}@${SERVER} 'cd ${DEPLOY_DIR} && docker compose ps'"
echo "    查看日志: ssh ${USER}@${SERVER} 'cd ${DEPLOY_DIR} && docker compose logs -f'"
echo "    重启服务: ssh ${USER}@${SERVER} 'cd ${DEPLOY_DIR} && docker compose restart'"
echo "=========================================="