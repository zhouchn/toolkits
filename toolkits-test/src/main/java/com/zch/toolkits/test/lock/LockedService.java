package com.zch.toolkits.test.lock;

import com.zch.toolkits.lock.annotation.DistributedLock;
import org.springframework.stereotype.Service;

@Service
public class LockedService {

    @DistributedLock(key = "#id")
    public void lock(String id) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(Thread.currentThread().getName() + " LockedService.lock");
    }
}
