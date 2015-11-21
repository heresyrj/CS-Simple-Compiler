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
    public Symbol getLastSymbol()
    {
        int num = symbolList.size();
        return getSymbol(num - 1);
    }
    public String getName() {return name;}

    public void addSymbol(Symbol s) {

        boolean containSame = false;

        //check for duplicate before add
        for (Symbol sym : symbolList) {
            boolean bool1 = sym.sym_getName().equals(s.sym_getName());
            boolean bool2 = sym.sym_getType().equals(s.sym_getType());
            if (bool1 && bool2) containSame = true;
        }

        if(containSame){
            System.out.println("DECLARATION ERROR " + s.sym_getName());
            System.exit(9);
        } else {
            symbolList.add(s);

            /**also need to add to hastMap for later use
             also it's faster*/
            generalUtils.addSymboltoTable(s.sym_getName(), s);
        }
    }

    public Scope getParentScope() {
        return parent;
    }

    public ArrayList<Symbol> getSymbolList() { return symbolList;}

    public void printSymbols() {

        for (Symbol s : symbolList) {
            String type = s.sym_getType();
            String name = s.sym_getName();

            if (type.equals("PROGRAM")) {
                //part1
                System.out.print("Symbol table GLOBAL\n");
                //part2
                Symbol_PROGRAM ps = (Symbol_PROGRAM) s;
                //
                ps.getOwnScope().printSymbols();

            } else if (type.equals("FUNCTION")) {
                System.out.println();
                //part1
                System.out.print("Symbol table " + name + "\n");
                //part2
                Symbol_FUCNTION fs = (Symbol_FUCNTION) s;
                fs.getOwnScope().printSymbols();

            } else if (type.equals("BLOCK")) {
                System.out.println("\n");
                System.out.print("Symbol table " + name);
                Symbol_Block bs = (Symbol_Block) s;
                bs.getOwnScope().printSymbols();
            } else {
                System.out.print("name ");
                System.out.print(name + " ");
                System.out.print("type ");
                System.out.print(type + " ");
            }

            if (type.equals("STRING")) {
                String value = ((Symbol_STRING) s).sym_getStr();
                System.out.print("value ");
                System.out.print(value);

            }
            System.out.println();

            //reset the string values to avoid infinite loop
            type = "";
            name = "";
        }

    }

}
