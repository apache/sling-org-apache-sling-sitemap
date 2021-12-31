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
package org.apache.sling.sitemap.impl.builder.extensions;

import org.apache.sling.sitemap.builder.extensions.GoogleVideoExtension;
import org.apache.sling.sitemap.spi.builder.AbstractExtension;
import org.apache.sling.sitemap.spi.builder.SitemapExtensionProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.stream.Collectors;

@Component(
        property = {
                SitemapExtensionProvider.PROPERTY_INTERFACE + "=org.apache.sling.sitemap.builder.extensions.GoogleVideoExtension",
                SitemapExtensionProvider.PROPERTY_PREFIX + "=video",
                SitemapExtensionProvider.PROPERTY_NAMESPACE + "=http://www.google.com/schemas/sitemap-video/1.1",
                SitemapExtensionProvider.PROPERTY_LOCAL_NAME + "=video"
        }
)
public class GoogleVideoExtensionProvider implements SitemapExtensionProvider {

    private static final Logger LOG = LoggerFactory.getLogger(GoogleVideoExtensionProvider.class);

    @Override
    @NotNull
    public AbstractExtension newInstance() {
        return new ExtensionImpl();
    }

    private static class PriceImpl {
        private String currency;
        private String type;
        private String resolution;
        private String price;
    }

    private static class ExtensionImpl extends AbstractExtension implements GoogleVideoExtension {

        private String thumbnailLocation;
        private String title;
        private String description;
        private String contentLocation;
        private String playerLocation;
        private String duration;
        private TemporalAccessor expirationDate;
        private String rating;
        private String viewCount;
        private TemporalAccessor publicationDate;
        private List<String> tags;
        private String category;
        private String familyFriendly;
        private String accessRestrictions;
        private String accessRestrictionsRel;
        private String platformRestrictions;
        private String platformRestrictionsRel;
        private Collection<PriceImpl> prices;
        private String requiresSubscription;
        private String uploader;
        private String uploaderInfo;
        private String live;

        private static String booleanToString(Boolean bool) {
            if (bool != null) {
                return bool ? "yes" : "no";
            } else {
                return null;
            }
        }

        private static String required(String object, String message) throws XMLStreamException {
            if (object == null) {
                throw new XMLStreamException(message);
            }
            return object;
        }

        private static void write(XMLStreamWriter writer, String value, String tag) throws XMLStreamException {
            writer.writeStartElement(tag);
            writer.writeCharacters(value);
            writer.writeEndElement();
        }

        private static void write(XMLStreamWriter writer, TemporalAccessor date, String tag) throws XMLStreamException {
            try {
                if (date instanceof LocalDate) {
                    write(writer, DateTimeFormatter.ISO_LOCAL_DATE.format(date), tag);
                } else {
                    write(writer, DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(date), tag);
                }
            } catch (RuntimeException ex) {
                throw new XMLStreamException("failed to write " + tag, ex);
            }
        }

        private static void writeCond(XMLStreamWriter writer, String value, String tag) throws XMLStreamException {
            if (value != null) {
                write(writer, value, tag);
            }
        }

        private static void writeCond(XMLStreamWriter writer, TemporalAccessor value, String tag) throws XMLStreamException {
            if (value != null) {
                write(writer, value, tag);
            }
        }

        private static void writeReq(XMLStreamWriter writer, String value, String tag, String msg) throws XMLStreamException {
            write(writer, required(value, msg), tag);
        }

        @Override
        @NotNull
        public GoogleVideoExtension setThumbnail(@NotNull String thumbnailLocation) {
            this.thumbnailLocation = thumbnailLocation;
            return this;
        }

        @Override
        @NotNull
        public GoogleVideoExtension setTitle(@NotNull String title) {
            this.title = title;
            return this;
        }

        @Override
        @NotNull
        public GoogleVideoExtension setDescription(@NotNull String description) {
            this.description = description;
            return this;
        }

        @Override
        @NotNull
        public GoogleVideoExtension setUrl(String contentLocation) {
            this.contentLocation = contentLocation;
            return this;
        }

