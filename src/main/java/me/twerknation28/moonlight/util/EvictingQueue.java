package me.twerknation28.moonlight.util;

import org.jetbrains.annotations.NotNull;
import java.util.concurrent.ConcurrentLinkedDeque;

public class EvictingQueue<E> extends ConcurrentLinkedDeque<E>
{
    private final int limit;
    
    public EvictingQueue(final int limit) {
        this.limit = limit;
    }
    
    @Override
    public boolean add(@NotNull final E element) {
        final boolean add = super.add(element);
        while (add && this.size() > this.limit) {
            super.remove();
        }
        return add;
    }
    
    @Override
    public void addFirst(@NotNull final E element) {
        super.addFirst(element);
        while (this.size() > this.limit) {
            super.removeLast();
        }
    }
    
    public int limit() {
        return this.limit;
    }
}
