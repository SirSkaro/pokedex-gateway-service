package skaro.pokedex.service.gateway;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import discord4j.discordjson.json.gateway.MessageCreate;
import reactor.core.publisher.Mono;
import skaro.pokedex.sdk.messaging.discord.DiscordEventMessage;
import skaro.pokedex.service.gateway.dispatch.Dispatcher;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MessageCreateDispatchRunner implements CommandLineRunner {

	private final static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	private Dispatcher<MessageCreate> dispatcher;
	
	public MessageCreateDispatchRunner(Dispatcher<MessageCreate> dispatcher) {
		this.dispatcher = dispatcher;
	}
	
	@Override
	public void run(String... args) throws Exception {
		dispatcher.dispatch()
			.onErrorResume(this::handleError)
			.subscribe();
	}
	
	private Mono<DiscordEventMessage> handleError(Throwable error) {
		LOG.error("Error in consuming dispatch", error);
		return Mono.empty();
	}

}
