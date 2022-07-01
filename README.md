
# 整体架构
![整体架构图](./resources/Rpc%E6%95%B4%E4%BD%93%E6%9E%B6%E6%9E%84.png)

# 测试方法
1、运行 Server：
```text
[main] INFO com.luoy.bigdata.rpc.server.Server - 创建并启动RpcEndPoint...
[main] INFO com.luoy.bigdata.rpc.netty.transport.RequestHandlerRegistry - Load request handler, type: 0, class: com.luoy.bigdata.rpc.netty.server.RpcRequestHandler.
[main] INFO com.luoy.bigdata.rpc.server.Server - 向RpcEndPoint注册com.luoy.bigdata.rpc.service.HelloService服务...
[main] INFO com.luoy.bigdata.rpc.netty.server.RpcRequestHandler - Add service: com.luoy.bigdata.rpc.service.HelloService, provider: com.luoy.bigdata.rpc.server.HelloServiceImpl.
[main] INFO com.luoy.bigdata.rpc.server.Server - 服务名: com.luoy.bigdata.rpc.service.HelloService, 向NameService注册...
[main] INFO com.luoy.bigdata.rpc.netty.nameservice.LocalFileNameService - Register service: com.luoy.bigdata.rpc.service.HelloService, uri: rpc://localhost:9999.
[main] INFO com.luoy.bigdata.rpc.netty.nameservice.LocalFileNameService - Metadata:
	Classname: com.luoy.bigdata.rpc.service.HelloService
	URIs:
		rpc://localhost:9999

[main] INFO com.luoy.bigdata.rpc.netty.serialize.SerializeSupport - Found serializer, class: java.lang.String, type: 0.
[main] INFO com.luoy.bigdata.rpc.netty.serialize.SerializeSupport - Found serializer, class: com.luoy.bigdata.rpc.netty.nameservice.Metadata, type: 100.
[main] INFO com.luoy.bigdata.rpc.netty.serialize.SerializeSupport - Found serializer, class: com.luoy.bigdata.rpc.netty.client.stubs.RpcRequest, type: 101.
[main] INFO com.luoy.bigdata.rpc.server.Server - 开始提供服务，按任何键退出.
```
2、运行 Client :
```text
[main] INFO com.luoy.bigdata.rpc.netty.serialize.SerializeSupport - Found serializer, class: java.lang.String, type: 0.
[main] INFO com.luoy.bigdata.rpc.netty.serialize.SerializeSupport - Found serializer, class: com.luoy.bigdata.rpc.netty.nameservice.Metadata, type: 100.
[main] INFO com.luoy.bigdata.rpc.netty.serialize.SerializeSupport - Found serializer, class: com.luoy.bigdata.rpc.netty.client.stubs.RpcRequest, type: 101.
[main] INFO com.luoy.bigdata.rpc.netty.nameservice.LocalFileNameService - Metadata:
	Classname: com.luoy.bigdata.rpc.service.HelloService
	URIs:
		rpc://localhost:9999

[main] INFO com.luoy.bigdata.rpc.client.Client - 找到服务com.luoy.bigdata.rpc.service.HelloService，提供者: rpc://localhost:9999.
channel链接建立
[main] INFO com.luoy.bigdata.rpc.client.Client - 请求服务, name: Master MQ...
处理数据---
[main] INFO com.luoy.bigdata.rpc.client.Client - 收到响应: Hello, Master MQ.
```