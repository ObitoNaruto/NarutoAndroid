# UML

>
http://www.jianshu.com/p/1256e2643923
http://www.cnblogs.com/carsonzhu/p/5313262.html



## 时序图示例

![Alt text](http://g.gravizo.com/g?
a --> b: how are you;
note right: greeting;
a -> a: i am thinking; 
b -> a: fine;
)



## 用例图

![Alt text](http://g.gravizo.com/g?
    left to right direction; skinparam packageStyle rect; actor customer;
    actor chef; rectangle restaurant{
    customer -> (eat food);
    customer -> (pay for food); chef -> (cook food); }
)

## 活动图

![Alt text](http://g.gravizo.com/g?
    (*) --> "buy 10 apples"; if "is there watermelon " then;
    -->[true] "buy a apple"; -right-> (*); else;
    ->[false] "Something else";
    -->(*); endif;
)

## 组件图

![Alt text](http://g.gravizo.com/g?
    HTTP - [web server];
    [web server] - [app server];
    database "mysql" {;
    [database];
    };
    [app server] - [database];
)


## 状态图

![Alt text](http://g.gravizo.com/g?
    [*] -> ready : start; 
    ready -> running : get cpu; 
    running -> ready : lost cpu; 
    running -down-> block : io, sleep, locked; 
    block -up-> ready : io return, sleep over, get lock; 
    running -> [*] : complete;
)

## 类图
- 访问控制权限

<!--![Alt text](http://g.gravizo.com/g?-->
    <!--class Dummy {-->
    <!--- private field1-->
    <!--# protected field2-->
    <!--~ package method1()-->
    <!--+ public method2()-->
    <!--};-->
<!--)-->


- 继承

![Alt text](http://g.gravizo.com/g?
    Father <|-- Son;
)

- 实现


![Alt text](http://g.gravizo.com/g?
    abstract class AbstractList;
    interface List ;
    List <|.. AbstractList;
)

- 依赖

类B的作为参数被类A在某个method中使用使用


![Alt text](http://g.gravizo.com/g?
    Human ..> Cigarette
)


- 关联

强依赖，类B以类属性的形式出现在关联类A中


![Alt text](http://g.gravizo.com/g?

    class Water;
    class Human;
    Human --> Water;
)

- 聚合

管理关系一种，整体与部分，拥有的关系，has-a，整体与部分可分离，有各自的生命周期


![Alt text](http://g.gravizo.com/g?

    Company o-- Human
)

- 组合

关联关系的一种，体现的contains-a关系，强聚合，整体与部分的关系，整体的生命周期结束也意味着部分的生命周期结束

![Alt text](http://g.gravizo.com/g?

    Human *-- Brain
    
)
