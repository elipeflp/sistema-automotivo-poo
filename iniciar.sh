#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"
exec java -jar dist/sistema-automotivo-1.0.0.jar
