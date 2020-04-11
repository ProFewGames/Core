package xyz.ufactions.chat.redis;

import xyz.ufactions.redis.ServerCommand;

public class RedisMessage extends ServerCommand {

    private String sender;
    private String target;

    private String server;
    private String message;

    public RedisMessage(String server, String sender, String receiver, String message) {
        super();
        this.server = server;
        this.sender = sender;
        this.target = receiver;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getTarget() {
        return target;
    }

    public String getSender() {
        return sender;
    }

    public String getServer() {
        return server;
    }
}