        @Override
        @NotNull
        public GoogleVideoExtension setPlayerUrl(String playerLocation) {
            this.playerLocation = playerLocation;
            return this;
        }

        @Override
        @NotNull
        public GoogleVideoExtension setDuration(Integer duration) {
            if (duration != null) {
                if (duration < 0 || duration > 28800) {
                    LOG.warn("Adjusting duration as it is out of bounds (0, 28800): {}", duration);
                    duration = Math.max(0, Math.min(duration, 28800));
                }
                this.duration = String.valueOf(duration);
            } else {
                this.duration = null;
            }
            return this;
        }

        @Override
        @NotNull
        public GoogleVideoExtension setExpirationDate(LocalDate date) {
            this.expirationDate = date;
            return this;
        }

        @Override
        @NotNull
        public GoogleVideoExtension setExpirationDate(OffsetDateTime date) {
            this.expirationDate = date;
            return this;
        }

        @Override
        @NotNull
        public GoogleVideoExtension setRating(Float rating) {
            if (rating != null) {
                if (rating < 0 || rating > 5) {
                    LOG.warn("Adjusting rating as it is out of bounds (0,5): {}", rating);
                    rating = Math.max(0.0f, Math.min(rating, 5.0f));
                }
                this.rating = String.valueOf(rating);
            } else {
                this.rating = null;
            }
            return this;
        }

        @Override
        @NotNull
        public GoogleVideoExtension setViewCount(Integer viewCount) {
            if (viewCount != null) {
                if (viewCount < 0) {
                    LOG.warn("Adjusting negative view count: {}", viewCount);
                    viewCount = 0;
                }
                this.viewCount = String.valueOf(viewCount);
            } else {
                this.viewCount = null;
            }
            return this;
        }

        @Override
        @NotNull
        public GoogleVideoExtension setPublicationDate(LocalDate date) {
            this.publicationDate = date;
            return this;
        }

        @Override
        @NotNull
        public GoogleVideoExtension setPublicationDate(OffsetDateTime date) {
            this.publicationDate = date;
            return this;
        }

        @Override
        @NotNull
        public GoogleVideoExtension setTags(Collection<String> tags) {
            this.tags = new ArrayList<>(tags);
            return this;
        }

        @Override
        @NotNull
        public GoogleVideoExtension setCategory(String category) {
            this.category = category;
            return this;
        }

        @Override
        @NotNull
        public GoogleVideoExtension setFamilyFriendly(Boolean familyFriendly) {
            this.familyFriendly = booleanToString(familyFriendly);
            return this;
        }

        @Override
        @NotNull
        public GoogleVideoExtension setAccessRestriction(@Nullable Access restriction, @Nullable Collection<String> countryCodes) {
            String accessRestrictionsCorrected = countryCodes != null
                    ? countryCodes.stream()
                    .map(countryCode -> countryCode.toUpperCase(Locale.ROOT))
                    .filter(countryCode -> countryCode.length() == 2)
                    .collect(Collectors.joining(" "))
                    : null;
            if (restriction != null && accessRestrictionsCorrected != null && accessRestrictionsCorrected.length() > 0) {
                this.accessRestrictions = accessRestrictionsCorrected;
                this.accessRestrictionsRel = restriction.getValue();
            } else {
                this.accessRestrictions = null;
                this.accessRestrictionsRel = null;
            }
            return this;
        }

        @Override
        @NotNull
        public GoogleVideoExtension setPlatformRestriction(@Nullable Access restriction, @Nullable Collection<Platform> platforms) {
            if (restriction != null && platforms != null) {
                this.platformRestrictions = platforms.stream().map(Platform::getValue).collect(Collectors.joining(" "));
                this.platformRestrictionsRel = restriction.getValue();
            } else {
                this.platformRestrictions = null;
                this.platformRestrictionsRel = null;
            }
            return this;
        }

