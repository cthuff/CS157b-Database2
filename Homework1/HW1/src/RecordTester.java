
import java.sql.*;

public class RecordTester
{
    String dbms, db, table_name, type, num_rows, num_columns;

    public RecordTester(String dbms, String db, String table_name, String type, String num_rows, String num_columns) {
        this.dbms = dbms;
        this.db = db;
        this.table_name = table_name;
        this.type = type;
        this.num_columns = num_columns;
        this.num_rows = num_rows;
    }


    public Connection getconnection() {
        Connection conn = null;
        try {
            String url = "jdbc:mysql://localhost:3306/" + this.db;
            System.out.println("Creating Database..");
            conn = DriverManager.getConnection(url, "root", "");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;

    }

    public void createTable(Statement stmt, Connection conn)
    {
        // Creating table Author
        String drop = "DROP TABLE " + this.table_name;
        try {
            stmt.execute(drop);
            System.out.println("Table Deleted");
            createTable(stmt,conn);}
        catch (SQLException s) {
            String Table1 = "CREATE TABLE " + this.table_name + "( "
                    + " col0 " + this.type + " not null)";
            try
            {
                // Executes the given tables to add tables to Database
                stmt.executeUpdate(Table1);
                System.out.println("Table Created");
            } catch (SQLException se)
            {
                // Catches if there are exceptions raised by SQL
                se.printStackTrace();
                s.printStackTrace();
            } catch (Exception e)
            {
                // Catches any Java Exceptions
                e.printStackTrace();
            }
        }
    }

    public void addToTable(Statement stmt, Connection conn) {
    for (int x = 1; x < Integer.parseInt(this.num_columns); x ++){
        String newRow = "ALTER TABLE " + this.table_name + " ADD col" + x + " " + this.type + " NULL;";
        try {
            stmt.execute(newRow);
        } catch (SQLException row)
        {
            row.printStackTrace();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    }

    public void fillRows(Statement stmt, Connection conn) {

        for (int x = 1; x <= Integer.parseInt(this.num_rows); x++) {
            String newRow = "";
            if (this.type.toUpperCase().contains("CHAR"))
                newRow = "INSERT INTO " + this.table_name + " (col0) VALUES ('xyz');";
            else if (this.type.equalsIgnoreCase("INTEGER") || this.type.equalsIgnoreCase("INT"))
                newRow = "INSERT INTO " + this.table_name + " (col0) VALUES (" + (int)(Math.random() * Integer.parseInt(this.num_rows)) + ");";
            else if (this.type.equalsIgnoreCase("BOOLEAN"))
                newRow = "INSERT INTO " + this.table_name + " (col0) VALUES (FALSE);";
            try {
                stmt.execute(newRow);
            } catch (SQLException row) {
                row.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (int x = 1; x < Integer.parseInt(this.num_columns); x++) {
            String addValue = "";
            if (this.type.toUpperCase().contains("CHAR"))
                addValue = "UPDATE " + this.table_name + " SET col" + x + " = 'abc';";
            else if (this.type.equalsIgnoreCase("INTEGER") || this.type.equalsIgnoreCase("INT"))
                addValue = "UPDATE " + this.table_name + " SET col" + x + " = " + (int)(Math.random() * Integer.parseInt(this.num_rows));
            else if (this.type.equalsIgnoreCase("BOOLEAN"))
                addValue = "UPDATE " + this.table_name + " SET col" + x + " = TRUE;";
            try {
                stmt.execute(addValue);

            } catch (SQLException row) {
                row.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("Rows added");
    }
    public static void main(String args[])
    {
        RecordTester rt = new RecordTester(args[0],args[1],args[2],args[3],args[4],args[5]);

        Connection conn;
        conn = rt.getconnection();
        try
        {
            Statement stmt = conn.createStatement();
            rt.createTable(stmt, conn);
            try {
                rt.addToTable(stmt, conn);
                try {
                    rt.fillRows(stmt, conn);
                } catch (Exception e2) {
                    System.out.println(e2);
                }
            } catch (Exception e2) {
               System.out.println(e2);
            }
        } catch (SQLException ex)
        {
            ex.printStackTrace();
        } catch (Exception e)
        {
        }
        try {conn.close();}
        catch (Exception e) {}
    }

}
