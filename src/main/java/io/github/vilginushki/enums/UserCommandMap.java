package io.github.vilginushki.enums;


public enum UserCommandMap {
    GETADDR("getaddr"),
    GETDATA("getdata"),
    SCAN("scan"),
    EXIT("exit"),
    STOP("stop"),
    PING("ping");

    private final String name;

    UserCommandMap(String name) {
        this.name = name;
    }

    public static String getInfo(UserCommandMap command) {
        return switch (command) {
            case GETADDR ->
                    "Requests info about active peers. Nodes respond with ADDR messages containing peer information.";
            case GETDATA -> "Requests a single block specified by hash.";
            case STOP -> "Ends scanning task.";
            case PING -> "Sends a ping to the target node.";
            default -> "ERROR";
        };

    }

    public static String getInfo(String command) {
        for (UserCommandMap value : UserCommandMap.values()) {
            if (value.name.equalsIgnoreCase(command))
                return UserCommandMap.getInfo(value);
        }
        return "Unknown command";
    }

    @Override
    public String toString() {
        return name.toLowerCase();
    }
}
