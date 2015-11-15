#!/bin/bash
rm test_if.* test_for.* test_adv.* step4_testcase.* step4_testcase2.*
make clean && make

java -cp lib/antlr.jar:classes/ Micro testcases/input/step4_testcase.micro > step4_testcase.out
java -cp lib/antlr.jar:classes/ Micro testcases/input/step4_testcase2.micro > step4_testcase2.out
java -cp lib/antlr.jar:classes/ Micro testcases/input/test_if.micro > test_if.out 
java -cp lib/antlr.jar:classes/ Micro testcases/input/test_for.micro > test_for.out
java -cp lib/antlr.jar:classes/ Micro testcases/input/test_adv.micro > test_adv.out

rm tiny
g++ -o tiny tinyNew.C 
chmod +x tiny

tiny step4_testcase.out < testcases/input/step4_testcase.input > step4_testcase.result
tiny step4_testcase2.out < testcases/input/step4_testcase2.input > step4_testcase2.result
tiny test_if.out < testcases/input/test_if.input > test_if.result
tiny test_for.out < testcases/input/test_for.input > test_for.result
tiny test_adv.out < testcases/input/test_adv.input > test_adv.result

tiny testcases/output/step4_testcase.out < testcases/input/step4_testcase.input > step4_testcase.answer
tiny testcases/output/step4_testcase2.out < testcases/input/step4_testcase2.input > step4_testcase2.answer
tiny testcases/output/test_if.out < testcases/input/test_if.input > test_if.answer
tiny testcases/output/test_for.out < testcases/input/test_for.input > test_for.answer
tiny testcases/output/test_adv.out < testcases/input/test_adv.input > test_adv.answer