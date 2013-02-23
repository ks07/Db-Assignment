public class Record {
    private final String[] fields;

    public Record(String[] values) {
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

        // Copy the provided array so that it cannot be modified externally.
        fields = new String[values.length];
        System.arraycopy(values, 0, fields, 0, values.length);
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
                fields[col] = value;
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

    public static void main(String[] args) {
        // Do some tests.
        String[] values = {
                "alpha",
                "beta",
                "gamma",
                "delta",
                "epsilon"
        };

        Record r = new Record(values);

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
    }
}
