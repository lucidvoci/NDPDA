package ndpdar;

/**
 *
 * @author lucie dvorakova
 */

public class PDSymbol {
    public enum Type {TERMINAL, NONTERMINAL, BOTTOM}
    
    private Type type = null;
    private String name = null;
    
    public PDSymbol(Type type, String name) {
        this.type = type;
        if(name == null){
            this.name = "#";
        }
        else {
            this.name = name;
        }
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return "{" + name + ":" + type.toString() + "}";
    }
    
    @Override
    public int hashCode() {
        return name.hashCode() * 10 + type.ordinal();
        
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PDSymbol other = (PDSymbol) obj;
        if (type != other.type)
            return false;
        if (!name.equals(other.name))
            return false;
        return true;
}
    
}
