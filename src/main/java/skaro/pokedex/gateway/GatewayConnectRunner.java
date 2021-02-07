package skaro.pokedex.gateway;

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

	private GatewayClient gatewayClient;
	private DiscordConfigurationProperties discordConfig;

	public GatewayConnectRunner(GatewayClient gatewayClient, DiscordConfigurationProperties discordConfig) {
		this.gatewayClient = gatewayClient;
		this.discordConfig = discordConfig;
	}

	@Override
	public void run(String... args) throws Exception {
		String gatewayUrl = constructGatewayUrl();
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
