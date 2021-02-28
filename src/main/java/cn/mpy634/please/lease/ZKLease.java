package cn.mpy634.please.lease;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.io.Closeable;

/**
 * @author LEO D PEN
 * @date 2021/2/25
 * @desc
 */
@Slf4j
public class ZKLease<R> implements Lease<R>, Closeable {


    private final ZooKeeper zkClient;
    /**
     * session超时时间
     */
    private static final int SESSION_TIMEOUT_MS = 10000;

    private static final String ROOT = "/leases";

    public ZKLease(String zkAddresses) {
        try {
            this.zkClient = new ZooKeeper(zkAddresses, SESSION_TIMEOUT_MS, null);
            if (this.zkClient.exists(ROOT, false) == null) {
                this.zkClient.create(ROOT, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (Exception e) {
            throw new IllegalStateException("初始化zk连接或创建根节点失败");
        }
    }

    // 如果失败，交由业务方决定接下来的活动；比如PFS就是放后台线程继续平方递增操作；
    @Override
    public R makePlease(String key, int waitTimeMs, int timeoutMs, Callback<R> success, Callback<R> fail) throws Throwable {
        boolean suc = false;
        try {
            if (suc = acquire(key, waitTimeMs, 0)) {
                return success.execute();
            }
        } finally {
            if (suc) {
                release(key);
            }
        }
        return fail.execute();
    }

    @Override
    public boolean acquire(String key, int waitTimeMs, int timeoutMs) throws Exception {
        key = ROOT.concat("/").concat(key);
        boolean getLock;
        if (!(getLock = createTempNode(key)) && waitTimeMs > 0) {
            long startTime = System.currentTimeMillis();
            int yieldTimes = 0;
            do {
                Thread.yield();
                if (getLock = createTempNode(key)) {
                    break;
                }
                if(yieldTimes++ >= 2 ) {
                    Thread.sleep(20 + 10 * yieldTimes);
                }
            } while (System.currentTimeMillis() - startTime < waitTimeMs);
        }
        return getLock;
    }

    @Override
    public boolean release(String key) {
        try {
            deleteNode(ROOT.concat("/").concat(key));
        }catch (Exception e) {
            // 删除本身节点出问题了，可能已经被删了吧
            log.error("ZKLease -> release({})", key, e);
            return false;
        }
        return true;
    }

    /**
     * 创建临时节点以实现租约
     * @param nodeName
     * @return boolean
     * @throws InterruptedException 创建被interrupt
     */
    private boolean createTempNode(String nodeName) throws InterruptedException {
        try {
            zkClient.create(nodeName, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            return true;
        } catch (KeeperException e) {
            if (!(e instanceof KeeperException.NodeExistsException)) {
                log.error("ZKLease -> createTempNode({})", nodeName , e);
            }
            return false;
        }
    }

    /**
     * 删除节点【递归删除】
     * @param nodeName 节点名称
     * @throws KeeperException
     * @throws InterruptedException
     */
    private void deleteNode(String nodeName) throws KeeperException, InterruptedException {
        try {
            zkClient.delete(nodeName, 0);
        } catch (KeeperException.NotEmptyException e) {
            zkClient.getChildren(nodeName, false).parallelStream().forEach(child -> {
                try {
                    deleteNode(nodeName.concat("/").concat(child));
                } catch (Exception e1) {
                    log.error("ZKLease -> deleteNode({})", nodeName, e1);
                }
            });
            zkClient.delete(nodeName, 0);
        }
    }

    @Override
    public void close() {
        if (zkClient != null) {
            try {
                zkClient.close();
            } catch (InterruptedException e) {
                log.error("关闭zkClient失败", e);
            }
        }
    }
}
