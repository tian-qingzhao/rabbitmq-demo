package com.tqz.rabbitmq.springbootmqdemo.producer;

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
public class OrderController {

    @Autowired
    private SpringBootProducer springBootProducer;

    @RequestMapping("/create-order")
    public Object createOrder() {
        for (int i = 0; i < 2000; i++) {
            String message = DateUtil.format(new Date(), DatePattern.NORM_DATETIME_MS_PATTERN);
            springBootProducer.sendMessage(message + " " + i);
        }
        return "下单成功";
    }
}
