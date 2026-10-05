#!/usr/bin/env bash
# Load every sample configuration into a running mock-api backend.
# Usage: ./load-samples.sh [BASE_URL]   (default: http://localhost:8080)
# Re-running is safe: POST /config overwrites a configuration with the same name.
set -euo pipefail

BASE_URL="${1:-http://localhost:8080}"
DIR="$(cd "$(dirname "$0")" && pwd)/configurations"

for file in "$DIR"/*.json; do
  name="$(basename "$file" .json)"
  status="$(curl -s -o /dev/null -w '%{http_code}' -X POST "$BASE_URL/config" \
    -H 'Content-Type: application/json' --data-binary "@$file")"
  echo "$status  $name"
done
