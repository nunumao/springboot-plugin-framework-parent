# Spring-Boot插件式开发框架

- 全新`3.0.0`版本上线啦，为动态扩展系统而生的框架。

### 背景 | Background
在当下后端市场，还是以`spring-boot`为核心框架进行系统开发，本框架可以在`spring-boot`系统上进行插件式的开发，将插件当做一个`mini`版本的`spring-boot`进行系统扩展开发，可以解决如下痛点：
1. 在`To-B`系统场景中，不同甲方会有不同的需求，在不打分支和改动系统核心代码的前提下，可以在插件中进行扩展开发特定功能，不同甲方使用不同插件，完美解决非核心系统的扩展功能开发。
2. 在`To-C`系统场景中，可以在主程序通过定义`java-interface`，在插件中做不同实现，来达到动态扩展系统功能。
3. 在开发中，由于引入了不同版本的依赖，导致系统无法运行，本框架可以完美解决在不同插件中定义不同版本的依赖，从底层进行隔离，以解决引入不同版本依赖冲突的问题。比如可以解决同一个程序同时连接`mysql-5`和`mysql-8`版本数据库。
4. 在开发中，不同插件依赖不同框架的功能，可以按需引入。比如在插件A引入连接`mysql`、在插件B引入连接`elasticsearch`、在插件C引入连接`oracle`。
5. 在插件中，可以任意集成不同的非`web`类型的`springboot-xx-starter`，然后将不同插件功能组装起来，以达到一个统一对外提供服务的完整系统，实现系统组装化、插拔化开发。
6. 在不重启主程序的前提下，对插件进行动态的安装、卸载。

### 介绍 | Intro
该框架可以在`spring-boot`项目上开发出插件功能，在插件中可以和`spring-boot`使用方式一模一样。使用了本框架您可以实现如下需求：

- 在插件中，您可以当成一个微型的`spring-boot`项目来开发，简单易用。
- 在插件中扩展出系统各种功能点，用于系统灵活扩展，再也不用使用分支来交付不同需求的项目了。
- 在插件中可以集成各种框架及其各种`spring-boot-xxx-starter`。
- 在插件中可以定义独立依赖包了，再也不用在主程序中定义依赖包了。
- 可以完美解决插件包与插件包、插件包与主程序因为同一框架的不同版本冲突问题了。各个插件可以定义同一依赖的不同版本框架。
- 无需重启主程序，可以自由实现插件包的动态安装部署，来动态扩展系统的功能。
- 插件也可以不依赖主程序独立集成微服务模块。
- 您可以丰富想象该框架给您带来哪些迫切的需求和扩展。

### 特性 | Features
1. 简化了框架的集成步骤，更容易上手。
2. 插件开发更加贴近`spring-boot`原生开发。
3. 使用`maven`打包插件，支持对插件的自主打包编译。目前支持: 

   开发打包：将插件打包成开发环境下的插件(仅需打包一次)。
   
   生产打包：将插件打包成一个`jar`、`zip`、`文件夹`等。
4. 插件支持两种运行模式

   插件模式： 作为一个插件，由主程序引导加载。
   
   自主启动模式：单独作为一个`spring-boot`项目来启动。
5. 自主的开发的类加载器，支持插件定义各种的依赖`jar`包。
6. 在插件中可以集成各种框架及其各种`spring-boot-xxx-starter`，比如集成`mybatis`、`mybatis-plus`、`spring-jpa`等。

### 运行环境 | Runtime Environment
1. jdk1.8+
2. apache maven 3.6+
3. spring-boot 2.0.0+

### 文档地址 | Document
- [https://www.yuque.com/starblues/spring-brick-3.0.0](https://www.yuque.com/starblues/spring-brick-3.0.0)

### 衍生产品 | Derivatives
#### 携带前后端插件功能的后台管理系统
- [Grape](https://gitee.com/starblues/grape)
#### ETL 工具
- [Rope](https://gitee.com/starblues/rope)

### 案例 | Demo
- [spring-brick 功能测试+案例](https://gitee.com/starblues/springboot-plugin-framework-example)

### 交流 | Contact
QQ交流群: 859570617

### 支持 | Support
如果您觉得框架使用起来不错的, 或者想支持本框架继续维护, 开源不易, 您可以通过如下方式进行支持:
- 对本框架进行 **Star**
- 将本框架 [spring-brick](https://gitee.com/starblues/springboot-plugin-framework-parent) 分享给您的朋友
- 进入 [框架首页](https://gitee.com/starblues/springboot-plugin-framework-parent), 点击**捐赠**按钮, 请作者吃点零食
![捐赠 spring-brick](img/spring_brick_donation.jpg "支持一下spring-brick")