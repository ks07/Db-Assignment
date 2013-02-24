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
    
    private void listInstructions() {
        p("Commands:");
        p("help                                Display this command list");
        p("exit                                Exit the program");
        p("print <table>                       Display the specified table");
        p("edit <table> <key> <column> <value> Change field in a table");
        //p("store <table>                       Saves the specified table to disc\n");
    }
    
    public void run() {
        // Create the database
        db = new Database();
        
        p("Database program\n================\n");
        listInstructions();
        
        String in = "";
        while (!in.equalsIgnoreCase("exit")) {
            String[] splitIn = in.split(" ");
            String cmd = splitIn[0];
            
            if (cmd.equalsIgnoreCase("help")) {
                listInstructions();
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
                if (splitIn.length == 5) {
                    Table t = db.table(splitIn[1]);
                    Record r = t.select(splitIn[2]);
                    
                    try {
                        r.field(t.column(splitIn[3]), splitIn[4]);
                    } catch (Error e) {
                        p("Unable to modify.");
                    }
                } else {
                    p("Wrong number of arguments.");
                }
            }
            
            p("Enter query:");
            in = readln();
        }
    }
    
    public static void main(String[] args) {
        Program p = new Program();
        p.run();
    }
}
