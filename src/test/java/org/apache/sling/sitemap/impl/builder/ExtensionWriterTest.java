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

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({ MockitoExtension.class })
class ExtensionWriterTest {

    @Mock
    public XMLStreamWriter delegate;
    public ExtensionWriter subject;

    @BeforeEach
    public void setup() {
        subject = new ExtensionWriter(delegate, "test");
    }

    @Test
    void testUnsupportedMethods() {
        assertThrows(UnsupportedOperationException.class, () -> subject.writeEndDocument());
        assertThrows(UnsupportedOperationException.class, () -> subject.close());
        assertThrows(UnsupportedOperationException.class, () -> subject.flush());
        assertThrows(UnsupportedOperationException.class, () -> subject.writeNamespace("foo", "bar"));
        assertThrows(UnsupportedOperationException.class, () -> subject.writeDefaultNamespace("bar"));
        assertThrows(UnsupportedOperationException.class, () -> subject.writeComment("foobar"));
        assertThrows(UnsupportedOperationException.class, () -> subject.writeProcessingInstruction("foobar"));
        assertThrows(UnsupportedOperationException.class, () -> subject.writeProcessingInstruction("foo", "bar"));
        assertThrows(UnsupportedOperationException.class, () -> subject.writeDTD("foobar"));
        assertThrows(UnsupportedOperationException.class, () -> subject.writeEntityRef("ref"));
        assertThrows(UnsupportedOperationException.class, () -> subject.writeStartDocument());
        assertThrows(UnsupportedOperationException.class, () -> subject.writeStartDocument("1.0"));
        assertThrows(UnsupportedOperationException.class, () -> subject.writeStartDocument("UTF-8", "1.0"));
        assertThrows(UnsupportedOperationException.class, () -> subject.setPrefix("test", "foobar"));
        assertThrows(UnsupportedOperationException.class, () -> subject.setDefaultNamespace("foo"));
        assertThrows(UnsupportedOperationException.class, () -> subject.setNamespaceContext(mock(NamespaceContext.class)));
    }

    @Test
    void testDelegatingMethods() throws XMLStreamException {
        subject.writeStartElement("foo");
        verify(delegate).writeStartElement("test", "foo");
        clearInvocations(delegate);

        subject.writeStartElement("test", "foo");
        verify(delegate).writeStartElement("test", "foo");
        clearInvocations(delegate);

        subject.writeStartElement("t", "foo", "test");
        verify(delegate).writeStartElement("test", "foo");
        clearInvocations(delegate);

        subject.writeEmptyElement("foo");
        verify(delegate).writeEmptyElement("test", "foo");
        clearInvocations(delegate);

        subject.writeEmptyElement("test", "foo");
        verify(delegate).writeEmptyElement("test", "foo");
        clearInvocations(delegate);

        subject.writeEmptyElement("t", "foo", "test");
        verify(delegate).writeEmptyElement("test", "foo");
        clearInvocations(delegate);

        subject.writeEndElement();
        verify(delegate).writeEndElement();
        clearInvocations(delegate);

        subject.writeAttribute("attr", "val");
        verify(delegate).writeAttribute("attr", "val");
        clearInvocations(delegate);

        subject.writeAttribute("test", "attr", "val");
        verify(delegate).writeAttribute("test", "attr", "val");
        clearInvocations(delegate);

        subject.writeAttribute("t", "test", "attr", "val");
        verify(delegate).writeAttribute("test", "attr", "val");
        clearInvocations(delegate);

        subject.writeCData("data");
        verify(delegate).writeCData("data");
        clearInvocations(delegate);

        subject.writeCharacters("text");
        verify(delegate).writeCharacters("text");
        clearInvocations(delegate);

        subject.writeCharacters("text".toCharArray(), 1, 2);
        verify(delegate).writeCharacters("text".toCharArray(), 1, 2);
        clearInvocations(delegate);

        when(delegate.getPrefix("test")).thenReturn("t");
        assertEquals("t", subject.getPrefix("test"));

        NamespaceContext nsc = mock(NamespaceContext.class);
        when(delegate.getNamespaceContext()).thenReturn(nsc);
        assertEquals(nsc, subject.getNamespaceContext());

        Object property = new Object();
        when(delegate.getProperty("name")).thenReturn(property);
        assertEquals(property, subject.getProperty("name"));
    }

    @Test
    void testThrowsOnNamespaceMismatch() throws XMLStreamException {
        assertThrows(IllegalArgumentException.class, () -> subject.writeStartElement("foo", "bar"));
        assertThrows(IllegalArgumentException.class, () -> subject.writeStartElement("t", "foo", "bar"));
        assertThrows(IllegalArgumentException.class, () -> subject.writeEmptyElement("foo", "bar"));
        assertThrows(IllegalArgumentException.class, () -> subject.writeEmptyElement("t", "foo", "bar"));
        assertThrows(IllegalArgumentException.class, () -> subject.writeAttribute("foo", "bar", "val"));
        assertThrows(IllegalArgumentException.class, () -> subject.writeAttribute("t", "foo", "bar", "val"));
    }
}
