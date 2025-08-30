package com.tqz.rabbitmq.v1;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: 田
 * @date: 2019/10/30 20:50
 * @Desc:
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RabbitmqApplication.class)
public class TestRabbitmq {

    @Autowired
    private Seder seder;

    @Test
    public void test1() throws Exception{
        while (true){
            Thread.sleep(1000);
            seder.send("学习rabbitMQ");
        }
    }
}