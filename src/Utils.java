package src;

public class Utils {
    public static String doubleTostring(double d) {
        if (d % 1 == 0) {
            return String.valueOf((int) d);
        }
        return String.valueOf(d);
    }
}
