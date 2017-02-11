package ndpdar;

import deepstack.DeepStack;
import deepstack.DeepStack.Node;
import ndpdar.PDSymbol.Type;
import ndpdar.NDPDA.Rule;

/**
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
    
    public Boolean expand(Rule rule) {
        int lvl = rule.depth - 1;
        Node expandNode = nonInputSym.peek(lvl);
        
        // Check if rule can be applied
        if(expandNode.getData() != rule.fromSym) {
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
    
    
    public PDSymbol pop() {
        PDSymbol sym = dpdaSym.pop();
        if (sym.getType() == Type.NONTERMINAL) {
            nonInputSym.pop();
        }
        return sym;
    }
    
    public Boolean isExpansionDone() {
        return nonInputSym.peek().getData() == BOTTOM_SYMBOL;
    }
    
    public Boolean isPDEmpty() {
        return dpdaSym.peek() == BOTTOM_SYMBOL;
    }
    
    private PDSymbol popNonInputSym(int lvl) {
        return null; //TODO
    }
    
    private void push(PDSymbol startSymbol){
        Node node = dpdaSym.push(startSymbol);
        nonInputSym.push(node);
    }
}
