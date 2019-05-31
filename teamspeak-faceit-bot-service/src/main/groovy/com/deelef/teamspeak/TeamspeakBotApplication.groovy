package com.deelef.teamspeak


import com.github.theholywaffle.teamspeak3.api.wrapper.DatabaseClientInfo
import groovy.util.logging.Slf4j
import org.apache.catalina.filters.RemoteIpFilter
import org.apache.http.client.config.RequestConfig
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.DispatcherServlet
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

@SpringBootApplication
@EnableScheduling
@Slf4j
@EnableAsync
public class TeamspeakBotApplication {

    public static void main(String[] args) {
		SpringApplication.run(TeamspeakBotApplication.class, args);
    }

    @Bean
    public RemoteIpFilter remoteIpFilter() {
        return new RemoteIpFilter();
    }


    @Bean
    public WebMvcConfigurer corsConfigurer() {
        log.info("Initialising the CORs configurer...");

        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("POST", "GET", "PUT", "HEAD", "OPTIONS")
                        .allowedHeaders("authToken", "Origin", "Accept", "X-Requested-With", "Content-Type", "Access-Control-Request-Method", "Access-Control-Request-Headers", "Authorization")
                        .allowCredentials(true);
            }
        };
    }

    @Bean
    public DispatcherServlet dispatcherServlet() {
        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);

        return dispatcherServlet;
    }

    @Bean
    @Profile("sync")
    @Transactional
    public CommandLineRunner fix(com.deelef.teamspeak.domain.repository.UserRepository userRepository, com.deelef.teamspeak.tsclient.TeamspeakBot teamspeakBot) {
        new CommandLineRunner() {
            @Override
            void run(String... args) throws Exception {
                List<com.deelef.teamspeak.domain.model.User> users = userRepository.findAll()
                for (com.deelef.teamspeak.domain.model.User user : users) {
                    DatabaseClientInfo dci = teamspeakBot.getClientByDatabaseId(user.getTeamspeakClient().getDatabaseId())
                    log.info("Nickname: " + dci.getNickname())
                    if (user.teamspeakClient.getUid() == null) {
                        log.info("Updating Nickname: " + dci.getNickname() + "with id: " + dci.getUniqueIdentifier())
                        user.teamspeakClient.uid = dci.getUniqueIdentifier()
                        userRepository.saveAndFlush(user)
                    }
                }
            }
        }
    }

    @Bean
    RestTemplate restTemplate(ClientHttpRequestFactory factory) {
        RestTemplate restTemplate = new RestTemplate(factory)
        return restTemplate
    }


    @Bean
    public ClientHttpRequestFactory createRequestFactory() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(100);
        connectionManager.setDefaultMaxPerRoute(50);

        RequestConfig config = RequestConfig.custom().setConnectTimeout(100000).build()
        CloseableHttpClient httpClient = HttpClientBuilder.create().setConnectionManager(connectionManager)
                .setDefaultRequestConfig(config).build()
        return new HttpComponentsClientHttpRequestFactory(httpClient)
    }
}
