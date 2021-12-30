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
package org.apache.sling.sitemap.impl.helper;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChainedIteratorTest {

    @Test
    void testEmptyNoIterator() {
        assertFalse(new ChainedIterator<>().hasNext());
    }

    @Test
    void testEmptyOneEmptyIterator() {
        assertFalse(new ChainedIterator<>(Collections.emptyIterator()).hasNext());
    }

    @Test
    void testEmptyMutlipleEmptyIterators() {
        assertFalse(new ChainedIterator<>(Collections.emptyIterator(), Collections.emptyIterator(), Collections.emptyIterator()).hasNext());
    }

    @Test
    void testThorwsNoSuchElementException() {
        assertThrows(NoSuchElementException.class, () -> new ChainedIterator<>().next());
    }

    @Test
    void testContainsAll() {
        Iterator<String> it = new ChainedIterator<>(
            Arrays.asList("a", "b").iterator(),
            Arrays.asList("c", "d").iterator()
        );

        assertTrue(it.hasNext());
        assertEquals("a", it.next());
        assertTrue(it.hasNext());
        assertEquals("b", it.next());
        assertTrue(it.hasNext());
        assertEquals("c", it.next());
        assertTrue(it.hasNext());
        assertEquals("d", it.next());
        assertFalse(it.hasNext());
    }
}
