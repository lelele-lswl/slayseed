#!/bin/bash
# ============================================
#  Slay Seed 一键部署脚本
# ============================================
#  使用方法：
#    bash deploy.sh <服务器IP>
#  例如：
#    bash deploy.sh 116.62.47.66
# ============================================

set -e

SERVER=${1:-"116.62.47.66"}
USER="root"
DEPLOY_DIR="/opt/slayseed"

echo "=========================================="
echo "  Slay Seed 部署到 ${USER}@${SERVER}"
echo "=========================================="

# -------- 1. 本地打包后端 --------
echo ""
echo "[1/5] 打包 Spring Boot 后端..."
cd "$(dirname "$0")/.."
mvn clean package -DskipTests -q
echo "✓ 后端打包完成"

# -------- 2. 本地打包前端 --------
echo ""
echo "[2/5] 构建 Vue 前端..."
cd frontend
npm run build --silent
cd ..
echo "✓ 前端构建完成"

# -------- 3. 上传到服务器 --------
echo ""
echo "[3/5] 上传文件到服务器..."
ssh ${USER}@${SERVER} "mkdir -p ${DEPLOY_DIR}/backend ${DEPLOY_DIR}/frontend/dist /var/log/slayseed"
scp target/*.jar ${USER}@${SERVER}:${DEPLOY_DIR}/backend/slayseed.jar
scp -r frontend/dist/* ${USER}@${SERVER}:${DEPLOY_DIR}/frontend/dist/
echo "✓ 文件上传完成"

# -------- 4. 配置 Nginx 和 systemd --------
echo ""
echo "[4/5] 配置 Nginx 与 systemd 服务..."
scp deploy/nginx.conf ${USER}@${SERVER}:/etc/nginx/conf.d/slayseed.conf
scp deploy/slayseed.service ${USER}@${SERVER}:/etc/systemd/system/slayseed.service
ssh ${USER}@${SERVER} "systemctl daemon-reload"
echo "✓ 服务配置完成"

# -------- 5. 启动服务 --------
echo ""
echo "[5/5] 启动服务..."
ssh ${USER}@${SERVER} "systemctl restart slayseed && systemctl restart nginx"
echo "✓ 服务启动完成"

echo ""
echo "=========================================="
echo "  部署完成！访问 http://${SERVER}"
echo "  查看日志： journalctl -u slayseed -f"
echo "=========================================="