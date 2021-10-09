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
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import com.ioleak.jnetcat.common.Logging;
import com.ioleak.jnetcat.common.properties.Observable;

public class KeyCharReader
        implements Observable {

  private final PropertyChangeSupport listenerManager = new PropertyChangeSupport(this);
  
  private static final List<Character> supportedCommands = Arrays.asList('k', 'q', 's');
  private static final Character COMMAND_STARTS_WITH_FIRST = ':';
  private static final Character COMMAND_STARTS_WITH_SECOND = '!';
  private static final String COMMAND_STARTS_WITH_FULL = String.format("%s%s", COMMAND_STARTS_WITH_FIRST, COMMAND_STARTS_WITH_SECOND);
  
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

  public void run() {
    boolean exceptionThrown = false;

    showInfo();

    while (!Thread.currentThread().isInterrupted() && !exceptionThrown) {
      try {
        readChar();
        Thread.sleep(WAIT_DELAY_NEXT_CHAR_MS);
      } catch (InterruptedException ex) {
        Logging.getLogger().error("KeyCharReader interrupted", ex);
      } catch (HitKeyCloseCharReaderException ex) {
        exceptionThrown = true;
        Logging.getLogger().warn(String.format("Kill command received: %s", ex.getMessage()));
      }
    }

    Logging.getLogger().warn("Keyboard thread is closed: console won't responds to command anymore");
  }

  public void showInfo() {
    Logging.getLogger().info(String.format("Hit key '%sk' to kill this key listener", COMMAND_STARTS_WITH_FULL));

    if (this.actionOnKeyS != null) {
      Logging.getLogger().info(String.format("Hit key '%ss' to stop an established connection", COMMAND_STARTS_WITH_FULL));
    }

    if (this.actionOnKeyQ != null) {
      Logging.getLogger().info(String.format("Hit key '%sq' to close this server", COMMAND_STARTS_WITH_FULL));
    }
  }

  public boolean readChar() {
    boolean charValid = true;
    StringBuilder command = new StringBuilder();

    try {
      while (charValid) {
        char key = (char) System.in.read();
        charValid = (key != 65535 && key != -1);

        buildCommandString(command, key);
        if (command.length() == 3) {
          executeCommand(command.toString());
          clearCommandIfComplete(command);
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

  private void buildCommandString(StringBuilder command, char key) {
    if (key == COMMAND_STARTS_WITH_FIRST) {
      command.append(key);
    }

    if (key == COMMAND_STARTS_WITH_SECOND) {
      if (command.toString().equals(COMMAND_STARTS_WITH_FIRST.toString())) {
        command.append(key);
      } else {
        command.setLength(0);
      }
    }

    if (supportedCommands.contains(key)) {
      if (command.toString().equals(COMMAND_STARTS_WITH_FULL)) {
        command.append(key);
      } else {
        command.setLength(0);
      }
    }
  }

  private void executeCommand(String command) {
    switch (command) {
      case ":!k":
        throw new HitKeyCloseCharReaderException("Exit program: k key used");
      case ":!q":
        if (actionOnKeyQ != null) {
          keyboardHitQuit = actionOnKeyQ.get();
        } else {
          Logging.getLogger().warn("No action is defined as a quit (q) action. No action is taken");
        }
        break;
      case ":!s":
        if (actionOnKeyS != null) {
          keyboardHitStop = actionOnKeyS.get();
        } else {
          Logging.getLogger().warn("No action is defined as a stop (s) action. No action is taken");
        }
        break;
      default:
        break;
    }
  }

  private void clearCommandIfComplete(StringBuilder command) {
    if (command.length() == 3 && command.toString().startsWith(COMMAND_STARTS_WITH_FULL)) {
      command.setLength(0);
    }
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