        @Override
        @NotNull
        public GoogleVideoExtension addPrice(float price, @NotNull String currency, @Nullable PriceType type, @Nullable Resolution resolution) {
            if (prices == null) {
                this.prices = new ArrayList<>();
            }
            PriceImpl newPrice = new PriceImpl();
            newPrice.price = String.valueOf(price);
            newPrice.currency = currency;
            newPrice.type = type != null ? type.getValue() : null;
            newPrice.resolution = resolution != null ? resolution.getValue() : null;
            prices.add(newPrice);
            return this;
        }

        @Override
        @NotNull
        public GoogleVideoExtension setRequiresSubscription(Boolean requiresSubscription) {
            this.requiresSubscription = booleanToString(requiresSubscription);
            return this;
        }

        @Override
        @NotNull
        public GoogleVideoExtension setUploader(String uploader) {
            this.uploader = uploader;
            return this;
        }

        @Override
        @NotNull
        public GoogleVideoExtension setUploaderUrl(String uploaderInfo) {
            this.uploaderInfo = uploaderInfo;
            return this;
        }

        @Override
        @NotNull
        public GoogleVideoExtension setLive(Boolean live) {
            this.live = booleanToString(live);
            return this;
        }

        @Override
        public void writeTo(@NotNull XMLStreamWriter writer) throws XMLStreamException {
            writeReq(writer, thumbnailLocation, "thumbnail_loc", "thumbnail location missing");
            writeReq(writer, title, "title", "title missing");
            writeReq(writer, description, "description", "description missing");

            if (contentLocation == null && playerLocation == null) {
                throw new XMLStreamException("either content location or player location is required");
            } else if (contentLocation != null) {
                write(writer, contentLocation, "content_loc");
            } else {
                write(writer, playerLocation, "player_loc");
            }

            writeCond(writer, duration, "duration");
            writeCond(writer, expirationDate, "expiration_date");
            writeCond(writer, rating, "rating");
            writeCond(writer, viewCount, "view_count");
            writeCond(writer, publicationDate, "publication_date");
            writeTags(writer);
            writeCond(writer, category, "category");
            writeCond(writer, familyFriendly, "family_friendly");
            writeRestriction(writer, accessRestrictions, accessRestrictionsRel, "restriction");
            writePrices(writer);
            writeCond(writer, requiresSubscription, "requires_subscription");
            writeUploader(writer);
            writeRestriction(writer, platformRestrictions, platformRestrictionsRel, "platform");
            writeCond(writer, live, "live");
        }

        private void writeTags(XMLStreamWriter writer) throws XMLStreamException {
            if (tags != null) {
                if (tags.size() > 32) {
                    LOG.warn("Truncating tags as more then 32 were given: {}", tags.size());
                }
                for (String tag : tags.subList(0, Math.min(tags.size(), 32))) {
                    writer.writeStartElement("tag");
                    writer.writeCharacters(tag);
                    writer.writeEndElement();
                }
            }
        }

        private void writePrices(XMLStreamWriter writer) throws XMLStreamException {
            if (prices != null) {
                for (PriceImpl price : prices) {
                    writer.writeStartElement("price");
                    writer.writeAttribute("currency", price.currency);
                    if (price.type != null) {
                        writer.writeAttribute("type", price.type);
                    }
                    if (price.resolution != null) {
                        writer.writeAttribute("resolution", price.resolution);
                    }
                    writer.writeCharacters(price.price);
                    writer.writeEndElement();
                }
            }
        }

        private void writeUploader(XMLStreamWriter writer) throws XMLStreamException {
            if (uploader != null) {
                writer.writeStartElement("uploader");
                if (uploaderInfo != null) {
                    writer.writeAttribute("info", uploaderInfo);
                }
                writer.writeCharacters(uploader);
                writer.writeEndElement();
            }
        }

        private static void writeRestriction(XMLStreamWriter writer, String value, String rel, String tag) throws XMLStreamException {
            if (value != null) {
                writer.writeStartElement(tag);
                writer.writeAttribute("relationship", rel);
                writer.writeCharacters(value);
                writer.writeEndElement();
            }
        }
    }
}
