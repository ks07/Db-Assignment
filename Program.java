import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Program {
    private BufferedReader reader;
    private Database db;
    
    public Program() {
        InputStreamReader isr = new InputStreamReader(System.in);
        reader = new BufferedReader(isr);
    }
    
    private void p(String out) {
        System.out.println(out);
    }
    
    private String readln() {
        try {
            return reader.readLine();
        } catch (IOException ioe) {
            throw new Error("Unable to read user input.");
        }
    }
    
    private void showMainHelp() {
        p("----------------------------------------------------------");
        p("Commands:");
        p("    help            - Display this command list");
        p("    exit            - Exit the program");
        p("    print <table>   - Display the specified table");
        p("    edit <table>    - Open the specified table for editing");
        p("----------------------------------------------------------\n");
    }
    
    public void run() {
        // Create the database
        db = new Database();
        
        p("DATABASE PROGRAM: By George Field & Alistair Wick");
        p("==========================================================\n");
        showMainHelp();
        
        String in = "";
        while (!in.equalsIgnoreCase("exit")) {
            String[] splitIn = in.split(" ");
            String cmd = splitIn[0];
            
            if (cmd.equalsIgnoreCase("help")) {
                showMainHelp();
            } else if (cmd.equalsIgnoreCase("print")) {
                if (splitIn.length == 2) {
                    Table t = db.table(splitIn[1]);
                    
                    if (t == null) {
                        p("Table " + splitIn[1] + " does not exist.");
                    } else {
                        t.print(System.out);
                    }
                } else {
                    p("Wrong number of arguments.");
                }
            } else if (cmd.equalsIgnoreCase("edit")) {
                if (splitIn.length == 2) {
                    Table t = db.table(splitIn[1]);
                    editTable(t);
                } else {
                    p("Wrong number of arguments.");
                }
            }
            
            p("Enter query:");
            in = readln();
        }
        
        p("Goodbye.");
    }
    
    private void showTableHelp() {
        p("----------------------------------------------------------");
        p("Table edit commands:");
        p("    help         - Display this command list");
        p("    done         - Stop editing and save changes to disc");
        p("    cancel       - Stop editing and discard changes");
        p("    print        - Display the open table");
        p("    edit <key>   - Modify record at <key>");
        //p("    add <key>    - Add a new record with key <key>");
        //p("    delete <key> - Delete record at <key>");
        p("----------------------------------------------------------\n");
    }
    
    public void editTable(Table t) {
        showTableHelp();
        
        String in = "";
        while (!in.equalsIgnoreCase("done")) {
            String[] splitIn = in.split(" ");
            String cmd = splitIn[0];
            
            if (cmd.equalsIgnoreCase("help")) {
                showTableHelp();
            } else if (cmd.equalsIgnoreCase("print")) {
                t.print(System.out);
            } else if (cmd.equalsIgnoreCase("edit")) {
                if (splitIn.length == 2) {
                    Record r = t.select(splitIn[1]);
                    
                    p("Enter name of column to change: ");
                    String col = readln();
                    p("Enter new value for field: ");
                    String val = readln();
                    
                    try {
                        r.field(t.column(col), val);
                    } catch (Error e) {
                        p("Unable to modify.");
                    }
                } else {
                    p("Wrong number of arguments.");
                }
            }
            
            p("Editing " + t.name() + ":");
            in = readln();
        }
        
        t.store();
        p("Finished editing table: " + t.name() + "\n");
    }
    
    public static void main(String[] args) {
        Program p = new Program();
        p.run();
    }
}
