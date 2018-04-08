import javax.xml.transform.Result;
import java.io.*;
import java.util.*;
import java.sql.*;
public class GridFileManager {

    private String dataBaseName;
    private File instructions;
    private static ArrayList<String> parsedText;
    private Connection conn;
    private int bucketID;
    GridRecord test1[];

    GridFileManager(String databaseName, File instructions) {
        this.dataBaseName = databaseName;
        this.instructions = instructions;
        this.test1 = new GridRecord[100];
        bucketID = 0;
        this.parsedText = new ArrayList<String>();
        try {
            Scanner inputFile = new Scanner(instructions);
            while (inputFile.hasNext())
            {
                // Read the next word.
                String word = inputFile.next();
                parsedText.add(word);
            }
        }
        catch (Exception e)
        {
            System.out.println(e + "\nThere was a problem reading the file");
        }
    }

    public void close(Connection conn)
    {
        try {
            conn.close();
        }
        catch (SQLException se) {
            System.out.println(se);
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
    public void getConnection() {
        try {
            this.conn = DriverManager.getConnection("jdbc:sqlite:" + dataBaseName);
            Statement statement = this.conn.createStatement();
            statement.executeUpdate("create table if not exists GRID_FILE(ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME VARCHAR(64), NUM_BUCKETS)");
            statement.executeUpdate("create table if not exists GRIDX(GRID_FILE_ID INTEGER PRIMARY KEY, LOW_VALUE INTEGER, HIGH_VALUE INTEGER, NUM_LINES INTEGER)");
            statement.executeUpdate("create table if not exists GRIDY(GRID_FILE_ID INTEGER PRIMARY KEY, LOW_VALUE INTEGER, HIGH_VALUE INTEGER, NUM_LINES INTEGER)");
            statement.executeUpdate("create table if not exists GRID_FILE_ROW(GRID_FILE_ID INTEGER, BUCKET_ID INTEGER, X REAL, Y REAL, LABEL CHAR(16), PRIMARY KEY(GRID_FILE_ID, BUCKET_ID))");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
       // return conn;

    }

    boolean createGridFile(String fileName, int lowX, int highX, int numLinesX, int lowY, int highY, int numLinesY, int numBuckets) {
        try {
            Statement statement = this.conn.createStatement();
            statement.executeUpdate("INSERT INTO GRID_FILE (NAME, NUM_BUCKETS) VALUES ('" + fileName +"', " + numBuckets + ");");
            ResultSet rs = statement.executeQuery("SELECT ID FROM GRID_FILE WHERE NAME = '" + fileName + "';");
            int id = rs.getInt("ID");
            statement.executeUpdate("INSERT INTO GRIDX (GRID_FILE_ID, LOW_VALUE, HIGH_VALUE, NUM_LINES) VALUES (" + id + ", " + lowX +", " + highX + ", " + numLinesX + ")");
            statement.executeUpdate("INSERT INTO GRIDY (GRID_FILE_ID, LOW_VALUE, HIGH_VALUE, NUM_LINES) VALUES (" + id + ", " + lowY +", " + highY + ", " + numLinesY + ")");
            System.out.println("Action was completed");
            return true;
        }
        catch (SQLException se)
        {
            System.out.println(se + " Action could not be completed");
            return false;
        }
        catch (Exception e)
        {
            System.out.println(e + " Action could not be completed");
            return false;
        }
    }

    boolean add(String fileName,  GridRecord record)
    {
        try{
            Statement statement = this.conn.createStatement();
            ResultSet rs = statement.executeQuery("SELECT ID, NUM_BUCKETS FROM GRID_FILE WHERE NAME = '" + fileName + "'" );
            bucketID = rs.getInt("NUM_BUCKETS"); //- (int)(Math.random() * bucketID-1);
            statement.executeUpdate("INSERT INTO GRID_FILE_ROW (GRID_FILE_ID, BUCKET_ID, X, Y, LABEL) VALUES ("+ rs.getInt("ID") + ", " + bucketID + ((int)(Math.random() * bucketID-1)) +  ", " + record.point.x+  ", " + record.point.y + ", '"+ record.label +"');");
            System.out.println(" Action was completed");
            return true;
        }
        catch (SQLException se)
        {
            //If the hash isn't unique, try again
            try {
                Statement statement = this.conn.createStatement();
                ResultSet rs = statement.executeQuery("SELECT ID, NUM_BUCKETS FROM GRID_FILE WHERE NAME = '" + fileName + "'" );
                bucketID = rs.getInt("NUM_BUCKETS"); //- (int)(Math.random() * bucketID-1);
                statement.executeUpdate("INSERT INTO GRID_FILE_ROW (GRID_FILE_ID, BUCKET_ID, X, Y, LABEL) VALUES (" + rs.getInt("ID") + ", " + bucketID + ((int) (Math.random() * bucketID - 1)) + ", " + record.point.x + ", " + record.point.y + ", '" + record.label + "');");
                System.out.println("Action was completed");
                return true;
            }
            catch (SQLException s)
            {
                return false;
            }

        }
        catch (Exception e)
        {
            System.out.println(e + " Action could not be completed");
            return false;
        }
//        System.out.println(record.toString());

    }

    GridRecord[] lookup(String fileName, GridPoint pt1, GridPoint pt2, int limit_offset, int limit_count){
        GridRecord[] grid = new GridRecord[limit_count];
        int counter = 0;
        try {
            ResultSet rs = conn.createStatement().executeQuery("SELECT ID FROM GRID_FILE WHERE NAME = '" + fileName + "'");
            int gridID = rs.getInt("ID");
            rs.close();
            ResultSet rs1 = conn.createStatement().executeQuery("SELECT * FROM GRID_FILE_ROW WHERE GRID_FILE_ID = " + gridID + " LIMIT " + limit_count + " OFFSET " + limit_offset);
            while (rs1.next()) {
                int x = rs1.getInt(3);
                int y = rs1.getInt(4);
                if ((x > pt1.x && x < pt2.x) && (y > pt1.y && y < pt2.y))
                    grid[counter] = new GridRecord("point", x, y);
                counter++;
            }
            rs1.close();
            //rs = statement.executeQuery("SELECT * FROM GRIDY WHERE GRID_FILE_ID = " + gridID + " LIMIT " + limit_count + " OFFSET " + limit_offset);
            System.out.println(" Action was completed");
        }
        catch (SQLException se)
        {
            System.out.println(se + " Action could not be completed");
        }
        catch (Exception e)
        {
            System.out.println(e + " Action could not be completed");

        }
        test1 = grid;
        return grid;
    }

    public static void main(String args[])
    {
        GridFileManager fileManager = new GridFileManager(args[0], new File(args[1]));//args[0] && args[1]));
        fileManager.getConnection();
        int i = 0;
        String word = "";
        try {
            while(true) {
                word = parsedText.get(i);
                switch (word) {
                    case "c": {
                        System.out.println("Creating a new Grid File");
                        fileManager.createGridFile(parsedText.get(i + 1), Integer.parseInt(parsedText.get(i + 2)), Integer.parseInt(parsedText.get(i + 3)), Integer.parseInt(parsedText.get(i + 4)), Integer.parseInt(parsedText.get(i + 5)), Integer.parseInt(parsedText.get(i + 6)), Integer.parseInt(parsedText.get(i + 7)), Integer.parseInt(parsedText.get(i + 8)));
                        i += 9;
                        break;
                    }
                    case "i": {
                        System.out.println("Insert into a Grid File");
                        fileManager.add(parsedText.get(i + 1), new GridRecord(parsedText.get(i + 2), Float.parseFloat(parsedText.get(i + 3)), Float.parseFloat(parsedText.get(i + 4))));
                        i += 5;
                        break;
                    }
                    case "l": {
                        System.out.println("Lookup from a Grid File ");
                        fileManager.test1 = fileManager.lookup(parsedText.get(i + 1), new GridPoint(Float.parseFloat(parsedText.get(i + 2)), Float.parseFloat(parsedText.get(i + 3))), new GridPoint(Float.parseFloat(parsedText.get(i + 4)), Float.parseFloat(parsedText.get(i + 5))), Integer.parseInt(parsedText.get(i + 6)), Integer.parseInt(parsedText.get(i + 7)));
                        i += 8;
                        break;
                    }
                    default: {
                        System.out.println("Invalid option");
                        i++;
                        break;
                    }
                }
            }
        }
        catch (IndexOutOfBoundsException e)
        {
        }
        fileManager.close(fileManager.conn);
    }
}