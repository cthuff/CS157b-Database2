import java.sql.*;

public class IsolationTest {

    private String connectString = "jdbc:mysql://localhost:3306/CS157b";

    private String root = "root";
    private String password = "";

    private String commonRead="SELECT * FROM R WHERE col0=4";
    private String commonWrite="INSERT INTO R VALUES (13)";

    private Connection conn;
    private Statement stmt;

    private Connection conn2;
    private Statement stmt2;


        public void readCommitted(Statement stm)
        {
            System.out.println("\n***Read Committed transaction...");
            open();
            execute(stm,"SET TRANSACTION ISOLATION LEVEL READ COMMITTED",
                    "set isolation level read committed",
                    "set isolation failure");
        }

        public void serializable(Statement stm)
        {
            System.out.println("\n***Read serializable transaction...");

            execute(stm,"SET TRANSACTION ISOLATION LEVEL SERIALIZABLE",
                    "set isolation level serializable",
                    "set isolation failure");
            executeRead(stm,"SELECT MAX(col0) FROM R",
                    "Read count succeeded",
                    "Read count failed");
        }

        public void create()
        {
            System.out.println("***Creating Database...");
            open();
            execute(stmt,"CREATE TABLE R(col0 INT)",
                    "Table R created.",
                    "Table R not created.");
            for(int i = 0; i < 10; i ++)
            {
                execute(stmt,"INSERT INTO R VALUES (" + i + ")",
                        "Row " + i + " inserted.",
                        "Row " + i + "not inserted");
            }
            close();
        }


        void open()
        {
            try
            {
                conn = DriverManager.getConnection(connectString, root, password);
                conn2 = DriverManager.getConnection(connectString, root, password);
                stmt = conn.createStatement();
                stmt2 = conn2.createStatement();
            }
            catch(SQLException e)
            {
                System.err.println("Error opening connection.");
                e.printStackTrace();
            }
        }


        void close()
        {
            try
            {
                stmt.close();
                conn.close();
            }
            catch(SQLException e)
            {
                System.err.println("Error closing connection.");
                e.printStackTrace();
            }
        }

        void execute(Statement stm, String sql, String msgSuccess, String msgFail)
        {
            try
            {
                stm.execute(sql);
                System.out.println(msgSuccess);
            }
            catch(SQLException e)
            {
                System.err.println(msgFail);
                e.printStackTrace();
            }
        }


        void executeRead(Statement stm, String sql, String msgSuccess, String msgFail)
        {
            execute(stm,sql,msgSuccess,msgFail);

            try
            {
                ResultSet rset = stmt.getResultSet();
                rset.next();
                System.out.println("Result: Col0=" + rset.getInt(1));
            }
            catch(SQLException e)
            {
                System.err.println(msgFail);
                e.printStackTrace();
            }
        }

        public void destroy()
        {
            System.out.println("***Destroying Database...");
            open();
            execute(stmt,"DROP TABLE R", "Dropped Table R", "Failed to Drop R.");
            close();
        }


        public static void main(String[] args)
        {

            IsolationTest it = new IsolationTest();
            IsolationTest it_2 = new IsolationTest();

            it.create();

            it.open();
            it_2.open();

            //READ COMMITTED SECITON
            it.readCommitted(it.stmt);

            it.executeRead(it.stmt, it.commonRead, "pass","fail");
            it_2.execute(it_2.stmt, it_2.commonWrite, "pass","fail");
            it.executeRead(it.stmt, it.commonRead, "pass","fail");
            it_2.execute(it_2.stmt, it_2.commonWrite, "pass","fail");

            it.close();
            it_2.close();

            it.open();
            it_2.open();

            //SERIALIZABLE SECITON
            it.serializable(it.stmt);

            it.executeRead(it.stmt, it.commonRead, "pass","fail");
            it_2.execute(it_2.stmt, it_2.commonWrite, "pass","fail");
            it.executeRead(it.stmt, it.commonRead, "pass","fail");
            it_2.execute(it_2.stmt, it_2.commonWrite, "pass","fail");

            it.close();
            it_2.close();

            it.destroy();

        }

    }

