package skaro.pokedex.gateway.dispatch;

import static java.util.function.Predicate.not;

import org.springframework.stereotype.Service;

import discord4j.discordjson.json.MessageData;
import discord4j.discordjson.json.gateway.MessageCreate;
import discord4j.gateway.GatewayClient;
import reactor.core.publisher.Flux;
import skaro.pokedex.gateway.messaging.DispatchPublisher;
import skaro.pokedex.sdk.messaging.discord.DiscordEventMessage;
import skaro.pokedex.sdk.messaging.discord.DiscordTextEventMessage;

@Service
public class MessageCreateDispatcher implements Dispatcher<MessageCreate> {

	private GatewayClient gatewayClient;
	private DispatchPublisher<MessageCreate, DiscordTextEventMessage> publisher;

	public MessageCreateDispatcher(GatewayClient gatewayClient, DispatchPublisher<MessageCreate, DiscordTextEventMessage> publisher) {
		this.gatewayClient = gatewayClient;
		this.publisher = publisher;
	}

	@Override
	public Flux<DiscordEventMessage> dispatch() {
		return gatewayClient.dispatch()
				.ofType(MessageCreate.class)
				.filter(not(this::userIsBot))
				.filter(not(this::hasMentions))
				.flatMap(publisher::publishEvent);
	}

	private boolean userIsBot(MessageCreate messageCreateEvent) {
		return messageCreateEvent.message()
				.author()
				.bot()
				.toOptional()
				.orElse(false);
	}

	private boolean hasMentions(MessageCreate messageCreateEvent) {
		MessageData message = messageCreateEvent.message();
		return message.mentionEveryone() || !message.mentionRoles().isEmpty();
	}


}