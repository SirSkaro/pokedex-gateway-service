package skaro.pokedex.gateway;

import javax.validation.Valid;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import discord4j.common.JacksonResources;
import discord4j.common.ReactorResources;
import discord4j.common.retry.ReconnectOptions;
import discord4j.gateway.DefaultGatewayClient;
import discord4j.gateway.GatewayClient;
import discord4j.gateway.GatewayObserver;
import discord4j.gateway.GatewayOptions;
import discord4j.gateway.GatewayReactorResources;
import discord4j.gateway.IdentifyOptions;
import discord4j.gateway.intent.Intent;
import discord4j.gateway.intent.IntentSet;
import discord4j.gateway.payload.JacksonPayloadReader;
import discord4j.gateway.payload.JacksonPayloadWriter;
import discord4j.gateway.payload.PayloadReader;
import discord4j.gateway.payload.PayloadWriter;

@Configuration
public class GatewayConfiguration {
	private static final String DISCORD_PROPERTIES_PREFIX = "discord";
	
	@Bean
	@ConfigurationProperties(DISCORD_PROPERTIES_PREFIX)
	@Valid
	public DiscordConfigurationProperties getDiscordConfigurationProperties() {
		return new DiscordConfigurationProperties();
	}
	
	@Bean
	public GatewayOptions getGatewayOptions(DiscordConfigurationProperties discordConfig) {
		ReactorResources reactorResources = ReactorResources.create();
        GatewayReactorResources gatewayReactorResources = new GatewayReactorResources(reactorResources);
        JacksonResources jacksonResources = JacksonResources.create();

        ObjectMapper objectMapper = jacksonResources.getObjectMapper();

        PayloadWriter payloadWriter = new JacksonPayloadWriter(objectMapper);
        PayloadReader payloadReader = new JacksonPayloadReader(objectMapper);

        ReconnectOptions reconnectOptions = ReconnectOptions.create();

        IdentifyOptions identifyOptions = IdentifyOptions.builder(discordConfig.getShardIndex(), discordConfig.getShardCount())
                .intents(IntentSet.of(Intent.GUILD_MESSAGES, Intent.GUILD_MESSAGE_REACTIONS))
                .build();

        return new GatewayOptions(
                discordConfig.getToken(),
                gatewayReactorResources,
                payloadReader,
                payloadWriter,
                reconnectOptions,
                identifyOptions,
                GatewayObserver.NOOP_LISTENER,
                s -> s,
                1
        );
	}
	
	@Bean
	public GatewayClient getGatewayClient(GatewayOptions gatewayOptions) {
		return new DefaultGatewayClient(gatewayOptions);
	}
	
}
