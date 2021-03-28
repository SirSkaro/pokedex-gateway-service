package skaro.pokedex.service.gateway.dispatch;

import discord4j.discordjson.json.gateway.Dispatch;
import reactor.core.publisher.Flux;
import skaro.pokedex.sdk.messaging.gateway.DiscordEventMessage;

public interface Dispatcher<T extends Dispatch> {

	Flux<DiscordEventMessage> dispatch();
	
}
