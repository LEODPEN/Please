package cn.mpy634.please.config;

import cn.mpy634.please.Please;
import cn.mpy634.please.lease.Lease;
import cn.mpy634.please.lease.ZKLease;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author LEO D PEN
 * @date 2021/2/25
 * @desc
 */
@Configuration
@ConditionalOnProperty(value = "please.addresses")
public class PleaseConfig {

    @Configuration
    @ConditionalOnClass(ZooKeeper.class)
    public class ZKLeaseGenBean {

        @Bean
        @ConditionalOnMissingBean
        public Lease lease(@Value("${please.addresses}") String addresses) {
            return new ZKLease(addresses);
        }
    }

    // 预留一下，redis 的位置
    public class RedisLeaseGenBean{}
}
