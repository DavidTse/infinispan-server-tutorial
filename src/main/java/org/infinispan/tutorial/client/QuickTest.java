package org.infinispan.tutorial.client;

import java.util.Iterator;
import java.util.Set;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;

public class QuickTest
{
    public static void main(String[] args)
    {
        try {
            /* Multiple cluster
            InputStream stream = QuickTest.class.getResourceAsStream("/hotrod-client-cluster1.properties");
            Properties prop = new Properties();
            prop.load(stream);
            stream.close();

            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.withProperties(prop);
            RemoteCacheManager remoteCacheManager = new RemoteCacheManager(builder.build());
            */
            System.out.println("Start");
            RemoteCacheManager remoteCacheManager = new RemoteCacheManager();
            String cacheName = "async-cache";
            RemoteCache<String, Double> remoteCache = remoteCacheManager.getCache(cacheName);
            //
            Set<String> names = remoteCacheManager.getCacheNames();
            Iterator<String> it = names.iterator();
            while (it.hasNext()) {
                System.out.println(it.next());
            }
            //
            for (int j=1; j<11; j++) {
                String key = Integer.toString(j);
                Double value = remoteCache.get(key);
                System.out.printf(">> key=%s Value=%s%n", key, value);
            }

            remoteCacheManager.close();
            System.out.println("Done");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
