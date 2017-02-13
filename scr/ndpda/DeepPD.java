package ndpdar;

import deepstack.DeepStack;
import deepstack.DeepStack.Node;
import static java.lang.System.out;
import ndpdar.PDSymbol.Type;
import ndpdar.NDPDA.Rule;

/**
 * Supporting stack for Deep Pushdown Automaton. Usees two deep stacks
 * that allow accesing symbols in certaing depth of stack. Stack 
 * dpdaSym saves both input and non-input symbols. Stack nonInputSym
 * saves refrences to nodes that contain non-input symbols in the
 * dpdaSym stack.
 * 
 * @author luciedvorakova
 */
public class DeepPD {
    final private PDSymbol BOTTOM_SYMBOL;
    final private DeepStack<Node> nonInputSym = new DeepStack(); 
    final private DeepStack<PDSymbol> dpdaSym = new DeepStack();
    
    public DeepPD(PDSymbol startSymbol){
        this.BOTTOM_SYMBOL = new PDSymbol(Type.BOTTOM, null);
        push(BOTTOM_SYMBOL);
        push(startSymbol);
    }
    
    /**
    * Performs expansion base on given rule. Checks if rule can 
    * be applied. Than proceeds to pop the non-input symbol at the
    * depth of the rule and push new symbols at its place.
    * @param rule by which expansion should be perfomed
    * @return True if expansion was successful.
    */
    public Boolean expand(Rule rule) {
        int lvl = rule.depth - 1;
        Node expandNode = nonInputSym.peek(lvl);
        
        
        //Check if rule can be applied
        if(!expandNode.getData().equals(rule.fromSym)) {
            out.println(rule + " cound't be applied!");
            return false;
        }
        
        nonInputSym.pop(lvl); // Pop from PD whith only non-input symbols
        Node origNode = expandNode; // Saving reference
        
        Node nonInputNode = null;
        for(PDSymbol sym : rule.toSymbols){
            if(sym.getType() == Type.TERMINAL){
                expandNode = dpdaSym.push(sym, expandNode);
            }
            else {
                expandNode = dpdaSym.push(sym, expandNode);
                if (nonInputNode == null) {
                    nonInputNode = nonInputSym.push(expandNode, lvl);
                }
                else {
                    nonInputNode = nonInputSym.push(expandNode, nonInputNode);
                }
            }
        }
        
        dpdaSym.pop(origNode); // Pop from PD with all the symbols;
        return true;
    }
    
    /**
    * Pop top symbol from entire stack.
    * @return Top symbol of the stack
    */
    public PDSymbol pop() {
        PDSymbol sym = dpdaSym.pop();
        return sym;
    }
    
    /**
    * Check if more expansions can be done. 
    * @return True if stack doesn't contain any non-input symbols
    *           other than botto symbol.
    */
    public Boolean isExpansionDone() {
        return nonInputSym.peek().getData() == BOTTOM_SYMBOL;
    }
    
    /**
    * Does the stack only contains the bottom symbol. 
    * @return True if stack only contains bottom symbol
    */
    public Boolean isPDEmpty() {
        return dpdaSym.peek() == BOTTOM_SYMBOL;
    }
    
    /**
    * Number of non-input symbols on stack. 
    * @return Number of non-input symbols on stack
    */
    public int numOfNonInput() {
        return nonInputSym.size();
    }
    
    /**
    * Initial push for bottom and start symbol.
    */
    private void push(PDSymbol startSymbol){
        Node node = dpdaSym.push(startSymbol);
        nonInputSym.push(node);
    }
    
    /**
    * Returns content of entire stack.
    * @return String representation of stack
    */
    @Override
    public String toString() {
        return dpdaSym.toString();
    }
    
    /**
    * Returns content of stack with references to non-input symbols.
    * @return String representation of stack
    */
    public String nonInputToString() {
        return nonInputSym.toString();
    }
}
