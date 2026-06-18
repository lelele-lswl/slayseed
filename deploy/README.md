# ============================================
#  Slay Seed 云服务器部署指南
# ============================================
#  服务器要求：Ubuntu 20.04+ / CentOS 7+
#  内存：至少 2GB
#  磁盘：至少 10GB
# ============================================


## 一、服务器环境准备

### 1. 安装 Java 17
```bash
# Ubuntu/Debian
apt update
apt install -y openjdk-17-jdk

# CentOS
yum install -y java-17-openjdk-devel

# 验证
java -version
```

### 2. 安装 MySQL
```bash
# Ubuntu/Debian
apt install -y mysql-server
systemctl start mysql
systemctl enable mysql

# 设置密码
mysql -e "ALTER USER 'root'@'localhost' IDENTIFIED BY 'your-password'; FLUSH PRIVILEGES;"

# 创建数据库
mysql -u root -p -e "CREATE DATABASE slayseed CHARACTER SET utf8mb4;"
```

### 3. 安装 Redis
```bash
# Ubuntu/Debian
apt install -y redis-server
systemctl start redis-server
systemctl enable redis-server

# 验证
redis-cli ping
```

### 4. 安装 Nginx
```bash
# Ubuntu/Debian
apt install -y nginx
systemctl start nginx
systemctl enable nginx
```

## 二、修改配置

### 1. 修改 slayseed.service 中的密码
```bash
# 编辑部署目录下的 slayseed.service，修改以下内容：
# - JASYPT_ENCRYPTOR_KEY=你的加密密钥
# - SPRING_DATASOURCE_PASSWORD=你的数据库密码
# - SPRING_DATA_REDIS_PASSWORD=你的Redis密码（如无则留空）
```

### 2. 上传文件到服务器

目录结构：
```
/opt/slayseed/
├── backend/
│   └── slayseed.jar          ← 后端jar包
└── frontend/
    └── dist/                ← 前端静态文件
/etc/nginx/conf.d/
    └── slayseed.conf        ← Nginx配置
/etc/systemd/system/
    └── slayseed.service      ← systemd服务
```

## 三、启动服务

```bash
# 重新加载 systemd
systemctl daemon-reload

# 启动应用
systemctl start slayseed
systemctl restart nginx

# 设置开机自启
systemctl enable slayseed
systemctl enable nginx

# 查看状态
systemctl status slayseed
systemctl status nginx

# 查看日志
journalctl -u slayseed -f
tail -f /var/log/slayseed/backend.log
```

## 四、验证

浏览器访问：http://你的服务器IP

## 五、常用命令

| 操作 | 命令 |
|------|------|
| 启动 | systemctl start slayseed |
| 停止 | systemctl stop slayseed |
| 重启 | systemctl restart slayseed |
| 查看状态 | systemctl status slayseed |
| 查看日志 | journalctl -u slayseed -f |
| 更新部署 | bash deploy/deploy.sh <服务器IP> |

## 六、防火墙开放端口

```bash
# Ubuntu/Debian (ufw)
ufw allow 80/tcp
ufw reload

# CentOS (firewalld)
firewall-cmd --permanent --add-service=http
firewall-cmd --reload
```

> 如果用阿里云/腾讯云，记得在云服务器后台的安全组中开放 80 端口。