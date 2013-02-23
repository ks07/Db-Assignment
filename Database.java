import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;

public class Database {
    private final HashMap<String, Table> tables;

    public Database() {
        File dataDir = new File(".");
        tables = new HashMap<String, Table>();

        for (File tbl : dataDir.listFiles(new TableFilter())) {
            int end = tbl.getName().length() - 4;
            String name = tbl.getName().substring(0, end);

            tables.put(name, new Table(name));
        }
    }

    public Table table(String name) {
        return tables.get(name);
    }

    // Internal method to add a table created after construction.
    protected void addTable(Table t) {
        tables.put(t.name(), t);
    }

    private static class TableFilter implements FileFilter {
        public boolean accept(File file) {
            return file.isFile() && file.canRead() && !file.isHidden()
                && file.getName().endsWith(".txt");
        }
    }

    public static void main(String[] args) {
        Database db = new Database();
    }
}
