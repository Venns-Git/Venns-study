# SpringCloud

## SpringCloud简介

- 基于springboot提供了一套微服务的解决方案，包括服务注册与发现，配置中心，全链路监控，服务网关，负载均衡等等
- 简化了分布式系统基础设施的开发，为开发人员快速构建了分布式系统的一些工具，包括配置管理，服务发现，断路器，路由，微代理，事件总线，全局锁，决策竞选，分布式会话等等
- 是分布式微服务架构下的一站式解决方案，是各个微服务架构落地技术的结合体，俗称微服务全家桶

## SpringCloud和SpringBoot的关系

- SpingBoot专注于快速方便的开发单个个体微服务
- SpringCloud是关注全局的微服务协调治理框架，它将SpringBoot开发的一个个单体微服务整合并管理起来，为各个微服务之间提供 配置管理，服务发现，断路器，路由，微代理，路由等等集成服务
- SpringBoot可以离开SpringCloud独立使用，开发项目，但是SpringCloud离不开SpringBoot，属于依赖关系
- **SpringBoot专注于快速，方便的开发单个个体微服务，SpringCloud关注全局的服务治理框架**

## SpringCloud能干嘛

- Distributed/versioned configuration （分布式/版本控制）
- Service registration and discovery （服务注册与发现）
- Routing （路由）
- Service-to-service calls （服务到服务之间的调用）
- Load balancing （负载均衡）
- Circuit Breakers （断路器）
- Global locks （全局锁）
- Distributed messaging （分布式消息管理）
- .....

# Eureka服务注册与发现

Eureka是Netfix的一个子模块，也是核心模块之一，Eureka是一个基于REST的服务，用于定位服务，以实现云端中间层服务发现和故障转移，功能类似于Dubbo的注册中心

## 原理简述

- 采用C-S架构设计，EurekaServer作为服务注册功能的服务器

- 系统中的其他微服务，使用Eureka的客户端连接到EurekaServer并维持心跳连接，这样系统的维护人员就可以通过EurekaServer来监控系统中各个微服务是否正常运行，SpringCloud的一些其他模块（比如Zuul）就可以通过EurekaServer来发现系统中的其他微服务，并执行相关的逻辑
- Eureka包含两个组件：**Eureka Server** 和 **Eureka Client**
- Eureka Server：提高服务注册服务，各个节点启动后，会在Eureka中进行注册，这样Eureka Server中的服务注册表中会将会话中所有可用节点的信息，服务节点的信息在界面中直观的看到
- Eureka Client：是一个Java客户端，用于简化EurekaServer的交互，客户端同时也具备一个内置的，使用轮询负载算法的负载均衡器，在应用启动后，会向EurekaServer发送心跳（默认周期为30s），如果Eureka Server在多个心跳周期内没有接收到某个节点的心跳，EurekaServer会从服务注册表中把这个服务节点删除掉（默认周期为90s）
- 三大角色
	- Eureka Server:提高服务的注册与发现
	- Service Provider：将自身服务注册到Eureka中，从而使消费方能够找到
	- Service Consumer：服务消费方从Eureka中获取注册服务列表，从而找到消费服务
- Eureka也具有自我保护机制，简单来说就是某个时刻某个服务不可以用了，Eureka不会立刻清理，而是会保存该服务信息

## 简单应用

### 服务端

1. 新建一个model，导入eureka依赖

	```xml
	<dependency>
	    <groupId>org.springframework.cloud</groupId>
	    <artifactId>spring-cloud-starter-eureka-server</artifactId>
	    <version>1.4.6.RELEASE</version>
	</dependency>
	```

2. 编写配置文件：application.yml

	```yaml
	server:
	  port: 7001
	
	# Eureka配置
	eureka:
	  instance:
	    hostname: localhost # Eureka服务端的实例名称
	  client:
	    register-with-eureka: false # 表示是否向Eureka注册中心注册自己
	    fetch-registry: false # 如果为false，则表示自己为注册中心
	    service-url: # 监控页面
	      defaultZone: http://${eureka.instance.hostname}:${server.port}/erueka/
	```

3. 新建主启动类，加上`@EnableEurekaServer`注解

	```java
	@SpringBootApplication
	@EnableEurekaServer
	public class EurekaServer_7001 {
	    public static void main(String[] args) {
	        SpringApplication.run(EurekaServer_7001.class,args);
	    }
	}
	```

