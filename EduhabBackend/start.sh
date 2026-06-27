#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
if [[ ! -f .env ]] && [[ -f .env.example ]]; then
  cp .env.example .env
fi
if [[ -f .env ]]; then
  set -a
  source .env
  set +a
fi
PORT="${BACKEND_PORT:-8067}"
echo "Starting PostgreSQL + backend on port ${PORT}..."
docker compose up -d --build
if ! command -v curl >/dev/null 2>&1; then
  echo "curl not installed. Containers started. Check:"
  echo "  apt install -y curl"
  echo "  docker compose ps"
  echo "  docker compose logs -f backend"
  exit 0
fi
echo "Waiting for API (up to ~2 min)..."
for i in $(seq 1 40); do
  if curl -sf "http://127.0.0.1:${PORT}/api/user" >/dev/null 2>&1; then
    echo ""
    echo "Ready: http://$(hostname -f 2>/dev/null || echo localhost):${PORT}/api/"
    docker compose ps
    exit 0
  fi
  if [[ "${PORT}" != "8081" ]] && curl -sf "http://127.0.0.1:8081/api/user" >/dev/null 2>&1; then
    echo ""
    echo "Ready on port 8081 (update .env: BACKEND_PORT=8081 or redeploy): http://localhost:8081/api/"
    docker compose ps
    exit 0
  fi
  printf "."
  sleep 3
done
echo ""
echo "Containers are up, but API did not respond on port ${PORT}."
echo "Check logs:"
echo "  docker compose logs --tail 80 backend"
echo "Try:"
echo "  curl http://127.0.0.1:${PORT}/api/user"
echo "  curl http://127.0.0.1:8081/api/user"
docker compose ps
