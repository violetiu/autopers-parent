package org.violetime.autopers.objects.part;

import org.violetime.autopers.objects.AutopersObject;
import org.violetime.autopers.query.AutopersQuery;
import org.violetime.autopers.session.objects.Query;

/**
 * 分表接口
 */
public interface AutopersPart {
    public String key(AutopersObject object);
    public  boolean query(String key, AutopersQuery query);
}
