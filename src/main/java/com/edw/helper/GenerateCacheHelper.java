package com.edw.helper;

import org.infinispan.client.hotrod.Flag;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <pre>
 *     com.edw.helper.GenerateCacheHelper
 * </pre>
 *
 * @author Muhammad Edwin < edwin at redhat dot com >
 * 03 Jan 2024 11:41
 */
@Service
public class GenerateCacheHelper {

    @Autowired
    private RemoteCacheManager cacheManager;

    private Logger logger = LoggerFactory.getLogger(GenerateCacheHelper.class);

    private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();


    public void sendToCacheWithRegularPut() {
        final RemoteCache cache = cacheManager.getCache("user-cache");
        for(int i = 0 ; i < 100; i ++) {
            for(int j = 0 ; j < 10; j ++) {
                executor.execute(() -> {
                    Integer key = new Random().nextInt(0, 10);
                    String newValue = UUID.randomUUID().toString();
                    String oldValue = (String) cache.withFlags(Flag.FORCE_RETURN_VALUE).putIfAbsent(key, newValue);

                    if(oldValue == null)
                        logger.info(" inserted key {} {} ", key, newValue);
                    else
                        logger.info(" unable to insert cache key {} with new value value {}, because have old value of {} ", key, newValue, oldValue);
                });
            }
        }
    }
}
