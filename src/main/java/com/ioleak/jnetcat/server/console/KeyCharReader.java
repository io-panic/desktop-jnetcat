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
package com.ioleak.jnetcat.server.console;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.function.Supplier;

import com.ioleak.jnetcat.common.Logging;
import com.ioleak.jnetcat.common.property.Observable;

public class KeyCharReader
        implements Runnable, Observable {

  private final PropertyChangeSupport listenerManager = new PropertyChangeSupport(this);

  private final Supplier<Boolean> actionOnStop;
  private boolean keyboardHitStop = false;

  public KeyCharReader(Supplier<Boolean> actionOnStop) {
    Logging.getLogger().info("Hit key 's' to stop an established connection");
    Logging.getLogger().info("Hit key 'q' to close this server");

    this.actionOnStop = actionOnStop;
  }

  @Override
  public void run() {
    while (!Thread.currentThread().isInterrupted()) {
      readChar();

      try {
        Thread.sleep(100);
      } catch (InterruptedException ex) {
        Logging.getLogger().error("KeyCharReader interrupted", ex);
      }
    }

    Logging.getLogger().warn("Keyboard thread is closed: console won't responds to command anymore");
  }

  private void readChar() {
    try {
      char key = (char) System.in.read();
      if ((int) key == 65535) {
        return;
      }

      listenerManager.firePropertyChange("keyreader", null, key);

      if (key == 'q') {
        Logging.getLogger().warn("Exit program: q key used");
        System.exit(0);
      } else if (key == 's') {
        keyboardHitStop = actionOnStop.get();
      }
    } catch (IOException ex) {
      Logging.getLogger().error(String.format("Unable to read char from keyboard: %s", ex.getMessage()));
    }
  }

  public boolean isInterrupted() {
    return keyboardHitStop;
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
