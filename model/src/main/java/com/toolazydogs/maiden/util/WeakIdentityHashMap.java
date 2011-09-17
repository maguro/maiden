/**
 *
 * Copyright 2011 (C) The original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.toolazydogs.maiden.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * A hashtable-based <tt>Map</tt> implementation with <em>weak keys</em>.
 * An entry in a <tt>WeakHashMap</tt> will automatically be removed when
 * its key is no longer in ordinary use.  More precisely, the presence of a
 * mapping for a given key will not prevent the key from being discarded by the
 * garbage collector, that is, made finalizable, finalized, and then reclaimed.
 * When a key has been discarded its entry is effectively removed from the map,
 * so this class behaves somewhat differently from other <tt>Map</tt>
 * implementations.
 * <p/>
 * <p> Both null values and the null key are supported. This class has
 * performance characteristics similar to those of the <tt>HashMap</tt>
 * class, and has the same efficiency parameters of <em>initial capacity</em>
 * and <em>load factor</em>.
 * <p/>
 * <p> Like most collection classes, this class is not synchronized.
 * A synchronized <tt>WeakHashMap</tt> may be constructed using the
 * {@link java.util.Collections#synchronizedMap Collections.synchronizedMap}
 * method.
 */
public class WeakIdentityHashMap<K, V> implements Map<K, V>
{
    private final ReferenceQueue<K> referenceQueue = new ReferenceQueue<K>();
    private final Map<WeakWrapper<K>, V> delegate;

    public WeakIdentityHashMap(int initialCapacity, float loadFactor)
    {
        delegate = new HashMap<WeakWrapper<K>, V>(initialCapacity, loadFactor);
    }

    public WeakIdentityHashMap(int initialCapacity)
    {
        delegate = new HashMap<WeakWrapper<K>, V>(initialCapacity);
    }

    public WeakIdentityHashMap()
    {
        delegate = new HashMap<WeakWrapper<K>, V>();
    }

    public WeakIdentityHashMap(Map<? extends K, ? extends V> m)
    {
        delegate = new HashMap<WeakWrapper<K>, V>();
        putAll(m);
    }

    @Override
    public int size()
    {
        purge();
        return delegate.size();
    }

    @Override
    public boolean isEmpty()
    {
        purge();
        return delegate.isEmpty();
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public boolean containsKey(Object key)
    {
        purge();
        return delegate.containsKey(new WeakWrapper<K>((K)key));
    }

    @Override
    public boolean containsValue(Object value)
    {
        purge();
        return delegate.containsValue(value);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public V get(Object key)
    {
        purge();
        return delegate.get(new WeakWrapper<K>((K)key));
    }

    @Override
    public V put(K key, V value)
    {
        purge();
        return delegate.put(new WeakWrapper<K>(key), value);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public V remove(Object key)
    {
        purge();
        return delegate.remove(new WeakWrapper<K>((K)key));
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m)
    {
        for (Entry<? extends K, ? extends V> entry : m.entrySet())
        {
            delegate.put(new WeakWrapper<K>(entry.getKey()), entry.getValue());
        }
    }

    @Override
    public void clear()
    {
        purge();
        delegate.clear();
    }

    @Override
    public Set<K> keySet()
    {
        purge();
        Set<K> keySet = new HashSet<K>();
        for (WeakWrapper<K> wrappedKey : delegate.keySet())
        {
            K pinned = wrappedKey.get();
            keySet.add(pinned);
        }
        return keySet;
    }

    @Override
    public Collection<V> values()
    {
        purge();
        return delegate.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet()
    {
        purge();
        Set<Entry<K, V>> keySet = new HashSet<Entry<K, V>>();
        for (final Entry<WeakWrapper<K>, V> entry : delegate.entrySet())
        {
            final K pinned = entry.getKey().get();
            keySet.add(new Entry<K, V>()
            {
                @Override
                public K getKey() { return pinned; }

                @Override
                public V getValue() { return entry.getValue(); }

                @Override
                public V setValue(V value) { return entry.setValue(value); }
            });
        }
        return keySet;
    }

    /**
     * Remove any entries whose key has been reclaimed by the garbage
     * collector.  This method is called by all the other public methods if
     * this class.  This method is public so that users of this class can
     * schedule the periodic purging of instances of this class.
     * <p/>
     * Note: this class is not thread safe.
     */
    public void purge()
    {
        WeakWrapper<K> key;
        while ((key = (WeakWrapper<K>)referenceQueue.poll()) != null) delegate.remove(key);
    }

    @SuppressWarnings({"unchecked"})
    private class WeakWrapper<K> extends WeakReference<K>
    {
        private final int hash;

        WeakWrapper(K key)
        {
            super(key, (ReferenceQueue<? super K>)referenceQueue);
            hash = System.identityHashCode(key);
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (!(o instanceof WeakWrapper)) return false;
            WeakWrapper ww = (WeakWrapper)o;
            Object pinned = get();
            return (pinned != null && pinned == ww.get());
        }

        @Override
        public int hashCode()
        {
            return hash;
        }
    }
}
