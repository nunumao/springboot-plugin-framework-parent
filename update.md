1. 【新增】主程序可通过`pluginInfo`对象获取插件的`ClassLoader`。
2. 【新增】新增配置`plugin.pluginSwaggerScan`可禁用扫描插件的 `swagger` 接口。
3. 【新增】插件配置文件`spring.profiles.active`的值可跟随主程序配置切换。
4. 【新增】插件的日志可配置为跟随主程序日志配置打印。 
5. 【新增】补充常见打包的 `META-INF\MANIFEST.MF` 文件内容。
6. 【优化】优化插件隔离模式下，内存占用过大的问题。
7. 【修复[#I61INH](https://gitee.com/starblues/springboot-plugin-framework-parent/issues/I61INH)】修复`PluginUser#getBean(String name, boolean includeMainBeans)`返回的`Bean`错误
 