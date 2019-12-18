package com.drools.rules.ruleTest;

import com.drools.rules.entity.*;
import com.drools.rules.service.HelloService;
import org.drools.core.base.RuleNameEndsWithAgendaFilter;
import org.drools.core.base.RuleNameStartsWithAgendaFilter;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.Channel;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.kie.api.runtime.rule.Row;
import org.kie.api.runtime.rule.ViewChangedEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    public void globalParam() {
        kieSession.setGlobal("isEnable", true);//全局对象isEnable
        StudentFact studentFact = new StudentFact();
        studentFact.setName("无名");
        kieSession.insert(studentFact); //入参对象name
        int sum = kieSession.fireAllRules(new RuleNameEndsWithAgendaFilter("globalParam"));
        System.out.print("sum:" + sum + "，规则修改后的name" + studentFact.getName());
        //如果设置isEnable为false,则sum=0（执行规则条数）
    }

    /**
     * 传入对象,DRL文件调用逻辑代码
     */
    @Test
    public void globalParamObj() {
        kieSession.setGlobal("helloService", helloService);
        kieSession.setGlobal("name", "莉莉");
        int sum = kieSession.fireAllRules(new RuleNameEndsWithAgendaFilter("globalParamObj"));
        System.out.print("sum:" + sum);
    }

    /**
     * channel 注册channel，发送和侦听消息
     */
    @Test
    public void registChannel() {
        kieSession.registerChannel("channelOne", new Channel() {
            @Override
            public void send(Object o) {
                System.out.print("规则Channel传过来的信息：" + o + "\n");
            }
        });
        int sum = kieSession.fireAllRules(new RuleNameEndsWithAgendaFilter("sendWithChannel"));
        kieSession.unregisterChannel("channelOne");
        System.out.print("sum:" + sum);
    }

    /**
     * for each
     */
    @Test
    public void forEachStudent() {
        List<StudentFact> studentFacts = new ArrayList<>();
        StudentFact studentFact;
        studentFact = new StudentFact("louis", 18, "male");
        studentFacts.add(studentFact);
        studentFact = new StudentFact("nicole", 26, "female");
        studentFacts.add(studentFact);
        studentFact = new StudentFact("fox", 45, "female");
        studentFacts.add(studentFact);

        kieSession.insert(studentFacts);
        kieSession.fireAllRules(new RuleNameEndsWithAgendaFilter("eachStudent"));

    }

    /**
     * query
     */
    @Test
    public void querySearch() {

        //TODO 注意此处，setGlobal按道理是没有必要传，因为我没有在When中定义，但是如果不加会报错

        //原因：因为还存在其他的规则，满足本规则的条件（虽然我在fireAllRules把他们过滤掉了），查看规则（globalParam），发现多了一个是否可用isEnable，
        //解决办法：
        //(1):去掉globalParam规则的isEnable参数；（2）本规则带入isEnable参数，setGlobal("isEnable",false) 或者setGlobal("isEnable",true) 都可以（具体原理因素不明白）

        kieSession.setGlobal("isEnable", true);//全局对象isEnable
        StudentFact studentFact = new StudentFact("louis", 19, "female");
        kieSession.insert(studentFact);

        QueryResults queryResults = kieSession.getQueryResults("girls", 18);
        for (QueryResultsRow row : queryResults) {
            StudentFact girl = (StudentFact) row.get("$girl");
            System.out.println("\n规则执行之前：按需查询："+girl.getName());
        }

        int sum = kieSession.fireAllRules(new RuleNameEndsWithAgendaFilter("editStudent"));
        System.out.print("匹配规则数："+sum+"、"+studentFact.getName()+"\n");

        queryResults = kieSession.getQueryResults("girls", 18);
        for (QueryResultsRow row : queryResults) {
            StudentFact girl = (StudentFact) row.get("$girl");
            System.out.println("\n规则执行之后：按需查询："+girl.getName());
        }

    }


    /**
     * query and query on live
     * 放在fireAllRules 之前和之后不一样
     */
    @Test
    public void querySearchOnLive() {
        SchoolFact schoolFact = new SchoolFact();
        schoolFact.setAddress("南京市仙林区阳山北路88号");
        schoolFact.setName("南京邮电大学");
        schoolFact.setClassNum(108);
        kieSession.insert(schoolFact);
        int sum = kieSession.fireAllRules(new RuleNameEndsWithAgendaFilter("editSchoolFact"));
        System.out.print("匹配规则数："+sum+"、"+schoolFact.getName()+"\n");

        kieSession.openLiveQuery("schoolsOnLive", new Object[24], new ViewChangedEventListener() {
            @Override
            public void rowInserted(Row row) {
                SchoolFact sch = (SchoolFact) row.get("$school");
                System.out.println("\nrowInserted："+sch.getName());
            }

            @Override
            public void rowDeleted(Row row) {
                SchoolFact sch = (SchoolFact) row.get("$school");
                System.out.println("\nrowDeleted："+sch.getName());
            }
            @Override
            public void rowUpdated(Row row) {
                SchoolFact sch = (SchoolFact) row.get("$school");
                System.out.println("\nrowUpdated："+sch.getName());
            }
        });
        //TODO query 和 query on live 可以一起用
        //按需查询
        QueryResults queryResults = kieSession.getQueryResults("schools", 18);
        for (QueryResultsRow row : queryResults) {
            SchoolFact girl = (SchoolFact) row.get("$school");
            System.out.println("\n按需查询："+girl.getName());
        }

    }

    /**
     * exist
     */
    @Test
    public void existSchool(){
        List<SchoolFact> schoolFacts = new ArrayList<>();
        SchoolFact schoolFact = new SchoolFact();
        schoolFact.setAddress("南京路88号");
        schoolFact.setName("南京默默大学");
        schoolFact.setClassNum(121);
        schoolFacts.add(schoolFact);

        schoolFact = new SchoolFact();
        schoolFact.setAddress("南京路86号");
        schoolFact.setName("南京啦啦大学");
        schoolFact.setClassNum(130);
        schoolFacts.add(schoolFact);

        schoolFact = new SchoolFact();
        schoolFact.setAddress("南京路89号");
        schoolFact.setName("南京哈哈大学");
        schoolFact.setClassNum(160);
        schoolFacts.add(schoolFact);

        kieSession.insert(schoolFacts);
        int num = kieSession.fireAllRules(new RuleNameStartsWithAgendaFilter("forallSchool"));
//        int num = kieSession.fireAllRules(new RuleNameStartsWithAgendaFilter("selectSchool"));
//        int num = kieSession.fireAllRules(new RuleNameStartsWithAgendaFilter("exitSchool"));

        System.out.print("num:"+num);

    }

    /**
     * collect 规则中获取集合中所有的女性
     */
    @Test
    public void collectionStudent() {
        List<PersonFact> studentFacts = new ArrayList<>();
        PersonFact studentFact;
        studentFact = new PersonFact("louis", 18, "male");
        studentFacts.add(studentFact);
        studentFact = new PersonFact("nicole", 26, "female");
        studentFacts.add(studentFact);
        studentFact = new PersonFact("fox", 45, "female");
        studentFacts.add(studentFact);

        kieSession.insert(studentFacts);
        int num = kieSession.fireAllRules(new RuleNameEndsWithAgendaFilter("collectRule"));
        System.out.print("\nnum:"+num);

    }

    @Test
    public void accumulate(){
        List<AnimalsFact> list = new ArrayList<>() ;
        AnimalsFact animalsFact;
        animalsFact = new AnimalsFact("狗","female",5,100);
        list.add(animalsFact);

        animalsFact = new AnimalsFact("兔子","female",5,80);
        list.add(animalsFact);

        animalsFact = new AnimalsFact("猫","female",3,123);
        list.add(animalsFact);

        animalsFact = new AnimalsFact("熊","male",10,234);
        list.add(animalsFact);
        int num=0;


        kieSession.insert(animalsFact);
        //单个动物的总价格
        num = kieSession.fireAllRules(new RuleNameEndsWithAgendaFilter("accumulateObjAction"));



        kieSession.insert(list);
        //所有动物的总价格（根据规则条件可二次筛选 见规则）
//        num = kieSession.fireAllRules(new RuleNameEndsWithAgendaFilter("accumulateListAction"));


        //所有动物的平均价格
//        num = kieSession.fireAllRules(new RuleNameEndsWithAgendaFilter("accumulateListAverageAction"));

        //找出最贵的动物
//        num = kieSession.fireAllRules(new RuleNameEndsWithAgendaFilter("accumulateListMaxAction"));

        //找出最便宜的动物
//        num = kieSession.fireAllRules(new RuleNameEndsWithAgendaFilter("accumulateListMinAction"));

        //低于120价格的动物上涨10块
//        num = kieSession.fireAllRules(new RuleNameEndsWithAgendaFilter("accumulateListMinAddTenAction"));


        //TODO 低于120价格的动物上涨10块 ，总和再减去10块，不知道为啥reserve没有执行成功
//        num = kieSession.fireAllRules(new RuleNameEndsWithAgendaFilter("accumulateListReverseAction"));

        System.out.print("\nnum:"+num);

        //取出所有价格低于120的动物的name集合
        ResultFact resultFact = new ResultFact();
        kieSession.insert(resultFact);
            //TODO 一开始我定义返回参数的时候直接用的 new ArrayList()，规则中也用List（）接的；执行完结果是拿到了，但是却执行了规则4次
            // （可能是因为，kieSession.insert(list);传入的也是集合，返回参数也是集合，导致规则无法识别，哪个对应哪个，从而只能按照每种组合的情况，进行匹配 ）
        num = kieSession.fireAllRules(new RuleNameEndsWithAgendaFilter("accumulateGetNamesListAction"));
        System.out.print("\nnum:"+num+",返回结果："+resultFact);


    }

}
