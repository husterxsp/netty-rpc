package com.rpc.registry.zookeeper;

import com.rpc.registry.ServiceRegistry;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xushaopeng
 * @date 2019/04/03
 */
public class ZooKeeperServiceRegistry implements ServiceRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZooKeeperServiceRegistry.class);

    private final ZkClient zkClient;

    public ZooKeeperServiceRegistry(String zkAddress) {
        // 创建 ZooKeeper 客户端
        zkClient = new ZkClient(zkAddress, Constant.ZK_SESSION_TIMEOUT, Constant.ZK_CONNECTION_TIMEOUT);
        LOGGER.debug("connect zookeeper");
    }

    @Override
    public void register(String serviceName, String serviceAddress) {
        // 创建 registry 节点（持久）
        String registryPath = Constant.ZK_REGISTRY_PATH;
        if (!zkClient.exists(registryPath)) {
            zkClient.createPersistent(registryPath);
            LOGGER.debug("create registry node: {}", registryPath);
        }
        // 创建 service 节点（持久）
        String servicePath = registryPath + "/" + serviceName;
        if (!zkClient.exists(servicePath)) {
            zkClient.createPersistent(servicePath);
            LOGGER.debug("create service node: {}", servicePath);
        }
        // 创建 address 节点（临时）
        // client断开连接后重连，session过期后也重新创建了。但是，server创建的临时节点消失了。
        // 原来，zookeeper的临时节点、watcher等都是和session绑定的！而一旦session过期，
        // zookeeper server就会清除和session有关的这些状态和数据。
        // client要做的就是重建连接，重新创建临时节点，重新watch。
        // 另外watch本身也有要注意的，收到事件通知后，需要重新注册watcher。
        // https://www.jianshu.com/p/f0de7750c066
        String addressPath = servicePath + "/address-";
        // 对于每个注册的地址，都创建一个临时顺序节点
        // zookeeper的四种节点类型
        // 持久节点（PERSISTENT）/持久顺序节点（PERSISTENT_SEQUENTIAL）
        // 临时节点（EPHEMERAL）/临时顺序节点（EPHEMERAL_SEQUENTIAL）
        String addressNode = zkClient.createEphemeralSequential(addressPath, serviceAddress);
        LOGGER.debug("create address node: {}", addressNode);
    }
}