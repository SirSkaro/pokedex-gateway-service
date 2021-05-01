package skaro.pokedex.service.gateway.dispatch;

import static java.util.function.Predicate.not;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import discord4j.discordjson.json.MessageData;
import discord4j.discordjson.json.gateway.MessageCreate;
import discord4j.gateway.GatewayClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import skaro.pokedex.sdk.messaging.gateway.DiscordEventMessage;
import skaro.pokedex.service.gateway.messaging.DispatchMessageSender;

@Service
public class MessageCreateDispatcher implements Dispatcher<MessageCreate> {
	private final static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	private GatewayClient gatewayClient;
	private DispatchMessageSender<MessageCreate> sender;

	public MessageCreateDispatcher(GatewayClient gatewayClient, DispatchMessageSender<MessageCreate> sender) {
		this.gatewayClient = gatewayClient;
		this.sender = sender;
	}

	@Override
	public Flux<DiscordEventMessage> dispatch() {
		return gatewayClient.dispatch()
				.ofType(MessageCreate.class)
				.filter(not(this::userIsBot))
				.filter(not(this::messageContentIsEmpty))
				.filter(not(this::hasMentions))
				.flatMap(this::sendEvent);
	}

	private boolean userIsBot(MessageCreate messageCreateEvent) {
		return messageCreateEvent.message()
				.author()
				.bot()
				.toOptional()
				.orElse(false);
	}

	private boolean messageContentIsEmpty(MessageCreate messageCreateEvent) {
		return messageCreateEvent.message().content().isBlank();
	}
	
	private boolean hasMentions(MessageCreate messageCreateEvent) {
		MessageData message = messageCreateEvent.message();
		return message.mentionEveryone() || !message.mentionRoles().isEmpty();
	}
	
	private Mono<DiscordEventMessage> sendEvent(MessageCreate createEvent) {
		return sender.sendEvent(createEvent)
				.onErrorResume(this::handleError);
	}

	private Mono<DiscordEventMessage> handleError(Throwable error) {
		LOG.error("Error in consuming dispatch", error);
		return Mono.empty();
	}
	
}
