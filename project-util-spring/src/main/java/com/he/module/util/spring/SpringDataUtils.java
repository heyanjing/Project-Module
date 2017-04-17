package com.he.module.util.spring;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.google.common.collect.Lists;
import com.he.module.util.Strings;

public class SpringDataUtils {
    public static Sort createSortFilter(Map<String, String> sort) {
        Sort sorter = null;
        List<Sort> sorters = Lists.newArrayList();
        if (sort != null && !sort.isEmpty()) {
            for (Entry<String, String> entry : sort.entrySet()) {
                String name = entry.getKey();
                String value = entry.getValue();
                if (Strings.isNullOrEmpty(value)) {
                    sorters.add(new Sort(Direction.ASC, name));
                } else {
                    if (value.trim().toLowerCase().equals("asc")) {
                        sorters.add(new Sort(Direction.ASC, name));
                    } else {
                        sorters.add(new Sort(Direction.DESC, name));
                    }
                }
            }
            sorter = sorters.get(0);
            for (int i = 1; i < sorters.size(); i++) {
                sorter = sorter.and(sorters.get(i));
            }
        }
        return sorter;
    }
}
