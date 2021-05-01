package skaro.pokedex.gateway.service.dispatch;

import static java.util.UUID.randomUUID;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import discord4j.discordjson.json.MessageData;
import discord4j.discordjson.json.UserData;
import discord4j.discordjson.json.gateway.MessageCreate;
import discord4j.discordjson.possible.Possible;
import discord4j.gateway.GatewayClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import skaro.pokedex.sdk.messaging.gateway.DiscordTextEventMessage;
import skaro.pokedex.service.gateway.dispatch.MessageCreateDispatcher;
import skaro.pokedex.service.gateway.messaging.DispatchMessageSender;

@ExtendWith(SpringExtension.class)
public class MessageCreateDispatcherTest {

	@Mock
	private GatewayClient gatewayClient;
	@Mock
	private DispatchMessageSender<MessageCreate> sender;

	private MessageCreateDispatcher dispatcher;

	@BeforeEach
	public void setup() {
		dispatcher = new MessageCreateDispatcher(gatewayClient, sender);
	}

	@Test
	public void testDispatch() {
		MessageCreate createEvent = mockMessageCreateFromUser(false);
		DiscordTextEventMessage queuedEventMessage = new DiscordTextEventMessage();
		Mockito.when(gatewayClient.dispatch()).thenReturn(Flux.just(createEvent));
		Mockito.when(sender.sendEvent(ArgumentMatchers.any(MessageCreate.class)))
			.thenReturn(Mono.just(queuedEventMessage));

		StepVerifier.create(dispatcher.dispatch())
			.expectNext(queuedEventMessage)
			.expectComplete()
			.verify();
	}

	@Test
	public void testDispatch_userIdBot() {
		MessageCreate createEvent = mockMessageCreateFromUser(true);
		Mockito.when(gatewayClient.dispatch()).thenReturn(Flux.just(createEvent));

		StepVerifier.create(dispatcher.dispatch())
			.expectNextCount(0)
			.expectComplete()
			.verify();

		Mockito.verifyNoInteractions(sender);
	}

	@Test
	public void testDispatch_mentionEveryone() {
		UserData authorData = Mockito.mock(UserData.class);
		when(authorData.bot()).thenReturn(Possible.of(false));

		MessageData messageData = Mockito.mock(MessageData.class);
		when(messageData.author()).thenReturn(authorData);
		when(messageData.mentionEveryone()).thenReturn(true);
		when(messageData.mentionRoles()).thenReturn(List.of());
		when(messageData.content()).thenReturn("content");

		MessageCreate createEvent = MessageCreate.builder()
				.message(messageData)
				.build();

		Mockito.when(gatewayClient.dispatch()).thenReturn(Flux.just(createEvent));

		StepVerifier.create(dispatcher.dispatch())
			.expectNextCount(0)
			.expectComplete()
			.verify();

		Mockito.verifyNoInteractions(sender);
	}
	
	@Test
	public void testDispatch_noContent() {
		UserData authorData = Mockito.mock(UserData.class);
		when(authorData.bot()).thenReturn(Possible.of(false));
		
		MessageData messageData = Mockito.mock(MessageData.class);
		when(messageData.author()).thenReturn(authorData);
		when(messageData.mentionEveryone()).thenReturn(false);
		when(messageData.mentionRoles()).thenReturn(List.of());
		when(messageData.content()).thenReturn("");
		
		MessageCreate createEvent = MessageCreate.builder()
				.message(messageData)
				.build();
		
		Mockito.when(gatewayClient.dispatch()).thenReturn(Flux.just(createEvent));
		
		StepVerifier.create(dispatcher.dispatch())
			.expectNextCount(0)
			.expectComplete()
			.verify();
		
		Mockito.verifyNoInteractions(sender);
	}

	@Test
	public void testDispatch_mentionRole() {
		UserData authorData = Mockito.mock(UserData.class);
		when(authorData.bot()).thenReturn(Possible.of(false));

		MessageData messageData = Mockito.mock(MessageData.class);
		when(messageData.author()).thenReturn(authorData);
		when(messageData.mentionEveryone()).thenReturn(false);
		when(messageData.mentionRoles()).thenReturn(List.of(randomUUID().toString()));
		when(messageData.content()).thenReturn("content");

		MessageCreate createEvent = MessageCreate.builder()
				.message(messageData)
				.build();

		Mockito.when(gatewayClient.dispatch()).thenReturn(Flux.just(createEvent));

		StepVerifier.create(dispatcher.dispatch())
			.expectNextCount(0)
			.expectComplete()
			.verify();

		Mockito.verifyNoInteractions(sender);
	}
	
	@Test
	public void testDispatch_continueOnSendingError() {
		MessageCreate createEvent = mockMessageCreateFromUser(false);
		Mockito.when(gatewayClient.dispatch()).thenReturn(Flux.just(createEvent));
		Mockito.when(sender.sendEvent(ArgumentMatchers.any(MessageCreate.class)))
			.thenReturn(Mono.error(new IOException()));

		StepVerifier.create(dispatcher.dispatch())
			.expectComplete()
			.verify();
	}

	private MessageCreate mockMessageCreateFromUser(boolean userIsBot) {
		UserData authorData = Mockito.mock(UserData.class);
		when(authorData.bot()).thenReturn(Possible.of(userIsBot));

		MessageData messageData = Mockito.mock(MessageData.class);
		when(messageData.author()).thenReturn(authorData);
		when(messageData.mentionEveryone()).thenReturn(false);
		when(messageData.mentionRoles()).thenReturn(List.of());
		when(messageData.content()).thenReturn("foo");

		return MessageCreate.builder()
				.message(messageData)
				.build();
	}

}