- 启动之后直接访问配置的端口即可

### 服务注册

1.  在服务提供者中添加依赖

	```xml
	<dependency>
	    <groupId>org.springframework.cloud</groupId>
	    <artifactId>spring-cloud-starter-eureka</artifactId>
	    <version>1.4.7.RELEASE</version>
	</dependency>
	<!--监控信息-->
	<dependency>
	    <groupId>org.springframework.boot</groupId>
	    <artifactId>spring-boot-starter-actuator</artifactId>
	</dependency>
	```

2. 编写配置

	```yaml
	# Eureka的配置
	eureka:
	  client:
	    service-url:
	      service-url:
	        defaultZone: http://localhost:7001/erueka/
	```

	即配置Eureka服务端的地址

3. 为服务提供者的主启动类添加注解`@EnbaleEurekaClient`,表示这是一个Eureka的客户端，服务启动后，自动注册到Eureka中

## 集群搭建

搭建3个Eureka服务端，端口分别为7001，7002，7003，进行关联

7001的配置文件:

```yaml
server:
  port: 7001
eureka:
  instance:
    hostname: localhost 
  client:
    register-with-eureka: false 
    fetch-registry: false 
    service-url: 
      defaultZone: http://localhost:7001/erueka/,http://localhost:7002/erueka/,http://localhost:7003/erueka/
```

相对应的更改7002和7003的配置文件,再将服务提供者的发布地址增加7002，7003:

```
eureka:
  client:
    service-url:
      service-url:
        defaultZone: http://localhost:7001/erueka/,http://localhost:7002/erueka/,http://localhost:7003/erueka/
```

## CAP原则

- C（Consistency）强一致性
- A（Availability）可用性
- P（Partition tolerance）分区容错性

### 核心

- 是在一个分布式系统中，一致性、可用性）、分区容错性,CAP 原则指的是，这三个要素最多只能同时实现两点，不可能三者兼顾
- CA：单点集群，满足一致性，可用性的系统，通常可扩展性较差 (Zookeeper)
- CP：满足一致性，分区容错性的系统，通常性能不是特别高
- AP：满足可用性，分区容错性的系统，通常可能一致性相对要求低一些 (Eureka)

# Ribbon及负载均衡

## 是什么

- Ribbon：基于Netfix Ribbon实现的一套**客户端负载均衡的工具**

- 简单的说，Ribbon的主要功能式提供客户端的软件负载均衡算法，将NetFix的中间层服务连接再一起，Ribbon的客户端组件提供一系列的原则的配置项如：连接超时，重试等等。简单的说就是在配置文件中列出LoadBalancer（简称LB：负载均衡）后面所有的机器，Ribbon会自动帮你基于某种算法规则（如简单轮询，随机连接等等）去连接这些机器，我们也很容易使用Ribbon实现自定义的负载均衡算法

## 能干嘛

- LB，即负载均衡（Load Balance），在微服务或分布式集群中经常用的一种应用。
- 负载均衡简单的说就是将客户的请求平摊的分配到多个服务上，从而达到系统的HA（高可用）。
- 常见的负载均衡软件有Nginx，Lvs等等。
- dubbo，SpringCloud都给我们提供了负载均衡，**SpringCloud的负载均衡算法可用自定义**

- 负载均衡简单分类:
	- 集中式LB
		- 即在服务的消费方和提供方之间使用独立的LB设备，如Nginx，由改设施负责把访问请求通过某种策略转发至服务的提供方
	- 进程式LB
		- 将LB逻辑集成到消费方，消费方从服务注册中心获知有哪些地址可用，然后自己再从这些地址中选出一个合适的服务器
		- **Ribbon就属于进程内LB**，它只是一个类库，集成于消费方进程，消费方通过它来获取到服务提供方的地址

## 简单应用

1. 服务消费者为80端口，进行Eureka配置

	```yaml
	# Eureka 配置
	eureka:
	  client:
	    register-with-eureka: false # 不向Eureka中注册自己
	    service-url:
	      defaultZone: http://localhost:7001/eureka/,http://localhost:7002/eureka/,http://localhost:7003/eureka/
	```

