package cc.carm.lib.easylistener.handler;

import cc.carm.lib.easylistener.EasyListener;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class MultiEventHandler<T extends Event> extends BaseEventHandler<T, MultiEventHandler<T>> {

    protected final Class<T> eventType;

    protected final Set<Class<? extends T>> eventClasses = new HashSet<>();
    protected Predicate<T> eventPredicate;

    public MultiEventHandler(EasyListener source, Class<T> eventType) {
        super(source);
        this.eventType = eventType;
    }

    @Override
    protected MultiEventHandler<T> getThis() {
        return this;
    }

    public MultiEventHandler<T> from(@NotNull Class<? extends T> eventClass) {
        this.eventClasses.add(eventClass);
        return this;
    }

    public EasyListener handle(@NotNull Consumer<T> eventConsumer) {

        for (Class<? extends T> clazz : this.eventClasses) {
            source.handle(clazz, priority, ignoreCancelled, (event) -> {
                Predicate<T> predicate = Optional.ofNullable(eventPredicate).orElse(t -> true);
                if (predicate.test(event)) eventConsumer.accept(event);
            });
        }

        return source;
    }

    public EasyListener cancel() {
        return cancel(null);
    }

    public EasyListener cancel(@Nullable Consumer<T> afterCancelled) {
        this.eventClasses.forEach(clazz -> source.cancel(clazz, priority, eventPredicate, afterCancelled));
        return source;
    }


}
