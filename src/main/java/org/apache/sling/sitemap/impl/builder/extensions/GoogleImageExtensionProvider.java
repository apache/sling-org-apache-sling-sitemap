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

import org.apache.sling.sitemap.builder.extensions.GoogleImageExtension;
import org.apache.sling.sitemap.spi.builder.AbstractExtension;
import org.apache.sling.sitemap.spi.builder.SitemapExtensionProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.service.component.annotations.Component;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

@Component(
        property = {
                SitemapExtensionProvider.PROPERTY_INTERFACE + "=org.apache.sling.sitemap.builder.extensions.GoogleImageExtension",
                SitemapExtensionProvider.PROPERTY_PREFIX + "=image",
                SitemapExtensionProvider.PROPERTY_NAMESPACE + "=http://www.google.com/schemas/sitemap-image/1.1",
                SitemapExtensionProvider.PROPERTY_LOCAL_NAME + "=image"
        }
)
public class GoogleImageExtensionProvider implements SitemapExtensionProvider {

    @Override
    @NotNull
    public AbstractExtension newInstance() {
        return new ExtensionImpl();
    }

    private static class ExtensionImpl extends AbstractExtension implements GoogleImageExtension {

        private String url;
        private String caption;
        private String geoLocation;
        private String title;
        private String license;

        private static String required(String object, String message) throws XMLStreamException {
            if (object == null) {
                throw new XMLStreamException(message);
            }
            return object;
        }

        @Override
        @NotNull
        public GoogleImageExtension setUrl(@NotNull String location) {
            this.url = location;
            return this;
        }

        @Override
        @NotNull
        public GoogleImageExtension setCaption(@Nullable String caption) {
            this.caption = caption;
            return this;
        }

        @Override
        @NotNull
        public GoogleImageExtension setGeoLocation(@Nullable String geoLocation) {
            this.geoLocation = geoLocation;
            return this;
        }

        @Override
        @NotNull
        public GoogleImageExtension setTitle(@Nullable String title) {
            this.title = title;
            return this;
        }

        @Override
        @NotNull
        public GoogleImageExtension setLicense(@Nullable String licenseLocation) {
            this.license = licenseLocation;
            return this;
        }

        @Override
        public void writeTo(@NotNull XMLStreamWriter writer) throws XMLStreamException {
            writer.writeStartElement("loc");
            writer.writeCharacters(required(url,"image:loc is missing"));
            writer.writeEndElement();

            if (caption != null) {
                writer.writeStartElement("caption");
                writer.writeCharacters(caption);
                writer.writeEndElement();
            }

            if (geoLocation != null) {
                writer.writeStartElement("geo_location");
                writer.writeCharacters(geoLocation);
                writer.writeEndElement();
            }

            if (title != null) {
                writer.writeStartElement("title");
                writer.writeCharacters(title);
                writer.writeEndElement();
            }

            if (license != null) {
                writer.writeStartElement("license");
                writer.writeCharacters(license);
                writer.writeEndElement();
            }
        }
    }
}
