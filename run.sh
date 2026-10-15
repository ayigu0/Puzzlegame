#!/usr/bin/env sh
set -eu
cd "$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
mkdir -p out
find src -name '*.java' -print > out/sources.txt
javac -encoding UTF-8 -d out @out/sources.txt
exec java -cp out App "$@"
