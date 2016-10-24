# 极光宝盒 - JBox

[![downloads-Android-brightgreen](https://img.shields.io/badge/downloads-Android-brightgreen.svg)]()
[![downloads-iOS-blue](https://img.shields.io/badge/downloads-iOS-blue.svg)]()

<p align="center">
    <a href="http://jbox.jiguang.cn/" target="_blank">
        <img src="https://github.com/jpush/jbox/blob/dev/ReadmeResource/Boxinbear%401x.png" alt="JBox" width=300/>
    </a>
</p>

[JBox](http://jbox.jiguang.cn/) 是一个能够接收你订阅消息的盒子，只需要简单配置就可以在 iOS、Android 端接收你关注的消息。

本项目包含 iOS、Android 和服务器端的所有源码。

## Feature

### 提供两种集成方式

- 自定义集成，在 JBox web 中创建自定义集成后会生成一个 Webhook，可以通过自己的服务器向该 Webhook 发送消息请求，JBox Server 会把该条消息转发给 JBox App。
- 直接使用 JBox 提供的第三方集成，在 JBox web 中添加所需要的第三方集成, 当第三方应用出现你所关注事件后，JBox App 就会自动接收到事件消息。

### 使用 Channel 来灵活管理订阅状态
两种集成方式都需要绑定 Channel。JBox App 通过扫描 JBox Server 提供的二维码来订阅指定 Channel。
在 JBox App 成功订阅 Channel 后，就能够收到集成发送的消息了，如果 JBox App 不再需要接收该集成的消息，只需要取消订阅的 Channel 即可。

### 使用极光推送服务
JBox 直接使用极光推送服务，能够保证消息快速、稳定的送达手机设备。


## Usage
![image](https://github.com/jpush/jbox/blob/dev/ReadmeResource/jbox流程图.png)

### Doc
- [自定义集成 API](http://jbox.jiguang.cn/document)
- [产品使用指导](http://jbox.jiguang.cn/guide)

## Support
- [极光社区](http://community.jiguang.cn/)
- [Look at the issues](https://github.com/jpush/jpush-phonegap-plugin/issues)

## License
MIT © [JiGuang](/LICENSE)
