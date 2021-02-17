package skaro.pokedex.service.gateway.messaging;

import discord4j.discordjson.json.gateway.Dispatch;
import reactor.core.publisher.Mono;
import skaro.pokedex.sdk.messaging.discord.DiscordEventMessage;

public interface DispatchMessageSender<T extends Dispatch> {
	
	Mono<DiscordEventMessage> sendEvent(T event);
	
}
