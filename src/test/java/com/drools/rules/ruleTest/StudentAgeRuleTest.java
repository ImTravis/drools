package com.drools.rules.ruleTest;

import com.drools.rules.entity.StudentFact;
import org.drools.core.base.RuleNameEndsWithAgendaFilter;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class StudentAgeRuleTest {

    @Autowired
    private KieSession kieSession;

    @Test
    public void checkAge(){
        StudentFact studentFact = new StudentFact();
        studentFact.setAge(19);
        studentFact.setName("莉莉");
        kieSession.insert(studentFact);
        int sum =  kieSession.fireAllRules(new RuleNameEndsWithAgendaFilter("checkAge"));
        System.out.print("sum:"+sum);
    }
}
