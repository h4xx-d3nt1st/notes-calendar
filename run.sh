#!/usr/bin/env bash
set -euo pipefail

wait_for_start() {
  # ждём появления строки о старте до 15 секунд
  for i in {1..15}; do
    if grep -q "Tomcat started on port(s): 8080" app.log 2>/dev/null; then
      echo "✅ Сервер поднялся на http://localhost:8080"
      return 0
    fi
    sleep 1
  done
  echo "⚠️  Сервер не поднялся за 15с. Последние строки лога:"
  tail -n 80 app.log || true
  return 1
}

case "${1:-restart}" in
  build)
    mvn -q clean package
    ;;
  start)
    JAR=$(ls -1 target/notes-calendar-*.jar | head -n1 2>/dev/null || true)
    if [[ -z "${JAR:-}" || ! -f "$JAR" ]]; then
      echo "JAR не найден. Сначала ./run.sh build"
      exit 1
    fi
    # если уже запущено — останавливаем
    [[ -f app.pid ]] && kill "$(cat app.pid)" 2>/dev/null || true
    lsof -ti :8080 | xargs -r kill 2>/dev/null || true

    : > app.log
    nohup java -jar "$JAR" >> app.log 2>&1 & echo $! > app.pid
    wait_for_start
    ;;
  stop)
    if [[ -f app.pid ]]; then
      kill "$(cat app.pid)" 2>/dev/null || true
      rm -f app.pid
    fi
    lsof -ti :8080 | xargs -r kill 2>/dev/null || true
    echo "⏹️ Сервер остановлен"
    ;;
  logs)
    tail -f app.log
    ;;
  restart)
    "$0" stop
    "$0" build
    "$0" start
    ;;
  *)
    echo "Usage: $0 {build|start|stop|restart|logs}"
    exit 2
    ;;
esac
