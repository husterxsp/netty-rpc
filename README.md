## Netty RPC框架

### 运行rpc-sample

1. 启动注册中心，etcd 或者zookeeper
2. 在rpc-sample-client、rpc-sample-server 的pom.xml中添加 rpc-registry-etcd 或者rpc-registry-zookeeper的依赖，修改`resources/spring.xml `引入对应的bean，修改`resources/rpc.properties`注册中心的地址
3. 启动server：`com.rpc.sample.server.RpcBootstrap`
4. 启动client：`com.rpc.sample.client.HelloClient`

### 主要参考

- https://my.oschina.net/huangyong/blog/361751
- https://github.com/hu1991die/netty-rpc
- https://blog.csdn.net/arctan90/article/details/77835944




