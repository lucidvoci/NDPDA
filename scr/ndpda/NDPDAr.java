package ndpdar;

import static java.lang.System.out;
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
    
    public NDPDAr(NDPDA regAuto){
        super(regAuto.getN(), regAuto.getStartState() + regAuto.getStartPDSym().getName(), "$", regAuto.getEndStates());
        
        for(Rule rule : regAuto.getExpansionRules()) {
            convertRule(rule, regAuto.getNonInputSymbols());
        }
        //out.println(this.toString());
    }

    private void convertRule(Rule rule, HashSet<String> nonInputSymbols){
        List<String> u = new LinkedList();
        List<String> v = new LinkedList();
        
        u.addAll(allPosNonInput(0, rule.depth - 1, "", nonInputSymbols));
        for(int i = 0; i <= super.getN() - rule.depth - 1; i++) {
            v.addAll(allPosNonInput(0, i, "", nonInputSymbols));
        }
        //out.println("Converting rule: " + rule);

        String specialSym;
        if(rule.fromSym.getName().equals("#")){
            specialSym = BOTTOM_SYMBOL.getName();
        }
        else {
            specialSym = SPECIAL_SYMBOL.getName();
        }
        
        for(String posU : u) {
            String nonInput = getNonInput(rule.toSymbols);
            String input = getInput(rule.toSymbols);
            
            for(String posV : v){
                String oldStack = posU + rule.fromSym.getName() + posV;
                String newStack = posU + nonInput + posV;
                //out.print("\t" + rule.depth + "<" + rule.startState + ";" + oldStack + ">" + specialSym + " -> ");
                //out.println("<" + rule.endState + ";" + newStack + ">" + input);

                super.addRule(rule.depth, rule.startState + oldStack , specialSym,  rule.endState + newStack, input);
                
                if(super.getEndStates().contains(rule.endState)){
                    super.addEndState(rule.endState + newStack);
                }
            }
        }
        
    }

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

    private String getNonInput(List<PDSymbol> symbols) {
        StringBuilder sb = new StringBuilder();
        for(PDSymbol sym : symbols){
            if(sym.getType() == Type.NONTERMINAL) {
                sb.append(sym.getName());
            }
        }
        return sb.toString();
    }

    private String getInput(List<PDSymbol> symbols) {
        StringBuilder sb = new StringBuilder();
        for(PDSymbol sym : symbols){
            if(sym.getType() == Type.NONTERMINAL) {
                sb.append(SPECIAL_SYMBOL.getName());
            }
            else {
                sb.append(sym.getName());
            }
        }
        return sb.toString();
    }
}
