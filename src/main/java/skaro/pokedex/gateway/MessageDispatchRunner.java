package skaro.pokedex.gateway;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import discord4j.discordjson.json.gateway.MessageCreate;
import discord4j.gateway.GatewayClient;
import reactor.core.publisher.Mono;
import skaro.pokedex.gateway.messaging.NewMessagePublisher;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MessageDispatchRunner implements CommandLineRunner {

	private final static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	private GatewayClient gatewayClient;
	private NewMessagePublisher publisher;
	
	public MessageDispatchRunner(GatewayClient gatewayClient, NewMessagePublisher publisher) {
		this.gatewayClient = gatewayClient;
		this.publisher = publisher;
	}
	
	@Override
	public void run(String... args) throws Exception {
		gatewayClient.dispatch().ofType(MessageCreate.class)
			.flatMap(publisher::publishEvent)
			.onErrorResume(this::handleError)
			.subscribe();
	}
	
	private Mono<Void> handleError(Throwable error) {
		LOG.error("Error in consuming dispatch: {}", error);
		return Mono.empty();
	}

}
