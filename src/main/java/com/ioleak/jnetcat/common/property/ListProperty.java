/*
 * Copyright (c) 2021, crashdump (<xxxx>@ioleak.com)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.ioleak.jnetcat.common.property;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ListProperty<T>
        implements List<T>, Observable {

  public final static String PROPERTYNAME = "list";
  
  private final PropertyChangeSupport listenerManager = new PropertyChangeSupport(this);
  private final List<T> observedList;

  public ListProperty() {
    this(new ArrayList<>());
  }

  public ListProperty(List<T> observedList) {
    this.observedList = observedList;
  }

  @Override
  public int size() {
    return observedList.size();
  }

  @Override
  public boolean isEmpty() {
    return observedList.isEmpty();
  }

  @Override
  public boolean contains(Object object) {
    return observedList.contains(object);
  }

  @Override
  public Iterator<T> iterator() {
    return observedList.iterator();
  }

  @Override
  public Object[] toArray() {
    return observedList.toArray();
  }

  @Override
  public <E> E[] toArray(E[] objects) {
    return observedList.toArray(objects);
  }

  @Override
  public boolean add(T object) {
    PropertyChangeEvent event = new PropertyChangeEvent(this, PROPERTYNAME, null, object);

    boolean added = observedList.add(object);
    listenerManager.firePropertyChange(event);
 
    return added;
  }

  @Override
  public boolean remove(Object object) {
    PropertyChangeEvent event = new PropertyChangeEvent(this, PROPERTYNAME, object, null);

    boolean removed = observedList.remove(object);
    if (removed) {
      listenerManager.firePropertyChange(event);
    }

    return removed;
  }

  @Override
  public boolean containsAll(Collection<?> collection) {
    return observedList.containsAll(collection);
  }

  @Override
  public boolean addAll(Collection<? extends T> collection) {
    PropertyChangeEvent event = new PropertyChangeEvent(this, PROPERTYNAME, collection, observedList);

    boolean modified = observedList.addAll(collection);
    listenerManager.firePropertyChange(event);

    return modified;
  }

  @Override
  public boolean addAll(int pos, Collection<? extends T> collection) {
    PropertyChangeEvent event = new PropertyChangeEvent(this, PROPERTYNAME, collection, observedList);

    boolean modified = observedList.addAll(pos, collection);
    listenerManager.firePropertyChange(event);
 
    return modified;
  }

  @Override
  public boolean removeAll(Collection<?> collection) {
    PropertyChangeEvent event = new PropertyChangeEvent(this, PROPERTYNAME, collection, observedList);

    boolean removed = observedList.removeAll(collection);
    if (removed) {
      listenerManager.firePropertyChange(event);
    }

    return removed;
  }

  @Override
  public boolean retainAll(Collection<?> collection) {
    return observedList.retainAll(collection);
  }

  @Override
  public void clear() {
    PropertyChangeEvent event = new PropertyChangeEvent(this, PROPERTYNAME, observedList, null);

    if (!observedList.isEmpty()) {
      observedList.clear();

      if (observedList.isEmpty()) {
        listenerManager.firePropertyChange(event);
      }
    }
  }

  @Override
  public T get(int pos) {
    return observedList.get(pos);
  }

  @Override
  public T set(int pos, T object) {
    T objectSet = null;

    if ((observedList.get(pos) == null && object != null)
        || !(observedList.get(pos) != null && observedList.get(pos).equals(object))) {

      PropertyChangeEvent event = new PropertyChangeEvent(this, PROPERTYNAME, observedList.get(pos), object);

      objectSet = observedList.set(pos, object);
      listenerManager.firePropertyChange(event);
    }

    return objectSet;
  }

  @Override
  public void add(int pos, T object) {
    PropertyChangeEvent event = new PropertyChangeEvent(this, PROPERTYNAME, observedList, object);
    observedList.add(pos, object);
    listenerManager.firePropertyChange(event);
  }

  @Override
  public T remove(int pos) {
    PropertyChangeEvent event = new PropertyChangeEvent(this, PROPERTYNAME, observedList, observedList.get(pos));
    T objectRemoved = observedList.remove(pos);
    listenerManager.firePropertyChange(event);

    return objectRemoved;
  }

  @Override
  public int indexOf(Object object) {
    return observedList.indexOf(object);
  }

  @Override
  public int lastIndexOf(Object object) {
    return observedList.lastIndexOf(object);
  }

  @Override
  public ListIterator<T> listIterator() {
    return observedList.listIterator();
  }

  @Override
  public ListIterator<T> listIterator(int index) {
    return observedList.listIterator(index);
  }

  @Override
  public List<T> subList(int from, int to) {
    return observedList.subList(from, to);
  }

  @Override
  public void addListener(PropertyChangeListener listener) {
    listenerManager.addPropertyChangeListener(listener);
  }

  @Override
  public void removeListener(PropertyChangeListener listener) {
    listenerManager.removePropertyChangeListener(listener);
  }
}
