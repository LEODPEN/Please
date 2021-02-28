package cn.mpy634.please.lease;

/**
 * @author LEO D PEN
 * @date 2021/2/25
 * @desc
 */

@FunctionalInterface
public interface Callback<R> {

    R execute() throws Throwable;
}
