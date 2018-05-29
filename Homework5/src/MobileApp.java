import java.sql.*;

public class MobileApp {

    String action, id, location, workoutType, startTime, duration, format, date;

    public MobileApp(String action, String id, String location, String workoutType, String startTime, String duration) {
        this.action = action;
        this.id = id;
        this.location = location;
        this.workoutType = workoutType;
        this.startTime = startTime;
        this.duration = duration;
    }

    public MobileApp(String action, String id, String date, String format) {
        this.action = action;
        this.id = id;
        this.date = date;
        this.format = format;
    }

    public MobileApp(String action, String id) {
        this.action = action;
        this.id = id;
    }

    public Connection SQLiteConnection() {
        Connection conn = null;
        try {
            String url = "jdbc:sqlite:/Users/craig/Desktop/" + this.id + ".sqlite";; //+Insert Data Base to Connect to;
            System.out.println("Loading Database..");
            conn = DriverManager.getConnection(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;

    }

    public Connection mySQLConnection() {
        Connection conn = null;
        try {
            String url = "jdbc:mysql://localhost:3306/CS157b";
            System.out.println("Loading Database..");
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
            String Table1 = "CREATE TABLE IF NOT EXISTS WORK_OUT ( " +
                     " location VARCHAR(20)," +
                     " type VARCHAR(20)," +
                     " start_time VARCHAR(20)," +
                     " duration VARCHAR(20));";
            String Table2 = "CREATE TABLE IF NOT EXISTS lastUpdate ( " +
                " time CURRENT_TIMESTAMP NOT NULL);";
            try
            {
                // Executes the given tables to add tables to Database
                stmt.executeUpdate(Table1);
                stmt.executeUpdate(Table2);
                stmt.executeUpdate("INSERT INTO lastUpdate VALUES (CURRENT_TIMESTAMP);");
            } catch (SQLException se)
            {
                // Catches if there are exceptions raised by SQL
                se.printStackTrace();
            } catch (Exception e)
            {
                // Catches any Java Exceptions
                e.printStackTrace();
            }
    }



    public static void main(String args[])
    {
        MobileApp ma;
        if(args[0].equals("workout"))
            ma = new MobileApp(args[0],args[1],args[2],args[3],args[4],args[5]);
        if(args[0].equals("show"))
            ma = new MobileApp(args[0],args[1],args[2],args[3]);
        else
            ma = new MobileApp(args[0],args[1]);

        Connection conn;
        conn = ma.SQLiteConnection();
        try
        {
            Statement stmt = conn.createStatement();
            ma.createTable(stmt, conn);
            try {
                if(ma.action.equals("show")) {
                    ResultSet rs = stmt.executeQuery("SELECT * FROM WORK_OUT");
                    while (rs.next()) {
                        // read the result set
                        if (ma.format.equals("duration") && rs.getString("start_time").contains(ma.date))
                            System.out.println(rs.getString("type") + " "+ rs.getString("duration"));
                        else if(ma.format.equals("location")&& rs.getString("start_time").contains(ma.date))
                            System.out.println(rs.getString("location") + " " +  rs.getString("duration"));
                    }
                }
                else if (ma.action.equals("workout")) {
                    stmt.executeUpdate("INSERT INTO WORK_OUT (location, type, start_time, duration, updateTime) VALUES (" +
                            "'" + ma.location + "'," +
                            "'" + ma.workoutType + "'," +
                            "'" + ma.startTime + "'," +
                            "'" + ma.duration + "'," +
                            " CURRENT_TIMESTAMP)");
                    System.out.println("Row Inserted");

                }
                else if (ma.action.equals("send")) {
                    Connection conn2 = ma.mySQLConnection();
                    Statement stmt2 = conn2.createStatement();

                    ResultSet rs = stmt.executeQuery("SELECT * FROM WORK_OUT, lastUpdate WHERE updateTime > lastUpdate.time");
                    while(rs.next()){
                        stmt2.executeUpdate("INSERT INTO WORK_OUT (location, type, start_time, duration) VALUES (" +
                                "'" + rs.getString("location") + "'," +
                                "'" + rs.getString("type") + "'," +
                                "'" + rs.getString("start_Time") + "'," +
                                "'" +rs.getString("duration") + "');");
                    }
                    stmt.executeUpdate("DELETE FROM lastUpdate WHERE time < CURRENT_TIMESTAMP");
                    stmt.executeUpdate("INSERT INTO lastUpdate VALUES (CURRENT_TIMESTAMP);");
                    conn2.close();
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
