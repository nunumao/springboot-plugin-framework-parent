1.  【新增[#I58CDB]([#I58CDB](https://gitee.com/starblues/springboot-plugin-framework-parent/issues/I58CDB))】 插件可触发`WebServerInitializedEvent`类型的事件
2.  【新增】插件`dev`模式打包, 新增`localJars`配置(配置本地`jar`依赖文件)
3.  【新增】插件新增`@AutowiredType`注解, 可指定依赖注入类型
4.  【新增】支持插件`Controller`可不配置地址前缀(配置后会影响插件拦截器和静态资源访问)
5.  【优化】优化静态资源文件加载问题
6.  【优化】优化插件在某些版本的`idea`中缺失`debug`包, 导致无法`debug`
7.  【优化】优化从主程序依赖加载`Class`资源模块
8.  【优化】优化插件中注入异常提示
9.  【优化】优化`Swagger`相关功能
10. 【修复】`enablePluginIdRestPathPrefix`不生效问题
11. 【修复】插件无法注入`ObjectProvider<T>`、`ObjectFactory<T>`类型为主程序的`Bean`
12. 【修复[#I58CDB](https://gitee.com/starblues/springboot-plugin-framework-parent/issues/I58CDB)】 插件`Controller`使用`Aop`后, 获取不到参数
13. 【修复[#I58GCI](https://gitee.com/starblues/springboot-plugin-framework-parent/issues/I58GCI)】 主程序打包参数`libDir`不生效问题
14. 【修复】修复主程序配置`version`, 插件未配置`requires`导致出现版本校验失败的问题
15. 【修复】修复`StopValidator`禁止插件停止时, 插件状态变为`STOPPED_FAILURE`问题


- 注意: 本次升级后, 从主程序注入的`Bean`, 需设置注入类型, 详见文档: [插件中注入主程序Bean说明](https://www.yuque.com/starblues/spring-brick-3.0.0/vot8gg)