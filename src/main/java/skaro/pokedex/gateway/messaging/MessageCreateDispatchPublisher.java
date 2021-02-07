package skaro.pokedex.gateway.messaging;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import discord4j.discordjson.json.MessageData;
import discord4j.discordjson.json.gateway.MessageCreate;
import reactor.core.publisher.Mono;
import skaro.pokedex.sdk.messaging.DiscordTextEventMessage;

@Service
public class MessageCreateDispatchPublisher implements DispatchPublisher<MessageCreate> {

	private final static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	private RabbitTemplate template;
	private Queue newMessageQueue;
	private MessagePostProcessor postProcessor;
	
	public MessageCreateDispatchPublisher(RabbitTemplate template, Queue newMessageQueue, MessagePostProcessor postProcessor) {
		this.template = template;
		this.newMessageQueue = newMessageQueue;
		this.postProcessor = postProcessor;
	}
	
	@Override
	public Mono<Void> publishEvent(MessageCreate event) {
		return Mono.fromRunnable(() -> queueMessageEvent(event.message()));
	}
	
	private void queueMessageEvent(MessageData message) {
		DiscordTextEventMessage messageToQueue = toEventMessage(message);
		template.convertAndSend(newMessageQueue.getName(), messageToQueue, postProcessor);
		LOG.info("Send message: {}", messageToQueue.getContent());
	}
	
	private DiscordTextEventMessage toEventMessage(MessageData message) {
		DiscordTextEventMessage eventMessage = new DiscordTextEventMessage();
		eventMessage.setChannelId(message.channelId());
		eventMessage.setContent(message.content());
		eventMessage.setAuthorId(message.author().id());
		message.guildId().toOptional().ifPresent(eventMessage::setGuildId);
		
		return eventMessage;
	}
	
}
