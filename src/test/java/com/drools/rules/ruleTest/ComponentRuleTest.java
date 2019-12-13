package com.drools.rules.ruleTest;

import com.drools.rules.entity.StudentFact;
import com.drools.rules.service.HelloService;
import org.drools.core.base.RuleNameEndsWithAgendaFilter;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ComponentRuleTest {
    @Autowired
    private KieSession kieSession;

    @Autowired
    private HelloService helloService;

    /**
     * 设置global变量
     */
    @Test
    public void validGlobalParam(){
        kieSession.setGlobal("isEnable",true);
        int sum =  kieSession.fireAllRules(new RuleNameEndsWithAgendaFilter("validGlobalParam"));
        System.out.print("sum:"+sum);
        //如果设置isEnable为false,则sum=0（执行规则条数）
    }

    /**
     * 传入对象,DRL文件调用逻辑代码
     */
    @Test
    public void validObj(){
        kieSession.setGlobal("helloService",helloService);
        kieSession.setGlobal("name","莉莉");
        int sum =  kieSession.fireAllRules(new RuleNameEndsWithAgendaFilter("validObj"));
        System.out.print("sum:"+sum);
    }
}
