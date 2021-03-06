package org.hadatac.utils;

import org.hadatac.console.controllers.sandbox.Sandbox;

public class Path {

    private String label;
    private String field;
    private String configFilePath;

    public Path(String label, String configFilePath, String field) {
        this.label = label;
        this.configFilePath = configFilePath;
        this.field = field;
    }

    public String getLabel() {
        return label;
    }

    public String getField() {
        return field;
    }

    public String getConfigFilePath() {
        return configFilePath;
    }

    public String getPath() {
        String path = ConfigProp.getPropertyValue(getConfigFilePath(), getField());

        if (CollectionUtil.isSandboxMode()) {
            return path + Sandbox.SUFFIX;
        }

        return path;
    }
}
