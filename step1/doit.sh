clear
make clean && make compiler
java -cp lib/antlr.jar:classes/ Micro
echo "diff fibonacci"
diff fibonacciOut.txt fibonacci.out
echo "diff nested"
diff nestedOut.txt nested.out
echo "diff loop"
diff loopOut.txt loop.out
echo "diff sqrt"
diff sqrtOut.txt sqrt.out

