#!/bin/zsh
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT"

# Use JAVA_HOME if it already points at a compiler. Otherwise take the Mac's
# default JDK, then any JDK under the standard install folders, then the JDK
# shipped with Cursor or VS Code's Java extension.
find_jdk() {
  if [[ -n "${JAVA_HOME:-}" && -x "${JAVA_HOME}/bin/javac" ]]; then
    return 0
  fi

  if [[ -x /usr/libexec/java_home ]]; then
    local detected
    detected="$(/usr/libexec/java_home 2>/dev/null || true)"
    if [[ -n "$detected" && -x "$detected/bin/javac" ]]; then
      export JAVA_HOME="$detected"
      return 0
    fi
  fi

  setopt local_options null_glob
  local candidate
  local -a candidates
  candidates=(
    /Library/Java/JavaVirtualMachines/*.jdk/Contents/Home
    "$HOME"/Library/Java/JavaVirtualMachines/*.jdk/Contents/Home
    /opt/homebrew/opt/openjdk*/libexec/openjdk.jdk/Contents/Home
    /usr/local/opt/openjdk*/libexec/openjdk.jdk/Contents/Home
    "$HOME"/.cursor/extensions/redhat.java-*/jre/*
    "$HOME"/.vscode/extensions/redhat.java-*/jre/*
  )

  for candidate in "${candidates[@]}"; do
    if [[ -x "$candidate/bin/javac" ]]; then
      export JAVA_HOME="$candidate"
      return 0
    fi
  done

  echo "No JDK found. Install a JDK, or set JAVA_HOME to a folder that contains bin/javac." >&2
  return 1
}

find_jdk
export PATH="$JAVA_HOME/bin:$PATH"

ALGS4="$ROOT/.idea/libraries/algs4 (2).jar"
LIBDIR="/Users/susanhiatt/Downloads/library-sp23-main"
CP="bin:$ALGS4"
if [[ -d "$LIBDIR" ]]; then
  CP="$CP:$LIBDIR/*"
fi

mkdir -p bin
echo "Compiling with $JAVA_HOME..."
javac -proc:none -cp "$CP" -d bin $(find byow -name '*.java')
echo "COMPILE_OK"

if [[ "${1:-}" == "compile" ]]; then
  exit 0
fi

echo "Starting BYOW..."
exec java -cp "$CP" byow.Core.Main
