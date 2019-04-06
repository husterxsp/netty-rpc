package com.rpc.registry.etcd;

import com.rpc.common.util.CollectionUtil;
import com.rpc.registry.ServiceDiscovery;
import com.rpc.registry.ServiceRegistry;
import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.responses.EtcdErrorCode;
import mousio.etcd4j.responses.EtcdException;
import mousio.etcd4j.responses.EtcdKeysResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author xushaopeng
 * @date 2019/04/05
 */
public class EtcdServiceDiscovery implements ServiceDiscovery {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRegistry.class);

    private EtcdClient client;

    public EtcdServiceDiscovery(String etcdAddress) {

        client = new EtcdClient(URI.create(etcdAddress));

        LOGGER.debug("connect etcd");

    }

    public static void main(String[] args) {
        EtcdServiceDiscovery discovery = new EtcdServiceDiscovery("http://127.0.0.1:2379");

        System.out.println(discovery.discover("com.rpc.sample.api.HelloService-sample.hello2"));
    }

    @Override
    public String discover(String serviceName) {
        try {

            String servicePath = Constant.REGISTRY_PATH + "/" + serviceName;

            EtcdKeysResponse response = null;
            // 找不到服务
            try {
                response = client.getDir(servicePath).send().get();
            } catch (EtcdException e) {
                if (e.isErrorCode(EtcdErrorCode.KeyNotFound)) {
                    throw new RuntimeException(String.format("can not find any service node on path: %s", servicePath));
                }
            }

            List<EtcdKeysResponse.EtcdNode> addressList = response.node.nodes;

            if (CollectionUtil.isEmpty(addressList)) {
                throw new RuntimeException(String.format("can not find any address node on path: %s", servicePath));
            }

            String address;
            int size = addressList.size();

            System.out.println("find " + size + " server address");
            if (size == 1) {
                address = addressList.get(0).value;
                LOGGER.debug("get only address node: {}", address);
            } else {
                // 若存在多个地址，则随机获取一个地址
                // 每一个线程有一个独立的随机数生成器，用于并发产生随机数，能够解决多个线程发生的竞争争夺。
                // 负载均衡功能的实现
                address = addressList.get(ThreadLocalRandom.current().nextInt(size)).value;
                LOGGER.debug("get random address node: {}", address);
            }

            return address;

        } catch (Exception e) {

            e.printStackTrace();

        }

        return "";
    }

}
