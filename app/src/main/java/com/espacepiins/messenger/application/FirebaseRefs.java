package com.espacepiins.messenger.application;

/**
 * Created by guillaume on 18-03-15.
 */

public final class FirebaseRefs {
    /**
     * Ex: registered-users/$uid
     * Return:
     * {
     *     uid: $uid,
     *     email: user@example.com,
     *     phone: 18885555555 | null,
     *     displayName: "John Doe",
     *     registeredAt: 134123555525
     * }
     */
    public final static String REGISTERED_USERS_REF = "registered-users";

    /**
     * Ex: rooms/$roomuid
     * Return:
     * {
     *     from: uid,
     *     to: uid
     *     lastMessageUID: "bxvbhjyedfasdfadf00adsf",
     *     lastMessage: "Hi!",
     *     lastMessageTimestamp: 1324444445
     *     displayName: "John Doe",
     *     createdAt: 1324444445
     * }
     */
    public final static String ROOMS_REF = "rooms";

    /**
     * Ex: messages/$roomuid/$messageKey
     * Return:
     * {
     *     content: "Hi!",
     *     content_type: "plain/text",
     *     sender: uid,
     *     sentAt: 13212213415
     * }
     */
    public final static String MESSAGES_REF = "messages";

    /**
     * Ex: profiles/$uuid
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
    public final static String USER_PROFILES_REF = "profiles";

    /**
     * Ex: .connected-users/$uid/connected (connected return a boolean)
     */
    public final static String USERS_PRESENCE_REF = ".connected-users";

    /**
     * Ex: .stats/$uid
     * Return:
     * {
     *     lastSeen: 123134551235
     * }
     */
    public final static String USER_STATS_REF = ".stats";
}
