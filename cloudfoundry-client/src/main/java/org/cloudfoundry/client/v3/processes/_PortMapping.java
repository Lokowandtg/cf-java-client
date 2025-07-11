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

package org.cloudfoundry.client.v3.processes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

/**
 * A mapping of internal to external ports
 */
@JsonDeserialize
@Value.Immutable
abstract class _PortMapping {

    /**
     * The external port
     */
    @JsonProperty("external")
    @Nullable
    abstract Integer getExternal();

    /**
     * The external TLS proxy port
     */
    @JsonProperty("external_tls_proxy_port")
    @Nullable
    abstract Integer getExternalTlsProxyPort();

    /**
     * The internal port
     */
    @JsonProperty("internal")
    @Nullable
    abstract Integer getInternal();

    /**
     * The internal TLS proxy port
     */
    @JsonProperty("internal_tls_proxy_port")
    @Nullable
    abstract Integer getInternalTlsProxyPort();

}
