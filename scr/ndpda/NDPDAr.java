package ndpdar;

import static java.lang.System.out;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import ndpdar.PDSymbol.Type;

/**
 * n-expandable Deep Pushdown Automaton with reduced pushdown alphabet. 
 * The automat only has 2 symbols in the pushdown alphabet. # as a bottom of the 
 * PD and one other special symbol $.
 * 
 * This reduced nDPDA hase same power as regulat nDPDA and the class 
 * includes automatic conversion of them.
 * 
 * @author luciedvorakova
 */
public class NDPDAr extends NDPDA{
    final private PDSymbol BOTTOM_SYMBOL = new PDSymbol(Type.BOTTOM, null);
    final private PDSymbol SPECIAL_SYMBOL = new PDSymbol(Type.NONTERMINAL, "$");
    
    final private HashSet<String> nonInputSymbols = new HashSet();
    
    public NDPDAr(int n, int r, String startState, String startPDSym, HashSet<String> endStates){
        super(n, startState, startPDSym, endStates);
    }
    
    /**
    * Construct that takes as input regular n-expandable DPDA.
    * 
    * @param regAuto NDPDA automat that will be converted
    */
    public NDPDAr(NDPDA regAuto){
        super(regAuto.getN(), regAuto.getStartState() + regAuto.getStartPDSym().getName(), "$", regAuto.getEndStates());
        
        for(Rule rule : regAuto.getExpansionRules()) {
            convertRule(rule, regAuto.getNonInputSymbols());
        }
        //out.println(this.toString());
    }

    /**
    * Converts rules od n-expandable DPDA to rules of the reduced DPDA.
    * Basic rule of conversion:
    *   If mqA -> pv is a rule, where m is depth, q, p are states, A is
    * non-input symbol and v is string of non-input and input symbols,
    * then add m(q;uAz)$ -> (p;uf(v)z)g(v) to reduced DPDA where u,z are strings 
    * of non-input symbols, m - 1 = |u|, n - m - 1 >= |v|, f() equals function 
    * getNonInput() and g() equals function getInput().
    * 
    * @param rule that will be converted
    * @param nonInputSymbols non-input symbols of original automaton
    */
    private void convertRule(Rule rule, HashSet<String> nonInputSymbols){
        List<String> u = new LinkedList();
        List<String> v = new LinkedList();
        
        u.addAll(allPosNonInput(0, rule.depth - 1, "", nonInputSymbols));
        for(int i = 0; i <= super.getN() - rule.depth - 1; i++) {
            v.addAll(allPosNonInput(0, i, "", nonInputSymbols));
        }
        out.println("Converting rule: " + rule);
        String specialSym;
        if(rule.fromSym.getName().equals("#")){
            specialSym = BOTTOM_SYMBOL.getName();
        }
        else {
            specialSym = SPECIAL_SYMBOL.getName();
        }
        
        for(String posU : u) {
            String nonInput = getNonInput(rule.toSymbols);
            ArrayList<String> input = getInput(rule.toSymbols);
            
            for(String posV : v){
                String oldStack = posU + rule.fromSym.getName() + posV;
                String newStack = posU + nonInput + posV;
                out.print("\t" + rule.depth + "<" + rule.startState + ";" + oldStack + ">" + specialSym + " -> ");
                out.println("<" + rule.endState + ";" + newStack + ">" + input);

                super.addRule(rule.depth, rule.startState + oldStack , specialSym,  rule.endState + newStack, input);
                
                if(super.getEndStates().contains(rule.endState)){
                    super.addEndState(rule.endState + newStack);
                }
            }
        }
        
    }
    
    /**
    * Creates all posible strings of certain lenght. Used
    * to create all combination of non-input symbols during
    * rule conversion
    * @param from lenght of current string
    * @param to max lenght of string
    * @param head already build string
    * @param tail set of symbols used to create string
    * @return all possible strings
    */
    private List<String> allPosNonInput(int from, int to, String head, HashSet<String> tail) {
        List<String> pos = new LinkedList();
        if(from == to){
            return Arrays.asList(head);
        }
        for (String nonInput : tail){
            pos.addAll(allPosNonInput(from + 1, to, head + nonInput, tail));
        }
        return pos;
    }

    /**
    * Converts list of symbols that are both non-input and input
    * to string of non-input symbols. Excluding #.
    * @param symbols list of symbols
    * @return String of non-input symbols
    */
    private String getNonInput(List<PDSymbol> symbols) {
        StringBuilder sb = new StringBuilder();
        for(PDSymbol sym : symbols){
            if(sym.getType() == Type.NONTERMINAL) {
                sb.append(sym.getName());
            }
        }
        return sb.toString();
    }

    /**
    * Converts list of symbols that are both non-input and input
    * to string of nput symbols. Including #.
    * @param symbols list of symbols
    * @return String of input symbols
    */
    private ArrayList<String> getInput(List<PDSymbol> symbols) {
        ArrayList<String> al = new ArrayList();
        for(PDSymbol sym : symbols){
            if(sym.getType() == Type.NONTERMINAL) {
                al.add(SPECIAL_SYMBOL.getName());
            }
            else {
                al.add(sym.getName());
            }
        }
        return al;
    }
}
