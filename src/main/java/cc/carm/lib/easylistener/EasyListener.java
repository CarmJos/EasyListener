package cc.carm.lib.easylistener;

import cc.carm.lib.easylistener.handler.BundleEventHandler;
import cc.carm.lib.easylistener.handler.MultiEventHandler;
import cc.carm.lib.easylistener.handler.SingleEventHandler;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.plugin.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 轻松(做)监听，简单快捷的通用Bukkit插件监听器类库。
 *
 * @author CarmJos
 */
public class EasyListener implements Listener {

    protected final Plugin plugin;

    public EasyListener(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * 创建一个新的 {@link EasyListener} 实例
     *
     * @param plugin {@link Plugin}插件实例
     * @return {@link EasyListener} 实例
     */
    public static @NotNull EasyListener create(@NotNull Plugin plugin) {
        return new EasyListener(plugin);
    }

    /**
     * 注销本监听器内的全部监听器。
     * <br> 也可以通过 {@link HandlerList#unregister(Listener)} 方法注销本监听器。
     */
    public void unregisterAll() {
        HandlerList.unregisterAll(this);
    }

    /**
     * 利用 functional 接口对单个 {@link Event} 进行监听处理。
     *
     * @param eventClass {@link Event} 事件类
     * @param <E>        {@link Event} 事件的类型
     * @return {@link SingleEventHandler} 构造器实例
     */
    public <E extends Event> @NotNull SingleEventHandler<E> handleEvent(@NotNull Class<E> eventClass) {
        return new SingleEventHandler<>(this, eventClass);
    }

    /**
     * 利用 functional 接口对多个同类 {@link Event} 进行监听处理。
     *
     * <br> 创建后，通过{@link MultiEventHandler#from(Class)} 申明具体监听的事件类型。
     *
     * @param eventType {@link Event} 的主要类型，如 {@link PlayerEvent}、{@link BlockEvent} 等。
     * @param <E>       {@link Event} 的类型
     * @return {@link MultiEventHandler} 构造器实例
     */
    public <E extends Event> @NotNull MultiEventHandler<E> handleEvents(@NotNull Class<E> eventType) {
        return new MultiEventHandler<>(this, eventType);
    }

    /**
     * 利用 functional 接口对多个同属 {@link Event} 进行监听处理。
     * <br> 创建后，通过{@link BundleEventHandler#from(Class, Function)}  申明具体监听的事件类型与转换函数。
     * <br> 通过转换函数，即可以将 {@link Event} 转换为给定类型实例，以方便统一处理。
     *
     * @param elementClass 要处理的目标类型类，如 {@link Player}、{@link Block} 等。
     * @param <T>          要处理的目标类型
     * @return {@link BundleEventHandler} 构造器实例
     */
    public <T> @NotNull BundleEventHandler<T, Event> handleBundle(@NotNull Class<T> elementClass) {
        return handleBundle(elementClass, Event.class);
    }

    /**
     * 利用 functional 接口对多个同属 {@link Event} 进行监听处理。
     * <br> 创建后，通过{@link BundleEventHandler#from(Class, Function)}  申明具体监听的事件类型与转换函数。
     * <br> 通过转换函数，即可以将 {@link Event} 转换为给定类型实例，以方便统一处理。
     *
     * @param elementClass 要处理的目标类型类，如 {@link Player}、{@link Block} 等。
     * @param eventType    {@link Event} 的主要类型，如 {@link PlayerEvent}、{@link BlockEvent} 等。
     * @param <T>          要处理的目标类型
     * @return {@link BundleEventHandler} 构造器实例
     */
    public <T, E extends Event> @NotNull BundleEventHandler<T, E> handleBundle(@NotNull Class<T> elementClass,
                                                                               @NotNull Class<E> eventType) {
        return new BundleEventHandler<>(this, elementClass, eventType);
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
        register(eventClass, eventConsumer, Optional.ofNullable(priority).orElse(EventPriority.NORMAL), ignoreCancelled);
        return this;
    }

    /**
     * 有条件地取消一个事件。
     * <br> 本方法在条件满足时对事件进行取消，不会改变事件取消的状态。
     * <br> 因此，已经被取消的事件将不再进行判断和取消。。
     *
     * @param eventClass     {@link Event} 事件类
     * @param priority       {@link EventPriority} 事件处理优先级
     * @param eventPredicate 判断事件是否可以取消的条件
     * @param afterCancelled 当事件被取消后执行的方法
     * @param <T>            {@link Event} 事件的类型，必须实现 {@link Cancellable} 。
     * @return 本实例
     * @throws IllegalArgumentException 如果事件没有实现 {@link Cancellable} 则抛出此异常
     */
    public <T extends Event> EasyListener cancel(@NotNull Class<T> eventClass,
                                                 @Nullable EventPriority priority,
                                                 @Nullable Predicate<? super T> eventPredicate,
                                                 @Nullable Consumer<? super T> afterCancelled) {
        if (!Cancellable.class.isAssignableFrom(eventClass)) {
            throw new IllegalArgumentException("Event class " + eventClass.getName() + " is not cancellable");
        }

        final Predicate<? super T> predicate = Optional.ofNullable(eventPredicate).orElse(t -> true);
        return handle(eventClass, priority, true, (event) -> {
            if (predicate.test(event)) {
                ((Cancellable) event).setCancelled(true);
                if (afterCancelled != null) afterCancelled.accept(event);
            }
        });
    }

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
    public <T extends Event> EasyListener cancel(@NotNull Class<T> eventClass,
                                                 @Nullable EventPriority priority,
                                                 @Nullable Predicate<? super T> eventPredicate) {
        return cancel(eventClass, priority, eventPredicate, null);
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
    public <T extends Event> EasyListener handle(@NotNull Class<T> eventClass,
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
    public <T extends Event> EasyListener handle(@NotNull Class<T> eventClass,
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
    public <T extends Event> EasyListener cancel(@NotNull Class<T> eventClass,
                                                 @Nullable Predicate<T> eventPredicate) {
        return cancel(eventClass, null, eventPredicate);
    }

    public Plugin getPlugin() {
        return plugin;
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
    protected <T extends Event> EventExecutor createExecutor(@NotNull Class<T> eventClass,
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
        register(eventClass, new RegisteredListener(this, executor, priority, getPlugin(), ignoreCancelled));
    }

    protected <T extends Event> void register(@NotNull Class<T> eventClass, @NotNull Consumer<T> eventConsumer,
                                              @NotNull EventPriority priority, boolean ignoreCancelled) {
        register(eventClass, createExecutor(eventClass, eventConsumer), priority, ignoreCancelled);
    }

}
