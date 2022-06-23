package cc.carm.lib.easylistener;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
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
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 轻松(做)监听，简单快捷的通用Bukkit插件监听器类库。
 *
 * @author CarmJos
 */
public interface EasyListener extends Listener {

    /**
     * 创建一个新的 {@link EasyListener} 实例
     *
     * @param plugin {@link Plugin}插件实例
     * @return {@link EasyListener} 实例
     */
    static @NotNull ListenerManager create(@NotNull Plugin plugin) {
        return new ListenerManager(plugin);
    }

    /**
     * 注销本监听器内的全部监听器。
     * <br> 也可以通过 {@link HandlerList#unregister(Listener)} 方法注销本监听器。
     */
    default void unregisterAll() {
        HandlerList.unregisterAll(this);
    }

    /**
     * 处理一个事件。
     *
     * @param eventClass      {@link Event} 事件类
     * @param priority        {@link EventPriority} 事件处理优先级
     * @param ignoreCancelled 是否忽略掉已经被取消的事件
     * @param eventConsumer   处理方法
     * @param <T>             {@link Event} 事件的类型
     * @return 本实例
     */
    <T extends Event> EasyListener handle(@NotNull Class<T> eventClass,
                                          @Nullable EventPriority priority, boolean ignoreCancelled,
                                          @NotNull Consumer<T> eventConsumer);

    /**
     * 有条件地取消一个事件。
     * <br> 本方法在条件满足时对事件进行取消，不会改变事件取消的状态。
     * <br> 因此，已经被取消的事件将不再进行判断和取消。。
     *
     * @param eventClass     {@link Event} 事件类
     * @param priority       {@link EventPriority} 事件处理优先级
     * @param eventPredicate 判断事件是否可以取消的条件
     * @param <T>            {@link Event} 事件的类型，必须实现 {@link Cancellable} 。
     * @return 本实例
     * @throws IllegalArgumentException 如果事件没有实现 {@link Cancellable} 则抛出此异常
     */
    <T extends Event> EasyListener cancel(@NotNull Class<T> eventClass,
                                          @Nullable EventPriority priority,
                                          @Nullable Predicate<T> eventPredicate);

    /**
     * 处理一个事件。
     *
     * @param eventClass    {@link Event} 事件类
     * @param eventConsumer 处理方法
     * @param <T>           {@link Event} 事件的类型
     * @return 本实例
     */
    default <T extends Event> EasyListener handle(@NotNull Class<T> eventClass,
                                                  @NotNull Consumer<T> eventConsumer) {
        return handle(eventClass, null, eventConsumer);
    }

    /**
     * 处理一个事件。
     *
     * @param eventClass      {@link Event} 事件类
     * @param ignoreCancelled 是否忽略掉已经被取消的事件
     * @param eventConsumer   处理方法
     * @param <T>             {@link Event} 事件的类型
     * @return 本实例
     */
    default <T extends Event> EasyListener handle(@NotNull Class<T> eventClass,
                                                  boolean ignoreCancelled,
                                                  @NotNull Consumer<T> eventConsumer) {
        return handle(eventClass, null, ignoreCancelled, eventConsumer);
    }

    /**
     * 处理一个事件。
     *
     * @param eventClass    {@link Event} 事件类
     * @param priority      {@link EventPriority} 事件处理优先级
     * @param eventConsumer 处理方法
     * @param <T>           {@link Event} 事件的类型
     * @return 本实例
     */
    default <T extends Event> EasyListener handle(@NotNull Class<T> eventClass,
                                                  @Nullable EventPriority priority,
                                                  @NotNull Consumer<T> eventConsumer) {
        return handle(eventClass, priority, false, eventConsumer);
    }

    /**
     * 无判别地取消一个事件。
     *
     * @param eventClass {@link Event} 事件类
     * @param <T>        {@link Event} 事件的类型，必须实现 {@link Cancellable} 。
     * @return 本实例
     * @throws IllegalArgumentException 如果事件没有实现 {@link Cancellable} 则抛出此异常
     */
    default <T extends Event> EasyListener cancel(@NotNull Class<T> eventClass) {
        return cancel(eventClass, null, null);
    }

    /**
     * 有条件地取消一个事件。
     *
     * @param eventClass     {@link Event} 事件类
     * @param eventPredicate 判断事件是否可以取消的条件
     * @param <T>            {@link Event} 事件的类型，必须实现 {@link Cancellable} 。
     * @return 本实例
     * @throws IllegalArgumentException 如果事件没有实现 {@link Cancellable} 则抛出此异常
     */
    default <T extends Event> EasyListener cancel(@NotNull Class<T> eventClass,
                                                  @Nullable Predicate<T> eventPredicate) {
        return cancel(eventClass, null, eventPredicate);
    }

    // >---------------------------
    // 预设快捷操作方法

