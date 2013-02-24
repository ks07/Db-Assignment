import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Type {
    private static ArrayList<Type> typeCache;
    
    private final TYPE type;
    private final String[] tags;
    private final Table ref;

    // One or more strings not containing ',' separated with ,
    private static final Pattern TAGPAT = Pattern.compile("[^,]+(,[^,]+)*");

    // Private constructor for internal use
    private Type(TYPE type, String[] tags, Table ref) {
        this.type = type;
        this.tags = tags;
        this.ref = ref;
    }
    
    public boolean allowed(String value) {
        // TODO: Check if string is allowed for this type
        return false;
    }
    
    public static Type type(Database db, String name) {
        // TODO: Retrieve / generate Type object for db & name
        Type ret = null;
        TYPE parsed = parseName(name);

        switch (parsed) {
        case STR:
        case INT:
            ret = new Type(parsed, null, null);
            break;
        case REF:
            Table ref = db.table(name.substring(4, name.length() - 1));

            if (ref != null) {
                ret = new Type(parsed, null, ref);
            } else {
                throw new Error("Type references non-existant table.");
            }
            break;
        case TAG:
            String inner = name.substring(4, name.length() - 1);
            Matcher m = TAGPAT.matcher(inner);

            if (m.matches()) {
                String[] tags = inner.split(",");
                ret = new Type(parsed, tags, null);
            } else {
                throw new Error("Type tags are invalid.");
            }
            break;
        }

        return ret;
    }

    private static TYPE parseName(String name) {
        if ("string".equals(name)) {
            return TYPE.STR;
        } else if ("integer".equals(name)) {
            return TYPE.INT;
        } else if (name.length() > 5) {
            if (name.startsWith("ref(") && name.endsWith(")")) {
                return TYPE.REF;
            } else if (name.startsWith("tag(") && name.endsWith(")")) {
                return TYPE.TAG;
            }
        }

        throw new Error("Invalid type name.");
    }
    
    public static void main(String[] args) {
        Database db = new Database();
        type(db, "string");
        type(db, "integer");
        type(db, "tag(yes,no)");
        type(db, "ref(people)");
    }

    private enum TYPE {
        STR, INT, TAG, REF;
    }
}
