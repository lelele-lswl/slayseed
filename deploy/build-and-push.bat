@echo off
chcp 65001 >nul
REM ============================================
REM   Slay Seed - 构建并推送 Docker 镜像 (Windows版)
REM   使用方法: build-and-push.bat [镜像标签]
REM   示例:   build-and-push.bat v1.0.0
REM ============================================

setlocal enabledelayedexpansion

cd /d %~dp0..

REM ---------- 配置 ----------
set IMAGE_TAG=%1
if "%IMAGE_TAG%"=="" set IMAGE_TAG=latest

set /p IMAGE_REGISTRY="请输入镜像仓库地址(如 registry.cn-hangzhou.aliyuncs.com): "
if "%IMAGE_REGISTRY%"=="" set IMAGE_REGISTRY=registry.cn-hangzhou.aliyuncs.com

set /p REGISTRY_USER="请输入镜像仓库用户名: "
set /p REGISTRY_PASS="请输入镜像仓库密码: "
set NAMESPACE=slayseed

echo.
echo ==========================================
echo   构建配置
echo   镜像仓库: %IMAGE_REGISTRY%
echo   命名空间: %NAMESPACE%
echo   标签:     %IMAGE_TAG%
echo ==========================================
echo.

REM ---------- 1. 登录镜像仓库 ----------
echo [1/6] 登录镜像仓库...
docker login %IMAGE_REGISTRY% -u %REGISTRY_USER% -p %REGISTRY_PASS%
if errorlevel 1 (
    echo ❌ 登录失败，请检查用户名密码
    pause
    exit /b 1
)
echo ✓ 登录成功
echo.

REM ---------- 2. 打包后端 Maven ----------
echo [2/6] 打包 Spring Boot 后端...
call mvn clean package -DskipTests -q
if errorlevel 1 (
    echo ❌ Maven 打包失败
    pause
    exit /b 1
)
echo ✓ 后端打包完成
echo.

REM ---------- 3. 构建后端镜像 ----------
echo [3/6] 构建后端 Docker 镜像...
docker build -f deploy/Dockerfile.backend -t %IMAGE_REGISTRY%/%NAMESPACE%/backend:%IMAGE_TAG% .
if errorlevel 1 (
    echo ❌ 后端镜像构建失败
    pause
    exit /b 1
)
echo ✓ 后端镜像构建完成
echo.

REM ---------- 4. 构建前端镜像 ----------
echo [4/6] 构建前端 Docker 镜像...
docker build -f deploy/Dockerfile.frontend -t %IMAGE_REGISTRY%/%NAMESPACE%/frontend:%IMAGE_TAG% .
if errorlevel 1 (
    echo ❌ 前端镜像构建失败
    pause
    exit /b 1
)
echo ✓ 前端镜像构建完成
echo.

REM ---------- 5. 推送镜像 ----------
echo [5/6] 推送后端镜像到仓库...
docker push %IMAGE_REGISTRY%/%NAMESPACE%/backend:%IMAGE_TAG%
if errorlevel 1 (
    echo ❌ 后端镜像推送失败
    pause
    exit /b 1
)
echo ✓ 后端镜像推送完成

echo [5.2/6] 推送前端镜像到仓库...
docker push %IMAGE_REGISTRY%/%NAMESPACE%/frontend:%IMAGE_TAG%
if errorlevel 1 (
    echo ❌ 前端镜像推送失败
    pause
    exit /b 1
)
echo ✓ 前端镜像推送完成
echo.

REM ---------- 6. 清理本地镜像 ----------
echo [6/6] 清理本地构建缓存（可选）...
docker rmi %IMAGE_REGISTRY%/%NAMESPACE%/backend:%IMAGE_TAG%
docker rmi %IMAGE_REGISTRY%/%NAMESPACE%/frontend:%IMAGE_TAG%
echo ✓ 清理完成
echo.

echo ==========================================
echo   ✓ 全部完成！
echo   后端镜像: %IMAGE_REGISTRY%/%NAMESPACE%/backend:%IMAGE_TAG%
echo   前端镜像: %IMAGE_REGISTRY%/%NAMESPACE%/frontend:%IMAGE_TAG%
echo.
echo   下一步:
echo   1. 在服务器上创建 docker-compose-prod.yml
echo   2. 创建 .env 文件配置密码
echo   3. 执行: docker compose -f docker-compose-prod.yml up -d
echo ==========================================
echo.
pause