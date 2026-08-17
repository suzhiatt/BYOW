#!/bin/zsh
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT"

# Prefer a real JDK over /usr/bin/java stubs
if [[ -z "${JAVA_HOME:-}" ]]; then
  if [[ -d "/Library/Java/JavaVirtualMachines/temurin-22.jdk/Contents/Home" ]]; then
    export JAVA_HOME="/Library/Java/JavaVirtualMachines/temurin-22.jdk/Contents/Home"
  elif [[ -d "/Library/Java/JavaVirtualMachines/jdk-22.jdk/Contents/Home" ]]; then
    export JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk-22.jdk/Contents/Home"
  fi
fi
export PATH="${JAVA_HOME:+$JAVA_HOME/bin:}$PATH"

ALGS4="$ROOT/.idea/libraries/algs4 (2).jar"
LIBDIR="/Users/susanhiatt/Downloads/library-sp23-main"
CP="bin:$ALGS4"
if [[ -d "$LIBDIR" ]]; then
  CP="$CP:$LIBDIR/*"
fi

mkdir -p bin
echo "Compiling..."
javac -proc:none -cp "$CP" -d bin $(find byow -name '*.java')
echo "Starting BYOW..."
exec java -cp "$CP" byow.Core.Main
