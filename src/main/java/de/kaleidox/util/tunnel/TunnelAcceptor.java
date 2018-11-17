package de.kaleidox.util.tunnel;

@FunctionalInterface
public interface TunnelAcceptor<T> {
    void accept(T result);
}
