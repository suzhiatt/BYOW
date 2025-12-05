#!/bin/bash
# Compile script for the BYOW project
# This includes the algs4.jar library in the classpath

cd "$(dirname "$0")/byow"

# Find all Java files
JAVA_FILES=$(find . -name "*.java" -type f | grep -v out | tr '\n' ' ')

# Compile with the library in the classpath
javac -cp "../../../library-sp23-main/*" -d out $JAVA_FILES

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
else
    echo "Compilation failed!"
    exit 1
fi



