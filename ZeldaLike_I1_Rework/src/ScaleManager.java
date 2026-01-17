public class ScaleManager {

    private static double scale = 1.0;

    public static double getScale() {
        return scale;
    }

    public static void zoomIn() {
        scale += 0.1;
    }

    public static void zoomOut() {
        scale = Math.max(0.5, scale - 0.1);
    }
}

