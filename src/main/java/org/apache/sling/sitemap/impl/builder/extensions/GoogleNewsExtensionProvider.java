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

import org.apache.sling.sitemap.builder.extensions.GoogleNewsExtension;
import org.apache.sling.sitemap.spi.builder.AbstractExtension;
import org.apache.sling.sitemap.spi.builder.SitemapExtensionProvider;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

@Component(
        property = {
                SitemapExtensionProvider.PROPERTY_INTERFACE + "=org.apache.sling.sitemap.builder.extensions.GoogleNewsExtension",
                SitemapExtensionProvider.PROPERTY_PREFIX + "=news",
                SitemapExtensionProvider.PROPERTY_NAMESPACE + "=http://www.google.com/schemas/sitemap-news/0.9",
                SitemapExtensionProvider.PROPERTY_LOCAL_NAME + "=news"
        }
)
public class GoogleNewsExtensionProvider implements SitemapExtensionProvider {

    @Override
    public AbstractExtension newInstance() {
        return new ExtensionImpl();
    }

    private static class ExtensionImpl extends AbstractExtension implements GoogleNewsExtension {

        private String publicationName;
        private String publicationLanguage;
        private String publicationDate;
        private String title;
        private String accessRestriction;
        private String genres;
        private String keywords;
        private String stockTickers;

        @Override
        @NotNull
        public GoogleNewsExtension setPublicationName(@NotNull String name) {
            this.publicationName = name;
            return this;
        }

        @Override
        @NotNull
        public GoogleNewsExtension setPublicationLanguage(@NotNull Locale locale) {
            this.publicationLanguage = locale.toLanguageTag().toLowerCase(Locale.ROOT);
            return this;
        }

        @Override
        @NotNull
        public GoogleNewsExtension setPublicationDate(@NotNull OffsetDateTime date) {
            this.publicationDate = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(date);
            return this;
        }

        @Override
        @NotNull
        public GoogleNewsExtension setPublicationDate(@NotNull LocalDate date) {
            this.publicationDate = DateTimeFormatter.ISO_LOCAL_DATE.format(date);
            return this;
        }

        @Override
        @NotNull
        public GoogleNewsExtension setTitle(@NotNull String title) {
            this.title = title;
            return this;
        }

        @Override
        @NotNull
        public GoogleNewsExtension setAccessRestriction(AccessRestriction accessRestriction) {
            this.accessRestriction = accessRestriction != null ? accessRestriction.getValue() : null;
            return this;
        }

        @Override
        @NotNull
        public GoogleNewsExtension setGenres(Genre... genres) {
            this.genres = genres.length > 0
                    ? Arrays.stream(genres).map(Genre::getValue).collect(Collectors.joining(","))
                    : null;
            return this;
        }

        @Override
        @NotNull
        public GoogleNewsExtension setKeywords(String... keywords) {
            this.keywords = keywords.length > 0
                    ? String.join(",", keywords)
                    : null;
            return this;
        }

        @Override
        @NotNull
        public GoogleNewsExtension setStockTickers(String... stockTickers) {
            this.stockTickers = stockTickers.length > 0
                    ? String.join(",", stockTickers)
                    : null;
            return this;
        }

        @Override
        public void writeTo(@NotNull XMLStreamWriter writer) throws XMLStreamException {
            writer.writeStartElement("publication");
            writer.writeStartElement("name");
            writer.writeCharacters(required(publicationName,"publication name missing"));
            writer.writeEndElement();
            writer.writeStartElement("language");
            writer.writeCharacters(required(publicationLanguage, "publication language missing"));
            writer.writeEndElement();
            writer.writeEndElement();

            if (accessRestriction != null) {
                writer.writeStartElement("access");
                writer.writeCharacters(accessRestriction);
                writer.writeEndElement();
            }

            if (genres != null) {
                writer.writeStartElement("genres");
                writer.writeCharacters(genres);
                writer.writeEndElement();
            }

            writer.writeStartElement("publication_date");
            writer.writeCharacters(required(publicationDate, "publication date missing"));
            writer.writeEndElement();

            writer.writeStartElement("title");
            writer.writeCharacters(required(title,"title missing"));
            writer.writeEndElement();

            if (keywords != null) {
                writer.writeStartElement("keywords");
                writer.writeCharacters(keywords);
                writer.writeEndElement();
            }

            if (stockTickers != null) {
                writer.writeStartElement("stock_tickers");
                writer.writeCharacters(stockTickers);
                writer.writeEndElement();
            }
        }

        private static String required(String object, String message) throws XMLStreamException {
            if (object == null) {
                throw new XMLStreamException(message);
            }
            return object;
        }
    }
}
