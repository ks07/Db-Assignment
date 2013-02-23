import java.util.ArrayList;

public class Table {
    private final String name;
    private final String[] columns;
    private final ArrayList<Record> records = new ArrayList<Record>();

    // Works mostly, but not particularly efficient.
    // Matches any comma, provided it is not escaped with a backslash, that
    // is not escaped itself. Hit trouble with many chained '\'.
    private static final String delimRegex = "(?<!(?<!\\\\)\\\\),";

    public Table(String name) {
        try {
            File inFile = new File(name + ".txt");
            Scanner in = new Scanner(inFile, "UTF-8");
            in.useDelimiter(delimRegex);
            
        } catch (IOException ioe) {
            throw new Error("Could not read table file.", ioe);
        }
    }

    public Table(String name, String[] columns) {
        this.name = name;

        for (String s : columns) {
            if (s == null) {
                throw new Error("Attempted to store a null value.");
            }
        }

        this.columns = new String[columns.length];
        System.arraycopy(columns, 0, this.columns, 0, columns.length);
    }

    public String name() {
        return name;
    }

    public int rows() {
        return records.size();
    }

    public int columns() {
        return columns.length;
    }

    public String name(int col) {
        if (checkColBounds(col)) {
            return columns[col];
        } else {
            throw new Error("Bad column.");
        }
    }

    public int column(String name) {
        for (int col = 0; col < columns.length; col++) {
            if (columns[col].equalsIgnoreCase(name)) {
                return col;
            }
        }

        return -1;
    }

    public Record select(int row) {
        if (checkRowBounds(row)) {
            return records.get(row);
        } else {
            throw new Error("Bad row.");
        }
    }

    public void insert(Record r) {
        if (find(r) < 0) {
            records.add(r);
        } else {
            throw new Error("Inserting duplicate record.");
        }
    }

    public void delete(Record r) {
        int row = find(r);

        if (row >= 0) {
            records.remove(row);
        } else {
            throw new Error("Deleting non-existant record.");
        }
    }

    private int find(Record r) {
        for (int row = 0; row < records.size(); row++) {
            if (records.get(row) == r) {
                return row;
            }
        }

        return -1;
    }

    private boolean checkRowBounds(int row) {
        return row >= 0 && row < records.size();
    }

    private boolean checkColBounds(int col) {
        return col >= 0 && col < columns.length;
    }

    public static void main(String[] args) {
        String[] cols = {
            "alpha",
            "beta",
            "gamma"
        };

        Table t = new Table("table", cols);

        if (t.columns() != cols.length) {
            throw new Error("Columns incorrect.");
        }
        if (!"table".equals(t.name())) {
            throw new Error("Name incorrect.");
        }
        if (!"beta".equals(t.name(1))) {
            throw new Error("Column name incorrect.");
        }
    }
}
