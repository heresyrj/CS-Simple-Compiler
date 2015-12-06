#!/bin/bash
rm fibonacci2.* factorial2.* fma.* step4*  test_*
make clean && make

java -cp lib/antlr.jar:classes/ Micro testcases/input/fma.micro > fma.myout
java -cp lib/antlr.jar:classes/ Micro testcases/input/factorial2.micro > factorial2.myout
java -cp lib/antlr.jar:classes/ Micro testcases/input/fibonacci2.micro > fibonacci2.myout
java -cp lib/antlr.jar:classes/ Micro testcases/input/step4_testcase.micro > step4_testcase.myout
java -cp lib/antlr.jar:classes/ Micro testcases/input/step4_testcase2.micro > step4_testcase2.myout
java -cp lib/antlr.jar:classes/ Micro testcases/input/step4_testcase3.micro > step4_testcase3.myout
java -cp lib/antlr.jar:classes/ Micro testcases/input/test_adv.micro > test_adv.myout
java -cp lib/antlr.jar:classes/ Micro testcases/input/test_expr.micro > test_expr.myout
java -cp lib/antlr.jar:classes/ Micro testcases/input/test_if.micro > test_if.myout
java -cp lib/antlr.jar:classes/ Micro testcases/input/test_for.micro > test_for.myout


# rm tiny
# g++ -o tiny tinyNew.C 
# chmod +x tiny

# rm tiny
# g++ -o tiny tiny4regs.C 
# chmod +x tiny

./tiny test_if.myout < ./testcases/input/test_if.input > test_if.result
./tiny test_for.myout < ./testcases/input/test_for.input > test_for.result
./tiny test_expr.myout > test_expr.result
./tiny test_adv.myout < ./testcases/input/test_adv.input > test_adv.result

./tiny ./testcases/output/test_if.out < ./testcases/input/test_if.input > test_if.answer
./tiny ./testcases/output/test_for.out < ./testcases/input/test_for.input > test_for.answer
./tiny ./testcases/output/test_expr.out > test_expr.answer
./tiny ./testcases/output/test_adv.out < ./testcases/input/test_adv.input > test_adv.answer




./tiny step4_testcase.myout < ./testcases/input/step4_testcase.input > step4_testcase.result
./tiny step4_testcase2.myout < ./testcases/input/step4_testcase2.input > step4_testcase2.result
./tiny step4_testcase3.myout > step4_testcase3.result

./tiny ./testcases/output/step4_testcase.out < ./testcases/input/step4_testcase.input > step4_testcase.answer
./tiny ./testcases/output/step4_testcase2.out < ./testcases/input/step4_testcase2.input > step4_testcase2.answer
./tiny ./testcases/output/step4_testcase3.out > step4_testcase3.answer



./tiny fma.myout < ./testcases/input/fma.input > fma.result
./tiny factorial2.myout < ./testcases/input/factorial2.input > factorial2.result
./tiny fibonacci2.myout < ./testcases/input/fibonacci2.input > fibonacci2.result

./tiny ./testcases/output/fma.out < ./testcases/input/fma.input > fma.answer
./tiny ./testcases/output/factorial2.out < ./testcases/input/factorial2.input > factorial2.answer
./tiny ./testcases/output/fibonacci2.out < ./testcases/input/fibonacci2.input > fibonacci2.answer





