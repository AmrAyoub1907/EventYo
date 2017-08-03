package com.amrayoub.eventyo;

/**
 * Created by Amr Ayoub on 8/1/2017.
 */

public class UserInfoHolder {
    private static UserInfo minput = null;
    public static void setInput(UserInfo value) { minput = value; }
    public static UserInfo getInput() { return minput; }
}