2. 为原来的RestTemplate配置负载均衡

	```java
	@Configuration //--spring applicationContext.xml
	public class ConfigBean {
	
	
	    //配置负载均衡实现RestTemplate
	    @Bean
	    @LoadBalanced //Ribbon
	    public RestTemplate getRestTemplate(){
	        return new RestTemplate();
	    }
	}
	```

3. 将REST_URL改为服务名称，不再是一个固定的服务提供者的地址

	```java
	//private static final String REST_URL_PREFIX = "http://localhost:8081";
	// 通过Ribbon实现的时候，地址应该是一个变量，通过服务名来进行访问
	private static final String REST_URL_PREFIX = "http://SPRINGCLOUD-PROVIDER-DEPT";
	```

4. 将原来的一个服务提供者8001扩展为3个，分别为8001，8002，8003，修改端口号和数据库，服务名称保持一致，这时候，再通过服务消费者去访问，就能查看实现效果（每次请求都获得不同数据库里的内容）

## 自定义负载均衡算法

核心接口：IRule

```java
package com.netflix.loadbalancer;

public interface IRule {
    Server choose(Object var1);

    void setLoadBalancer(ILoadBalancer var1);

    ILoadBalancer getLoadBalancer();
}
```

此接口下有许多实现类：

`AbstractLoadBalancerRule`: 抽象的负载均衡规则，一般不用

`AvailabilityFilteringRule`: 会先过滤掉崩溃，访问故障的服务，在剩下的服务中进行轮询

`RoundRobinRule`: 轮询，默认的负载均衡算法

`RandomRule`: 随机

`ReTry`:会先按照轮询获取服务，如果轮询失败，则会在指定的时间内重试

### 更改默认算法

只需在ConfigBean中配置即可。

```java
@Configuration //--spring applicationContext.xml
public class ConfigBean {


    //配置负载均衡实现RestTemplate
    @Bean
    @LoadBalanced //Ribbon
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }

    @Bean
    public IRule myRule(){
        //将默认轮询算法改为随机算法
        return new RandomRule();
    }
}
```

### 自定义算法规则

规则：每个服务访问5次，然后访问下一个服务

1. 模仿随机算法写自己的规则

	```java
	public class MyRule extends AbstractLoadBalancerRule {
	    private int total = 0; //共访问服务次数
	    private int currentIndex = 0; //当前服务的下标
	    public MyRule() {
	    }
	
	    public Server choose(ILoadBalancer lb, Object key) {
	        if (lb == null) {
	            return null;
	        } else {
	            Server server = null;
	
	            while(server == null) {
	                if (Thread.interrupted()) {
	                    return null;
	                }
	
	                List<Server> upList = lb.getReachableServers(); //获得所有可用的服务
	                List<Server> allList = lb.getAllServers(); //获得所有的服务
	                int serverCount = allList.size();
	                if (serverCount == 0) {
	                    return null;
	                }
	
	                if (total < 5){
	                    server =  upList.get(currentIndex);
	                    total++;
	                }else {
	                    total = 0;
	                    currentIndex++;
	                    if (currentIndex >= upList.size()){
	                        currentIndex = 0;
	                    }
	                    server = upList.get(currentIndex);
	                }
	
	                if (server == null) {
	                    Thread.yield();
	                } else {
	                    if (server.isAlive()) {
	                        return server;
	                    }
	
	                    server = null;
	                    Thread.yield();
	                }
	            }
	
	            return server;
	        }
	    }
	
	    protected int chooseRandomInt(int serverCount) {
	        return ThreadLocalRandom.current().nextInt(serverCount);
	    }
	
	    public Server choose(Object key) {
	        return this.choose(this.getLoadBalancer(), key);
	    }
	
	    public void initWithNiwsConfig(IClientConfig clientConfig) {
	    }
	}
	```

2. 配置自己的config，注入自己写的规则

	```java
	@Configurable
	public class MyRuleConfig {
	    @Bean
	    public IRule myRule(){
	        return new MyRule();
	    }
	}
	```

3. 在主启动类上加上`@RibbonClient`注解

	```java
	@SpringBootApplication
	@EnableEurekaClient
	@RibbonClient(name = "SPRINGCLOUD-PROVIDER-DEPT",configuration = MyRuleConfig.class)
	public class DeptConsumer_80 {
	    public static void main(String[] args) {
	        SpringApplication.run(DeptConsumer_80.class,args);
	    }
	}
	```

	- name为服务名
	- configuration为自定义Ribbon的配置类
	- 注意：自定义的Ribbon配置类和规则类不能放在主启动类的统计或者下级目录

