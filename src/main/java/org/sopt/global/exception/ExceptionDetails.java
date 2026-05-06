package org.sopt.global.exception;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExceptionDetails {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private final Map<String, Object> details = new LinkedHashMap<>();

        private Builder() {
        }

        public Builder put(String key, Object value) {
            details.put(key, value);
            return this;
        }

        public Map<String, Object> build() {
            return Collections.unmodifiableMap(new LinkedHashMap<>(details));
        }
    }
}
