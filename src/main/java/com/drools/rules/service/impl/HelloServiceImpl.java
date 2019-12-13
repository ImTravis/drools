package com.drools.rules.service.impl;

import com.drools.rules.service.HelloService;
import org.springframework.stereotype.Service;

@Service
public class HelloServiceImpl implements HelloService {

    @Override
    public void sayHelloWord(String name) {
        System.out.print("Nice to meet you  "+name);
    }
}
