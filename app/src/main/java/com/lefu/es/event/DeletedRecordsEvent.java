package com.lefu.es.event;

import com.lefu.es.entity.Records;

/**
 * 作者: Administrator on 2016/12/7.
 * 作用:
 */

public class DeletedRecordsEvent {
    private Records lastRecod;

    public DeletedRecordsEvent(Records records){
        lastRecod = records;
    }

    public Records getLastRecod() {
        return lastRecod;
    }

    public void setLastRecod(Records lastRecod) {
        this.lastRecod = lastRecod;
    }
}
