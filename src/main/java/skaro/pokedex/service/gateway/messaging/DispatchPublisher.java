package skaro.pokedex.service.gateway.messaging;

import discord4j.discordjson.json.gateway.Dispatch;
import reactor.core.publisher.Mono;
import skaro.pokedex.sdk.messaging.discord.DiscordEventMessage;

public interface DispatchPublisher<T extends Dispatch> {
	
	Mono<DiscordEventMessage> publishEvent(T event);
	
}
