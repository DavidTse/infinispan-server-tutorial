package org.infinispan.tutorial.client;

import java.io.InputStream;
import java.util.Properties;

import javax.transaction.TransactionManager;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

/**
 * The Hot Rod transaction simple tutorial.for Data Grid deployed in OpenShift
 * <p>
 * see https://infinispan.org/docs/13.0.x/titles/hotrod_java/hotrod_java.html#hotrod-client-configuration_hotrod-java-client
 * & https://github.com/redhat-developer/redhat-datagrid-tutorials
 * <p>
 * Hot Rod Transactions are available as of Infinispan version 9.3.
 *
 * Infinispan Server includes a default property realm that requires authentication.
 * OpenShift automatically create default credentials & tls cert
 * see Credentials & Accessing Hot Rod API for Outside DMZ (via Route) in README.md
 *
 * default hotrod-client.properties provided in the classpath does not include transaction
 *
 * @author David Tse
 */
public class InfinispanRemoteTx
{
    public static void main(String[] args)
    {
        try {
            InputStream stream = InfinispanRemoteTx.class.getResourceAsStream("/hotrod-client-transaction.properties");
            Properties prop = new Properties();
            prop.load(stream);
            stream.close();

            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.withProperties(prop);

            System.out.println("Start");
            RemoteCacheManager remoteCacheManager = new RemoteCacheManager(builder.build());

            RemoteCache<String, String> cache = remoteCacheManager.getCache("tx-cache");
            // Obtain the transaction manager
            TransactionManager transactionManager = cache.getTransactionManager();
            // Perform some operations within a transaction and commit it
            transactionManager.begin();
            cache.put("key1", "value1");
            cache.put("key2", "value2");
            transactionManager.commit();
            // Display the current cache contents
            System.out.printf("key1 = %s\nkey2 = %s\n", cache.get("key1"), cache.get("key2"));
            // Perform some operations within a transaction and roll it back
            transactionManager.begin();
            cache.put("key1", "value3");
            cache.put("key2", "value4");
            transactionManager.rollback();
            // Display the current cache contents
            System.out.printf("key1 = %s\nkey2 = %s\n", cache.get("key1"), cache.get("key2"));
            // Stop the cache manager and release all resources
            remoteCacheManager.stop();

            remoteCacheManager.close();
            System.out.println("Done");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
