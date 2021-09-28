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
  private static final String THREAD_FORMAT_NAME = "Thread-%s";
  private static final int WAIT_DELAY_NEXT_CHAR_MS = 100;
  
  private Supplier<Boolean> actionOnKeyS;
  private Supplier<Boolean> actionOnKeyQ;
    
  private boolean keyboardHitStop = false;
  private boolean keyboardHitQuit = false;

  public KeyCharReader() {
    this(null, null);
  }

  public KeyCharReader(Supplier<Boolean> actionOnKeyS, Supplier<Boolean> actionOnKeyQ) {
    this.actionOnKeyS = actionOnKeyS;
    this.actionOnKeyQ = actionOnKeyQ;
  }

  @Override
  public void run() {
    Thread.currentThread().setName(String.format(THREAD_FORMAT_NAME, getClass().getSimpleName()));
    showInfo();
    
    while (!Thread.currentThread().isInterrupted()) {
      try {
        readChar();
        Thread.sleep(WAIT_DELAY_NEXT_CHAR_MS);
      } catch (InterruptedException ex) {
        Logging.getLogger().error("KeyCharReader interrupted", ex);
      } catch (HitKeyCloseCharReaderException ex) {
        //Thread.currentThread().interrupt();
        Logging.getLogger().error(String.format("Thread will exit: %s", ex.getMessage()));
      }
    }

    Logging.getLogger().warn("Keyboard thread is closed: console won't responds to command anymore");
  }

  public void showInfo() {
    Logging.getLogger().info("Hit key 'k' to kill this key listener");
    
    if (this.actionOnKeyS != null) {
      Logging.getLogger().info("Hit key 's' to stop an established connection");
    }
    
    if (this.actionOnKeyQ != null) {
      Logging.getLogger().info("Hit key 'q' to close this server");
    }
  }
    
  public boolean readChar() {
    boolean charValid = true;

    try {
      while (charValid) {
        char key = (char) System.in.read();
        charValid = (key != 65535 && key != -1);

        switch (key) {
          case 'k':
            throw new HitKeyCloseCharReaderException("Exit program: k key used");
          case 'q':
            if (actionOnKeyQ != null) {
              keyboardHitQuit = actionOnKeyQ.get();
            } else {
              Logging.getLogger().warn("No action is defined as a quit (q) action. No action is taken");
            }
            break;
          case 's':
            if (actionOnKeyS != null) {
              keyboardHitStop = actionOnKeyS.get();
            } else {
              Logging.getLogger().warn("No action is defined as a stop (s) action. No action is taken");
            } break;
          default:
            break;
        }

        if (charValid) {
          listenerManager.firePropertyChange("keyreader", null, key);
        }
      }

    } catch (IOException ex) {
      Logging.getLogger().error(String.format("Unable to read char from keyboard: %s", ex.getMessage()));
    }
    
    return charValid;
  }

  public boolean isKeyStopPressed() {
    return keyboardHitStop;
  }

  public boolean isKeyQuitPressed() {
    return keyboardHitQuit;
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
