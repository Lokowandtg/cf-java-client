/*
 * Copyright 2013-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cloudfoundry.client.v2.buildpacks;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The lifecycle type
 */
public enum LifecycleType {
    BUILDPACK("buildpack"),

    DOCKER("docker"),

    CNB("cnb");

    private final String value;

    private LifecycleType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static LifecycleType from(String s) {
        switch (s.toLowerCase()) {
            case "buildpack":
                return BUILDPACK;
            case "docker":
                return DOCKER;
            case "cnb":
                return CNB;
            default:
                throw new IllegalArgumentException(String.format("Unknown lifecycle type: %s", s));
        }
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return getValue();
    }
}
