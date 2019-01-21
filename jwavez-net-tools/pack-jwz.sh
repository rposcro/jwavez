#!/bin/bash
mkdir -p $1/target/jwz
cp $1/target/jwavez-net-tools.jar $1/target/jwz
cp $1/src/main/bash/* $1/target/jwz
tar -c -z -f $1/target/jwz.tar.gz -C target jwz
