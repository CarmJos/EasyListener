package cc.carm.lib.easylistener.defaults;

import cc.carm.lib.easylistener.EasyListener;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class CommonListeners {

    public static void cancelJoinMessage(@NotNull EasyListener source) {
        handleJoinMessage(source, null);
    }

    public static void handleJoinMessage(@NotNull EasyListener source, @Nullable Function<Player, String> joinMessage) {
        final Function<Player, String> message = Optional.ofNullable(joinMessage).orElse(t -> "");
        source.handle(PlayerJoinEvent.class, (event) -> event.setJoinMessage(message.apply(event.getPlayer())));
    }

    public static void cancelQuitMessage(@NotNull EasyListener source) {
        handleQuitMessage(source, null);
    }

    public static void handleQuitMessage(@NotNull EasyListener source, @Nullable Function<Player, String> quitMessage) {
        final Function<Player, String> message = Optional.ofNullable(quitMessage).orElse(t -> "");
        source.handle(PlayerQuitEvent.class, (event) -> event.setQuitMessage(message.apply(event.getPlayer())));
    }

    public static void cancelWeatherChange(@NotNull EasyListener source) {
        cancelWeatherChange(source, null);
    }

    public static void cancelWeatherChange(@NotNull EasyListener source,
                                           @Nullable Predicate<WeatherChangeEvent> weatherPredicate) {
        source.cancel(WeatherChangeEvent.class, weatherPredicate);
    }

    public static void cancelBreak(@NotNull EasyListener source, @Nullable Predicate<Player> playerPredicate) {
        source.handleBundle(Player.class)
                .from(BlockBreakEvent.class, BlockBreakEvent::getPlayer)
                .from(PlayerBucketFillEvent.class, PlayerBucketFillEvent::getPlayer)
                .filter(playerPredicate).cancel();
    }

    public static void cancelPlace(@NotNull EasyListener source, @Nullable Predicate<Player> playerPredicate) {
        source.handleBundle(Player.class)
                .from(BlockPlaceEvent.class, BlockPlaceEvent::getPlayer)
                .from(PlayerBucketEmptyEvent.class, PlayerBucketEmptyEvent::getPlayer)
                .filter(playerPredicate).cancel();
    }

    /**
     * 有条件的取消玩家PVP。
     *
     * @param source    用于注册的源 {@link  EasyListener} 对象。
     * @param predicate 判断器，返回true则取消事件。两参数分别为 attacker 与 victim 。
     */
    public static void cancelPVP(@NotNull EasyListener source,
                                 @Nullable BiPredicate<Player, Player> predicate) {
        final BiPredicate<Player, Player> p = Optional.ofNullable(predicate).orElse((attacker, victim) -> true);
        cancelAttack(source, (attacker, damager) -> {
            if (!(attacker instanceof Player) || !(damager instanceof Player)) return false;
            else return p.test((Player) attacker, (Player) damager);
        });
    }

    /**
     * 有条件的取消两个实体间的伤害。
     *
     * @param source    用于注册的源 {@link  EasyListener} 对象。
     * @param predicate 判断器，返回true则取消事件。两参数分别为 attacker 与 victim 。
     */
    public static void cancelAttack(@NotNull EasyListener source,
                                    @Nullable BiPredicate<Entity/*attacker*/, Entity/*victim*/> predicate) {
        final BiPredicate<Entity, Entity> p = Optional.ofNullable(predicate).orElse((attacker, victim) -> true);
        source.cancel(EntityDamageByEntityEvent.class, (event) -> p.test(event.getDamager(), event.getEntity()));
    }

    public static void cancelDeath(@NotNull EasyListener source,
                                   @Nullable Predicate<Player> predicate) {
        cancelDeath(source, predicate, event -> {
            event.setDeathMessage(null);
            event.setKeepInventory(true);
            event.setKeepLevel(true);
        });
    }

    @SuppressWarnings("deprecation")
    public static void cancelDeath(@NotNull EasyListener source,
                                   @Nullable Predicate<Player> predicate,
                                   @Nullable Consumer<PlayerDeathEvent> handler) {
        final Predicate<Player> p = Optional.ofNullable(predicate).orElse((player) -> true);
        source.handle(PlayerDeathEvent.class, (event) -> {
            if (!p.test(event.getEntity())) return;
            event.getEntity().setHealth(event.getEntity().getMaxHealth());
            Optional.ofNullable(handler).ifPresent(consumer -> consumer.accept(event));
        });
    }

    public static void cancelSpawn(@NotNull EasyListener source,
                                   @Nullable BiPredicate<Entity, Location> predicate) {
        final BiPredicate<Entity, Location> p = Optional.ofNullable(predicate).orElse((entity, location) -> !(entity instanceof Player));
        source.cancel(EntitySpawnEvent.class, (event) -> p.test(event.getEntity(), event.getLocation()));
    }

}
