package org.infinispan.tutorial.client;

import java.util.Random;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;

/**
 * Usage example for {@link DataChangeMonitor}.
 *
 * @author David Tse
 */
public class DataChangeProducerConsumer
{
    public static void main(String[] args)
    {
        try {
            System.out.println("Start");
            RemoteCacheManager remoteCacheManager = new RemoteCacheManager();

            String cacheName = "async-cache";
            RemoteCache<String, Double> remoteCache = remoteCacheManager.getCache(cacheName);
            System.out.println("RemoteCache");
            DataChangeMonitor listener = new DataChangeMonitor();
            listener.setRemoteCache(remoteCache);
            remoteCache.addClientListener(listener);

            remoteCache.clear();
            Random random = new Random();
            for (int j=1; j<11; j++) {
                System.out.println(j);
                for (int i=1; i<11; i++) {
                    double value = random.nextDouble() + j;
                    remoteCache.put(Integer.toString(i), value);
                }
                Thread.sleep(500l);
            }

            //Thread.sleep(500l);
            remoteCache.removeClientListener(listener);

            remoteCacheManager.close();
            System.out.println("Done");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
