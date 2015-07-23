package com.acme.kanban.interceptor;


import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.Date;
    
public class AuditInterceptor extends EmptyInterceptor {
    public boolean onFlushDirty(Object entity,
                                Serializable id,
                                Object[] currentState,
                                Object[] previousState,
                                String[] propertyNames,
                                Type[] types) {


        for (int i = 0; i < propertyNames.length; i++) {
            System.out.println(propertyNames[i] + ": " +previousState[i].toString() +" -> "+currentState[i].toString());
            if ("lastUpdateTimestamp".equals(propertyNames[i])) {
                currentState[i] = new Date();
                return true;
            }
        }

        return false;
    }
}
