package rbasamoyai.betsyross.foundation;

import java.util.function.Supplier;

public class ObjectHolder<T> implements Supplier<T> {

    private final Supplier<T> sup;
    private T resolved = null;

    public ObjectHolder(Supplier<T> sup) {
        this.sup = sup;
    }

    @Override
    public T get() {
        if (this.resolved == null) {
            this.resolved = this.sup.get();
            if (this.resolved == null)
                throw new IllegalStateException("Resolved object is null");
        }
        return this.resolved;
    }

}
