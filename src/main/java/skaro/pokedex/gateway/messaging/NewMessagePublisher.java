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

@Service
public class NewMessagePublisher implements DispatchPublisher<MessageCreate> {

	private final static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	private RabbitTemplate template;
	private Queue newMessageQueue;
	private MessagePostProcessor postProcessor;
	
	public NewMessagePublisher(RabbitTemplate template, Queue newMessageQueue, MessagePostProcessor postProcessor) {
		this.template = template;
		this.newMessageQueue = newMessageQueue;
		this.postProcessor = postProcessor;
	}
	
	@Override
	public Mono<Void> publishEvent(MessageCreate event) {
		return Mono.fromRunnable(() -> queueMessageEvent(event.message()));
	}
	
	private void queueMessageEvent(MessageData message) {
		template.convertAndSend(newMessageQueue.getName(), (Object)message.content(), postProcessor);
		LOG.info("Send message: {}", message.content());
	}
	
}
