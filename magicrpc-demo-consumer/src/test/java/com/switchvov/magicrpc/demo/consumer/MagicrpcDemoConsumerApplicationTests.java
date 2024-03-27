package com.switchvov.magicrpc.demo.consumer;

import com.switchvov.magicrpc.core.test.TestZKServer;
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
@SpringBootTest(classes = MagicrpcDemoConsumerApplication.class)
@Slf4j
public class MagicrpcDemoConsumerApplicationTests {

    private static ApplicationContext context;
    public static final TestZKServer zkServer = new TestZKServer();

    @BeforeAll
    public static void init() {
        log.info(" ====================================== ");
        log.info(" ====================================== ");
        log.info(" ====================================== ");
        log.info(" ====================================== ");
        log.info(" ====================================== ");
        log.info(" ====================================== ");
        zkServer.start();
        context = SpringApplication.run(MagicrpcDemoProviderApplication.class,
                "--server.port=8075", "--magicrpc.zk_server=localhost:2182",
                "--logging.level.com.switchvov.magicrpc=debug");
    }

    @AfterAll
    public static void destroy() {
        SpringApplication.exit(context, () -> 1);
        zkServer.stop();
    }

    @Test
    public void contextLoads() {
        log.info(" ===> test ...... ");
    }
}
