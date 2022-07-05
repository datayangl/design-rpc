- [整体架构](#整体架构)
- [network](#network)
	- [核心数据结构](#核心数据结构)
	- [网络调用栈](#网络调用栈)
- [模块拆解](#模块拆解)
	- [rpc](#rpc)
	- [nameservice 服务注册](#nameservice-服务注册)
- [测试方法](#测试方法)

# 整体架构
![整体架构图](./resources/Rpc%E6%95%B4%E4%BD%93%E6%9E%B6%E6%9E%84.png)

# network
## 核心数据结构
![网络数据结构](./resources/%E7%BD%91%E7%BB%9C%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84.png)


# 模块拆解
## rpc 
1、服务器&客户端

TransportClient：网络通信客户端接口
```java
	createTransport(SocketAddress address, long connectionTimeout) // 
	void close(); // 关闭
```

NettyClient：TransportClient 的 netty 实现

TransportServer：网络通信服务器接口
```java
	start(RequestHandlerRegistry requestHandlerRegistry, int port) // 启动
	stop() // 关闭
```
NettyServer：TransportServer 的netty 实现

2、核心抽象

RpcEndPoint 接口：rpc 服务抽象，核心方法如下：
```java
	getRemoteService() // 客户端获取远程服务的引用
	addServiceProvider() // 服务端注册服务的实现实例
	getNameService() // 获取注册中心的引用
	startServer() // 服务端启动RPC框架，监听接口，开始提供远程服务。
```

NettyRpcEndPoint ：RpcEndPoint 接口的 netty 实现类。

3、请求&响应

Command：网络请求，核心数据类型是 Header 和数据(字节数组)
```java
    protected Header header;
    private byte [] payload;
```

Code：返回码
```java
    SUCCESS(0, "SUCCESS"),
    NO_PROVIDER(-2, "NO_PROVIDER"),
    UNKNOWN_ERROR(-1, "UNKNOWN_ERROR");
```

Header：请求头，用于标识请求，主要字段如下
```java
	private int requestId;
	private int version;
	private int type;
```


4、handler
CommandDecoder：Command 解码器抽象类，子类需实现 decodeHeader()

RequestDecoder：继承 CommandDecoder，实现 decodeHeader()

CommandEncoder：Command 编码器抽象类，子类需实现 encodeHeader()

RequestEncoder：继承 CommandEncoder，实现 encodeHeader()

RequestInvocation：处理请求的handler，负责根据请求类型找到实际的 handler 处理

整体 handler 链路：RequestDecoder -> ResponseEncoder -> RequestInvocation

5、序列化

Serializer：序列化接口
```java
public interface Serializer<T> {
    /**
     * 计算对象序列化后的长度，主要用于申请存放序列化数据的字节数组
     * @param entry 待序列化的对象
     * @return 对象序列化后的长度
     */
    int size(T entry);

    /**
     * 序列化对象。将给定的对象序列化成字节数组
     * @param entry 待序列化的对象
     * @param bytes 存放序列化数据的字节数组
     * @param offset 数组的偏移量，从这个位置开始写入序列化数据
     * @param length 对象序列化后的长度，也就是{@link Serializer#size(java.lang.Object)}方法的返回值。
     */
    void serialize(T entry, byte[] bytes, int offset, int length);

    /**
     * 反序列化对象
     * @param bytes 存放序列化数据的字节数组
     * @param offset 数组的偏移量，从这个位置开始写入序列化数据
     * @param length 对象序列化后的长度
     * @return 反序列化之后生成的对象
     */
    T parse(byte[] bytes, int offset, int length);

    /**
     * 用一个字节标识对象类型，每种类型的数据应该具有不同的类型值
     */
    byte type();

    /**
     * 返回序列化对象类型的Class对象。
     */
    Class<T> getSerializeClass();
}
```

![序列化实现概览](./resources/Serializer%E5%AE%9E%E7%8E%B0.png)

SerializeSupport：存储数据类 Class 和对应的序列化实现类

RpcRequestSerializer：RpcRequest 序列化器

StringSerializer：字符串序列化器

MetadataSerializer：元数据序列化器


## nameservice 服务注册
NameService：服务注册中心
```java
	supportedSchemes() //返回所有支持的协议
	connect(URI nameServiceUri) //连接注册中心
	registerService(String serviceName, URI uri) //注册服务
	lookupService(String serviceName) //查询服务地址
```

LocalFileNameService：服务注册中心的本地文件实现

Metadata：元数据，即服务名 -> 服务提供者 URI 列表

ServiceProviderRegistry：服务注册接口
```java
	<T> void addServiceProvider(Class<? extends T> serviceClass, T serviceProvider)
```


ServiceStub：桩，负责为服务调用代理 rpc 请求

StubFactory：桩工厂类

DynamicStubFactory：动态桩工厂类实现

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