# Feign负载均衡

## 简介

Feign是声明式Web Service客户端，它让微服务之间的调用变得更简单，类似controller调用service。SpringCloud集成了Ribbon和Eureka，可以使用Feigin提供负载均衡的http客户端

只需要创建一个接口，添加注解即可。

Feign，主要是社区版，大家都习惯面向接口编程。这个是很多开发人员的规范。调用微服务访问两种方法：

1. 微服务名字 【ribbon】
2. 接口和注解 【feign】

**Feign能干什么**

- Feign旨在使编写Java Http客户端变得更容易

- 前面在使用Ribbon + RestTemplate时，利用RestTemplate对Http请求的封装处理，形成了一套模板化的调用方法。但是在实际开发中，由于对服务依赖的调用可能不止一处，往往一个接口会被多处调用，所以通常都会针对每个微服务自行封装一个客户端类来包装这些依赖服务的调用。所以，Feign在此基础上做了进一步的封装，由他来帮助我们定义和实现依赖服务接口的定义，**在Feign的实现下，我们只需要创建一个接口并使用注解的方式来配置它 (类似以前Dao接口上标注Mapper注解，现在是一个微服务接口上面标注一个Feign注解)**，即可完成对服务提供方的接口绑定，简化了使用Spring Cloud Ribbon 时，自动封装服务调用客户端的开发量。

**Feign集成了Ribbon**

- 利用Ribbon维护了MicroServiceCloud-Dept的服务列表信息，并且通过轮询实现了客户端的负载均衡，而与Ribbon不同的是，通过Feign只需要定义服务绑定接口且以声明式的方法，优雅而简单的实现了服务调用。

## 使用步骤

1. 依据springcloud-consumer-ribbon-80模块复制，创建springcloud-consumer-feign-80模块，修改依赖

	```xml
	<!--实体类+web-->
	<dependencies>
	    <dependency>
	        <groupId>com.venns</groupId>
	        <artifactId>springcloud-api</artifactId>
	        <version>1.0-SNAPSHOT</version>
	    </dependency>
	    <dependency>
	        <groupId>org.springframework.boot</groupId>
	        <artifactId>spring-boot-starter-web</artifactId>
	    </dependency>
	    <dependency>
	        <groupId>org.springframework.boot</groupId>
	        <artifactId>spring-boot-devtools</artifactId>
	    </dependency>
	    <!-- feign -->
	    <dependency>
	        <groupId>org.springframework.cloud</groupId>
	        <artifactId>spring-cloud-starter-feign</artifactId>
	        <version>1.4.6.RELEASE</version>
	    </dependency>
	    <!-- eureka -->
	    <dependency>
	        <groupId>org.springframework.cloud</groupId>
	        <artifactId>spring-cloud-starter-eureka</artifactId>
	        <version>1.4.6.RELEASE</version>
	    </dependency>
	</dependencies>
	```

2. 在实体项目下（springcloud-api）中新建 DeptClientService类

	```java
	@Component
	@FeignClient(value = "SPRINGCLOUD-PROVIDER-DEPT")
	public interface DeptClientService {
	
	    @RequestMapping("/dept/get/{id}")
	    public Dept queryById(@PathVariable("id") long id);
	
	    @RequestMapping("/dept/list")
	    public List<Dept> queryAll();
	
	    @RequestMapping("/dept/list")
	    public boolean addDept(Dept dept);
	}
	```

3. 将服务消费者springcloud-consumer-feign-80模块中的服务改为 DeptClientService

	```java
	@RestController
	public class DeptConsumerController {
	    /*
	     * 消费者调用服务者的server层
	     */
	
	    @Autowired
	    private DeptClientService deptClientService;
	
	    @RequestMapping("/consumer/dept/get/{id}")
	    public Dept get(@PathVariable("id") long id){
	        return this.deptClientService.queryById(id);
	    }
	
	    @RequestMapping("/consumer/dept/add")
	    public boolean add(Dept dept){
	        return this.deptClientService.addDept(dept);
	    }
	
	    @RequestMapping("/consumer/dept/list")
	    public List<Dept> list(){
	        return this.deptClientService.queryAll();
	    }
	}
	```

