public class GridRecord
{
    public String label;
    public GridPoint point;
    public GridRecord(String label, GridPoint point)
    {
        this.label = label;
        this.point = point;
    }
    public GridRecord(String label, float x, float y)
    {
        this.label = label;
        this.point = new GridPoint(x, y);
    }
    public String toString()
    {
        return label + point.toString();
    }
}