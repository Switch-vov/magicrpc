package com.switchvov.magicrpc.demo.consumer;

import com.switchvov.magicrpc.demo.provider.MagicrpcDemoProviderApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

/**
 * @author switch
 * @since 2024/3/10
 */
@SpringBootTest
@Slf4j
public class MagicrpcDemoConsumerApplicationTests {

    private static ApplicationContext context;

    @BeforeAll
    public static void init() {
        context = SpringApplication.run(MagicrpcDemoProviderApplication.class,
                "--server.port=8075", "--logging.level.com.switchvov.magicrpc=debug");
    }

    @AfterAll
    public static void destroy() {

    }

    @Test
    public void contextLoads() {
        SpringApplication.exit(context, () -> 1);
    }
}
