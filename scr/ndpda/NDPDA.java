package ndpdar;

import static java.lang.System.err;
import static java.lang.System.out;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import ndpdar.PDSymbol.Type;

/**
 * n-expandable Deep Pushdown Automaton. Automaton that can
 * be have maximum of n non-input symbols on pushdown (PD)
 * (including bottom symbol #), non-input can be expended in 
 * certain depth (naturally less than n).
 * 
 * The hash sets of states, input alphabet and non-input alhabet are
 * generated based on added rules.
 * 
 * @author luciedvorakova
 */
public class NDPDA {
    final private PDSymbol BOTTOM_SYMBOL = new PDSymbol(Type.BOTTOM, null);
    final private int n; // number of non-input symbols on PD
    final private HashSet<String> states = new HashSet();
    final private HashSet<PDSymbol> inputAlph = new HashSet();
    final private HashSet<PDSymbol> nonInputAlph = new HashSet();
    final private List<Rule> expansionRules = new ArrayList();
    
    
    private HashSet<String> allSymbols = new HashSet();
    private HashSet<String> nonInputSymbols = new HashSet();

    final private String startState;
    final private PDSymbol startPDSym;
    final private HashSet<String> endStates;
    
    final private DeepPD dpda;
    
    private Boolean settingDone = false;
    private String curState;
    
    public NDPDA(int n, String startState, String startPDSym, HashSet<String> endStates){
        this.n = n;
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
            }
            else {
                this.fromSym = new PDSymbol(Type.NONTERMINAL, fromSym); 
            }
            this.toSymbolString = toSymbolsString.toCharArray();
            numOfNonInputSym = 0;
        }
        
        @Override
        public String toString() {
            return depth + startState + fromSym.getName() + "->" + endState + String.valueOf(toSymbolString);
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
    //public void addRule (String startState, String fromSym , String endState) {
    //    Rule rule = new Rule(1, startState, endState, fromSym, null);
    //    insertValues(rule);
    //}
    
    /**
    * Parsing rules to create input and non-input alphabet.
    * @param rule 
    */
    private void insertValues(Rule rule) {
        states.add(rule.startState);
        states.add(rule.endState);
        
        nonInputSymbols.add(rule.fromSym.getName());
        nonInputAlph.add(rule.fromSym);
        for(char sym : rule.toSymbolString) {
            allSymbols.add(String.valueOf(sym));
        }
        expansionRules.add(rule);

        settingDone = false;
    }
    
    
    /**
    * Before parsing using automaton, all symbols on the right
    * side of the rules are coverted to PDSymbol. 
    * This operation is done only first time the automaton is run.
    */
    public void automatSettingDone() {
        for(String sym : allSymbols){
            if(!nonInputSymbols.contains(sym)){
                inputAlph.add(new PDSymbol(Type.TERMINAL, sym));
            }
        }
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
    
    /**
    * Top-down parsing using the specified automata. 
    * If list of rules is given the simulator will follow
    * it, in other case it will try its best.
    * @param input String of input character
    * @param rulesNum list giving order of used rules
    * @return Returns true if string is accepted by automaton, 
    *          else returns false.
    */
    public Boolean simulate(String input, List<Integer> rulesNum) {
        if (!settingDone){
            automatSettingDone();
        }
        out.println("Input string: " + input + "\n");
        curState = startState;
        
        Rule rule;
        for(int ruleNum : rulesNum) {
            rule = expansionRules.get(ruleNum - 1);
   
            if(!rule.startState.equals(curState)) {
                err.println("Rule start state " + rule.startState + " doesn't match with current state " + curState + ".");
                return false;
            }
            
            // Applying the rule would exceed the nomber of non-input
            // symbols on the pushdown.
            if(dpda.numOfNonInput() + rule.numOfNonInputSym - 1 > n){
                err.println("Rule cannot be applied, number od non-input symbols on PD exceeds n (n = " + n + " ).");
                return false;
            }
            Boolean succ = dpda.expand(rule);
            if(succ) {
                out.println("Successfully applied: " + rule);
                curState = rule.endState;
            }
            else {
                err.println("Application of rule was unsuccessful: " + rule);
                return false;
            }
            out.println(dpda.toString());
            out.println(dpda.nonInputToString());
        }
        
        out.println();
        if(dpda.isExpansionDone()){
            out.println("Expansion phase done!\n");
        }
        else {
            return false;
        }
        out.println(dpda.toString());
        out.println(dpda.nonInputToString());
        
        for(char inputSym : input.toCharArray()){
            if(!dpda.pop().getName().equals(String.valueOf(inputSym))){
                err.println(input + " doesn't mach symbols on stack.");
                return false;
            }
        }
        
        out.println("Poping phase done!\n");
        out.println(dpda.toString());
        out.println(dpda.nonInputToString());
        
        if(!endStates.contains(curState)){
            err.println(curState + " is not an end state.");
            return false;
        }

        return dpda.isPDEmpty();
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
        int i = 1;
        for(Rule rule : expansionRules){
            sb.append(i).append(":\t").append(rule.toString()).append("\n");
            i++;
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
    
    public int getN() {
        return n;
    }

    public String getStartState() {
        return startState;
    }

    public PDSymbol getStartPDSym() {
        return startPDSym;
    }
    
    public HashSet<String> getEndStates() {
        return endStates;
    }
    
    public List<Rule> getExpansionRules() {
        return expansionRules;
    }
    
    public HashSet<String> getNonInputSymbols() {
        return nonInputSymbols;
    }
    
    public void addEndState(String endState) {
        endStates.add(endState);
    }
    
}
