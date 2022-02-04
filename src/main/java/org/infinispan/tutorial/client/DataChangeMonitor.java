package org.infinispan.tutorial.client;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.annotation.ClientCacheEntryCreated;
import org.infinispan.client.hotrod.annotation.ClientCacheEntryModified;
import org.infinispan.client.hotrod.annotation.ClientListener;
import org.infinispan.client.hotrod.event.ClientCacheEntryCreatedEvent;
import org.infinispan.client.hotrod.event.ClientCacheEntryModifiedEvent;

/**
 * The Hot Rod Event simple tutorial.for Data Grid deployed in OpenShift.
 * Usage example for this listener is provider in {@link DataChangeProducerConsumer}.
 * infinispan-server-tutorial demo usage of producer and consumer in different JVM,
 * see TemperatureMonitorApp & TemperatureLoaderApp
 * <p>
 * see https://infinispan.org/docs/13.0.x/titles/hotrod_java/hotrod_java.html#hotrod-java-client,
 * https://infinispan.org/docs/13.0.x/titles/hotrod_java/hotrod_java.html#hotrod-client-api_hotrod-java-client,
 * https://github.com/redhat-developer/redhat-datagrid-tutorials,
 * & https://github.com/infinispan/infinispan-server-tutorial
 * <p>
 *
 * Infinispan Server includes a default property realm that requires authentication.
 * OpenShift automatically create default credentials & tls cert
 * see Credentials & Accessing Hot Rod API for Outside DMZ (via Route) in README.md
 *
 * @author David Tse
 */
@ClientListener
public class DataChangeMonitor
{
    private RemoteCache<String, Double> remoteCache;

    public DataChangeMonitor() {}

    @ClientCacheEntryCreated
    public void entryCreated(ClientCacheEntryCreatedEvent<String> event) {
        String key = event.getKey();
        remoteCache.getAsync(key).whenComplete((value, ex) ->
        System.out.printf(">> Created: key=%s Value=%s%n", key, value));
    }

    @ClientCacheEntryModified
    public void entryModified(ClientCacheEntryModifiedEvent<String> event) {
        String key = event.getKey();
        remoteCache.getAsync(key).whenComplete((value, ex) ->
        System.out.printf(">> Modified: key=%s Value=%s%n", key, value));
    }

    public RemoteCache<String, Double> getRemoteCache() {
        return remoteCache;
    }

    public void setRemoteCache(RemoteCache<String, Double> remoteCache) {
        this.remoteCache = remoteCache;
    }
}
