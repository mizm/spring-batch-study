package com.ildong.batch.config;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableAutoConfiguration
@EnableBatchProcessing
@EntityScan("com.ildong.batch.model")
@EnableJpaRepositories("com.ildong.batch.repository")
@EnableTransactionManagement
public class TestJpaBatchConfig {

    /**
     * @EntityScan("com.ildong.batch.model")
     * @EnableJpaRepositories("com.ildong.batch.repository")
     * @EnableTransactionManagement
     * 위의 어노테이션을 추가해줌으로써 jpa 설정을 추가해준다
     * 테스트코드에서 정상 동작한다.
     */
}