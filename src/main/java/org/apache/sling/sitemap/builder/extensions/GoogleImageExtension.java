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

/**
 * An extension to add image links and metadata to an {@link org.apache.sling.sitemap.builder.Url}.
 *
 * @see <a href="https://developers.google.com/search/docs/advanced/sitemaps/image-sitemaps">Image sitemaps</a>
 */
@ProviderType
public interface GoogleImageExtension extends Extension {

    @NotNull
    GoogleImageExtension setUrl(@NotNull String location);

    @NotNull
    GoogleImageExtension setCaption(@Nullable String caption);

    @NotNull
    GoogleImageExtension setGeoLocation(@Nullable String geoLocation);

    @NotNull
    GoogleImageExtension setTitle(@Nullable String title);

    @NotNull
    GoogleImageExtension setLicense(@Nullable String licenseLocation);

}
