package cc.carm.lib.easylistener;

import org.bukkit.Bukkit;
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
import org.bukkit.plugin.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 轻松(做)监听，简单快捷的通用Bukkit插件监听器类库。
 *
 * @author CarmJos
 * @since 1.0.0
 */
public class EasyListener implements Listener {

    protected final Plugin plugin;

    public EasyListener(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * 注销本监听器内的全部监听器。
     * <br> 也可以通过 {@link HandlerList#unregister(Listener)} 方法注销本监听器。
     */
    public void unregisterAll() {
        HandlerList.unregisterAll(this);
    }

    /**
     * 通过 {@link SimplePluginManager} 获取到一个事件类的 {@link HandlerList} 。
     *
     * @param eventClass 事件类
     * @return 事件类的 {@link HandlerList}
     */
    private @NotNull HandlerList getEventListeners(@NotNull Class<? extends Event> eventClass) {
        try {
            Method method = SimplePluginManager.class.getDeclaredMethod("getEventListeners", Class.class);
            method.setAccessible(true);
            return (HandlerList) method.invoke(Bukkit.getPluginManager(), eventClass);
        } catch (Exception e) {
            throw new IllegalPluginAccessException(e.toString());
        }
    }

    /**
     * 创建一个事件的执行器实例。
     *
     * @param eventClass    事件类
     * @param eventConsumer 事件执行内容
     * @param <T>           事件类型
     * @return 事件的执行器实例
     */
    private <T extends Event> EventExecutor createExecutor(@NotNull Class<T> eventClass,
                                                           @NotNull Consumer<T> eventConsumer) {
        return (listener, event) -> {
            try {
                if (!eventClass.isAssignableFrom(event.getClass())) return;
                eventConsumer.accept(eventClass.cast(event));
            } catch (Throwable t) {
                throw new EventException(t);
            }
        };
    }

    protected void register(@NotNull Class<? extends Event> eventClass, @NotNull RegisteredListener listener) {
        getEventListeners(eventClass).register(listener);
    }

    protected void register(@NotNull Class<? extends Event> eventClass, @NotNull EventExecutor executor,
                            @NotNull EventPriority priority, boolean ignoreCancelled) {
        register(eventClass, new RegisteredListener(this, executor, priority, this.plugin, ignoreCancelled));
    }

    /**
     * 处理一个事件。
     *
     * @param eventClass    {@link Event} 事件类
     * @param eventConsumer 处理方法
     * @param <T>           {@link Event} 事件的类型
     * @return 本实例
     */
    public <T extends Event> EasyListener handle(@NotNull Class<T> eventClass,
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
    public <T extends Event> EasyListener handle(@NotNull Class<T> eventClass, boolean ignoreCancelled,
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
    public <T extends Event> EasyListener handle(@NotNull Class<T> eventClass, @Nullable EventPriority priority,
                                                 @NotNull Consumer<T> eventConsumer) {
        return handle(eventClass, priority, false, eventConsumer);
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
    public <T extends Event> EasyListener handle(@NotNull Class<T> eventClass,
                                                 @Nullable EventPriority priority, boolean ignoreCancelled,
                                                 @NotNull Consumer<T> eventConsumer) {
        final EventPriority eventPriority = Optional.ofNullable(priority).orElse(EventPriority.NORMAL);
        final EventExecutor executor = createExecutor(eventClass, eventConsumer);
        register(eventClass, executor, eventPriority, ignoreCancelled);
        return this;
    }

    /**
     * 无判别地取消一个事件。
     *
     * @param eventClass {@link Event} 事件类
     * @param <T>        {@link Event} 事件的类型，必须实现 {@link Cancellable} 。
     * @return 本实例
     * @throws IllegalArgumentException 如果事件没有实现 {@link Cancellable} 则抛出此异常
     */
    public <T extends Event> EasyListener cancel(@NotNull Class<T> eventClass) {
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
    public <T extends Event> EasyListener cancel(@NotNull Class<T> eventClass, @Nullable Predicate<T> eventPredicate) {
        return cancel(eventClass, null, eventPredicate);
    }

    /**
     * 有条件地取消一个事件。
     *
     * @param eventClass     {@link Event} 事件类
     * @param priority       {@link EventPriority} 事件处理优先级
     * @param eventPredicate 判断事件是否可以取消的条件
     * @param <T>            {@link Event} 事件的类型，必须实现 {@link Cancellable} 。
     * @return 本实例
     * @throws IllegalArgumentException 如果事件没有实现 {@link Cancellable} 则抛出此异常
     */
    public <T extends Event> EasyListener cancel(@NotNull Class<T> eventClass, @Nullable EventPriority priority,
                                                 @Nullable Predicate<T> eventPredicate) {
        if (!Cancellable.class.isAssignableFrom(eventClass)) {
            throw new IllegalArgumentException("Event class " + eventClass.getName() + " is not cancellable");
        }

        Predicate<T> predicate = Optional.ofNullable(eventPredicate).orElse(t -> true);
        return handle(eventClass, priority, (event) -> {
            if (predicate.test(event)) ((Cancellable) event).setCancelled(true);
        });
    }

    public EasyListener cancelJoinMessage() {
        return handleJoinMessage(null);
    }

    public EasyListener handleJoinMessage(@Nullable Function<Player, String> joinMessage) {
        final Function<Player, String> message = Optional.ofNullable(joinMessage).orElse(t -> "");
        return handle(PlayerJoinEvent.class, (event) -> event.setJoinMessage(message.apply(event.getPlayer())));
    }

    public EasyListener cancelQuitMessage() {
        return handleQuitMessage(null);
    }

    public EasyListener handleQuitMessage(@Nullable Function<Player, String> quitMessage) {
        final Function<Player, String> message = Optional.ofNullable(quitMessage).orElse(t -> "");
        return handle(PlayerQuitEvent.class, (event) -> event.setQuitMessage(message.apply(event.getPlayer())));
    }

    public EasyListener cancelWeatherChange() {
        return cancelWeatherChange(null);
    }

    public EasyListener cancelWeatherChange(@Nullable Predicate<WeatherChangeEvent> weatherPredicate) {
        return cancel(WeatherChangeEvent.class, weatherPredicate);
    }

    public EasyListener cancelBreak(@Nullable Predicate<Player> player) {
        final Predicate<Player> predicate = Optional.ofNullable(player).orElse(t -> true);
        return cancelBreak(
                (event) -> predicate.test(event.getPlayer()),
                (event) -> predicate.test(event.getPlayer())
        );
    }

    public EasyListener cancelBreak(@Nullable Predicate<BlockBreakEvent> blockBreakPredicate,
                                    @Nullable Predicate<PlayerBucketFillEvent> bucketFillPredicate) {
        return cancel(BlockBreakEvent.class, blockBreakPredicate)
                .cancel(PlayerBucketFillEvent.class, bucketFillPredicate);
    }

    public EasyListener cancelPlace(@Nullable Predicate<Player> player) {
        final Predicate<Player> predicate = Optional.ofNullable(player).orElse(t -> true);
        return cancelPlace(
                (event) -> predicate.test(event.getPlayer()),
                (event) -> predicate.test(event.getPlayer())
        );
    }

    public EasyListener cancelPlace(@Nullable Predicate<BlockPlaceEvent> blockBreakPredicate,
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
    public EasyListener cancelPVP(@Nullable BiPredicate<Player, Player> predicate) {
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
    public EasyListener cancelAttack(@Nullable BiPredicate<Entity/*attacker*/, Entity/*victim*/> predicate) {
        final BiPredicate<Entity, Entity> p = Optional.ofNullable(predicate).orElse((attacker, victim) -> true);
        return cancel(EntityDamageByEntityEvent.class, (event) -> p.test(event.getDamager(), event.getEntity()));
    }

    public EasyListener cancelDeath(@Nullable Predicate<Player> predicate) {
        return cancelDeath(predicate, (event) -> {
            event.setDeathMessage(null);
            event.setKeepInventory(true);
            event.setKeepLevel(true);
        });
    }

    public EasyListener cancelDeath(@Nullable Predicate<Player> predicate,
                                    @Nullable Consumer<PlayerDeathEvent> handler) {
        final Predicate<Player> p = Optional.ofNullable(predicate).orElse((player) -> true);
        return handle(PlayerDeathEvent.class, (event) -> {
            if (!p.test(event.getEntity())) return;
            event.getEntity().setHealth(event.getEntity().getMaxHealth());
            Optional.ofNullable(handler).ifPresent(consumer -> consumer.accept(event));
        });
    }

    public EasyListener cancelSpawn(@Nullable BiPredicate<Entity, Location> predicate) {
        final BiPredicate<Entity, Location> p = Optional.ofNullable(predicate).orElse((entity, location) -> !(entity instanceof Player));
        return cancel(EntitySpawnEvent.class, (event) -> p.test(event.getEntity(), event.getLocation()));
    }

}
