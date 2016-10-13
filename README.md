![image](https://github.com/jpush/jbox/blob/dev/ReadmeResource/Boxinbear%401x.png)

[JBox](http://jbox.jiguang.cn/) 是一个能够接收你订阅消息的盒子，只需要简单配置就可以在 iOS、android 端接收你关注的消息。

本项目包含JBox iOS、android 和服务端的全套源代码。

###JBox特点

####提供两种集成方式。
一种是自定义集成，在JBox web 中创建自定义集成后会生成一个 Webhook ，可以通过自己的服务器给该 Webhook 发送消息请求，JBox Server 会把该条消息转发给 JBox app。
另一种是直接使用 JBox 提供的第三方集成，在 JBox web 中成功添加第三方集成后, 第三方应用出现你所关注事件后，会把该条消息转发给 JBox app。

####使用 Channel 来灵活管理订阅状态
两种集成方式都需要绑定 Channel 才能起作用 。 JBox app 通过扫描 JBox server 提供的二维码来订阅指定 Channel。
在 JBox app 成功订阅该 Channel 后，就可以接收集成发送的消息了，如果 JBox app 不再需要接受该集成的消息，只需要取消订阅集成所绑定的 Channel 即可。

####使用极光推送服务
JBox 直接使用极光推送服务，能够保证消息快速、稳定的送达手机设备。

###快速上手
[产品使用指导](http://jbox.jiguang.cn/guide) 能够帮助你快速使用JBox

###文档
[接口文档](http://jbox.jiguang.cn/document) 介绍自定义集成需要对接的 API

###社区
如果使用 JBox 遇到问题，不妨在 [极光社区](https://community.jiguang.cn)提出你的疑惑
