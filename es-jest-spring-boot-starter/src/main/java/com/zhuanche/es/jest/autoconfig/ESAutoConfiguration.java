package com.zhuanche.es.jest.autoconfig;

import com.zhuanche.es.jest.ElasticSearchService;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@ConditionalOnClass(JestClient.class)
public class ESAutoConfiguration {

    @Autowired
    private Environment env;


    @Bean
    @ConditionalOnMissingBean
    public JestClientFactory jestClientFactory(){
        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig
                .Builder(env.getProperty("es.url"))
                .multiThreaded(true)
                .defaultMaxTotalConnectionPerRoute(Integer.valueOf(env.getProperty("es.max-total", "100")))
                .maxTotalConnection(Integer.valueOf(env.getProperty("es.per-total", "100")))
                .build());
        return factory;
    }

    @Bean
    @ConditionalOnMissingBean
    public JestClient jestClient(JestClientFactory factory){

        return factory.getObject();
    }

    @Bean
    public ElasticSearchService elasticSearchService(JestClient client){
        return new ElasticSearchService(client);
    }
}
