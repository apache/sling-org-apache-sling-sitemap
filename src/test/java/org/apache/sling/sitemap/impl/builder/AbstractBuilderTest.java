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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class AbstractBuilderTest {

    public static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

    private static final URL XML_XSD = AbstractBuilderTest.class.getClassLoader().getResource("xml.xsd");
    private static final URL XHTML_XSD = AbstractBuilderTest.class.getClassLoader().getResource("xhtml1-strict.xsd");
    private static final URL SITEMAP_XSD = AbstractBuilderTest.class.getClassLoader().getResource("sitemap-0.9.xsd");
    private static final URL SITEMAP_INDEX_XSD = AbstractBuilderTest.class.getClassLoader().getResource("siteindex-0.9.xsd");
    private static final URL SITEMAP_IMAGE_XSD = AbstractBuilderTest.class.getClassLoader().getResource("sitemap-image-1.1.xsd");
    private static final URL SITEMAP_NEWS_XSD = AbstractBuilderTest.class.getClassLoader().getResource("sitemap-news-0.9.xsd");

    protected void assertSitemap(String expected, String given) {
        assertEqualsAndValid(expected, given, XML_XSD, XHTML_XSD, SITEMAP_XSD, SITEMAP_IMAGE_XSD, SITEMAP_NEWS_XSD);
    }

    protected void assertSitemapIndex(String expected, String given) {
        assertEqualsAndValid(expected, given, SITEMAP_INDEX_XSD);
    }

    private void assertEqualsAndValid(String expected, String given, URL... schemaUrl) {
        assertEquals(expected, given);
        assertValid(expected, schemaUrl);
        assertValid(given, schemaUrl);
    }

    private void assertValid(String xml, URL... schemaUrls) {
        try {
            assert schemaUrls.length > 0;
            Source[] sources = new Source[schemaUrls.length];
            for (int i = 0; i < schemaUrls.length; i++) {
                sources[i] = new StreamSource(schemaUrls[i].openStream());
            }
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(sources);
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))));
        } catch (IOException | SAXException ex) {
            throw new AssertionError(ex);
        }
    }
}
