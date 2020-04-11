package xyz.ufactions.chat.redis;

import xyz.ufactions.redis.ServerCommand;

public class RedisMessageCallback extends ServerCommand {

    private RedisMessage message;

    public RedisMessageCallback(RedisMessage message) {
        this.message = message;
    }

    public RedisMessage getMessage() {
        return message;
    }
}