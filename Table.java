import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Table {
    private final String name;
    private final String[] columns;
    private final ArrayList<Record> records;

    public Table(String name) {
        this.name = name;

        try {
            ArrayList<Record> rec = new ArrayList<Record>();
            File inFile = new File(name + ".txt");
            BufferedReader in = new BufferedReader(new FileReader(inFile));
            String[] line = readNext(in);

            if (line != null) {
                // First line gives the column names.
                this.columns = line;
            } else {
                throw new Error("Table file empty.");
            }

            for (line = readNext(in); line != null; line = readNext(in)) {
                if (line.length == this.columns.length) {
                    rec.add(new Record(line));
                } else {
                    throw new Error("Table file invalid.");
                }
            }

            this.records = rec;
        } catch (IOException ioe) {
            throw new Error("Could not read table file.", ioe);
        }
    }

    // Reads the next record from the given BufferedReader as an array of Strings.
    // Returns null if there are no more lines.
    private static String[] readNext(BufferedReader in) throws IOException {
        ArrayList<String> ret = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        int read = in.read();
        boolean isEscaped = false;
        char c;

        while (read >= 0) {
            c = (char)read;

            if (isEscaped && (c == '\\' || c == '\n' || c == ',')) {
                if (c == '\\' || c == '\n' || c == ',') {
                    // Met a valid escape sequence, append.
                    sb.append(c);
                } else {
                    // Found an invalid escape sequence (e.g. \a).
                    throw new Error("Table file invalid.");
                }
            } else if (c == '\\') {
                // Met an escape character, ignore and set state.
                isEscaped = true;
            } else if (c == ',') {
                // Met a field delimiter, add to the list.
                ret.add(sb.toString());
            } else if (c == '\n') {
                // Met a record delimiter, add & return the array.
                ret.add(sb.toString());
                return ret.toArray(new String[ret.size()]);
            } else {
                // Found a regular character, append.
                sb.append(c);
            }

            read = in.read();
        }

        // Table files should always end with a record delimiter.
        // Return null if we get to EOF.
        return null;
    }

    public void store() {
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(this.name + ".txt")));

            for (int col = 0; col < this.columns.length - 1; col++) {
                out.print(columns[col]);
                out.print(',');
            }
            out.print(columns[columns.length - 1]);
            out.print('\n');

            for (Record r : this.records) {
                for (int f = 0; f < r.fields() - 1; f++) {
                    out.print(r.field(f));
                    out.print(',');
                }
                out.print(r.field(r.fields() - 1));
                out.print('\n');
            }

            out.close();
        } catch (IOException ioe) {
            throw new Error("Failed to write table file.", ioe);
        }
    }

    public Table(String name, String[] columns) {
        this.records = new ArrayList<Record>();
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

        Table t = new Table("test", cols);
        t.insert(new Record(new String[] {"a", "b", "c"}));
        t.insert(new Record(new String[] {"1", "2", "3"}));

        if (t.columns() != cols.length) {
            throw new Error("Columns incorrect.");
        }
        if (!"test".equals(t.name())) {
            throw new Error("Name incorrect.");
        }
        if (!"beta".equals(t.name(1))) {
            throw new Error("Column name incorrect.");
        }
        // Store in a file.
        t.store();

        // Test loading of the file.
        t = new Table("test");
    }
}
