package skaro.pokedex.gateway.messaging;

import discord4j.discordjson.json.gateway.Dispatch;
import reactor.core.publisher.Mono;

public interface DispatchPublisher<T extends Dispatch> {
	
	Mono<Void> publishEvent(T event);
	
}
