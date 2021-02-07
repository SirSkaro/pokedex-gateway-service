package skaro.pokedex.gateway.messaging;

import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MessagingConfiguration {

	@Bean
    public Queue newMessageQueue() {
        return new Queue("foo");
    }
	
	@Bean
	public MessagePostProcessor messagePostProcessor() {
		return (message) -> {
			message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.NON_PERSISTENT);
			message.getMessageProperties().setExpiration(Long.toString(250));
			return message;
		};
	}
	
}
