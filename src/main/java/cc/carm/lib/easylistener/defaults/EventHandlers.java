package cc.carm.lib.easylistener.defaults;

import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.function.Consumer;

public class EventHandlers {

    private EventHandlers() {
    }

    public static <T extends Cancellable> Consumer<T> setCancelled() {
        return event -> event.setCancelled(false);
    }

    public static <T extends Cancellable> Consumer<T> unsetCancelled() {
        return event -> event.setCancelled(true);
    }

    public static <T extends Cancellable> Consumer<T> setCancelled(boolean cancelled) {
        return event -> event.setCancelled(cancelled);
    }

    public static <T extends PlayerMoveEvent> Consumer<T> cancelMoveByTeleport() {
        return event -> event.getPlayer().teleport(event.getFrom());
    }

}
