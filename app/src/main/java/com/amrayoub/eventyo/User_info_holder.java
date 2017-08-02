package com.amrayoub.eventyo;

/**
 * Created by Amr Ayoub on 8/1/2017.
 */

public class User_info_holder {
    private static User_info minput = null;
    public static void setInput(User_info value) { minput = value; }
    public static User_info getInput() { return minput; }
}
