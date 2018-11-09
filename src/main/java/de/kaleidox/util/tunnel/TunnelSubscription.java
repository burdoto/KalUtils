package de.kaleidox.util.tunnel;

public class TunnelSubscription<T> {
    private final Object base;
    private final Class<T> tClass;
    private final TunnelAcceptor<T> acceptor;

    public TunnelSubscription(Object base, Class<T> tClass, TunnelAcceptor<T> acceptor) {
        this.base = base;
        this.tClass = tClass;
        this.acceptor = acceptor;
    }

    public Object getBase() {
        return base;
    }

    public Class<T> gettClass() {
        return tClass;
    }

    public TunnelAcceptor<T> getAcceptor() {
        return acceptor;
    }
}
