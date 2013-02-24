import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Type {
    // Map database and names to type objects. Ref will need an object per db and table.
    private static HashMap<TypeKey, Type> typeCache = new HashMap<TypeKey, Type>();
    
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
        TYPE parsed = parseName(name);
        TypeKey key = new TypeKey(db, parsed, name);
        Type ret = typeCache.get(key);

        // If not cached, create new.
        if (ret == null) {
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

            typeCache.put(key, ret);
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
        type(db, "ref(people)");
        Type tag0 = type(db, "tag(yes,no)");
        Type tag1 = type(db, "tag(yes,no)");

        if (tag0 != tag1) {
            throw new Error("Did not retrieve tag from cache.");
        }
    }

    private enum TYPE {
        STR, INT, TAG, REF;
    }

    private static class TypeKey {
        private final Database db;
        private final TYPE type;
        private final String name;

        public TypeKey(Database db, TYPE type, String name) {
            this.type = type;
            this.name = name;

            // Db is only relevant if this is a ref type.
            if (type == TYPE.REF) {
                this.db = db;
            } else {
                this.db = null;
            }
        }

        public int hashCode() {
            // Try and avoid collisions.
            if (db == null) {
                return name.hashCode();
            } else {
                // TODO: Possible improvements?
                return (db.toString() + name).hashCode();
            }
        }

        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            } else if (obj == this) {
                return true;
            } else if (!(obj instanceof TypeKey)) {
                return false;
            } else {
                TypeKey other = (TypeKey)obj;
                boolean dbEquals = (db == null && other.db == null) || db.equals(other.db);
                return dbEquals && name.equals(other.name);
            }
        }
    }
}