4. 再将服务消费者的主启动类加上`@EnableFeignClients`注解

	```java
	@SpringBootApplication
	@EnableEurekaClient
	@EnableFeignClients(basePackages = {"com.venns.springcloud"})
	public class FeignDeptConsumer_80 {
	    public static void main(String[] args) {
	        SpringApplication.run(FeignDeptConsumer_80.class,args);
	    }
	}
	```

	- basePackages为扫描包，Feign会扫描指定包下的带有`@FeignClient`注解的类

5. 启动服务提供者8001，8002，8003，Eureka服务注册中心7001，再启动采用Feigin的服务消费者，默认也采用轮询的规则

# Hystrix服务熔断/降级

**分布式系统面临的问题**
复杂分布式体系结构中的应用程序有数十个依赖关系，每个依赖关系在某些时候将不可避免地失败。

**服务雪崩**

- 多个微服务之间调用的时候，假设微服务A调用微服务B和微服务C，微服务B和微服务C又调用其它的微服务，这就是所谓的“扇出”。如果扇出的链路上某个微服务的调用响应时间过长或者不可用，对微服务A的调用就会占用越来越多的系统资源，进而引起系统崩溃，所谓的“雪崩效应”.
- 对于高流量的应用来说，单一的后端依赖可能会导致所有服务器上的所有资源都在几秒钟内饱和。比失败更糟糕的是，这些应用程序还可能导致服务之间的延迟增加，备份队列，线程和其他系统资源紧张，导致整个系统发生更多的级联故障。这些都表示需要对故障和延迟进行隔离和管理，以便单个依赖关系的失败，不能取消整个应用程序或系统
- 我们需要弃车保帅

**什么是Hystrix**

- Hystrix是一个用于处理分布式系统的延迟和容错的开源库，在分布式系统里，许多依赖不可避免的会调用失败，比如超时、异常等，Hystrix能够保证在一个依赖出问题的情况下，不会导致整体服务失败，避免级联故障，以提高分布式系统的弹性。
- “断路器”本身是一种开关装置，当某个服务单元发生故障之后，通过断路器的故障监控（类似熔断保险丝），向调用方返回一个符合预期的、可处理的备选响应（FallBack），而不是长时间的等待或者抛出调用方无法处理的异常，这样就保证了服务调用方的线程不会被长时间、不必要地占用，从而避免了故障在分布式系统中的蔓延，乃至雪崩。

**Hystrix能干嘛**

- 服务熔断
- 服务降级
- 服务降流
- 接近实时的监控

## 服务熔断（主要用户服务端）

**是什么**

- 熔断机制是应对雪崩效应的一种微服务链路保护机制
- 扇出链路的某个微服务不可用或者响应时间太长，会进行服务的降级，进而熔断该节点微服务的调用，快速返回"错误"的响应信息。当检测到该节点微服务调用响应正常回复后恢复调用链路。在springCloud框架里熔断机制通过Hystrix实现。Hystrix会监控微服务服务间调用的状况，当失败的调用到达一定的阈值，缺省是5秒内20次调用失败就会启动熔断机制，熔断机制的注解是 `@HystrixCommand`

**简单应用**

1. 为一个服务提供者添加hystrix依赖

	```xml
	<!-- hystrix -->
	<dependency>
	    <groupId>org.springframework.cloud</groupId>
	    <artifactId>spring-cloud-starter-hystrix</artifactId>
	    <version>1.4.6.RELEASE</version>
	</dependency>
	```

2. 为方法指定熔断的解决方案

	```java
	@ResponseBody
	@Controller
	public class DeptController {
	
	    @Autowired
	    private DeptService deptService;
	
	    @GetMapping("/dept/get/{id}")
	    @HystrixCommand(fallbackMethod = "hystrixQueryById")
	    public Dept queryById(@PathVariable("id") long id){
	        Dept dept = deptService.queryById(id);
	
	        if (dept == null) throw new RuntimeException("id => " + id + ",不存在该用户");
	        return dept;
	    }
	
	    // 备选方法
	    public Dept hystrixQueryById(@PathVariable("id") long id){
	        return new Dept()
	                .setDeptno(id)
	                .setDname("id => " + id + ",不存在该用户 -- @Hystrix")
	                .setDb_source("no this database in mysql");
	    }
	}
	```

