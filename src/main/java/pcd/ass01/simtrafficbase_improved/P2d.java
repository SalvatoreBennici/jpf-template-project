package pcd.ass01.simtrafficbase_improved;

/**
 * Represents a point in a 2D space
 */
public class P2d {
    private final double x;
    private final double y;

    public P2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public P2d sum(V2d v) {
        return new P2d(x + v.getX(), y + v.getY());
    }

    public V2d sub(P2d v) {
        return new V2d(x - v.x, y - v.y);
    }

    public String toString() {
        return "P2d(" + x + "," + y + ")";
    }

    public static double len(P2d p0, P2d p1) {
        double dx = p0.x - p1.x;
        double dy = p0.y - p1.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
