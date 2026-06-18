#!/bin/bash
# ============================================
#  Slay Seed - 云服务器初始化脚本
#  在云服务器上执行，安装 Docker 和 docker-compose
#  使用方法：
#    bash server-setup.sh
# ============================================

set -e

echo "=========================================="
echo "  Slay Seed 服务器初始化"
echo "  安装 Docker + Docker Compose"
echo "=========================================="

if [ -f /etc/os-release ]; then
    . /etc/os-release
    OS=$ID
elif [ -f /etc/redhat-release ]; then
    OS="centos"
else
    echo "无法检测操作系统"
    exit 1
fi

echo ""
echo "检测到系统: ${OS}"
echo ""

if [ "$OS" = "ubuntu" ] || [ "$OS" = "debian" ]; then
    echo "[1/4] 更新系统软件包..."
    apt update -y && apt upgrade -y -q
    echo "系统更新完成"

    echo ""
    echo "[2/4] 安装必要依赖..."
    apt install -y -q curl apt-transport-https ca-certificates gnupg lsb-release
    echo "依赖安装完成"

    echo ""
    echo "[3/4] 添加 Docker 官方 GPG 密钥和仓库..."
    curl -fsSL https://download.docker.com/linux/${OS}/gpg | gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
    echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/${OS} $(lsb_release -cs) stable" | tee /etc/apt/sources.list.d/docker.list > /dev/null
    echo "Docker 仓库添加完成"

    echo ""
    echo "[4/4] 安装 Docker Engine 和 Docker Compose..."
    apt update -y -q
    apt install -y -q docker-ce docker-ce-cli containerd.io docker-compose-plugin
    echo "Docker 安装完成"

elif [ "$OS" = "centos" ] || [ "$OS" = "rhel" ] || [ "$OS" = "rocky" ] || [ "$OS" = "ol" ]; then
    echo "[1/4] 更新系统软件包..."
    yum update -y -q
    echo "系统更新完成"

    echo ""
    echo "[2/4] 安装必要依赖..."
    yum install -y -q yum-utils device-mapper-persistent-data lvm2
    echo "依赖安装完成"

    echo ""
    echo "[3/4] 添加 Docker 仓库..."
    yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
    echo "Docker 仓库添加完成"

    echo ""
    echo "[4/4] 安装 Docker Engine 和 Docker Compose..."
    yum install -y -q docker-ce docker-ce-cli containerd.io docker-compose-plugin
    echo "Docker 安装完成"

    systemctl start docker
    systemctl enable docker

else
    echo "不支持的系统: ${OS}"
    exit 1
fi

echo ""
echo "=========================================="
echo "  验证安装..."
docker --version
docker compose version
echo ""
echo "Docker 和 Docker Compose 安装成功！"
echo ""
echo "=========================================="
echo "  下一步："
echo "  1. 创建部署目录: mkdir -p /opt/slayseed"
echo "  2. 上传 docker-compose-prod.yml 和 .env 文件"
echo "  3. 启动服务: cd /opt/slayseed && docker compose up -d"
echo "=========================================="