3. 为主启动类添加`@EnableCircuitBreaker`注解，添加对断路器的支持

	```java
	@SpringBootApplication
	@EnableEurekaClient
	@EnableCircuitBreaker // 添加对熔断的支持
	public class DeptProvider_Hystrix_8001 {
	    public static void main(String[] args) {
	        SpringApplication.run(DeptProvider_Hystrix_8001.class,args);
	    }
	}
	```

4. 启动测试，当get一个不存在的id时，前端会返回一个新的dept，提示不存在

## 服务降级（主要用于客户端）

**是什么**

银行办理业务，A业务过于繁忙，请求帮助。暂时关闭C窗口，节约资源，去帮助A处理业务。当有人来C窗口办理业务时，C窗口显示暂停业务。帮助A处理完业务，再回来开启C窗口。

所谓降级，一般是从整体符合考虑。就是当某个服务熔断之后，服务器将不再被调用，此时客户端可以自己准备一个本地的fallback回调，返回一个缺省值。这样做，虽然服务水平下降，但好歹能用，比直接挂断强。

服务降级处理是在客户端实现完成的，与服务端没有关系

**简单应用**

1. 为springcloud-api的DeptClientService新建实现类DeptClientServiceFallbcakFactory

	```java
	@Component
	public class DeptClientServiceFallbcakFactory implements FallbackFactory {
	    @Override
	    public DeptClientService create(Throwable throwable) {
	        return new DeptClientService() {
	            @Override
	            public Dept queryById(long id) {
	                return new Dept().setDeptno(id).setDname("id == > "+id + "，没有对应的信息，客户端提供了降级的服务，这个服务已经被关闭").setDb_source("没有数据");
	            }
	
	            @Override
	            public List<Dept> queryAll() {
	                return null;
	            }
	
	            @Override
	            public boolean addDept(Dept dept) {
	                return false;
	            }
	        };
	    }
	}
	```

2. 给DeptClientService的Feign注解添加参数

	```java
	@Component
	@FeignClient(value = "SPRINGCLOUD-PROVIDER-DEPT", fallbackFactory = DeptClientServiceFallbcakFactory.class)
	public interface DeptClientService {
	
	    @RequestMapping("/dept/get/{id}")
	    public Dept queryById(@PathVariable("id") long id);
	
	    @RequestMapping("/dept/list")
	    public List<Dept> queryAll();
	
	    @RequestMapping("/dept/list")
	    public boolean addDept(Dept dept);
	}
	```

3. 给配置了feign的服务消费者配置hystrix降级

	```yaml
	# 开启降级Feign.hystrix
	feign:
	  hystrix:
	    enabled: true
	```

4. 启动测试，正常开启服务时，正常访问，当关闭服务时，网页依旧可以访问，只是会向服务消费者提示消息

## 小结

服务熔断：服务端 某个服务超时或者异常，引起熔断

服务降级：客户端 从整体的网站请求负载考虑，当某个服务熔断或者关闭之后，服务将不再被调用，此时在客户端可以准备一个自己的FallbackFactory，返回一个默认值，整体的服务水平下降了，但是好歹能用，比直接挂掉强

# Zuul路由网关

## 概述

**什么是Zuul**

zuul包含了对请求的路由和过滤两个最主要的功能：

其中路由功能负责将外部请求转发到剧透的微服务实例上，是实现外部访问统一入口的基础而过滤器功能则负责对请求的处理过程进行干预，是实现请求校验、服务聚合等功能的基础，Zuul和Eureka进行整合，将Zuul自身注册为Eureka服务治理下的应用，同时从Eureka中获得其他微服务的消息，也即以后的访问微服务都是通过Zuul转发后获得。

注意：Zuul服务最终还是会注册进Eureka

**Zuul能干嘛**

- 路由
- 过滤

## 简单应用

