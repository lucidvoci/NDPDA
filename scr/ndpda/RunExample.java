package ndpdar;

import static java.lang.System.out;
import java.util.ArrayList;
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
        
        /* 
        * -- EXAMPLE 1 --
        * Create a NDPDA for the language
        *   L = {a^n b^n c^n: n >= 1} 
        * Convert NDPDA to NDPDAr
        * Run example parsing on string "aabbcc"
        */
        //runExample1();
        
        /*
        * -- EXAMPLE 2 --
        * Create a NDPDA for the language that
        * checks if variable is declared
        * Convert NDPDA to NDPDAr
        * Run example parsing on code
        *   var dog;
        *   var cat;
        *   var cow;
        *   func {
        *       dog = val;
        *       cat = val;
        *       cow = val;
        *   }
        */
        //runExample2();
        
        /*
        * -- EXAMPLE 3 --
        * Create a NDPDA for the language that
        * checks if variable is declared
        * Convert NDPDA to NDPDAr
        * Run example parsing on code
        *   var aaa;
        *   func {
        *       aaa = val;
        *   }
        */
        runExample3();

    }
    
    private static void runExample3() {
        HashSet<String> endStates = new HashSet();
        endStates.add("f");
        
        // Create automaton
        NDPDA automaton = new NDPDA(5, "s", "S", endStates);
        
        // Expansion rules
        automaton.addRule("s", "S", "p", new ArrayList<>(Arrays.asList("V","P")));
        automaton.addRule(2, "p", "P", "p", new ArrayList<>(Arrays.asList("func","{", "P", "}")));
        automaton.addRule(2, "p", "P", "p", new ArrayList<>(Arrays.asList("I", "=", "N", ";", "P")));
        automaton.addRule(2,"p", "N", "p", new ArrayList<>(Arrays.asList("val")));
        
        
        String letter = "a";
        // First letter of variable
        automaton.addRule(2,"p", "I", "<r;" + letter + ">", new ArrayList<>(Arrays.asList(letter, "I'")));
        automaton.addRule(1,"<r;" + letter + ">", "V", "p", new ArrayList<>(Arrays.asList("var",letter,"V")));

        // Middle letters of variable
        automaton.addRule(2,"p", "I'", "<t;" + letter + ">", new ArrayList<>(Arrays.asList(letter, "I'")));
        automaton.addRule(1,"<t;" + letter + ">", "V", "p", new ArrayList<>(Arrays.asList(letter,"V")));

        // Last letters of variable
        automaton.addRule(2,"p", "I'", "<u;" + letter + ">", new ArrayList<>(Arrays.asList(letter)));
        automaton.addRule(1,"<u;" + letter + ">", "V", "p", new ArrayList<>(Arrays.asList(letter,";","V")));
        
        // End of declarations
        automaton.addRule(2,"p", "P", "g", new ArrayList<>(Arrays.asList()));
        automaton.addRule(1,"g", "V", "f", new ArrayList<>(Arrays.asList()));

        automaton.automatSettingDone();
        
        // convert to NDPDAr automaton
        NDPDAr reducedAutomaton = new NDPDAr(automaton);
        reducedAutomaton.automatSettingDone();
        out.println(reducedAutomaton.toString());
        
        List<Integer> rulesNum = Arrays.asList(1, 346, 
                                               604, 1151, 1539, 1668, 2056, 2185, 2357, 863, //aaa
                                               2671, 2843
                                               ); 
        String input = "var aaa;       "
                     + "func {         "
                     + "    aaa = val; "
                     + "}              ";
        
         //Run simulation
        if(reducedAutomaton.simulate(input, rulesNum)){
            out.println("=> Success: Automaton M does accept given input.\n");
        }
        else {
            out.println("=> Fail: Automaton M doesn't accept given input!\n");
        }
        
    }
    private static void runExample2() {
        HashSet<String> endStates = new HashSet();
        endStates.add("f");
        
        // Create automaton
        NDPDA automaton = new NDPDA(5, "s", "S", endStates);
        
        // Expansion rules
        automaton.addRule("s", "S", "p", new ArrayList<>(Arrays.asList("V","P")));
        automaton.addRule(2, "p", "P", "p", new ArrayList<>(Arrays.asList("func","{", "P", "}")));
        automaton.addRule(2, "p", "P", "p", new ArrayList<>(Arrays.asList("I", "=", "N", ";", "P")));
        automaton.addRule(2,"p", "N", "p", new ArrayList<>(Arrays.asList("val")));
        
        for(int i = 97; i < 123; i++) {
            String letter = Character.toString((char) i);
            // First letter of variable
            automaton.addRule(2,"p", "I", "<r;" + letter + ">", new ArrayList<>(Arrays.asList(letter, "I'")));
            automaton.addRule(1,"<r;" + letter + ">", "V", "p", new ArrayList<>(Arrays.asList("var",letter,"V")));
        
            // Middle letters of variable
            automaton.addRule(2,"p", "I'", "<t;" + letter + ">", new ArrayList<>(Arrays.asList(letter, "I'")));
            automaton.addRule(1,"<t;" + letter + ">", "V", "p", new ArrayList<>(Arrays.asList(letter,"V")));
        
            // Last letters of variable
            automaton.addRule(2,"p", "I'", "<u;" + letter + ">", new ArrayList<>(Arrays.asList(letter)));
            automaton.addRule(1,"<u;" + letter + ">", "V", "p", new ArrayList<>(Arrays.asList(letter,";","V")));
        }
        
        // End of declarations
        automaton.addRule(2,"p", "P", "g", new ArrayList<>(Arrays.asList()));
        automaton.addRule(1,"g", "V", "f", new ArrayList<>(Arrays.asList()));

        automaton.automatSettingDone();
        out.println(automaton.toString());
        
        List<Integer> rulesNum = Arrays.asList(1, 2, 
                                               3, 23, 24, 91, 92, 45, 46, 4, //dog
                                               3, 17, 18, 7, 8, 123, 124, 4, //cat
                                               3, 17, 18, 91, 92, 141, 142, 4, //bird
                                               161, 162);

        String input = "var dog;       "
                     + "var cat;       "
                     + "var cow;       "
                     + "func {         "
                     + "    dog = val; "
                     + "    cat = val; "
                     + "    cow = val; "
                     + "}              ";
        
        //Run simulation
        if(automaton.simulate(input, rulesNum)){
            out.println("=> Success: Automaton M does accept given input.\n");
        }
        else {
            out.println("=> Fail: Automaton M doesn't accept given input!\n");
        }
        
    }   
    

    private static void runExample1() {
        HashSet<String> endStates = new HashSet();
        endStates.add("f");
        
        // Create automaton
        NDPDA automaton = new NDPDA(3, "s", "S", endStates);
        
        // Expansion rules
        automaton.addRule("s", "S", "q", new ArrayList<>(Arrays.asList("A","A")));
        automaton.addRule("q", "A", "f", new ArrayList<>(Arrays.asList("a","b")));
        automaton.addRule("f", "A", "f", new ArrayList<>(Arrays.asList("c")));
        automaton.addRule("q", "A", "p", new ArrayList<>(Arrays.asList("a","A","b")));
        automaton.addRule(2,"p", "A", "q", new ArrayList<>(Arrays.asList("A","c")));

        automaton.automatSettingDone();
        
        // Set of rules that will be used (in correct order)
        /*List<Integer> rulesNum = Arrays.asList(1, 4, 5, 2, 3);
        
        // Run simulatio
        if(automaton.simulate("aabbcc", rulesNum)){
            out.println("=> Automaton M does accept given input.\n");
            out.println(automaton.toString());
        }
        else {
            out.println("=> Automaton M doesn't accept given input!\n");
            out.println(automaton.toString());
        }*/
        
        // Set of rules that will be used in this order
        List<Integer> rulesNum = Arrays.asList(1, 11, 13, 5, 7);
        
        // Convert NDPDA automaton to NDPDAr
        NDPDAr reducedAutomaton = new NDPDAr(automaton);
        out.println(reducedAutomaton.toString());
        
        // Run simulation
        if(reducedAutomaton.simulate("aabbcc", rulesNum)){
            out.println("=> Success: Automaton M does accept given input.\n");
        }
        else {
            out.println("=> Fail: Automaton M doesn't accept given input!\n");
        }
    }
}
