package skaro.pokedex.gateway.service.messaging;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import discord4j.discordjson.json.MessageData;
import discord4j.discordjson.json.UserData;
import discord4j.discordjson.json.gateway.MessageCreate;
import discord4j.discordjson.possible.Possible;
import skaro.pokedex.sdk.messaging.discord.DiscordTextEventMessage;
import skaro.pokedex.service.gateway.messaging.MessageCreateSender;

@ExtendWith(SpringExtension.class)
public class MessageCreateSenderTest {

	@Mock
	private RabbitTemplate template;
	@Mock 
	Queue queue;
	@Mock 
	MessagePostProcessor postProcessor;
	
	private MessageCreateSender publisher;
	
	@BeforeEach
	void setup() {
		publisher = new MessageCreateSender(template, queue, postProcessor);
	}
	
	@Test
	public void testQueueMessageEvent() {
		String userId = randomUUID().toString();
		String guildId = randomUUID().toString();
		String channelId = randomUUID().toString();
		String messageContent = randomUUID().toString();
		String queueName = randomUUID().toString();
		MessageCreate messageCreateEvent = mockMessageCreate(userId, guildId, channelId, messageContent);
		
		when(queue.getName()).thenReturn(queueName);
		
		DiscordTextEventMessage queuedMessage = (DiscordTextEventMessage)publisher.sendEvent(messageCreateEvent).block();
		
		assertEquals(userId, queuedMessage.getAuthorId());
		assertEquals(guildId, queuedMessage.getGuildId());
		assertEquals(channelId, queuedMessage.getChannelId());
		assertEquals(messageContent, queuedMessage.getContent());
	}
	
	private MessageCreate mockMessageCreate(String userId, String guildId, String channelId, String message) {
		UserData authorData = Mockito.mock(UserData.class);
		when(authorData.id()).thenReturn(userId);
		
		MessageData messageData = Mockito.mock(MessageData.class);
		when(messageData.author()).thenReturn(authorData);
		when(messageData.guildId()).thenReturn(Possible.of(guildId));
		when(messageData.channelId()).thenReturn(channelId);
		when(messageData.content()).thenReturn(message);
		
		return MessageCreate.builder()
				.message(messageData)
				.build();
	}
	
}
