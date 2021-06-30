package org.violetime.autopers.function;

import org.violetime.autopers.platform.AutopersPlatform;

import java.util.Map;

public interface AutopersFunctionSuper {
    public Map<String, Object[]> getFunctions();

    public String getSQl(String column, AutopersPlatform platform);

}
