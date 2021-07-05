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
package org.apache.sling.sitemap.impl.builder;

import org.apache.sling.sitemap.SitemapException;
import org.apache.sling.sitemap.builder.Extension;
import org.apache.sling.sitemap.builder.Url;
import org.apache.sling.sitemap.spi.builder.AbstractExtension;
import org.apache.sling.sitemap.spi.builder.SitemapExtensionProvider;
import org.apache.sling.sitemap.impl.builder.extensions.ExtensionProviderManager;
import org.apache.sling.testing.mock.sling.junit5.SlingContext;
import org.apache.sling.testing.mock.sling.junit5.SlingContextExtension;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({ SlingContextExtension.class })
class UrlImplTest extends AbstractBuilderTest {

    private static final Instant DATETIME = Instant.ofEpochMilli(1622122594000L);

    final SlingContext context = new SlingContext();

    private ExtensionProviderManager extensionManager;

    @BeforeEach
    void setup() {
        context.registerService(
                SitemapExtensionProvider.class, new TestExtensionProvider(),
                "extension.interface", TestExtension.class.getName(),
                "extension.prefix", TestExtensionProvider.PREFIX,
                "extension.namespace", TestExtensionProvider.NAMESPACE,
                "extension.localName", TestExtensionProvider.LOCAL_NAME
        );
        extensionManager = context.registerInjectActivateService(new ExtensionProviderManager());
    }

    @Test
    void testAddFullUrl() throws SitemapException, IOException {
        // given
        StringWriter writer = new StringWriter();
        SitemapImpl subject = new SitemapImpl(writer, extensionManager);

        // when
        subject.addUrl("http://example.com")
                .setChangeFrequency(Url.ChangeFrequency.DAILY)
                .setPriority(0.6)
                .setLastModified(DATETIME)
        ;

        subject.close();

        // then
        assertSitemap(
                AbstractBuilderTest.XML_HEADER + "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" " +
                        "xmlns:tst=\"http://localhost/schema/test/1.0\">"
                        + "<url>"
                        + "<loc>http://example.com</loc>"
                        + "<lastmod>2021-05-27T13:36:34Z</lastmod>"
                        + "<changefreq>daily</changefreq>"
                        + "<priority>0.6</priority>"
                        + "</url>"
                        + "</urlset>",
                writer.toString()
        );
    }

    @Test
    void testPriorityNormalization() throws SitemapException, IOException {
        // given
        StringWriter writer = new StringWriter();
        SitemapImpl subject = new SitemapImpl(writer, extensionManager);

        // when
        subject.addUrl("http://example.com/page1.html").setPriority(-1.0);
        subject.addUrl("http://example.com/page2.html").setPriority(5.0);
        subject.close();

        // then
        assertSitemap(
                AbstractBuilderTest.XML_HEADER + "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" " +
                        "xmlns:tst=\"http://localhost/schema/test/1.0\">"
                        + "<url>"
                        + "<loc>http://example.com/page1.html</loc>"
                        + "<priority>0.0</priority>"
                        + "</url>"
                        + "<url>"
                        + "<loc>http://example.com/page2.html</loc>"
                        + "<priority>1.0</priority>"
                        + "</url>"
                        + "</urlset>",
                writer.toString()
        );
    }

    @Test
    void testWithExtensions() throws SitemapException, IOException {
        // given
        StringWriter writer = new StringWriter();
        SitemapImpl subject = new SitemapImpl(writer, extensionManager);

        // when
        Url url = subject.addUrl("http://example.com/page1.html");
        TestExtension extension = url.addExtension(TestExtension.class);
        extension.setValue("foobar");
        TestExtension2 extension2 = url.addExtension(TestExtension2.class);
        assertNull(extension2);
        subject.close();

        // then
        assertEquals(
                AbstractBuilderTest.XML_HEADER + "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" " +
                        "xmlns:tst=\"http://localhost/schema/test/1.0\">"
                        + "<url>"
                        + "<loc>http://example.com/page1.html</loc>"
                        + "<tst:test>"
                        + "<tst:value>foobar</tst:value>"
                        + "</tst:test>"
                        + "</url>"
                        + "</urlset>",
                writer.toString()
        );
    }

    @Test
    void testWritingToWrittenUrlThrows() throws SitemapException, IOException {
        // given
        StringWriter writer = new StringWriter();
        SitemapImpl subject = new SitemapImpl(writer, extensionManager);
        Instant now = Instant.now();

        // when
        Url url = subject.addUrl("http://example.com/page1.html");
        TestExtension extension = url.addExtension(TestExtension.class);
        extension.setValue("foobar");
        subject.close();

        assertThrows(IllegalStateException.class, () -> url.setPriority(0.0));
        assertThrows(IllegalStateException.class, () -> url.setChangeFrequency(Url.ChangeFrequency.ALWAYS));
        assertThrows(IllegalStateException.class, () -> url.setLastModified(now));
        assertThrows(IllegalStateException.class, () -> url.addExtension(TestExtension.class));
    }

    interface TestExtension2 extends Extension {
        TestExtension setValue(String value);
    }

    interface TestExtension extends Extension {
        TestExtension setValue(String value);
    }

    static class TestExtensionProvider implements SitemapExtensionProvider {

        static String NAMESPACE = "http://localhost/schema/test/1.0";
        static String PREFIX = "tst";
        static String LOCAL_NAME = "test";

        @Override
        public @NotNull ExtensionImpl newInstance() {
            return new ExtensionImpl();
        }

        static class ExtensionImpl extends AbstractExtension implements TestExtension {

            private String value;

            @Override
            public TestExtension setValue(String value) {
                this.value = value;
                return this;
            }

            @Override
            public void writeTo(@NotNull XMLStreamWriter writer) throws XMLStreamException {
                writer.writeStartElement("value");
                writer.writeCharacters(value);
                writer.writeEndElement();
            }
        }
    }
}
