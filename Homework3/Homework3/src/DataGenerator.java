import java.io.*;

public class DataGenerator {

    public static void makeData(int startNumber, int numberRows, int wrapNumber, String fileName) {
        try {
            PrintStream outFile = new PrintStream(new FileOutputStream(fileName));
            int column = 1;
            int endNumber = startNumber + numberRows;
            String row;
            for (int columnA = startNumber; columnA < endNumber; columnA++) {
                row = "" + columnA + " " + column;
                outFile.println(row);
                column = (column >= wrapNumber) ? 1 : column + (int)(Math.random()*wrapNumber);
            }
            outFile.close();
        } catch (Exception ie) {
            ie.printStackTrace();
        }
    }

    public static void main(String[] args) {
        makeData(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]), args[3]);
    }
}