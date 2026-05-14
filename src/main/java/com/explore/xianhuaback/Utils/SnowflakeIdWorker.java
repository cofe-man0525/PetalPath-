package com.explore.xianhuaback.Utils;

import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import java.net.InetAddress;

@Component
public class SnowflakeIdWorker {

    // ========== 参数配置 ==========
    /** 起始时间戳 (2024-01-01 00:00:00) */
    private final long START_TIMESTAMP = 1704067200000L;

    /** 机器ID位数 */
    private final long WORKER_ID_BITS = 10L;

    /** 序列号位数 */
    private final long SEQUENCE_BITS = 12L;

    /** 最大机器ID (1023) */
    private final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);

    /** 序列号最大值 (4095) */
    private final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    /** 机器ID左移位数 */
    private final long WORKER_ID_SHIFT = SEQUENCE_BITS;

    /** 时间戳左移位数 */
    private final long TIMESTAMP_SHIFT = WORKER_ID_BITS + SEQUENCE_BITS;


    // ========== 实例变量 ==========
    private long workerId = 1;
    private long sequence = 0L;
    private long lastTimestamp = -1L;


    // ========== 初始化 ==========
    @PostConstruct
    public void init() {
        // 自动获取机器ID（根据IP最后一段）
        try {
            InetAddress address = InetAddress.getLocalHost();
            String ip = address.getHostAddress();
            String[] parts = ip.split("\\.");
            workerId = Long.parseLong(parts[parts.length - 1]) % 1024;
            System.out.println("雪花算法初始化：workerId=" + workerId);
        } catch (Exception e) {
            workerId = 1;
            System.out.println("雪花算法使用默认workerId=1");
        }
    }


    // ========== 核心方法 ==========

    /**
     * 生成订单号
     */
    //加锁的目的是出现并发的情况下能够正常的进行获取到对应的订单id
    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();

        // 时钟回拨处理
        if (timestamp < lastTimestamp) {
            long offset = lastTimestamp - timestamp;
            if (offset <= 5) {
                try {
                    wait(offset << 1);
                    timestamp = System.currentTimeMillis();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new RuntimeException("时钟回拨，拒绝生成ID");
            }
        }

        // 同一毫秒内
        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                while (timestamp <= lastTimestamp) {
                    timestamp = System.currentTimeMillis();
                }
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        // 组合ID
        return ((timestamp - START_TIMESTAMP) << TIMESTAMP_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    /**
     * 生成订单号（字符串格式）
     */
    public String nextOrderNo() {
        return String.valueOf(nextId());
    }

    /**
     * 生成退款单号
     */
    public String nextRefundNo() {
        return "REF" + String.valueOf(nextId());
    }
}