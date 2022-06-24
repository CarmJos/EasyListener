package cc.carm.lib.easylistener.defaults;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.WorldEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

public class EventFilters {

    public <T extends Cancellable> Predicate<T> isCancelled() {
        return Cancellable::isCancelled;
    }

    public <T extends Cancellable> Predicate<T> notCancelled() {
        return e -> !e.isCancelled();
    }

    public <T extends PlayerMoveEvent> Predicate<T> moveInSameBlock() {
        return e -> {
            if (e.getTo() == null) return false;

            World fromWorld = e.getFrom().getWorld();
            World toWorld = e.getTo().getWorld();
            if (!Objects.equals(fromWorld, toWorld)) return false;

            return e.getFrom().getBlockX() != e.getTo().getBlockX() ||
                   e.getFrom().getBlockZ() != e.getTo().getBlockZ() ||
                   e.getFrom().getBlockY() != e.getTo().getBlockY();
        };
    }

    public static <T extends PlayerInteractEvent> Predicate<T> interactAction(Action... actionType) {
        return e -> Arrays.stream(actionType).anyMatch(a -> a == e.getAction());
    }

    public static <T extends InventoryClickEvent> Predicate<T> invClickType(ClickType... clickTypes) {
        return e -> Arrays.stream(clickTypes).anyMatch(a -> a == e.getClick());
    }

    public static <T extends PlayerEvent> Predicate<T> playerIsOp() {
        return e -> e.getPlayer().isOp();
    }

    public static <T extends PlayerEvent> Predicate<T> playerHasPerm(@NotNull String permission) {
        return e -> e.getPlayer().hasPermission(permission);
    }

    public static <T extends PlayerEvent> Predicate<T> playerInWorld(@NotNull String worldName) {
        return e -> e.getPlayer().getWorld().getName().equals(worldName);
    }

    public <T extends EntityEvent> Predicate<T> entityOf(@NotNull EntityType type) {
        return e -> e.getEntityType() == type;
    }

    public <T extends WorldEvent> Predicate<T> worldOf(@NotNull String worldName) {
        return e -> e.getWorld().getName().equals(worldName);
    }

    public <T extends BlockEvent> Predicate<T> blockOf(@NotNull Material material) {
        return e -> e.getBlock().getType() == material;
    }

    public <T extends BlockEvent> Predicate<T> blockInWorld(@NotNull String worldName) {
        return e -> e.getBlock().getWorld().getName().equals(worldName);
    }

}
