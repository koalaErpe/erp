package pl.korbeldaniel.erpe.client.local;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.enterprise.client.jaxrs.api.RestClient;

@ApplicationScoped
public class Configuration {

	public void configure() {
		RestClient.setApplicationRoot("rest");
		RestClient.setJacksonMarshallingActive(true);
	}
}
