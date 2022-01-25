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

import org.apache.sling.sitemap.builder.Extension;
import org.apache.sling.sitemap.spi.builder.SitemapExtensionProvider;
import org.apache.sling.testing.mock.sling.junit5.SlingContext;
import org.apache.sling.testing.mock.sling.junit5.SlingContextExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith({SlingContextExtension.class, MockitoExtension.class})
class ExtensionProviderManagerTest {

    final SlingContext context = new SlingContext();

    interface Ext1 extends Extension {
    }

    interface Ext2 extends Extension {
    }

    @Mock
    SitemapExtensionProvider extension1;
    @Mock
    SitemapExtensionProvider extension2;

    @Test
    void testWithMultipleExtensionProviders() {
        context.registerService(
                SitemapExtensionProvider.class,
                extension1,
                SitemapExtensionProvider.PROPERTY_INTERFACE, Ext1.class.getName(),
                SitemapExtensionProvider.PROPERTY_PREFIX, "a",
                SitemapExtensionProvider.PROPERTY_LOCAL_NAME, "ext1",
                SitemapExtensionProvider.PROPERTY_NAMESPACE, "https://example.com/");
        context.registerService(
                SitemapExtensionProvider.class,
                extension2,
                SitemapExtensionProvider.PROPERTY_INTERFACE, Ext2.class.getName(),
                SitemapExtensionProvider.PROPERTY_PREFIX, "b",
                SitemapExtensionProvider.PROPERTY_LOCAL_NAME, "ext2",
                SitemapExtensionProvider.PROPERTY_NAMESPACE, "https://example.com/");

        ExtensionProviderManager subject = context.registerInjectActivateService(new ExtensionProviderManager());

        ExtensionFactory factory1 = subject.getExtensionFactory(Ext1.class);
        assertNotNull(factory1);
        ExtensionFactory factory2 = subject.getExtensionFactory(Ext2.class);
        assertNotNull(factory2);

        // they share the same namespace and so the prefix must be equal
        assertEquals(factory1.getPrefix(), factory2.getPrefix());

        assertEquals(1, subject.getNamespaces().size());
        assertEquals("a", subject.getNamespaces().get("https://example.com/"));
    }
}
