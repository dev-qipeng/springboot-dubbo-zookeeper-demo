# Spring Boot 整合dubbo(dubbo-spring-boot-starter)+zookeeper入门demo示例

> 本文是将Spring Boot与阿里巴巴的开源[dubbo-spring-boot-starter](https://github.com/alibaba/dubbo-spring-boot-starter)整合示例，而非官方Apache的[incubator-dubbo-spring-boot-project](https://github.com/apache/incubator-dubbo-spring-boot-project)，两种开源框架的配置和使用方式有差别，这里选用阿里巴巴自家更新的版本。有关Dubbo介绍请自行百度，本文仅是整合示例。本demo代码地址：https://github.com/dev-qipeng/springboot-dubbo-zookeeper-demo

### 一 .创建Spring Boot项目

需要创建一个maven父项目，及三个module项目，之后讲解各个module的用途，首先使用Idea中Spring Initializr创建maven父项目，只勾选Web，新建好后将pom文件`<packaging>`标签值改成pom，并新增以下依赖，此后各个子module项目无需再引入任何依赖。

![](https://raw.githubusercontent.com/dev-qipeng/springboot-dubbo-zookeeper-demo/master/docs/%E5%8B%BE%E9%80%89%E4%BE%9D%E8%B5%96.png)

```xml
<dependency>
    <groupId>com.alibaba.spring.boot</groupId>
    <artifactId>dubbo-spring-boot-starter</artifactId>
    <version>2.0.0</version>
</dependency>
<dependency>
    <groupId>org.apache.zookeeper</groupId>
    <artifactId>zookeeper</artifactId>
    <version>3.4.6</version>
    <exclusions>
        <exclusion>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-log4j12</artifactId>
        </exclusion>
        <exclusion>
            <groupId>log4j</groupId>
            <artifactId>log4j</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<!-- https://mvnrepository.com/artifact/com.101tec/zkclient -->
<dependency>
    <groupId>com.101tec</groupId>
    <artifactId>zkclient</artifactId>
    <version>0.10</version>
</dependency>
```

#### dubbo-parent项目的pom文件如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>site.qipeng</groupId>
    <artifactId>dubbo-parent</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>pom</packaging>

    <name>dubbo-parent</name>
    <description>dubbo-parent</description>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.0.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>

    <modules>
        <module>dubbo-api</module>
        <module>dubbo-provider</module>
        <module>dubbo-consumer</module>
    </modules>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>com.alibaba.spring.boot</groupId>
            <artifactId>dubbo-spring-boot-starter</artifactId>
            <version>2.0.0</version>
        </dependency>
        <dependency>
            <groupId>org.apache.zookeeper</groupId>
            <artifactId>zookeeper</artifactId>
            <version>3.4.6</version>
            <exclusions>
                <exclusion>
                    <groupId>org.slf4j</groupId>
                    <artifactId>slf4j-log4j12</artifactId>
                </exclusion>
                <exclusion>
                    <groupId>log4j</groupId>
                    <artifactId>log4j</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <!-- https://mvnrepository.com/artifact/com.101tec/zkclient -->
        <dependency>
            <groupId>com.101tec</groupId>
            <artifactId>zkclient</artifactId>
            <version>0.10</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>

```

新建三个module

- dubbo-api 公共接口，其他项目可以导入这个jar包依赖，共同使用这个项目里的接口
- dubbo-provider 生产者服务，发布接口
- dubbo-consumer 消费者服务，消费接口

#### dubbo-api的pom文件如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>site.qipeng</groupId>
    <artifactId>dubbo-api</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>dubbo-api</name>
    <description>dubbo-api</description>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>

    </dependencies>

</project>
```

#### dubbo-provider的pom文件需要引入dubbo-api依赖，并且指定parent，如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>site.qipeng</groupId>
    <artifactId>dubbo-provider</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>dubbo-provider</name>
    <description>dubbo-provider</description>

    <parent>
        <groupId>site.qipeng</groupId>
        <artifactId>dubbo-parent</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>site.qipeng</groupId>
            <artifactId>dubbo-api</artifactId>
            <version>0.0.1-SNAPSHOT</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```

#### dubbo-consumer的pom文件需要引入dubbo-api依赖，并且指定parent，如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>site.qipeng</groupId>
    <artifactId>dubbo-consumer</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>dubbo-consumer</name>
    <description>dubbo-consumer</description>

    <parent>
        <groupId>site.qipeng</groupId>
        <artifactId>dubbo-parent</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>site.qipeng</groupId>
            <artifactId>dubbo-api</artifactId>
            <version>0.0.1-SNAPSHOT</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```

### 二. 配置dubbo和zookeeper

zookeeper的安装、配置和启动可以参考https://www.cnblogs.com/Lzf127/p/7155316.html，windows下直接双击zkServer.cmd就启动了。

#### dubbo-api项目配置

在此dubbo-api项目中新建HelloService.class的接口，供dubbo-provider和dubbo-consumer使用：

```java
package site.qipeng.dubboapi;

public interface HelloService {
    String seyHello(String name);
}
```

#### dubbo-provider提供者服务配置

1. 在application.properties配置文件里加入一下配置：

   ```properties
   spring.application.name=dubbo-provider
   
   spring.dubbo.application.name=provider
   # zk注册中心地址
   spring.dubbo.registry.address=zookeeper://127.0.0.1:2181
   # dubbo 协议
   spring.dubbo.protocol.name=dubbo
   spring.dubbo.protocol.port=20880
   spring.dubbo.protocol.host=127.0.0.1
   ```

2. 再项目启动类上加`@EnableDubboConfiguration`注解开启dubbo

   ```java
   @SpringBootApplication
   @EnableDubboConfiguration
   public class DubboProviderApplication {
   
       public static void main(String[] args) {
           SpringApplication.run(DubboProviderApplication.class, args);
       }
   }
   ```

3. 新建要发布的服务，新建HelloService.java，使用Dubbo提供的`@Service`注解，并注册为bean，如下：

   ```java
   package site.qipeng.dubboprovider.service;
   
   import com.alibaba.dubbo.config.annotation.Service;
   import org.springframework.stereotype.Component;
   import site.qipeng.dubboapi.HelloService;
   
   @Service(interfaceClass = HelloService.class)
   @Component
   public class HelloServiceImpl implements HelloService {
   
       @Override
       public String seyHello(String name) {
           return "Hello " + name + " , this is provider";
       }
   }
   ```

#### dubbo-consumer消费者服务配置

1. 在application.properties配置文件中加入如下配置：

   ```properties
   spring.application.name=dubbo-consumer
   
   ## 避免和 server 工程端口冲突
   server.port=8082
   
   ## Dubbo 服务消费者配置
   spring.dubbo.application.name=consumer
   spring.dubbo.registry.address=zookeeper://127.0.0.1:2181
   ```

2. 在项目启动类上加`@EnableDubboConfiguration`开启dubbo服务

   ```java
   package site.qipeng.dubboconsumer;
   
   import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
   import org.springframework.boot.SpringApplication;
   import org.springframework.boot.autoconfigure.SpringBootApplication;
   
   @SpringBootApplication
   @EnableDubboConfiguration
   public class DubboConsumerApplication {
   
       public static void main(String[] args) {
           SpringApplication.run(DubboConsumerApplication.class, args);
       }
   }
   ```

3. 新建一个Controller，使用`@Reference`注解，将HelloService注入进来，然后就可以像调用本地方法一样调用远程服务了，代码如下：

   ```java
   package site.qipeng.dubboconsumer.controller;
   
   import com.alibaba.dubbo.config.annotation.Reference;
   import org.springframework.web.bind.annotation.RequestMapping;
   import org.springframework.web.bind.annotation.RequestParam;
   import org.springframework.web.bind.annotation.RestController;
   import site.qipeng.dubboapi.HelloService;
   
   @RestController
   public class HelloController {
   
       @Reference
       private HelloService helloService;
   
       @RequestMapping("/hello")
       public String hello(@RequestParam("name") String name){
           return helloService.seyHello(name);
       }
   
   }
   ```

### 三. 启动测试

分别启动zookeeper、dubbo-provider、dubbo-consumer，然后打开浏览器访问：

http://localhost:8082/hello?name=123

即可看到dubbo-consumer服务调用了dubbo-provider服务实现的方法。

![](https://raw.githubusercontent.com/dev-qipeng/springboot-dubbo-zookeeper-demo/master/docs/%E8%BF%90%E8%A1%8C%E7%BB%93%E6%9E%9C.png)