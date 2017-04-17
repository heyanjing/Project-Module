package com.he.module.util.hibernate;

import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;

public final class Querys {

    public enum Type {
        SQL, HQL, JPQL
    }

    // ----------------------------------------------------------------
    // -----------------hibernate的Query ---------------------------------
    // ----------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public static Query createQuery(Session session, String sql, Object params, Type queryType) {
        Query query = null;
        if (queryType.equals(Type.SQL)) {
            query = session.createSQLQuery(sql);
        } else {
            query = session.createQuery(sql);
        }
        if (params != null) {
            if (params instanceof Map) {
                // query.setParameter("name", "name1x");
                Map<String, Object> map = (Map<String, Object>) params;
                for (Map.Entry<String, Object> param : map.entrySet()) {
                    query.setParameter(param.getKey(), param.getValue());
                }
            } else {
                Object[] args = (Object[]) params;
                for (int i = 0; i < args.length; i++) {
                    query.setParameter(i, args[i]);
                }
            }
        }
        return query;
    }
}