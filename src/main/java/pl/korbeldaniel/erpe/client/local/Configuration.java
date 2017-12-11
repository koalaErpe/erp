package pl.korbeldaniel.erpe.client.local;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.enterprise.client.jaxrs.api.RestClient;

@ApplicationScoped
public class Configuration {

	public void initialize() {
		RestClient.setApplicationRoot("rest");
		RestClient.setJacksonMarshallingActive(true);
	}
}
