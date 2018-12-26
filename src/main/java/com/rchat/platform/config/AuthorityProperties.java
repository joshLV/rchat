package com.rchat.platform.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "rchat.authority")
public class AuthorityProperties {
    private final OperationProperties operation = new OperationProperties();
    private boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public OperationProperties getOperation() {
        return operation;
    }

    public class OperationProperties {
        /**
         * 创建方法前缀
         */
        private List<String> createPrefixes;
        /**
         * 删除方法前缀
         */
        private List<String> deletePrefixes;
        /**
         * 更新方法前缀
         */
        private List<String> updatePrefixes;
        /**
         * 查询方法前缀
         */
        private List<String> retrievePrefixes;

        public List<String> getCreatePrefixes() {
            return createPrefixes;
        }

        public void setCreatePrefixes(List<String> createPrefixes) {
            this.createPrefixes = createPrefixes;
        }

        public List<String> getDeletePrefixes() {
            return deletePrefixes;
        }

        public void setDeletePrefixes(List<String> deletePrefixes) {
            this.deletePrefixes = deletePrefixes;
        }

        public List<String> getUpdatePrefixes() {
            return updatePrefixes;
        }

        public void setUpdatePrefixes(List<String> updatePrefixes) {
            this.updatePrefixes = updatePrefixes;
        }

        public List<String> getRetrievePrefixes() {
            return retrievePrefixes;
        }

        public void setRetrievePrefixes(List<String> retrievePrefixes) {
            this.retrievePrefixes = retrievePrefixes;
        }

    }
}
