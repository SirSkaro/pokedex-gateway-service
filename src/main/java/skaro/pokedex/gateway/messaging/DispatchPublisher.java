package skaro.pokedex.gateway.messaging;

import discord4j.discordjson.json.gateway.Dispatch;
import reactor.core.publisher.Mono;
import skaro.pokedex.sdk.messaging.discord.DiscordEventMessage;

public interface DispatchPublisher<T extends Dispatch, E extends DiscordEventMessage> {
	
	Mono<E> publishEvent(T event);
	
}
