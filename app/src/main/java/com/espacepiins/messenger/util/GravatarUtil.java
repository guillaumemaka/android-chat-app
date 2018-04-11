package com.espacepiins.messenger.util;

public class GravatarUtil {
    public static String getGravatar(String email, int size) {
        if (email == null)
            email = "person@example.com";
        else if (email.isEmpty())
            email = "person@example.com";

        String hash = MD5Util.md5Hex(email);

        if (hash != null) {
            return String.format("http://www.gravatar.com/avatar/%s.jpg?s=%s&d=mm", hash, size);
        }

        return null;
    }
}
