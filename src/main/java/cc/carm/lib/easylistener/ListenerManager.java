package cc.carm.lib.easylistener;

import org.bukkit.Bukkit;
import org.bukkit.event.*;
import org.bukkit.plugin.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

@SuppressWarnings("SameParameterValue")
public class ListenerManager implements EasyListener {

    protected final Plugin plugin;

    public ListenerManager(Plugin plugin) {
        this.plugin = plugin;
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

    protected void requireType(@NotNull Class<?> target, @NotNull Class<?> value,
                               @Nullable String message) throws IllegalArgumentException {
        if (target.isAssignableFrom(value)) return;
        if (message == null) throw new IllegalArgumentException();
        else throw new IllegalArgumentException(message);
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

    @Override
    public <T extends Event> EasyListener handle(@NotNull Class<T> eventClass,
                                                 @Nullable EventPriority priority, boolean ignoreCancelled,
                                                 @NotNull Consumer<T> eventConsumer) {
        register(eventClass, eventConsumer, Optional.ofNullable(priority).orElse(EventPriority.NORMAL), ignoreCancelled);
        return this;
    }

    @Override
    public <T extends Event> EasyListener cancel(@NotNull Class<T> eventClass, @Nullable EventPriority priority,
                                                 @Nullable Predicate<T> eventPredicate) {
        requireType(Cancellable.class, eventClass, "Event class " + eventClass.getName() + " is not cancellable");

        final Predicate<T> predicate = Optional.ofNullable(eventPredicate).orElse(t -> true);
        return handle(eventClass, priority, true, (event) -> {
            if (predicate.test(event)) ((Cancellable) event).setCancelled(true);
        });
    }

}
