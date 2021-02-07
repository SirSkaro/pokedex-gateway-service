package skaro.pokedex.gateway;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import discord4j.gateway.GatewayClient;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class GatewayConnectRunner implements CommandLineRunner {
	private final static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	private GatewayClient gatewayClient;
	private DiscordGatewayConfigurationProperties discordConfig;

	public GatewayConnectRunner(GatewayClient gatewayClient, DiscordGatewayConfigurationProperties discordConfig) {
		this.gatewayClient = gatewayClient;
		this.discordConfig = discordConfig;
	}

	@Override
	public void run(String... args) throws Exception {
		String gatewayUrl = constructGatewayUrl();
		LOG.info("Connecting to gateway at {}", gatewayUrl);
		gatewayClient.execute(gatewayUrl).block();
	}
	
	private String constructGatewayUrl() {
		MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
		discordConfig.getGatewayParams().forEach(queryParams::add);
		
		return UriComponentsBuilder.fromUri(discordConfig.getGatewayBaseUri())
			.queryParams(queryParams)
			.build()
			.toUriString();
	}
}
