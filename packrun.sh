#!/bin/bash

mkdir target

tar cfvz target/www.tar.gz www


cd fsm
cd ./target/universal/stage
tar cfvz ../../../../target/fsm.tar.gz ./

cd -

cd ../mcs
cd ./target/universal/stage
tar cfvz ../../../../target/mcs.tar.gz ./


