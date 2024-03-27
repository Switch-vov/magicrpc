package com.switchvov.magicrpc.demo.provider;

import com.switchvov.magicrpc.core.test.TestZKServer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = MagicrpcDemoProviderApplication.class)
@Slf4j
public class MagicrpcDemoProviderApplicationTests {
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
    }

    @AfterAll
    public static void destroy() {
        zkServer.stop();
    }

    @Test
    public void contextLoads() {
        log.info(" ===> test ...... ");
    }
}
