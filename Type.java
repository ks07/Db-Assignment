import java.util.ArrayList;

enum TYPE {
    STR, INT, TAG, REF
};

public class Type {
    static private ArrayList<Type> typeCache;
    
    private String[] tags;
    private TYPE type;
    
    // Private constructor for internal use
    private Type(TYPE type, String[] tags) {
        this.type = type;
        this.tags = tags;
    }
    
    public boolean allowed(String value) {
        // TODO: Check if string is allowed for this type
        return false;
    }
    
    public static Type type(Database db, String name) {
        // TODO: Retrieve / generate Type object for db & name
        return null;
    }
    
    public static void main(String[] args) {

    }
}
