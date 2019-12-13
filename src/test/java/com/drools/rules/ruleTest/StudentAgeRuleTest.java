package com.drools.rules.ruleTest;

import com.drools.rules.entity.StudentFact;
import org.drools.core.base.RuleNameEndsWithAgendaFilter;
import org.drools.core.base.RuleNameStartsWithAgendaFilter;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class StudentAgeRuleTest {

    @Autowired
    private KieSession kieSession;

    /**
     * Hello word
     */
    @Test
    public void checkAge(){
        StudentFact studentFact = new StudentFact();
        studentFact.setAge(19);
        studentFact.setName("莉莉");
        kieSession.insert(studentFact);
        int sum =  kieSession.fireAllRules(new RuleNameEndsWithAgendaFilter("checkAge"));
        System.out.print("sum:"+sum);
    }



    /**
     * 技术点：activation-group
     * 对低于70、60分的同学进行处理
     */
    @Test
    public void gradeLow(){
        StudentFact studentFact = new StudentFact();
        studentFact.setAge(19);
        studentFact.setGrade(59);
        studentFact.setName("莉莉");
        kieSession.insert(studentFact);

        int sum =  kieSession.fireAllRules(new RuleNameStartsWithAgendaFilter("gradeLow"));
        System.out.print("sum:"+sum);
    }



    /**
     * 技术点：agenda-group
     * 对低于70、60分的同学进行处理
     */
    @Test
    public void gradeLow_aAgendaGroup(){
        StudentFact studentFact = new StudentFact();
        studentFact.setAge(19);
        studentFact.setGrade(59);
        studentFact.setName("莉莉");
        kieSession.getAgenda().getAgendaGroup("gradeLow").setFocus();
        kieSession.insert(studentFact);
        int sum =  kieSession.fireAllRules(new RuleNameEndsWithAgendaFilter("gradeLowSixty_aAgendaGroup"));
        System.out.print("sum:"+sum);
    }


}
