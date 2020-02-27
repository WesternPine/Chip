package proj.chip.utility;

public class Output {
    
    public static void print(String string) {
        System.out.println("[Chip] >> " + string);
    }
    
    public static void success(String string) {
        System.out.println("[Chip Success] >> " + string);
    }
    
    public static void error(String string) {
        System.out.println("[Chip Error] >> " + string);
    }
    
    public static void fatal(String string) {
        System.out.println("[Chip Fatal] >> " + string);
        System.out.println("Shutting down.");
    }

}
