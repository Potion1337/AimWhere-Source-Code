package me.kiras.aimwhere.utils.other;
public class DecodeUtils {
    public static String getText(String name) {
        char[] c = name.toCharArray();
        for(int i = 0; i < c.length; i++) {
            c[i] = (char)(c[i] ^ 20000);
        }
        return new String(c);
    }
}
