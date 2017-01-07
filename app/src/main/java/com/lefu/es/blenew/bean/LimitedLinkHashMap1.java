package com.lefu.es.blenew.bean;


import java.util.LinkedHashMap;

/**
 * 作者: andy on 2016/11/9.
 * 作用:
 */
public class LimitedLinkHashMap1<K, V> extends LinkedHashMap<K, V>{
    private static final long serialVersionUID = -5375660288461724925L;

    private final int mMaxSize;

    public LimitedLinkHashMap1(int maxSize){
        super(maxSize + 1, 1, false);
        mMaxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Entry<K, V> eldest) {
        return this.size() > mMaxSize;
    }
}

