1. 【新增】增加主包MAINIFEST中title和version定义, 标准jar包中包含`Implementation-Version`和`Implementation-Title`属性
2. 【新增】新增根据个人需求选择开发模式，支持隔离式开发模式(目前已有的)、共享式开发模式
3. 【修复】修复插件中`LiveBeansView`注册异常问题
4. 【修复[#I5IFR4](https://gitee.com/starblues/springboot-plugin-framework-parent/issues/I5IFR3)】 `ExtractFactory#getExtractByCoordinate` 类型转换`Bug`
5. 【修复[#I5GJO9](https://gitee.com/starblues/springboot-plugin-framework-parent/issues/I5GJO9)】`DefaultPluginManager#install` 异常无法抛出
6. 【修复】修复插件无法加载其他包依赖中的`mybatis-xml`问题
7. 【修复】修复插件子启动问题
8. 【优化】优化依赖资源默认不缓存, 以减少内存
