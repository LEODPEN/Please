package cn.mpy634.please.lease;

/**
 * @author LEO D PEN
 * @date 2021/2/25
 * @desc
 */
public interface Lease<R> {


    R makePlease(String key, int waitTimeMs, int timeoutMs, Callback<R> success, Callback<R> fail) throws Throwable;

    /**
     * 获取租约
     * @param key
     * @param waitTimeMs [不设置超时时间，如果之后加超时时间，也直接搞个默认的就行了吧]
     * @param timeoutMs
     * @return boolean 是否获取成功
     * @throws Exception
     */
    boolean acquire(String key, int waitTimeMs, int timeoutMs) throws Exception;

    /**
     * 释放租约
     * @param key
     * @return boolean 是否释放成功
     */
    boolean release(String key);

}
