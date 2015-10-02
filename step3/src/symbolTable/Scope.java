package symbolTable;

import java.util.ArrayList;

/**
 * Created by jianruan on 9/19/15.
 */
public class Scope {


    //use ArrayList to store symbols within the scope
    private ArrayList<Symbol> symbolList;
    private String name;
    private Scope parent;

    public Scope(String name, Scope parent) {
        this.name = name;
        this.parent = parent;
        symbolList = new ArrayList<>();
    }

    public Symbol getSymbol(int i) {
        return symbolList.get(i);
    }

    public void addSymbol(Symbol s) {
        symbolList.add(s);
    }

    public Scope getParentScope() {
        return parent;
    }


    public void printSymbols() {

        for (Symbol s : symbolList) {
            String type = s.sym_getType();
            String name = s.sym_getName();

            if (type.equals("PROGRAM")) {
                //part1
                System.out.print("Symbol table Global");
                //part2
                programSymbol ps = (programSymbol) s;
                //
                ps.getOwnScope().printSymbols();

            } else if (type.equals("FUNCTION")) {
                //part1
                System.out.print("Symbol table " + name);
                //part2
                funcSymbol fs = (funcSymbol) s;
                fs.sym_getParentScope().printSymbols();
                //part3
                System.out.println();//add one empty line

            } else if (type.equals("BLOCK")) {
                //to be implemented


            } else {
                System.out.print("name ");
                System.out.print(name + " ");
                System.out.print("type ");
                System.out.print(type + " ");
            }

            if (type.equals("STRING")) {
                String value = ((strSymbol) s).sym_getStr();
                System.out.print("value ");
                System.out.print(value);
            }
            System.out.println();
        }

    }

}
