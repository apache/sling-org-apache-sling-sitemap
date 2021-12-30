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

import org.apache.sling.sitemap.SitemapException;
import org.apache.sling.sitemap.builder.Url;
import org.apache.sling.sitemap.builder.extensions.GoogleImageExtension;
import org.apache.sling.sitemap.impl.builder.AbstractBuilderTest;
import org.apache.sling.sitemap.impl.builder.SitemapImpl;
import org.apache.sling.testing.mock.sling.junit5.SlingContext;
import org.apache.sling.testing.mock.sling.junit5.SlingContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.io.StringWriter;

@ExtendWith({SlingContextExtension.class})
public class GoogleImageExtensionTest extends AbstractBuilderTest {

    final SlingContext context = new SlingContext();

    private ExtensionProviderManager extensionProviderManager;

    @BeforeEach
    void setup() {
        context.registerInjectActivateService(new GoogleImageExtensionProvider());
        extensionProviderManager = context.registerInjectActivateService(new ExtensionProviderManager());
    }

    @Test
    void testGoogleImageCombinations() throws SitemapException, IOException {
        // given
        StringWriter writer = new StringWriter();
        SitemapImpl sitemap = new SitemapImpl(writer, extensionProviderManager);

        // when
        Url url = sitemap.addUrl("http://example.ch/de.html");
        url.addExtension(GoogleImageExtension.class)
                .setUrl("http://example.ch/dam/de/images/hero.jpg");
        url.addExtension(GoogleImageExtension.class)
                .setUrl("http://example.ch/dam/de/images/brand.jpg")
                .setCaption("Nothing can go wrong trusting our strong brand.")
                .setTitle("Brand image")
                .setGeoLocation("Europe/Zurich")
                .setLicense("https://creativecommons.org/publicdomain/zero/1.0/");
        sitemap.close();

        // then
        assertSitemap(
                AbstractBuilderTest.XML_HEADER + "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" " +
                        "xmlns:image=\"http://www.google.com/schemas/sitemap-image/1.1\">"
                        + "<url>"
                        + "<loc>http://example.ch/de.html</loc>"
                        + "<image:image>"
                        + "<image:loc>http://example.ch/dam/de/images/hero.jpg</image:loc>"
                        + "</image:image>"
                        + "<image:image>"
                        + "<image:loc>http://example.ch/dam/de/images/brand.jpg</image:loc>"
                        + "<image:caption>Nothing can go wrong trusting our strong brand.</image:caption>"
                        + "<image:geo_location>Europe/Zurich</image:geo_location>"
                        + "<image:title>Brand image</image:title>"
                        + "<image:license>https://creativecommons.org/publicdomain/zero/1.0/</image:license>"
                        + "</image:image>"
                        + "</url>"
                        + "</urlset>",
                writer.toString()
        );
    }

    @Test
    void testNothingWrittenWhenExtensionMissesMandatoryProperties() throws SitemapException, IOException {
        // given
        StringWriter writer = new StringWriter();
        SitemapImpl sitemap = new SitemapImpl(writer, extensionProviderManager);

        // when
        Url url = sitemap.addUrl("http://example.ch/de.html");
        url.addExtension(GoogleImageExtension.class);
        sitemap.close();

        // then
        assertSitemap(
                AbstractBuilderTest.XML_HEADER + "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" " +
                        "xmlns:image=\"http://www.google.com/schemas/sitemap-image/1.1\">"
                        + "<url>"
                        + "<loc>http://example.ch/de.html</loc>"
                        + "</url>"
                        + "</urlset>",
                writer.toString()
        );
    }

}
