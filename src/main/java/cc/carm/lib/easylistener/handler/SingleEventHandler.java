package cc.carm.lib.easylistener.handler;

import cc.carm.lib.easylistener.EasyListener;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class SingleEventHandler<T extends Event> extends BaseEventHandler<T, SingleEventHandler<T>> {

    protected final Class<T> eventClass;

    public SingleEventHandler(EasyListener source, Class<T> eventClass) {
        super(source);
        this.eventClass = eventClass;
    }

    @Override
    protected SingleEventHandler<T> getThis() {
        return this;
    }

    public EasyListener handle(@NotNull Consumer<T> eventConsumer) {
        return source.handle(eventClass, priority, ignoreCancelled, (event) -> {
            Predicate<T> predicate = Optional.ofNullable(this.predicate).orElse(t -> true);
            if (predicate.test(event)) eventConsumer.accept(event);
        });
    }

    public EasyListener cancel() {
        return cancel(null);
    }

    public EasyListener cancel(@Nullable Consumer<T> afterCancelled) {
        return source.cancel(eventClass, priority, predicate, afterCancelled);
    }


}
