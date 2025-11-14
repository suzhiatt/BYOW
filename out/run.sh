#!/bin/bash
# Run script for the BYOW project
# This includes the algs4.jar library in the classpath

cd "$(dirname "$0")/byow"

# Run the Main class with the library in the classpath
java -cp "out:../../../library-sp23-main/*" byow.Core.Main "$@"


