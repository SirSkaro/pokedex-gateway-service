package skaro.pokedex.gateway;

import java.net.URI;
import java.util.Map;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import skaro.pokedex.sdk.DiscordConfigurationProperties;

public class DiscordGatewayConfigurationProperties extends DiscordConfigurationProperties {
	@NotNull
	@PositiveOrZero
	private Integer shardIndex;
	@NotNull
	@Positive
	private Integer shardCount;
	@NotNull
	private URI gatewayBaseUri;
	private Map<String, @NotEmpty String> gatewayParams;
	
	public Integer getShardIndex() {
		return shardIndex;
	}
	public void setShardIndex(Integer shardIndex) {
		this.shardIndex = shardIndex;
	}
	public Integer getShardCount() {
		return shardCount;
	}
	public void setShardCount(Integer shardCount) {
		this.shardCount = shardCount;
	}
	public URI getGatewayBaseUri() {
		return gatewayBaseUri;
	}
	public void setGatewayBaseUri(URI gatewayBaseUri) {
		this.gatewayBaseUri = gatewayBaseUri;
	}
	public Map<String, String> getGatewayParams() {
		return gatewayParams;
	}
	public void setGatewayParams(Map<String, String> gatewayParams) {
		this.gatewayParams = gatewayParams;
	}
	
}
