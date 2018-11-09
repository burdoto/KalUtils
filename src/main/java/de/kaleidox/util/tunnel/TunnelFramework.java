package de.kaleidox.util.tunnel;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class TunnelFramework {
    private final Hashtable<Object, List<TunnelSubscription>> subscriptions;

    public TunnelFramework() {
        subscriptions = new Hashtable<>();
    }

    public <T> TunnelSubscription<T> subscribe(Object base, Class<T> tClass, TunnelAcceptor<T> acceptor) {
        TunnelSubscription<T> subscription = new TunnelSubscription<>(base, tClass, acceptor);
        subscriptions.putIfAbsent(base, new ArrayList<>());
        subscriptions.get(base).add(subscription);
        return subscription;
    }

    @SuppressWarnings("unchecked")
    public void accept(Object result, Object... base) {
        for (Object o : base) {
            if (o != null) {
                List<TunnelSubscription> tunnelSubscriptions = subscriptions.get(o);

                for (TunnelSubscription tunnelSubscription : tunnelSubscriptions) {
                    if (tunnelSubscription.gettClass().isAssignableFrom(result.getClass()))
                        tunnelSubscription.getAcceptor().accept(result);
                }
            }
        }
    }
}