1. 新建模块，添加依赖

	```xml
	<dependencies>
	    <!-- zuul -->
	    <dependency>
	        <groupId>org.springframework.cloud</groupId>
	        <artifactId>spring-cloud-starter-zuul</artifactId>
	        <version>1.4.6.RELEASE</version>
	    </dependency>
	
	    <!-- hystrix -->
	    <dependency>
	        <groupId>org.springframework.cloud</groupId>
	        <artifactId>spring-cloud-starter-hystrix</artifactId>
	        <version>1.4.6.RELEASE</version>
	    </dependency>
	
	    <dependency>
	        <groupId>org.springframework.cloud</groupId>
	        <artifactId>spring-cloud-starter-hystrix-dashboard</artifactId>
	        <version>1.4.6.RELEASE</version>
	    </dependency>
	
	    <!-- Ribbon -->
	    <dependency>
	        <groupId>org.springframework.cloud</groupId>
	        <artifactId>spring-cloud-starter-ribbon</artifactId>
	        <version>1.4.6.RELEASE</version>
	    </dependency>
	    <!-- eureka -->
	    <dependency>
	        <groupId>org.springframework.cloud</groupId>
	        <artifactId>spring-cloud-starter-eureka</artifactId>
	        <version>1.4.6.RELEASE</version>
	    </dependency>
	    <dependency>
	        <groupId>com.venns</groupId>
	        <artifactId>springcloud-api</artifactId>
	        <version>1.0-SNAPSHOT</version>
	    </dependency>
	    <dependency>
	        <groupId>org.springframework.boot</groupId>
	        <artifactId>spring-boot-starter-web</artifactId>
	    </dependency>
	    <dependency>
	        <groupId>org.springframework.boot</groupId>
	        <artifactId>spring-boot-devtools</artifactId>
	    </dependency>
	</dependencies>
	```

2. 在apolication.xml 进行配置

	```yaml
	server:
	  port: 9527
	spring:
	  application:
	    name: springcloud-zuul
	eureka:
	  client:
	    service-url:
	      defaultZone: http://localhost:7001/eureka/,http://localhost:7002/eureka/,http://localhost:7003/eureka/
	  instance:
	    instance-id: zuul9627.com
	    prefer-ip-address: true # 显示ip地址
	```

3. 为主启动类添加注解支持

	```java
	@SpringBootApplication
	@EnableZuulProxy
	public class ZuulApplication_9527 {
	    public static void main(String[] args) {
	        SpringApplication.run(ZuulApplication_9527.class,args);
	    }
	}
	```

4. 启动服务提供者，消费者，以及Eureka注册中心，访问Eureka可以看到已经注册了两个服务，一个是服务提供者提供的服务，一个我们自己的Zuul服务，也就是SPRINGCLOUD-ZUUL
5. 同时，我们访问zuul服务的端口加上服务名也能访问其他服务，例如: localhost:9237/springcloud-dept/get/1，这样就不用再具体的去访问每一个服务

同时也可以改变微服务的名字：

```yaml
zuul:
  routes:
    mydept.serviceId: springcloud-provider-dept # 服务ID
    mydept.path: /mydept/** # 访问路径
```

- 这样，当访问指定服务时，路径也会变成自己指定的路径,但是同样也可以使用服务id进行访问，如果需要只能用指定路径进行访问，不能使用服务id进行访问，也可以在配置文件中进行修改:

```yaml
zuul:
  routes:
    mydept.serviceId: springcloud-provider-dept
    mydept.path: /mydept/**
  ignored-services: springcloud-provider-dept # 不能使用服务id进行访问 也可以使用通配符 "*" 禁止所有的服务通过服务id进行访问
```

# config 分布式配置

## 概述

#### 分布式系统系统面临的配置文件的问题

微服务意味着要将单体应用中的业务拆分成一个个子服务，每个服务的粒度相对较小，因此系统中会出现大量的服务，由于每个服务都需要配置必要的配置信息才能运行，所以一套集中的，动态的配置管理设施必不可少。

SpringCloud提供了ConfigServer来解决这个问题，我们每一个微服务自己带有一个application.yaml，那上百个配置文件修改起来，那工作量是相当的大

#### 什么是SpringCloud config 分布式配置中心

![image-20210324045232963](image-20210324045232963.png)

 Spring Cloud Config为微服务架构中的微服务提供集中化的外部配置支持，配置服务器为每个不同微服务应用的所有环节提供了一个**中心化的外部配置。**

 Spring Cloud Config 分为**服务端**和**客户端**两部分：

