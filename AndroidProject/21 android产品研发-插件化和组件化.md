
# 组件化和插件化

>项目发展到一定程度，随着人员的增多，代码越来越臃肿，这时候就必须进行模块化的拆分。在我看来，模块化是一种指导理念，其核心思想就是分而治之、降低耦合。而在Android工程中如何实施，目前有两种途径，也是两大流派，一个是组件化，一个是插件化。本文主要讲解了组件化的方案

1.Android彻底组件化方案实践
http://www.jianshu.com/p/1b1d77f58e84

代码解耦：module(ok)
组件单独运行：配置切换成library和application(ok)
组件的数据传输：通过面向服务的实现暴露service(ok)
组件之间的UI跳转：URLRouter(ok)
组件的生命周期：每个组件service有onCreate和onDestroy(ok)
集成调试：组件开发OK后，打包成aar，上传到本地公共仓库(未完成)
代码隔离：通过gradle插件保证只在assembleDebug或者assembleRelease的时候把aar引入进来(未完成)













