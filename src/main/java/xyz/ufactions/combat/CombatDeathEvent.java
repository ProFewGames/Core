package xyz.ufactions.combat;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDeathEvent;

public class CombatDeathEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private EntityDeathEvent event;
    private ClientCombat clientCombat;
    private CombatLog log;
    private DeathMessageType messageType = DeathMessageType.Detailed;

    public CombatDeathEvent(EntityDeathEvent event, ClientCombat clientCombat, CombatLog log) {
        this.event = event;
        this.clientCombat = clientCombat;
        this.log = log;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public ClientCombat getClientCombat() {
        return clientCombat;
    }

    public CombatLog getLog() {
        return log;
    }

    public EntityDeathEvent getEvent() {
        return event;
    }

    public void setBroadcastType(DeathMessageType messageType) {
        this.messageType = messageType;
    }

    public DeathMessageType getBroadcastType() {
        return messageType;
    }
}
