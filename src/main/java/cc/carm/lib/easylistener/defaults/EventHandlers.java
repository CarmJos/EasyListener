package cc.carm.lib.easylistener.defaults;

import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.function.Consumer;

public class EventHandlers {

    private EventHandlers() {
    }

    public static final Consumer<? extends Cancellable> SET_CANCELLED = event -> event.setCancelled(true);
    public static final Consumer<? extends Cancellable> UNSET_CANCELLED = event -> event.setCancelled(false);

    public static final Consumer<? extends PlayerMoveEvent> CANCEL_MOVE_BY_TELEPORT = event -> event.getPlayer().teleport(event.getFrom());

    public static Consumer<? extends Cancellable> setCancelled() {
        return SET_CANCELLED;
    }

    public static Consumer<? extends Cancellable> unsetCancelled() {
        return UNSET_CANCELLED;
    }

    public static Consumer<? extends Cancellable> setCancelled(boolean cancelled) {
        return cancelled ? SET_CANCELLED : UNSET_CANCELLED;
    }

    public static Consumer<? extends PlayerMoveEvent> cancelMoveByTeleport() {
        return CANCEL_MOVE_BY_TELEPORT;
    }

}
