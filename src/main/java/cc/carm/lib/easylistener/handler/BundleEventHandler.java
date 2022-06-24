package cc.carm.lib.easylistener.handler;

import cc.carm.lib.easylistener.EasyListener;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.*;

public class BundleEventHandler<T, E extends Event> {

    protected final EasyListener source;

    protected final Class<E> eventType;
    protected final Class<T> handleClass;
    protected final Map<Class<? extends E>, EventWrapper<? extends E, T>> wrappers = new HashMap<>();

    protected BiPredicate<T, E> predicate;

    public BundleEventHandler(EasyListener source, Class<T> handleClass, Class<E> eventType) {
        this.source = source;
        this.eventType = eventType;
        this.handleClass = handleClass;
    }

    public <U extends E> BundleEventHandler<T, E> from(@NotNull Class<U> eventClass,
                                                       @NotNull Function<U, T> wrapFunction) {
        return from(eventClass, null, wrapFunction);
    }

    public <U extends E> BundleEventHandler<T, E> from(@NotNull Class<U> eventClass,
                                                       @Nullable EventPriority priority,
                                                       @NotNull Function<U, T> wrapFunction) {
        this.wrappers.put(eventClass, new EventWrapper<>(eventClass, priority, wrapFunction));
        return this;
    }

    public BundleEventHandler<T, E> filter(@Nullable BiPredicate<T, E> predicate) {
        if (predicate == null) return this;

        this.predicate = Optional.ofNullable(this.predicate).map(p -> p.and(predicate)).orElse(predicate);
        return this;
    }

    public BundleEventHandler<T, E> filter(@Nullable Predicate<T> predicate) {
        if (predicate == null) return this;
        BiPredicate<T, E> append = (t, e) -> predicate.test(t);
        this.predicate = Optional.ofNullable(this.predicate).map(p -> p.and(append)).orElse(append);
        return this;
    }

    public EasyListener handle(@NotNull BiConsumer<T, E> consumer) {
        BiPredicate<T, E> predicate = Optional.ofNullable(this.predicate).orElse((t, e) -> true);
        this.wrappers.values().forEach(wrapper -> wrapper.handle(source, predicate, consumer));
        return source;
    }

    public EasyListener handle(@NotNull Consumer<T> consumer) {
        return handle((t, e) -> consumer.accept(t));
    }

    public EasyListener cancel() {
        return cancel((BiConsumer<T, E>) null);
    }

    public EasyListener cancel(@Nullable BiConsumer<T, E> afterCancelled) {
        BiConsumer<T, E> consumer = Optional.ofNullable(afterCancelled).orElse((t, e) -> {
            //Do nothing
        });
        this.wrappers.values().forEach(wrapper -> wrapper.cancel(source, predicate, consumer));
        return source;
    }

    public EasyListener cancel(@Nullable Consumer<T> afterCancelled) {
        return cancel((t, e) -> Optional.ofNullable(afterCancelled).ifPresent(c -> c.accept(t)));
    }

    protected static class EventWrapper<E extends Event, T> {

        protected final @NotNull Class<E> eventClass;
        protected final @NotNull Function<E, T> wrapper;

        protected final EventPriority priority;

        public EventWrapper(@NotNull Class<E> eventClass, EventPriority priority,
                            @NotNull Function<E, T> wrapper) {
            this.eventClass = eventClass;
            this.wrapper = wrapper;
            this.priority = priority;
        }

        public void handle(@NotNull EasyListener source,
                           @NotNull BiPredicate<T, ? super E> predicate, @NotNull BiConsumer<T, ? super E> consumer) {
            source.handle(this.eventClass, this.priority, event -> {
                T wrapper = this.wrapper.apply(event);
                if (predicate.test(wrapper, event)) consumer.accept(wrapper, event);
            });
        }

        public void cancel(@NotNull EasyListener source,
                           @NotNull BiPredicate<T, ? super E> predicate,
                           @NotNull BiConsumer<T, ? super E> afterCancelled) {
            source.cancel(
                    this.eventClass, this.priority,
                    event -> predicate.test(this.wrapper.apply(event), event),
                    event -> afterCancelled.accept(this.wrapper.apply(event), event)
            );
        }

    }


}
