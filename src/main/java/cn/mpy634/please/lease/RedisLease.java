package cn.mpy634.please.lease;

/**
 * @author LEO D PEN
 * @date 2021/2/25
 * @desc
 */
public class RedisLease implements Lease{

    @Override
    public boolean acquire(String key, int waitTimeMs, int timeoutMs) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean release(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object makePlease(String key, int waitTimeMs, int timeoutMs, Callback success, Callback fail) throws Throwable {
        throw new UnsupportedOperationException();
    }
}
