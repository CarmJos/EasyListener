package cc.carm.lib.easylistener.handler;

import cc.carm.lib.easylistener.EasyListener;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Predicate;

public abstract class BaseEventHandler<T, S extends BaseEventHandler<T, S>> {

    protected final @NotNull EasyListener source;

    protected EventPriority priority = null;
    protected boolean ignoreCancelled = false;

    protected Predicate<T> predicate;

    public BaseEventHandler(@NotNull EasyListener source) {
        this.source = source;
    }

    protected abstract S getThis();

    public S priority(@Nullable EventPriority priority) {
        this.priority = priority;
        return getThis();
    }

    public S setIgnoreCancelled(boolean ignore) {
        this.ignoreCancelled = ignore;
        return getThis();
    }

    public S acceptCancelled() {
        return setIgnoreCancelled(false);
    }

    public S ignoreCancelled() {
        return setIgnoreCancelled(true);
    }

    public S filter(@Nullable Predicate<T> predicate) {
        if (predicate == null) return getThis();
        this.predicate = Optional.ofNullable(this.predicate).map(p -> p.and(predicate)).orElse(predicate);
        return getThis();
    }

}
