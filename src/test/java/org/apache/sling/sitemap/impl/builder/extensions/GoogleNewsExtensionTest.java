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
import org.apache.sling.sitemap.builder.extensions.GoogleNewsExtension;
import org.apache.sling.sitemap.impl.builder.AbstractBuilderTest;
import org.apache.sling.sitemap.impl.builder.SitemapImpl;
import org.apache.sling.testing.mock.sling.junit5.SlingContext;
import org.apache.sling.testing.mock.sling.junit5.SlingContextExtension;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.io.StringWriter;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

@ExtendWith({SlingContextExtension.class})
class GoogleNewsExtensionTest extends AbstractBuilderTest {

    final SlingContext context = new SlingContext();

    private ExtensionProviderManager extensionProviderManager;

    @BeforeEach
    void setup() {
        context.registerInjectActivateService(new GoogleNewsExtensionProvider());
        extensionProviderManager = context.registerInjectActivateService(new ExtensionProviderManager());
    }

    @Test
    void testGoogleNewsCombinations() throws SitemapException, IOException {
        // given
        StringWriter writer = new StringWriter();
        SitemapImpl sitemap = new SitemapImpl(writer, extensionProviderManager);

        // when
        Url url = sitemap.addUrl("http://example.ch/de.html");
        url.addExtension(GoogleNewsExtension.class)
                .setPublicationName("News 1")
                .setPublicationLanguage(Locale.ENGLISH)
                .setPublicationDate(LocalDate.parse("2021-12-30"))
                .setTitle("News Title 1");
        url.addExtension(GoogleNewsExtension.class)
                .setPublicationName("News 2")
                .setPublicationLanguage(Locale.GERMAN)
                .setPublicationDate(OffsetDateTime.parse("2021-12-30T12:34:56.000+01:00"))
                .setTitle("News Title 2");
        url.addExtension(GoogleNewsExtension.class)
                .setPublicationName("News 3")
                .setPublicationLanguage(new Locale("zh", "tw"))
                .setPublicationDate(OffsetDateTime.parse("2021-12-30T12:34:56.000+02:00"))
                .setTitle("News Title 3")
                .setGenres(GoogleNewsExtension.Genre.PRESS_RELEASE, GoogleNewsExtension.Genre.SATIRE)
                .setAccessRestriction(GoogleNewsExtension.AccessRestriction.SUBSCRIPTION)
                .setKeywords("foo", "bar")
                .setStockTickers("NASDAQ:FOO");
        sitemap.close();

        // then

        assertSitemap(
                AbstractBuilderTest.XML_HEADER + "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" " +
                        "xmlns:news=\"http://www.google.com/schemas/sitemap-news/0.9\">"
                        + "<url>"
                        + "<loc>http://example.ch/de.html</loc>"
                        + "<news:news>"
                        + "<news:publication>"
                        + "<news:name>News 1</news:name>"
                        + "<news:language>en</news:language>"
                        + "</news:publication>"
                        + "<news:publication_date>2021-12-30</news:publication_date>"
                        + "<news:title>News Title 1</news:title>"
                        + "</news:news>"
                        + "<news:news>"
                        + "<news:publication>"
                        + "<news:name>News 2</news:name>"
                        + "<news:language>de</news:language>"
                        + "</news:publication>"
                        + "<news:publication_date>2021-12-30T12:34:56+01:00</news:publication_date>"
                        + "<news:title>News Title 2</news:title>"
                        + "</news:news>"
                        + "<news:news>"
                        + "<news:publication>"
                        + "<news:name>News 3</news:name>"
                        + "<news:language>zh-tw</news:language>"
                        + "</news:publication>"
                        + "<news:access>Subscription</news:access>"
                        + "<news:genres>PressRelease,Satire</news:genres>"
                        + "<news:publication_date>2021-12-30T12:34:56+02:00</news:publication_date>"
                        + "<news:title>News Title 3</news:title>"
                        + "<news:keywords>foo,bar</news:keywords>"
                        + "<news:stock_tickers>NASDAQ:FOO</news:stock_tickers>"
                        + "</news:news>"
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
        url.addExtension(GoogleNewsExtension.class);
        url.addExtension(GoogleNewsExtension.class)
                .setPublicationName("name");
        url.addExtension(GoogleNewsExtension.class)
                .setPublicationName("name")
                .setPublicationLanguage(Locale.ENGLISH);
        url.addExtension(GoogleNewsExtension.class)
                .setPublicationName("name")
                .setPublicationLanguage(Locale.ENGLISH)
                .setPublicationDate(LocalDate.now());
        sitemap.close();

        // then
        assertSitemap(
                AbstractBuilderTest.XML_HEADER + "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" " +
                        "xmlns:news=\"http://www.google.com/schemas/sitemap-news/0.9\">"
                        + "<url>"
                        + "<loc>http://example.ch/de.html</loc>"
                        + "</url>"
                        + "</urlset>",
                writer.toString()
        );
    }

}
