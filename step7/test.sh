#!/bin/bash
rm fibonacci2.* factorial2.* fma.*
make clean && make

java -cp lib/antlr.jar:classes/ Micro testcases/input/fma.micro > fma.out
java -cp lib/antlr.jar:classes/ Micro testcases/input/factorial2.micro > factorial2.out
java -cp lib/antlr.jar:classes/ Micro testcases/input/fibonacci2.micro > fibonacci2.out

rm tiny
g++ -o tiny tinyNew.C 
chmod +x tiny

./tiny fma.out < ./testcases/input/fma.input > fma.result
./tiny factorial2.out < ./testcases/input/factorial2.input > factorial2.result
./tiny fibonacci2.out < ./testcases/input/fibonacci2.input > fibonacci2.result


./tiny ./testcases/output/fma.out < ./testcases/input/fma.input > fma.answer
./tiny ./testcases/output/factorial2.out < ./testcases/input/factorial2.input > factorial2.answer
./tiny ./testcases/output/fibonacci2.out < ./testcases/input/fibonacci2.input > fibonacci2.answer