    default EasyListener cancelJoinMessage() {
        return handleJoinMessage(null);
    }

    default EasyListener handleJoinMessage(@Nullable Function<Player, String> joinMessage) {
        final Function<Player, String> message = Optional.ofNullable(joinMessage).orElse(t -> "");
        return handle(PlayerJoinEvent.class, (event) -> event.setJoinMessage(message.apply(event.getPlayer())));
    }

    default EasyListener cancelQuitMessage() {
        return handleQuitMessage(null);
    }

    default EasyListener handleQuitMessage(@Nullable Function<Player, String> quitMessage) {
        final Function<Player, String> message = Optional.ofNullable(quitMessage).orElse(t -> "");
        return handle(PlayerQuitEvent.class, (event) -> event.setQuitMessage(message.apply(event.getPlayer())));
    }

    default EasyListener cancelWeatherChange() {
        return cancelWeatherChange(null);
    }

    default EasyListener cancelWeatherChange(@Nullable Predicate<WeatherChangeEvent> weatherPredicate) {
        return cancel(WeatherChangeEvent.class, weatherPredicate);
    }

    default EasyListener cancelBreak(@Nullable Predicate<Player> player) {
        final Predicate<Player> predicate = Optional.ofNullable(player).orElse(t -> true);
        return cancelBreak(
                (event) -> predicate.test(event.getPlayer()),
                (event) -> predicate.test(event.getPlayer())
        );
    }

    default EasyListener cancelBreak(@Nullable Predicate<BlockBreakEvent> blockBreakPredicate,
                                     @Nullable Predicate<PlayerBucketFillEvent> bucketFillPredicate) {
        return cancel(BlockBreakEvent.class, blockBreakPredicate)
                .cancel(PlayerBucketFillEvent.class, bucketFillPredicate);
    }

    default EasyListener cancelPlace(@Nullable Predicate<Player> player) {
        final Predicate<Player> predicate = Optional.ofNullable(player).orElse(t -> true);
        return cancelPlace(
                (event) -> predicate.test(event.getPlayer()),
                (event) -> predicate.test(event.getPlayer())
        );
    }

    default EasyListener cancelPlace(@Nullable Predicate<BlockPlaceEvent> blockBreakPredicate,
                                     @Nullable Predicate<PlayerBucketEmptyEvent> bucketEmptyPredicate) {
        return cancel(BlockPlaceEvent.class, blockBreakPredicate)
                .cancel(PlayerBucketEmptyEvent.class, bucketEmptyPredicate);
    }

    /**
     * 有条件的取消玩家PVP。
     *
     * @param predicate 判断器，返回true则取消事件。两参数分别为 attacker 与 victim 。
     * @return 当前实例
     */
    default EasyListener cancelPVP(@Nullable BiPredicate<Player, Player> predicate) {
        final BiPredicate<Player, Player> p = Optional.ofNullable(predicate).orElse((attacker, victim) -> true);
        return cancelAttack((attacker, damager) -> {
            if (!(attacker instanceof Player) || !(damager instanceof Player)) return false;
            else return p.test((Player) attacker, (Player) damager);
        });
    }

    /**
     * 有条件的取消两个实体间的伤害。
     *
     * @param predicate 判断器，返回true则取消事件。两参数分别为 attacker 与 victim 。
     * @return 当前实例
     */
    default EasyListener cancelAttack(@Nullable BiPredicate<Entity/*attacker*/, Entity/*victim*/> predicate) {
        final BiPredicate<Entity, Entity> p = Optional.ofNullable(predicate).orElse((attacker, victim) -> true);
        return cancel(EntityDamageByEntityEvent.class, (event) -> p.test(event.getDamager(), event.getEntity()));
    }

    default EasyListener cancelDeath(@Nullable Predicate<Player> predicate) {
        return cancelDeath(predicate, (event) -> {
            event.setDeathMessage(null);
            event.setKeepInventory(true);
            event.setKeepLevel(true);
        });
    }

    @SuppressWarnings("deprecation")
    default EasyListener cancelDeath(@Nullable Predicate<Player> predicate,
                                     @Nullable Consumer<PlayerDeathEvent> handler) {
        final Predicate<Player> p = Optional.ofNullable(predicate).orElse((player) -> true);
        return handle(PlayerDeathEvent.class, (event) -> {
            if (!p.test(event.getEntity())) return;
            event.getEntity().setHealth(event.getEntity().getMaxHealth());
            Optional.ofNullable(handler).ifPresent(consumer -> consumer.accept(event));
        });
    }

    default EasyListener cancelSpawn(@Nullable BiPredicate<Entity, Location> predicate) {
        final BiPredicate<Entity, Location> p = Optional.ofNullable(predicate).orElse((entity, location) -> !(entity instanceof Player));
        return cancel(EntitySpawnEvent.class, (event) -> p.test(event.getEntity(), event.getLocation()));
    }

}
