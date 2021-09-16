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
import java.util.Iterator;

public class ChainedIterator<T> implements Iterator<T> {

    private final Iterator<Iterator<T>> iterators;
    private Iterator<T> currentIterator;
    private T currentItem;

    public ChainedIterator(Iterator<T>... iterators) {
        this(Arrays.asList(iterators));
    }

    public ChainedIterator(Iterator<T> iterator1, Iterator<T> iterator2) {
        this(Arrays.asList(iterator1, iterator2));
    }

    public ChainedIterator(Iterable<Iterator<T>> iterators) {
        this.iterators = iterators.iterator();
        seek();
    }

    private void seek() {
        while (currentItem == null) {
            if (currentIterator != null && currentIterator.hasNext()) {
                currentItem = currentIterator.next();
            } else {
                if (iterators.hasNext()) {
                    currentIterator = iterators.next();
                } else {
                    break;
                }
            }
        }
    }

    @Override
    public boolean hasNext() {
        return currentItem != null;
    }

    @Override public T next() {
        T item = currentItem;
        currentItem = null;
        seek();
        return item;
    }
}
