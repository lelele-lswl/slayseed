@echo off
chcp 65001 >nul
REM ============================================
REM   Slay Seed 云服务器部署指南 (Windows版)
REM ============================================
REM   需要先安装：
REM     1. Git Bash / WSL / Git Bash
REM     2. Maven
REM     3. Node.js
REM ============================================

echo ==========================================
echo   请按以下步骤手动部署
echo ==========================================
echo.

echo [步骤1] 本地打包后端
echo   cd /d %~dp0..
echo   mvn clean package -DskipTests
echo.

echo [步骤2] 本地构建前端
echo   cd frontend
echo   npm run build
echo.

echo [步骤3] 上传到服务器
echo   用 WinSCP 或 FileZilla 连接服务器
echo   - 后端 jar -^> /opt/slayseed/backend/slayseed.jar
echo   - 前端 dist/ -^> /opt/slayseed/frontend/dist/
echo   - deploy/nginx.conf -^> /etc/nginx/conf.d/slayseed.conf
echo   - deploy/slayseed.service -^> /etc/systemd/system/slayseed.service
echo.

echo [步骤4] 在服务器上执行
echo   mkdir -p /opt/slayseed/backend /opt/slayseed/frontend/dist /var/log/slayseed
echo   systemctl daemon-reload
echo   systemctl restart slayseed
echo   systemctl restart nginx
echo.

echo ==========================================
echo  完成！ 访问 http://你的服务器IP
echo ==========================================
pause