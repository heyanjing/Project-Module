package com.timingbar.data.dynamic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicDataSource extends AbstractRoutingDataSource {

    public static Logger logger = LoggerFactory.getLogger(DynamicDataSource.class);

    @Autowired(required = false)
    private DynamicDataSourceHolder dynamicDataSourceHolder;

    @Override
    protected Object determineCurrentLookupKey() {
        Object ds = null;
        try {
            ds = dynamicDataSourceHolder.getDataSource();
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("使用默认数据源");
            }
        }
        return ds;
    }

}
