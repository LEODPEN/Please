package cn.mpy634.please;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author LEO D PEN
 * @date 2021/2/25
 * @desc
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Please {

    /**
     * key：collectionId，所在函数必须传一下
     * 不是直接是哪个key，而是对应的参数名称
     */
    String key();

    int waitTimeMs() default 0;

    /**
     * 过期时间/毫秒，当前未用到；如果断开连接直接关临时节点吧
     */
    int timeoutMs() default 50000;

    /**
     * 日志去用吧
     */
    String failMsg() default "正在写入 或 出现问题，请查看具体日志";

}
