import java.sql.*;

public class Olap {

    String region, type, timePeriod;

    public Olap(String region, String type, String timePeriod) {
        this.region = region;
        this.type = type;
        this.timePeriod = timePeriod;
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
    public static void main(String[] args) {

        Olap olap  = new Olap(args[0],args[1],args[2]);

        Connection conn;
        conn = olap.mySQLConnection();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM WORK_OUT GROUP BY '" + olap.region + "' WITH ROLLUP;");
            System.out.println("type year month avg");
            while (rs.next()) {
                // read the result set
                if (rs.getString("start_time").contains(olap.timePeriod) && rs.getString("type").contains(olap.type))
                    System.out.println(rs.getString("type") + olap.timePeriod.substring(0,4) + " " + olap.timePeriod.substring(4,olap.timePeriod.length()) + " " + rs.getString("duration"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
        }
        try {
            conn.close();
        } catch (Exception e) {
        }
    }
}

