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

/**
 * An extension to add video links and metadata to an {@link org.apache.sling.sitemap.builder.Url}.
 *
 * @see <a href="https://developers.google.com/search/docs/advanced/sitemaps/video-sitemaps">Video sitemaps and alternatives</a>
 */
@ProviderType
public interface GoogleVideoExtension extends Extension {

    enum Platform {
        WEB("web"),
        MOBILE("mobile"),
        TV("tv");

        private final String value;

        Platform(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    enum Access {
        ALLOW("allow"),
        DENY("deny");

        private final String value;

        Access(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    enum PriceType {
        PURCHASE("purchase"),
        RENT("rent");

        private final String value;

        PriceType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    enum Resolution {
        STANDARD_DEFINITION("sd"),
        HIGH_DEFINITION("hd");

        private final String value;

        Resolution(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * {@code thumbnail_loc}
     *
     * @param thumbnailLocation
     * @return
     */
    @NotNull
    GoogleVideoExtension setThumbnail(@NotNull String thumbnailLocation);

    @NotNull
    GoogleVideoExtension setTitle(@NotNull String title);

    @NotNull
    GoogleVideoExtension setDescription(@NotNull String description);

    /**
     * {@code content_loc}
     *
     * @param contentLocation
     * @return
     */
    @NotNull
    GoogleVideoExtension setUrl(String contentLocation);

    /**
     * {@code player_loc}
     *
     * @param playerLocation
     * @return
     */
    @NotNull
    GoogleVideoExtension setPlayerUrl(String playerLocation);

    @NotNull
    GoogleVideoExtension setDuration(Integer duration);

    @NotNull
    GoogleVideoExtension setExpirationDate(LocalDate date);

    @NotNull
    GoogleVideoExtension setExpirationDate(OffsetDateTime date);

    @NotNull
    GoogleVideoExtension setRating(Float rating);

    @NotNull
    GoogleVideoExtension setViewCount(Integer viewCount);

    @NotNull
    GoogleVideoExtension setPublicationDate(LocalDate date);

    @NotNull
    GoogleVideoExtension setPublicationDate(OffsetDateTime date);

    @NotNull
    GoogleVideoExtension setTags(Collection<String> tags);

    @NotNull
    GoogleVideoExtension setCategory(String category);

    @NotNull
    GoogleVideoExtension setFamilyFriendly(Boolean familyFriendly);

    /**
     * {@code restriction}
     *
     * @param restriction
     * @param countryCodes
     * @return
     */
    @NotNull
    GoogleVideoExtension setAccessRestriction(Access restriction, Collection<String> countryCodes);

    /**
     * {@code platform}
     *
     * @param restriction
     * @param platforms
     * @return
     */
    @NotNull
    GoogleVideoExtension setPlatformRestriction(Access restriction, Collection<Platform> platforms);

    @NotNull
    GoogleVideoExtension addPrice(float price, String currency, @Nullable PriceType type, @Nullable Resolution resolution);

    @NotNull
    GoogleVideoExtension setRequiresSubscription(Boolean requiresSubscription);

    @NotNull
    GoogleVideoExtension setUploader(String uploader);

    @NotNull
    GoogleVideoExtension setUploaderUrl(String uploaderInfo);

    @NotNull
    GoogleVideoExtension setLive(Boolean live);


}
