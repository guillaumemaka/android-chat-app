package com.espacepiins.messenger.application;

/**
 * Created by guillaume on 18-03-15.
 */

public final class FirebaseRefs {
    /**
     * Ex: /registered-users/$uid
     * Return:
     * {
     *     uid: $uid,
     *     email: user@example.com,
     *     phone: 18885555555 | null,
     *     displayName: "John Doe",
     *     registeredAt: 134123555525
     * }
     */
    public final static String REGISTERED_USERS_REF = "/registered-users";

    /**
     * Ex: /rooms/$roomuid
     * Return:
     * {
     * from: uid,
     * fromDisplayname,
     * to: uid,
     * toDisplayname
     * lastMessageUID: "bxvbhjyedfasdfadf00adsf",
     * lastMessage: "Hi!",
     * lastMessageTimestamp: 1324444445
     * createdAt: 1324444445
     * }
     */
    public final static String ROOMS_REF(String roomId) {
        return String.format("/rooms/%s", roomId);
    }

    /**
     * Ex: /users/$userUID/rooms
     * Return:
     * {
     *     from: uid,
     *     fromDisplayname,
     *     to: uid,
     *     toDisplayname
     *     lastMessageUID: "bxvbhjyedfasdfadf00adsf",
     *     lastMessage: "Hi!",
     *     lastMessageTimestamp: 1324444445
     *     createdAt: 1324444445
     * }
     */
    public final static String USER_ROOMS_REF(String userId) {
        return String.format("/users/%s/rooms", userId);
    }

    /**
     * Ex: /messages/$roomuid/$messageKey
     * Return:
     * {
     *     roomUID: "adsfasgggggsfg",
     *     content: "Hi!",
     *     sender: uid,
     *     to: uid,
     *     timestamp: 13212213415
     * }
     */
    public final static String MESSAGES_REF(String roomUID) {
        return String.format("/messages/%s", roomUID);
    }

    /**
     * Ex: /profiles/$uuid
     * Return:
     * {
     *     avatarUrl: "https://cdn.example.com/avatar/...."
     *     uid: $uid,
     *     email: user@example.com,
     *     phone: 18885555555 | null,
     *     displayName: "John Doe",
     *     createdAt: 1324444445
     * }
     */
    public final static String USER_PROFILES_REF(String userUID) {
        return String.format("/profiles/%s", userUID);
    }

    /**
     * Ex: /users/$uuid/notificationTokens/$token
     * Return: {
     *     $token: true
     * }
     */
    public final static String USER_NOTIFICATION_TOKEN_REF(String userUID, String token) {
        return String.format("/users/%s/notificationTokens/%s", userUID, token);
    }

    /**
     * Ex: /users/$uuid/connected (connected return a boolean)
     */
    public final static String USERS_PRESENCE_REF(String userUID) {
        return String.format("/users/%s/connected", userUID);
    }

    /**
     * Ex: /users/$uid/lastOnline
     * Return: Boolean
     */
    public final static String USER_LAST_ONLINE_REF(String userUID) {
        return String.format("/users/%s/lastOnline", userUID);
    }

    public final static String USERS_AVATAR_STORAGE = "user-avatar";
}
