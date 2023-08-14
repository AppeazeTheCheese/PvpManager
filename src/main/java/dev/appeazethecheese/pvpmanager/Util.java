package dev.appeazethecheese.pvpmanager;

import java.util.LinkedHashSet;

public class Util {
    public static String FormatTimeout(int seconds){
        var days = seconds / (60 * 60 * 24);
        var hours = seconds / (60 * 60);
        var minutes = (seconds % (60 * 60)) / 60;
        var secs = seconds % 60;

        var parts = new LinkedHashSet<String>();

        if(days > 0){
            var dayStr = days + "d";
            parts.add(dayStr);
        }

        if(hours > 0 || parts.size() > 0){
            var hourStr = hours + "h";
            parts.add(hourStr);
        }

        if(minutes > 0 || parts.size() > 0){
            var minuteStr = minutes + "m";
            parts.add(minuteStr);
        }

        var secondStr = "";
        if(parts.size() > 0){
            secondStr += "and ";
        }
        secondStr += secs + "s";
        parts.add(secondStr);

        return String.join(", ", parts);
    }
}
