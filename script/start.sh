#!/bin/bash

# ======================= 应用配置 (请修改这里) =======================

# JAR包文件名
APP_NAME="simple-1.0.0.jar"

# JVM 启动参数
JAVA_OPTS="-Xms256m -Xmx1024m"

# Spring Boot 启动参数
# 需要修改 server.port、spring.datasource.*、file.upload-path 参数
APP_ARGS=(
  --spring.profiles.active=prod
  --server.port=8080
  --spring.datasource.url="jdbc:mysql://prod-db-host:3306/simple_api_prod_db?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai"
  --spring.datasource.username="prod_user"
  --spring.datasource.password="YOUR_SECURE_PROD_PASSWORD"
)

# ======================= 脚本核心 (一般无需修改) =======================

# PID 文件路径
PID_FILE="app.pid"

is_running() {
  if [ -f "$PID_FILE" ]; then
    PID=$(cat "$PID_FILE")
    if kill -0 $PID > /dev/null 2>&1; then
      return 0
    fi
  fi
  return 1
}

start() {
  if is_running; then
    echo "错误：应用 ($APP_NAME) 已经在运行中 (PID: $(cat $PID_FILE))。"
    exit 1
  fi

  echo "正在启动应用: $APP_NAME ..."

  nohup java ${JAVA_OPTS} -jar ${APP_NAME} "${APP_ARGS[@]}" > console.log 2>&1 &

  echo $! > "$PID_FILE"

  sleep 2
  if is_running; then
    echo "应用启动成功 (PID: $(cat $PID_FILE))。"
  else
    echo "错误：应用启动失败！请检查 console.log 文件获取错误信息。"
    rm -f "$PID_FILE"
  fi
}

# 停止函数
stop() {
  if ! is_running; then
    echo "提示：应用 ($APP_NAME) 未在运行。"
    return
  fi

  PID=$(cat "$PID_FILE")
  echo "正在停止应用 (PID: $PID)..."
  kill $PID

  for i in {1..10}; do
    if ! is_running; then
      rm -f "$PID_FILE"
      echo "应用已成功停止。"
      return
    fi
    sleep 1
  done

  echo "警告：应用未能停止，将强制关闭 (kill -9)..."
  kill -9 $PID
  rm -f "$PID_FILE"
  echo "应用已被强制停止。"
}

case "$1" in
  start)
    start
    ;;
  stop)
    stop
    ;;
  restart)
    stop
    start
    ;;
  status)
    if is_running; then
      echo "状态：应用 ($APP_NAME) 正在运行 (PID: $(cat $PID_FILE))。"
    else
      echo "状态：应用 ($APP_NAME) 未在运行。"
    fi
    ;;
  *)
    echo "用法: $0 {start|stop|restart|status}"
    exit 1
    ;;
esac