/**
 * Created by jianruan on 10/15/15.
 */
public class opNode extends ASTnode {

    private ASTnode leftNode;
    private ASTnode rightNode;


    public opNode (String opcode, ASTnode left, ASTnode right) {
        super("OP", opcode);
        leftNode = left;
        rightNode =right;
    }

    public String getOpcode () {return getValue();}
    public String getResult () {return result;}

    public void CodeAndResult()
    {

        String thisOperation = getValue();

        //case 1: simplest code form
        boolean bool1 = leftNode.getType().equals("INT");
        boolean bool2 = rightNode.getType().equals("INT");
        boolean bool3 = leftNode.getType().equals("FLOAT");
        boolean bool4 = rightNode.getType().equals("FLOAT");
        boolean bool5 = leftNode.getType().equals("VAR");
        boolean bool6 = rightNode.getType().equals("VAR");

        if (bool1 && bool2) //two number
        {
            if(thisOperation.equals("+")) {
                code = "ADDI";
            }

            if(thisOperation.equals("-")) {
                code = "SUBI";
            }

            if(thisOperation.equals("*")) {
                code = "MULTI";
            }

            if(thisOperation.equals("/")) {
                code = "DIVI";
            }
            /*if(leftValue.contains("."))//left is a float
            {
                if(rightValue.contains("."))
                {
                    float rel = Float.parseFloat(leftValue) * Float.parseFloat(rightValue);
                    result = Float.toString(rel);
                }
            } else {//left is an int
                if(!rightValue.contains("."))
                {
                    int rel = Integer.parseInt(leftValue) * Integer.parseInt(rightValue);
                    result = Integer.toString(rel);
                }
            }
            //since it's just a number
            code = result;*/
        }
        else if (bool3 && bool4) //two variables
        {
            if(thisOperation.equals("+")) {
                code = "ADDF";
            }

            if(thisOperation.equals("-")) {
                code = "SUBF";
            }

            if(thisOperation.equals("*")) {
                code = "MULTF";
            }

            if(thisOperation.equals("/")) {
                code = "DIVF";
            }
        }
        else if((bool1&&bool4) || (bool2&&bool3))
        {
            if(thisOperation.equals("+")) {
                code = "ADDF";
            }

            if(thisOperation.equals("-")) {
                code = "SUBF";
            }

            if(thisOperation.equals("*")) {
                code = "MULTF";
            }

            if(thisOperation.equals("/")) {
                code = "DIVF";
            }
        }
        else if(bool5&&bool6) {
            String leftValue = leftNode.getValue();
            String rightValue = rightNode.getValue();

            String lefttype = getVarType(leftValue);
            String righttype = getVarType(righttValue);

            if(lefttype.equals("INT") && righttype.equals("INT")) {
                if(thisOperation.equals("+")) {
                code = "ADDI";
                }

                if(thisOperation.equals("-")) {
                    code = "SUBI";
                }

                if(thisOperation.equals("*")) {
                    code = "MULTI";
                }

                if(thisOperation.equals("/")) {
                    code = "DIVI";
                }        
            }

            if(lefttype.equals("FLOAT") && righttype.equals("FLOAT")) {
                if(thisOperation.equals("+")) {
                code = "ADDF";
                }

                if(thisOperation.equals("-")) {
                    code = "SUBF";
                }

                if(thisOperation.equals("*")) {
                    code = "MULTF";
                }

                if(thisOperation.equals("/")) {
                    code = "DIVF";
                }        
            }

            if((lefttype.equals("FLOAT") && righttype.equals("INT")) || (lefttype.equals("INT") && righttype.equals("FLOAT"))) {
                if(thisOperation.equals("+")) {
                code = "ADDF";
                }

                if(thisOperation.equals("-")) {
                    code = "SUBF";
                }

                if(thisOperation.equals("*")) {
                    code = "MULTF";
                }

                if(thisOperation.equals("/")) {
                    code = "DIVF";
                }        
            }   
        }

        else if(bool1&&bool6) {
            String rightValue = rightNode.getValue();
            String righttype = getVarType(righttValue);

            if(righttype.equals("INT")) {
                if(thisOperation.equals("+")) {
                code = "ADDI";
                }

                if(thisOperation.equals("-")) {
                    code = "SUBI";
                }

                if(thisOperation.equals("*")) {
                    code = "MULTI";
                }

                if(thisOperation.equals("/")) {
                    code = "DIVI";
                }
            }
            else if(righttype.equals("FLOAT")) {
                if(thisOperation.equals("+")) {
                code = "ADDF";
                }

                if(thisOperation.equals("-")) {
                    code = "SUBF";
                }

                if(thisOperation.equals("*")) {
                    code = "MULTF";
                }

                if(thisOperation.equals("/")) {
                    code = "DIVF";
                }
            }
        }
        else if(bool3&&bool6) {
            String rightValue = rightNode.getValue();
            String righttype = getVarType(righttValue);

            if(righttype.equals("INT")) {
                if(thisOperation.equals("+")) {
                code = "ADDF";
                }

                if(thisOperation.equals("-")) {
                    code = "SUBF";
                }

                if(thisOperation.equals("*")) {
                    code = "MULTF";
                }

                if(thisOperation.equals("/")) {
                    code = "DIVF";
                }
            }
            else if(righttype.equals("FLOAT")) {
                if(thisOperation.equals("+")) {
                code = "ADDF";
                }

                if(thisOperation.equals("-")) {
                    code = "SUBF";
                }

                if(thisOperation.equals("*")) {
                    code = "MULTF";
                }

                if(thisOperation.equals("/")) {
                    code = "DIVF";
                }
            }
        }

        else if(bool5&&bool2) {
            String leftValue = leftNode.getValue();
            String lefttype = getVarType(righttValue);

            if(lefttype.equals("INT")) {
                if(thisOperation.equals("+")) {
                code = "ADDI";
                }

                if(thisOperation.equals("-")) {
                    code = "SUBI";
                }

                if(thisOperation.equals("*")) {
                    code = "MULTI";
                }

                if(thisOperation.equals("/")) {
                    code = "DIVI";
                }
            }
            else if(lefttype.equals("FLOAT")) {
                if(thisOperation.equals("+")) {
                code = "ADDF";
                }

                if(thisOperation.equals("-")) {
                    code = "SUBF";
                }

                if(thisOperation.equals("*")) {
                    code = "MULTF";
                }

                if(thisOperation.equals("/")) {
                    code = "DIVF";
                }
            }
        }

        else if(bool5&&bool4) {
            String leftValue = leftNode.getValue();
            String lefttype = getVarType(righttValue);

            if(lefttype.equals("INT")) {
                if(thisOperation.equals("+")) {
                code = "ADDF";
                }

                if(thisOperation.equals("-")) {
                    code = "SUBF";
                }

                if(thisOperation.equals("*")) {
                    code = "MULTF";
                }

                if(thisOperation.equals("/")) {
                    code = "DIVF";
                }
            }
            else if(lefttype.equals("FLOAT")) {
                if(thisOperation.equals("+")) {
                code = "ADDF";
                }

                if(thisOperation.equals("-")) {
                    code = "SUBF";
                }

                if(thisOperation.equals("*")) {
                    code = "MULTF";
                }

                if(thisOperation.equals("/")) {
                    code = "DIVF";
                }
            }
        }
    }




}
