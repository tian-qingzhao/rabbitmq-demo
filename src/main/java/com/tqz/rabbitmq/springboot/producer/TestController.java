package com.tqz.rabbitmq.springboot.producer;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @Author: tian
 * @Date: 2020/4/25 23:54
 * @Desc:
 */
@RestController
public class TestController {

    @Autowired
    private SpringBootProducer springBootProducer;

    @RequestMapping("/create-order")
    public Object createOrder() throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            String message = DateUtil.format(new Date(), DatePattern.NORM_DATETIME_MS_PATTERN);
            springBootProducer.sendCreateOrder(message + " " + i);
            Thread.sleep(800);
        }

        return "下单成功";
    }

    @RequestMapping("/create-user")
    public Object createUser() throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            String message = DateUtil.format(new Date(), DatePattern.NORM_DATETIME_MS_PATTERN);
            springBootProducer.sendCreateUser(message + " " + i);
            Thread.sleep(800);
        }

        return "下单成功";
    }
}
