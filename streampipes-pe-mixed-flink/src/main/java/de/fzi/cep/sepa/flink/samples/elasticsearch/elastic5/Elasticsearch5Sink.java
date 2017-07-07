package de.fzi.cep.sepa.flink.samples.elasticsearch.elastic5;

import de.fzi.cep.sepa.flink.samples.elasticsearch.elastic5.util.NoOpFailureHandler;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.client.transport.TransportClient;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

/**
 * Created by riemer on 21.03.2017.
 */
/**
 * Elasticsearch 5.x sink that requests multiple {@link ActionRequest ActionRequests}
 * against a cluster for each incoming element.
 *
 * <p>
 * The sink internally uses a {@link TransportClient} to communicate with an Elasticsearch cluster.
 * The sink will fail if no cluster can be connected to using the provided transport addresses passed to the constructor.
 *
 * <p>
 * The {@link Map} passed to the constructor is used to create the {@code TransportClient}. The config keys can be found
 * in the <a href="https://www.elastic.io">Elasticsearch documentation</a>. An important setting is {@code cluster.name},
 * which should be set to the name of the cluster that the sink should emit to.
 *
 * <p>
 * Internally, the sink will use a {@link BulkProcessor} to send {@link ActionRequest ActionRequests}.
 * This will buffer elements before sending a request to the cluster. The behaviour of the
 * {@code BulkProcessor} can be configured using these config keys:
 * <ul>
 *   <li> {@code bulk.flush.max.actions}: Maximum amount of elements to buffer
 *   <li> {@code bulk.flush.max.size.mb}: Maximum amount of data (in megabytes) to buffer
 *   <li> {@code bulk.flush.interval.ms}: Interval at which to flush data regardless of the other two
 *   settings in milliseconds
 * </ul>
 *
 * <p>
 * You also have to provide an {@link ElasticsearchSinkFunction}. This is used to create multiple
 * {@link ActionRequest ActionRequests} for each incoming element. See the class level documentation of
 * {@link ElasticsearchSinkFunction} for an example.
 *
 * @param <T> Type of the elements handled by this sink
 */
public class Elasticsearch5Sink<T> extends ElasticsearchSinkBase<T> {

  private static final long serialVersionUID = 1L;

  /**
   * Creates a new {@code ElasticsearchSink} that connects to the cluster using a {@link TransportClient}.
   *
   * @param userConfig The map of user settings that are used when constructing the {@link TransportClient} and {@link BulkProcessor}
   * @param transportAddresses The addresses of Elasticsearch nodes to which to connect using a {@link TransportClient}
   * @param elasticsearchSinkFunction This is used to generate multiple {@link ActionRequest} from the incoming element
   */
  public Elasticsearch5Sink(
          Map<String, String> userConfig,
          List<InetSocketAddress> transportAddresses,
          ElasticsearchSinkFunction<T> elasticsearchSinkFunction) {

    this(userConfig, transportAddresses, elasticsearchSinkFunction, new NoOpFailureHandler());
  }

  /**
   * Creates a new {@code ElasticsearchSink} that connects to the cluster using a {@link TransportClient}.
   *
   * @param userConfig The map of user settings that are used when constructing the {@link TransportClient} and {@link BulkProcessor}
   * @param transportAddresses The addresses of Elasticsearch nodes to which to connect using a {@link TransportClient}
   * @param elasticsearchSinkFunction This is used to generate multiple {@link ActionRequest} from the incoming element
   * @param failureHandler This is used to handle failed {@link ActionRequest}
   */
  public Elasticsearch5Sink(
          Map<String, String> userConfig,
          List<InetSocketAddress> transportAddresses,
          ElasticsearchSinkFunction<T> elasticsearchSinkFunction,
          ActionRequestFailureHandler failureHandler) {

    super(new Elasticsearch5ApiCallBridge(transportAddresses), userConfig, elasticsearchSinkFunction, failureHandler);
  }
}
