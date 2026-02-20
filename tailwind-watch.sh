#!/bin/bash
PROJECT_ROOT="$(cd "$(dirname "$0")" && pwd)"
# PROJECT_ROOT="$(cd "$(dirname "$0")" && pwd)"

INPUT="$PROJECT_ROOT/src/main/resources/static/css/tailwind.input.css"
OUTPUT="$PROJECT_ROOT/src/main/resources/static/css/tailwind.css"
CLI="$PROJECT_ROOT/tailwindcss"

# CLI 파일 존재 여부 확인
if [ ! -f "$CLI" ]; then
    echo "❌ tailwindcss CLI가 없습니다. 아래 명령어로 다운로드 해주세요."
    echo ""
    echo "  # M1/M2/M3 Mac (arm64)"
    echo "  curl -sLO https://github.com/tailwindlabs/tailwindcss/releases/latest/download/tailwindcss-macos-arm64"
    echo "  chmod +x tailwindcss-macos-arm64 && mv tailwindcss-macos-arm64 tailwindcss"
    echo ""
    echo "  # Intel Mac (x64)"
    echo "  curl -sLO https://github.com/tailwindlabs/tailwindcss/releases/latest/download/tailwindcss-macos-x64"
    echo "  chmod +x tailwindcss-macos-x64 && mv tailwindcss-macos-x64 tailwindcss"
    exit 1
fi

echo "==== TAILWIND-CLI-WATCH INIT ========"
echo "| input  : $INPUT"
echo "| output : $OUTPUT"
echo "====================================="

"$CLI" -i "$INPUT" -o "$OUTPUT" --watch