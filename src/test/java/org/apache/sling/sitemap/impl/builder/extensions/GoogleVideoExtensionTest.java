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
import org.apache.sling.sitemap.builder.extensions.GoogleVideoExtension;
import org.apache.sling.sitemap.impl.builder.AbstractBuilderTest;
import org.apache.sling.sitemap.impl.builder.SitemapImpl;
import org.apache.sling.testing.mock.sling.junit5.SlingContext;
import org.apache.sling.testing.mock.sling.junit5.SlingContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ExtendWith({SlingContextExtension.class})
class GoogleVideoExtensionTest extends AbstractBuilderTest {

    final SlingContext context = new SlingContext();

    private ExtensionProviderManager extensionProviderManager;

    @BeforeEach
    void setup() {
        context.registerInjectActivateService(new GoogleVideoExtensionProvider());
        extensionProviderManager = context.registerInjectActivateService(new ExtensionProviderManager());
    }

    @Test
    void testGoogleImageCombinations() throws SitemapException, IOException {
        // given
        StringWriter writer = new StringWriter();
        SitemapImpl sitemap = new SitemapImpl(writer, extensionProviderManager);

        // when
        Url url = sitemap.addUrl("http://example.ch/de.html");
        url.addExtension(GoogleVideoExtension.class)
                .setUrl("http://example.ch/dam/de/videos/hero.mov")
                .setThumbnail("http://example.ch/dam/de/images/hero_thumbnail.jpg")
                .setTitle("Hero Video")
                .setDescription("foo bar");
        url.addExtension(GoogleVideoExtension.class)
                .setUrl("http://example.ch/dam/de/videos/hero2.mov")
                .setThumbnail("http://example.ch/dam/de/images/hero2_thumbnail.jpg")
                .setTitle("Hero Video 2")
                .setDescription("foo bar 2")
                .setExpirationDate(LocalDate.parse("2022-12-31"))
                .setPublicationDate(LocalDate.parse("2021-12-31"));
        url.addExtension(GoogleVideoExtension.class)
                .setPlayerUrl("http://example.ch/player.swf?video=hero3")
                .setThumbnail("http://example.ch/dam/de/images/hero3_thumbnail.jpg")
                .setTitle("Hero Video 3")
                .setDescription("foo bar 3")
                .setExpirationDate(OffsetDateTime.parse("2022-12-31T12:34:56.000+01:00"))
                .setPublicationDate(OffsetDateTime.parse("2021-12-31T12:34:56.000+01:00"))
                .setCategory("category")
                .setAccessRestriction(GoogleVideoExtension.Access.ALLOW, Arrays.asList("de", "ch", "at"))
                .setDuration(123)
                .setViewCount(200)
                .setFamilyFriendly(true)
                .setLive(false)
                .setPlatformRestriction(GoogleVideoExtension.Access.DENY, Collections.singleton(GoogleVideoExtension.Platform.TV))
                .setRating(4.7f)
                .setTags(Arrays.asList("foo", "bar"))
                .addPrice(9.99f, "EUR", GoogleVideoExtension.PriceType.RENT, GoogleVideoExtension.Resolution.HIGH_DEFINITION)
                .addPrice(19.99f, "EUR", null, null)
                .setRequiresSubscription(true)
                .setUploader("Foo Bar")
                .setUploaderUrl("http://example.ch/people/foo-bar");
        url.addExtension(GoogleVideoExtension.class)
                .setPlayerUrl("http://example.ch/player.swf?video=hero4")
                .setThumbnail("http://example.ch/dam/de/images/hero4_thumbnail.jpg")
                .setTitle("Hero Video 4")
                .setDescription("foo bar 4")
                .setAccessRestriction(GoogleVideoExtension.Access.ALLOW, Collections.singleton("invalid"))
                .setDuration(Integer.MAX_VALUE)
                .setViewCount(-5)
                .setRating(100.0f)
                .setTags(IntStream.range(1, 50).mapToObj(String::valueOf).collect(Collectors.toList()));
        sitemap.close();

        // then
        assertSitemap(
                AbstractBuilderTest.XML_HEADER + "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" " +
                        "xmlns:video=\"http://www.google.com/schemas/sitemap-video/1.1\">"
                        + "<url>"
                        + "<loc>http://example.ch/de.html</loc>"

                        + "<video:video>"
                        + "<video:thumbnail_loc>http://example.ch/dam/de/images/hero_thumbnail.jpg</video:thumbnail_loc>"
                        + "<video:title>Hero Video</video:title>"
                        + "<video:description>foo bar</video:description>"
                        + "<video:content_loc>http://example.ch/dam/de/videos/hero.mov</video:content_loc>"
                        + "</video:video>"

                        + "<video:video>"
                        + "<video:thumbnail_loc>http://example.ch/dam/de/images/hero2_thumbnail.jpg</video:thumbnail_loc>"
                        + "<video:title>Hero Video 2</video:title>"
                        + "<video:description>foo bar 2</video:description>"
                        + "<video:content_loc>http://example.ch/dam/de/videos/hero2.mov</video:content_loc>"
                        + "<video:expiration_date>2022-12-31</video:expiration_date>"
                        + "<video:publication_date>2021-12-31</video:publication_date>"
                        + "</video:video>"

                        + "<video:video>"
                        + "<video:thumbnail_loc>http://example.ch/dam/de/images/hero3_thumbnail.jpg</video:thumbnail_loc>"
                        + "<video:title>Hero Video 3</video:title>"
                        + "<video:description>foo bar 3</video:description>"
                        + "<video:player_loc>http://example.ch/player.swf?video=hero3</video:player_loc>"
                        + "<video:duration>123</video:duration>"
                        + "<video:expiration_date>2022-12-31T12:34:56+01:00</video:expiration_date>"
                        + "<video:rating>4.7</video:rating>"
                        + "<video:view_count>200</video:view_count>"
                        + "<video:publication_date>2021-12-31T12:34:56+01:00</video:publication_date>"
                        + "<video:tag>foo</video:tag>"
                        + "<video:tag>bar</video:tag>"
                        + "<video:category>category</video:category>"
                        + "<video:family_friendly>yes</video:family_friendly>"
                        + "<video:restriction relationship=\"allow\">DE CH AT</video:restriction>"
                        + "<video:price currency=\"EUR\" type=\"rent\" resolution=\"hd\">9.99</video:price>"
                        + "<video:price currency=\"EUR\">19.99</video:price>"
                        + "<video:requires_subscription>yes</video:requires_subscription>"
                        + "<video:uploader info=\"http://example.ch/people/foo-bar\">Foo Bar</video:uploader>"
                        + "<video:platform relationship=\"deny\">tv</video:platform>"
                        + "<video:live>no</video:live>"
                        + "</video:video>"

                        + "<video:video>"
                        + "<video:thumbnail_loc>http://example.ch/dam/de/images/hero4_thumbnail.jpg</video:thumbnail_loc>"
                        + "<video:title>Hero Video 4</video:title>"
                        + "<video:description>foo bar 4</video:description>"
                        + "<video:player_loc>http://example.ch/player.swf?video=hero4</video:player_loc>"
                        + "<video:duration>28800</video:duration>"
                        + "<video:rating>5.0</video:rating>"
                        + "<video:view_count>0</video:view_count>"
                        + IntStream.range(1, 33)
                        .mapToObj(i -> "<video:tag>" + i + "</video:tag>")
                        .collect(Collectors.joining())
                        + "</video:video>"

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
        url.addExtension(GoogleVideoExtension.class);
        url.addExtension(GoogleVideoExtension.class)
                .setThumbnail("foobar");
        url.addExtension(GoogleVideoExtension.class)
                .setThumbnail("thumbnail location")
                .setTitle("title");
        url.addExtension(GoogleVideoExtension.class)
                .setThumbnail("thumbnail location")
                .setTitle("title")
                .setDescription("description");
        sitemap.close();

        // then
        assertSitemap(
                AbstractBuilderTest.XML_HEADER + "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" " +
                        "xmlns:video=\"http://www.google.com/schemas/sitemap-video/1.1\">"
                        + "<url>"
                        + "<loc>http://example.ch/de.html</loc>"
                        + "</url>"
                        + "</urlset>",
                writer.toString()
        );
    }
}
