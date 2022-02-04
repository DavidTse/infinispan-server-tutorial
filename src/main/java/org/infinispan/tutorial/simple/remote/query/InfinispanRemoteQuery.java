package org.infinispan.tutorial.simple.remote.query;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.Search;
import org.infinispan.client.hotrod.annotation.ClientCacheEntryCreated;
import org.infinispan.client.hotrod.annotation.ClientCacheEntryModified;
import org.infinispan.client.hotrod.annotation.ClientListener;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.event.ClientCacheEntryCreatedEvent;
import org.infinispan.client.hotrod.event.ClientCacheEntryModifiedEvent;
import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryFactory;

import static org.infinispan.query.remote.client.ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME;

public class InfinispanRemoteQuery 
{
	public static void main(String[] args) 
	{
		try {
			InputStream stream = InfinispanRemoteQuery.class.getResourceAsStream("/hotrod-client.properties");
			Properties prop = new Properties();
			prop.load(stream);
			stream.close();
			
			ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.withProperties(prop);
			
			System.out.println("Start");
			RemoteCacheManager remoteCacheManager = new RemoteCacheManager(builder.build());
			
			// Add the Protobuf serialization context in the client
			builder.addContextInitializer(new QuerySchemaBuilderImpl());
			
		      RemoteCacheManager client = new RemoteCacheManager(builder.build());

		      // Create and add the Protobuf schema in the server
		      addPersonSchema(client);

		      // Get the people cache, create it if needed with the default configuration
		      RemoteCache<String, Person> peopleCache = client.getCache("remote-query");

		      // Create the persons dataset to be stored in the cache
		      Map<String, Person> people = new HashMap<>();
		      people.put("1", new Person("Oihana", "Rossignol", 2016, "Paris"));
		      people.put("2", new Person("Elaia", "Rossignol", 2018, "Paris"));
		      people.put("3", new Person("Yago", "Steiner", 2013, "Saint-Mandé"));
		      people.put("4", new Person("Alberto", "Steiner", 2016, "Paris"));

		      // Put all the values in the cache
		      peopleCache.putAll(people);

		      // Get a query factory from the cache
		      QueryFactory queryFactory = Search.getQueryFactory(peopleCache);

		      // Create a query with lastName parameter
		      Query query = queryFactory.create("FROM tutorial.Person p where p.lastName = :lastName");

		      // Set the parameter value
		      query.setParameter("lastName", "Rossignol");

		      // Execute the query
		      List<Person> rossignols = query.execute().list();

		      // Print the results
		      System.out.println(rossignols);

			remoteCacheManager.stop();
			System.out.println("Done");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	   private static void addPersonSchema(RemoteCacheManager cacheManager) {
	      // Retrieve metadata cache
	      RemoteCache<String, String> metadataCache =
	            cacheManager.getCache(PROTOBUF_METADATA_CACHE_NAME);

	      // Define the new schema on the server too
	      GeneratedSchema schema = new QuerySchemaBuilderImpl();
	      metadataCache.put(schema.getProtoFileName(), schema.getProtoFile());
	   }
}
