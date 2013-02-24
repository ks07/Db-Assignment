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
        p("help\tDisplay this command list");
        p("exit\tExit the program");
        p("tables\tList table names in the database\n");
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
            } else if (cmd.equalsIgnoreCase("tables")) {
                Table[] tables = db.tables();
                for (Table t : tables) {
                    p(t.name());
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
