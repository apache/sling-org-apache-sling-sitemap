/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.sling.sitemap.builder.extensions;

import org.apache.sling.sitemap.builder.Extension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ProviderType;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Locale;

/**
 * An extension to add news metadata to an {@link org.apache.sling.sitemap.builder.Url}.
 *
 * @see <a href="https://developers.google.com/search/docs/advanced/sitemaps/news-sitemap">Google News sitemaps</a>
 */
@ProviderType
public interface GoogleNewsExtension extends Extension {

    enum AccessRestriction {
        SUBSCRIPTION("Subscription"),
        REGISTRATION("Registration");

        private final String value;

        AccessRestriction(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    enum Genre {
        PRESS_RELEASE("PressRelease"),
        SATIRE("Satire"),
        BLOG("Blog"),
        OP_ED("OpEd"),
        OPINION("Opinion"),
        USER_GENERATED("UserGenerated");

        private final String value;

        Genre(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    @NotNull
    GoogleNewsExtension setPublication(@NotNull String name, @NotNull Locale locale);

    @NotNull
    GoogleNewsExtension setPublicationDate(@NotNull OffsetDateTime date);

    @NotNull
    GoogleNewsExtension setPublicationDate(@NotNull LocalDate date);

    @NotNull
    GoogleNewsExtension setTitle(@NotNull String title);

    @NotNull
    GoogleNewsExtension setAccessRestriction(@Nullable AccessRestriction accessRestriction);

    @NotNull
    GoogleNewsExtension setGenres(@Nullable Collection<Genre> genres);

    @NotNull
    GoogleNewsExtension setKeywords(@Nullable Collection<String> keywords);

    @NotNull
    GoogleNewsExtension setStockTickers(@Nullable Collection<String> stockTickers);
}
