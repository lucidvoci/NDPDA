package ndpdar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import ndpdar.PDSymbol.Type;

/**
 *
 * @author luciedvorakova
 */
public class NDPDA {
    final private PDSymbol BOTTOM_SYMBOL = new PDSymbol(Type.BOTTOM, null);
    private HashSet<String> states = new HashSet();
    private HashSet<PDSymbol> inputAlph = new HashSet();
    private HashSet<PDSymbol> nonInputAlph = new HashSet();
    private List<Rule> expansionRules = new ArrayList();
    private List<Rule> popRules = new ArrayList();
    
    
    private HashSet<String> allSymbols = new HashSet();
    private HashSet<String> nonInputSymbols = new HashSet();
    
    final private String startState;
    final private PDSymbol startPDSym;
    final private List<String> endStates;
    
    final private DeepPD dpda;
    
    private Boolean settingDone = false;
    
    public NDPDA(String startState, String startPDSym, List<String> endStates){
        this.startState = startState;
        this.startPDSym = new PDSymbol(Type.NONTERMINAL, startPDSym);
        this.endStates = endStates;
        
        dpda = new DeepPD(this.startPDSym);
        nonInputAlph.add(BOTTOM_SYMBOL);
    }

    public class Rule{
        final int depth;
        final String startState;
        final String endState;
        final PDSymbol fromSym;
        final List<PDSymbol> toSymbols = new LinkedList();
        final char[] toSymbolString;
        
        int numOfNonInputSym = 0;
        
        public Rule(int depth, String startState, String endState, String fromSym, String toSymbolsString) {
            this.depth = depth;
            this.startState = startState;
            this.endState = endState;
            
            if(fromSym.equals("#")) {
                this.fromSym = BOTTOM_SYMBOL;
                this.toSymbolString = toSymbolsString.toCharArray();
            }
            else if (toSymbolsString == null || toSymbolsString.equals("")) {
                this.fromSym = new PDSymbol(Type.TERMINAL, fromSym);
                this.toSymbolString = null;
            }
            else {
                this.fromSym = new PDSymbol(Type.NONTERMINAL, fromSym);
                this.toSymbolString = toSymbolsString.toCharArray();
            }
            
            numOfNonInputSym = 0;
        }  
    } 
    
    /**
     * Adding individual rule. 
     * @param depth If no depth is specifi the depth is set to 1
     * @param startState From state
     * @param endState To state
     * @param fromSym Non-input symbol, that will be expanded
     * @param toSymbols Symbols replacing the expanded non-iput symbol
    */
    public void addRule(int depth, String startState,  String fromSym, String endState, String toSymbols) {
        Rule rule = new Rule(depth, startState, endState, fromSym, toSymbols);
        insertValues(rule);
    }
    
    public void addRule(String startState,  String fromSym, String endState, String toSymbols) {
        Rule rule = new Rule(1, startState, endState, fromSym, toSymbols);
        insertValues(rule);
    }
    
    /**
     * Adding individual pop rule. Pop rules are perfomed after all expantion are made
     * @param startState From state
     * @param endState To state
     * @param fromSym Input symbol, that will be poped
    */
    public void addRule (String startState, String fromSym , String endState) {
        Rule rule = new Rule(1, startState, endState, fromSym, null);
        insertValues(rule);
    }
    
    private void insertValues(Rule rule) {
        states.add(rule.startState);
        states.add(rule.endState);
        
        if(rule.toSymbolString != null) {
            nonInputSymbols.add(rule.fromSym.getName());
            nonInputAlph.add(rule.fromSym);
            for(char sym : rule.toSymbolString) {
                allSymbols.add(String.valueOf(sym));
            }
            expansionRules.add(rule);
        }
        else {
            inputAlph.add(rule.fromSym);
            popRules.add(rule);
        }

    }
    
    /**
    * Before parsing using automaton, all symbols on the right
    * side of the rules are coverted to PDSymbol. 
    * This operation is done only first time the automaton is run.
    */
    private void automatSettingDone() {
        for(Rule rule : expansionRules){
            for(char sym : rule.toSymbolString){
                String symString = String.valueOf(sym);
                if(nonInputSymbols.contains(symString)) {
                    rule.toSymbols.add(new PDSymbol(Type.NONTERMINAL, symString));
                    rule.numOfNonInputSym++;
                }
                else if(sym == '#') {
                    rule.toSymbols.add(BOTTOM_SYMBOL);
                    rule.numOfNonInputSym++;
                }
                else {
                    rule.toSymbols.add(new PDSymbol(Type.TERMINAL, symString));
                }
            }
        }
        settingDone = true;
    }
    
    public Boolean simulate(String input) {
        if (!settingDone){
            automatSettingDone();
        }
        
        //
        // TODO
        //
        
        return null;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("M = (Q, \u03A3, \u0393, R, ").append(startState).append(", ").append(startPDSym.getName()).append(", F)\n");
        sb.append("Q = {");
        String prefix = "";
        for(String q : states){
            sb.append(prefix);
            prefix = ", ";
            sb.append(q);
        }
        sb.append("}\n\u03A3 = {");
        prefix = "";
        for(PDSymbol a : inputAlph){
            sb.append(prefix);
            prefix = ", ";
            sb.append(a.getName());
        }
        sb.append("}\n\u0393 = {");
        prefix = "";
        for(PDSymbol a : inputAlph){
            sb.append(prefix);
            prefix = ", ";
            sb.append(a.getName());
        }
        for(PDSymbol a : nonInputAlph){
            sb.append(prefix);
            prefix = ", ";
            sb.append(a.getName());
        }
        sb.append("}\nR = {\n");
        for(Rule rule : expansionRules){
            sb.append("\t").append(rule.depth).append(rule.startState).append(rule.fromSym.getName());
            sb.append(" -> ").append(rule.endState).append(rule.toSymbolString).append("\n");
        }
        for(Rule rule : popRules){
            sb.append("\t").append(rule.depth).append(rule.startState).append(rule.fromSym.getName());
            sb.append(" -> ").append(rule.endState).append("\n");
        }
        sb.append("}\nF = {");
        prefix = "";
        if(endStates != null){
            for(String q : endStates){
                sb.append(prefix);
                prefix = ", ";
                sb.append(q);
            }
        }
         sb.append("}");
        return sb.toString();
    }
    
}
