package dev.westernpine.utility;

public class Levels {

    private static final long threshhold = 50;
    private static final long multiplier = 8;
    
    public static long getLevel(long messages) {
        Double level = (1+Math.sqrt(1+multiplier*messages/threshhold))/2;
        return level.longValue();
    }
    
    public static long getTotalForLevel(long level) {
        return (((((level*2)-1)*((level*2)-1))-1)/multiplier)*threshhold;
    }
    
    public static long untilNextLevel(long messages) {
        long level = getLevel(messages);
        long needed = getTotalForLevel(level+1);
        return needed - messages;
    }

}
