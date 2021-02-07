package skaro.pokedex.gateway.messaging;

import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import skaro.pokedex.sdk.GatewayMessagingConfiguration;


@Configuration
@Import(GatewayMessagingConfiguration.class)
public class MessagingConfiguration {

	@Bean
	public MessagePostProcessor messagePostProcessor() {
		return (message) -> {
			message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.NON_PERSISTENT);
			message.getMessageProperties().setExpiration(Long.toString(0));
			return message;
		};
	}
	
}
