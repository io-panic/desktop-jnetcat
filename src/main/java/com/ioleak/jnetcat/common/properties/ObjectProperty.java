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
package com.ioleak.jnetcat.common.properties;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ObjectProperty<T>
        implements Observable {

  public final static String PROPERTYNAME = "object";

  private final PropertyChangeSupport listenerManager = new PropertyChangeSupport(this);
  private T object;
  private boolean verifyEquals = false;
  
  public ObjectProperty() {
    this(null);
  }

  public ObjectProperty(T objectValue) {
    this.object = objectValue;
  }

  public void setVerifyEquals(boolean verifyEquals) {
    this.verifyEquals = verifyEquals;
  }
  
  @Override
  public void addListener(PropertyChangeListener listener) {
    listenerManager.addPropertyChangeListener(listener);
  }

  @Override
  public void removeListener(PropertyChangeListener listener) {
    listenerManager.removePropertyChangeListener(listener);
  }

  public T get() {
    return object;
  }

  public void set(T object) {
    if (!verifyEquals) {
      if (this.object == object) {
        this.object = null;
      }
    }
      
    PropertyChangeEvent event = new PropertyChangeEvent(this, PROPERTYNAME, this.object, object);
    this.object = object;
    listenerManager.firePropertyChange(event);
  }
}
