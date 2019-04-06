package com.rpc.registry.etcd;

import com.rpc.registry.ServiceRegistry;
import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.responses.EtcdErrorCode;
import mousio.etcd4j.responses.EtcdException;
import mousio.etcd4j.responses.EtcdKeysResponse;
import mousio.etcd4j.responses.EtcdResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * @author xushaopeng
 * @date 2019/04/05
 */
public class EtcdServiceRegistry implements ServiceRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRegistry.class);

    private EtcdClient client;

    public EtcdServiceRegistry(String etcdAddress) {

        client = new EtcdClient(URI.create(etcdAddress));

        LOGGER.debug("connect etcd");

    }

    public static void main(String[] args) {
        EtcdServiceRegistry registry = new EtcdServiceRegistry("http://127.0.0.1:2379");

        registry.register("hello", "world");
    }


    /**
     * 参考：https://blog.csdn.net/arctan90/article/details/77835944
     *
     * @param serviceName    服务名称
     * @param serviceAddress 服务地址
     */
    @Override
    public void register(String serviceName, String serviceAddress) {

        try {
            final String servicePath = Constant.REGISTRY_PATH + "/" + serviceName + "/" + serviceAddress;

            /**
             * etcd和zk不一样，没有临时节点，需要客户端自己来实现。实现的大概逻辑是这样的：
             * 设置一个一段时间超时的节点，比如60秒超时，如果超时了etcd上就找不到这个节点，
             * 然后客户端用一个更小的时间间隔刷新这个节点的超时时间，比如每隔40秒刷新一次，重新把ttl设置成60秒。
             * 这样就可以保证在etcd上只要服务存活节点就一定存在，当服务关掉的时候，节点过一阵就消失了。
             * */

            client.put(servicePath, serviceAddress).ttl(60).send().get();

            System.out.println("put node: " + servicePath + ": " + serviceAddress);

            // 定时任务刷新节点
            new Thread(new GuardEtcd(servicePath)).start();

            // 服务终止，删掉节点
//            Runtime.getRuntime().addShutdownHook(new Thread() {
//                @Override
//                public void run() {
//                    try {
//                        client.deleteDir(servicePath).recursive().send().get();
//                        client.close();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class GuardEtcd implements Runnable {
        private String servicePath;

        public GuardEtcd(String servicePath) {
            this.servicePath = servicePath;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(40);
                    client.refresh(servicePath, 60).send();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