服务端：服务端也成为分布式配置中心，他是一个独立的微服务应用，用来连接配置服务器并为客户端提供获取配置信息，加密，解密信息等访问接口
客户端：客户顿则是通过指定的配置中心来管理应用该资源，以及与业务相关的配置内容，并在启动的时候从配置中心获取和加载配置信息，配置服务器默认采用git来存储配置信息，这样就有助于对配置环境进行版本管理，并且还可以通过git客户端工具来方便管理和访问配置内容

#### SpringCloud config分布式配置中心能干嘛

- 集中管理配置文件
- 不同环境，不同配置，动态化的配置更新，分环境部署，比如 /dev ，/test，/beta，/release
- 运行期间动态调整配置，不需要在每个服务部署的机器上编写配置文件，服务会向配置中心统一拉取配置自己的信息
- 当配置发生变动时，服务不需要重启，即可感知到配置的变化，并应用新的配置
- 将配置信息以REST接口的形式暴露

#### SpringCloud config分布式配置中心与github整合

由于SpringCloud config默认使用Git来存储配置文件（也有其他方式，比如支持SVN和本地文件），但是最推荐的还是Git，而且是使用http / htpps 访问的形式

## 简单应用

#### 服务端搭建

1. 添加config依赖

	```xml
	<dependencies>
	    <dependency>
	        <groupId>org.springframework.boot</groupId>
	        <artifactId>spring-boot-starter-web</artifactId>
	    </dependency>
	    <dependency>
	        <groupId>org.springframework.cloud</groupId>
	        <artifactId>spring-cloud-config-server</artifactId>
	        <version>2.1.1.RELEASE</version>
	    </dependency>
	</dependencies>
	```

2. 编写配置文件

	```yaml
	server:
	  port: 3344
	spring:
	  application:
	    name: springcloud-config-server
	  # 连接远程仓库
	  cloud:
	    config:
	      server:
	        git:
	          uri: https://github.com/Venns-Git/Venns-study.git # 远程git仓库地址 只能是https的。
	```

3. 主启动类添加注解支持

	```java
	@SpringBootApplication
	@EnableConfigServer
	public class Config_Server_3344 {
	    public static void main(String[] args) {
	        SpringApplication.run(Config_Server_3344.class,args);
	    }
	}
	```

4. 启动测试，可以访问git上的资源以及配置,格式如下 ：

	```
	/{application}/{profile}[/{label}] 
	/{application}-{profile}.yml
	/{label}/{application}-{profile}.yml
	/{application}-{profile}.properties
	/{label}/{application}-{profile}.properties
	```

#### 客户端搭建

1. 导入客户端依赖

	```xml
	<dependencies>
	    <dependency>
	        <groupId>org.springframework.boot</groupId>
	        <artifactId>spring-boot-starter-web</artifactId>
	    </dependency>
	
	    <dependency>
	        <groupId>org.springframework.cloud</groupId>
	        <artifactId>spring-cloud-starter-config</artifactId>
	        <version>2.1.1.RELEASE</version>
	    </dependency>
	</dependencies>
	```

2. 编写配置文件 -- bootstrap.yml bootstrap.yml 为系统级别的配置文件，application.yml为用户级别的配置文件

	```yaml
	spring:
	  cloud:
	    config:
	      uri: http://localhost:3344 # config 服务端地址
	      name: config-client # 需要从git上读取的资源 不需要后缀
	      profile: dev # 需要读取哪个环境的资源
	      label: master # 需要到哪个分支去拿
	  application:
	    name: springcloud-config-client-3355
	```

3. 编写一个controller，通过访问服务端读取远程git上的配置信息

	```java
	@RestController
	public class ConfigClientController {
	
	    // 读取远程git上的配置文件中的值
	
	    @Value("$(spring.application.name)")
	    private String applicationName;
	
	    @Value("$(eureka.client.service-url)")
	    private String eurekaServer;
	
	    @Value("$(server.port)")
	    private String port;
	
	
	    @RequestMapping("/config")
	    public String getConfig(){
	        return "spring.application.name: " + applicationName +
	                "eureka.client.service-url: " + eurekaServer +
	                "server.port: " + port;
	
	    }
	}
	```

4. 启动测试，端口号即为配置文件中绑定的配置文件中的端口号，访问`/config`请求，即可获得当前正在使用的配置信息