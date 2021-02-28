# Please
PFS 之租约工具

> 当前只支持Zookeeper

## 使用

```yaml
please.addresses: *.*.*.*:xxxx,*.*.*.*:xxxx
```

租约方法：
```java
@Please(key= "collectionId", waitTimeMs = 5000)
public void acquireLease(long collectionId) {
     // TODO 写入文件
}
```
