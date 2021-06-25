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

import java.io.FilterWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.time.Instant;

import org.apache.sling.sitemap.SitemapException;
import org.junit.jupiter.api.Test;

import static org.apache.sling.sitemap.impl.builder.SitemapImplTest.XML_HEADER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SitemapIndexImplTest extends AbstractBuilderTest {

    private static final Instant DATETIME = Instant.ofEpochMilli(1622122594000L);

    @Test
    public void testAfterCloseFails() throws IOException {
        // given
        StringWriter writer = new StringWriter();
        SitemapIndexImpl subject = new SitemapIndexImpl(writer);

        // when
        subject.close();

        // then
        assertThrows(IllegalStateException.class, subject::close);
        assertThrows(IllegalStateException.class, () -> subject.addSitemap("any"));
        assertThrows(IllegalStateException.class, () -> subject.addSitemap("any", DATETIME));
    }

    @Test
    public void testIOExceptionWrapped() throws IOException {
        // given
        Writer writer = new FilterWriter(new StringWriter()) {
            @Override public void flush() throws IOException {
                throw new IOException();
            }

            @Override public void close() throws IOException {
                throw new IOException();
            }
        };
        SitemapIndexImpl subject = new SitemapIndexImpl(writer);

        // when, then
        assertThrows(SitemapException.class, () -> subject.addSitemap("any"));
        assertThrows(IOException.class, () -> subject.close());
    }

    @Test
    public void testEmptyIndex() throws IOException {
        // given
        StringWriter writer = new StringWriter();
        SitemapIndexImpl subject = new SitemapIndexImpl(writer);

        // when
        subject.close();

        // then
        assertEquals(
            XML_HEADER + "<sitemapindex xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\"></sitemapindex>",
            writer.toString()
        );
    }

    @Test
    public void testAddSitemapNoLastmod() throws IOException, SitemapException {
        // given
        StringWriter writer = new StringWriter();
        SitemapIndexImpl subject = new SitemapIndexImpl(writer);

        // when
        subject.addSitemap("http://localhost:8080/site/en.sitemap.xml");
        subject.close();

        // then
        assertSitemapIndex(
            XML_HEADER + "<sitemapindex xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">"
                + "<sitemap>"
                + "<loc>http://localhost:8080/site/en.sitemap.xml</loc>"
                + "</sitemap>"
                + "</sitemapindex>",
            writer.toString()
        );
    }

    @Test
    public void testAddSitemapWithLastmod() throws IOException, SitemapException {
        // given
        StringWriter writer = new StringWriter();
        SitemapIndexImpl subject = new SitemapIndexImpl(writer);

        // when
        subject.addSitemap("http://localhost:8080/site/en.sitemap.xml", DATETIME);
        subject.close();

        // then
        assertSitemapIndex(
            XML_HEADER + "<sitemapindex xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">"
                + "<sitemap>"
                + "<loc>http://localhost:8080/site/en.sitemap.xml</loc>"
                + "<lastmod>2021-05-27T13:36:34Z</lastmod>"
                + "</sitemap>"
                + "</sitemapindex>",
            writer.toString()
        );
    }
}
