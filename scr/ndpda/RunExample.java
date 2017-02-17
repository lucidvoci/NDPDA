package ndpdar;

import static java.lang.System.out;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Example hot to set and run NDPDA automaton and how to convert it
 * to NDPDAr automaton.
 * 
 * @author luciedvorakova
 */
public class RunExample {
    public static void main(String[] args) {
        HashSet<String> endStates = new HashSet();
        endStates.add("f");
        
        // Create automaton
        NDPDA automaton = new NDPDA(3, "s", "S", endStates);
        
        // Expansion rules
        automaton.addRule("s", "S", "q", "AA");
        automaton.addRule("q", "A", "f", "ab");
        automaton.addRule("f", "A", "f", "c");
        automaton.addRule("q", "A", "p", "aAb");
        automaton.addRule(2,"p", "A", "q", "Ac");

        automaton.automatSettingDone();
        
        // Set of rules that will be used (in correct order)
        List<Integer> rulesNum = Arrays.asList(1, 4, 5);
        
        // Run simulatio
        if(automaton.simulate("aabbcc", rulesNum)){
            out.println("=> Automaton M does accept given input.\n");
            out.println(automaton.toString());
        }
        else {
            out.println("=> Automaton M doesn't accept given input!\n");
            out.println(automaton.toString());
        }
        
 
        
        // Set of rules that will be used in this order
        rulesNum = Arrays.asList(1, 11, 13, 5, 7);
        
        // Convert NDPDA automaton to NDPDAr
        NDPDAr reducedAutomaton = new NDPDAr(automaton);
        
        // Run simulation
        if(reducedAutomaton.simulate("aabbcc", rulesNum)){
            out.println("=> Automaton M does accept given input.\n");
            out.println(reducedAutomaton.toString());
        }
        else {
            out.println("=> Automaton M doesn't accept given input!\n");
            out.println(automaton.toString());
        }
    }
}
