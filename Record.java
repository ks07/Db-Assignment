public class Record {
    private String[] fields;
    private final Table parent;

    public Record(Table table, String key) {
        // Check the key isn't in use
        if (table.select(key) != null) {
            throw new Error("Key already in use.");
        }
        
        fields = new String[table.columns()];
        fields[0] = key;

        // Determine number of columns to use and
        // insert if the table already has a header
        int cols;
        if (table.columns() == 0) { 
            cols = 1;
        } else {
            cols = table.columns();
            table.insert(this);
        }
        
        // Fill with blank strings
        for (int i = 1; i < cols; i++) {
            fields[i] = "";
        }
        
        parent = table;
    }

    public Record(Table table, String[] values) {
        // Check that the array is valid
        if (values == null) {
            throw new Error("Attempted to store a null value.");
        }

        // Check that no array elements are null.
        for (String s : values) {
            if (s == null) {
                throw new Error("Attempted to store a null value.");
            }
        }
        
        // Check the key isn't in use
        if (table.select(values[0]) != null) {
            throw new Error("Key already in use.");
        }
        
        // Check the array fits into table
        if (table.columns() != 0 && values.length > table.columns()) {
            throw new Error("Record length exceeds table size.");
        }
        
        // Determine number of columns to use and
        // insert if the table already has a header
        int cols;
        if (table.columns() == 0) {
            cols = values.length;
            fields = new String[cols];
        } else {
            cols = table.columns();
            fields = new String[cols];
            table.insert(this);
        }

        // Copy the provided array so that it cannot be modified externally.
        System.arraycopy(values, 0, fields, 0, values.length);
        
        // Fill in the remainder with blanks
        for (int i = values.length; i < cols; i++) {
            fields[i] = "";
        }
        
        parent = table;
    }

    private boolean checkBounds(int col) {
        return col >= 0 && col < fields.length;
    }

    public String field(int col) {
        if (checkBounds(col)) {
            return fields[col];
        } else {
            throw new Error("Bad column.");
        }
    }

    public void field(int col, String value) {
        if (checkBounds(col)) {
            if (value != null) {
                if (col == 0 && parent.select(value) != null) {
                    throw new Error("Key already in use.");
                } else {
                    fields[col] = value;
                }
            } else {
                throw new Error("Attempted to store a null value.");
            }
        } else {
            throw new Error("Bad column.");
        }
    }

    public int fields() {
        return fields.length;
    }
    
    public String key() {
        if (fields.length > 0) {
            return fields[0];
        } else {
            throw new Error("Bad column.");
        }
    }
    
    public void delete() {
        if (fields == null) {
            throw new Error("Record has already been deleted.");
        }
        
        if (parent.select(key()) != null) {
            fields = null;
            parent.delete(this);
        } else {
            throw new Error("Attempted to delete header.");
        }
    }

    public static void main(String[] args) {
        // Do some tests.
        String[] cols = {
            "Key",
            "A",
            "B",
            "C",
            "D"
        };
        String[] values = {
            "alpha",
            "beta",
            "gamma",
            "delta",
            "epsilon"
        };

        Database db = new Database();
        Table t = new Table(db, "test", cols);
        Record r = new Record(t, values);

        if (r.checkBounds(-1)) {
            throw new Error("Bounds check failed.");
        }
        if (r.fields() != values.length) {
            throw new Error("Fields incorrect.");
        }
        if (!values[0].equals(r.field(0))) {
            throw new Error("Field get incorrect.");
        }

        r.field(0, "apple");
        if (!"apple".equals(r.field(0))) {
            throw new Error("Field set failed.");
        }
        
        t.print(System.out);
        
        if (!t.select("apple").equals(r)) {
            throw new Error("Record not in table.");
        }
        
        r.delete();
        if (t.select("apple") != null) {
            throw new Error("Deleted record still in table.");
        }
        
        Record r1 = new Record(t, values);
        r1.field(0, "apple");
        Record r2 = new Record(t, values);
        r2.field(0, "endothermic");
        Record r3 = new Record(t, values);
        r3.field(0, "To Kill a Mockingbird");
        
        t.print(System.out);
        
        r3.delete();
        
        boolean fail = true;
        
        try {
            r3.field(0, "Ditto");
        } catch (Error e) {
            fail = false;
        } catch (NullPointerException npe) {
            fail = false;
        } finally {
            if (fail) {
                throw new Error("Deleted record still functional.");
            }
        }
    }
}
