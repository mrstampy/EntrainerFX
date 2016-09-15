/*
 *      ______      __             _                 _______  __
 *     / ____/___  / /__________ _(_)___  ___  _____/ ____/ |/ /
 *    / __/ / __ \/ __/ ___/ __ `/ / __ \/ _ \/ ___/ /_   |   / 
 *   / /___/ / / / /_/ /  / /_/ / / / / /  __/ /  / __/  /   |  
 *  /_____/_/ /_/\__/_/   \__,_/_/_/ /_/\___/_/  /_/    /_/|_|  
 *                                                          
 *
 * Copyright (C) 2008 - 2016 Burton Alexander
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 */
package net.sourceforge.entrainer.gui.jfx;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicBoolean;

import org.reactfx.inhibeans.property.SimpleIntegerProperty;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Duration;
import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.ReceiverAdapter;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;

// TODO: Auto-generated Javadoc
/**
 * The Class ProgramHUD displays the program name, elapsed and total time.
 */
public class ProgramHUD extends VBox {

  private Font display;

  private Label label;

  private Label name;

  private int minutes;

  private int seconds;

  private int currentMinutes;
  private int currentSeconds;

  private IntegerProperty totalSeconds = new SimpleIntegerProperty();

  private AtomicBoolean program = new AtomicBoolean(false);

  private DecimalFormat df = new DecimalFormat("00");

  private Timeline tl;

  /**
   * Instantiates a new program hud.
   */
  public ProgramHUD() {
    super(20);

    setOpacity(0.5);

    display = Font.font(60);

    label = new Label();
    label.setFont(display);
    label.setVisible(false);

    name = new Label();
    name.setFont(display);
    name.setVisible(false);

    VBox.setVgrow(name, Priority.ALWAYS);
    VBox.setVgrow(label, Priority.ALWAYS);

    getChildren().addAll(name, label);

    initMediator();
  }

  private void initMediator() {
    EntrainerMediator.getInstance().addReceiver(new ReceiverAdapter(this) {

      @Override
      protected void processReceiverChangeEvent(ReceiverChangeEvent e) {
        switch (e.getParm()) {
        case START_ENTRAINMENT:
          if (!program.get()) return;
          JFXUtils.runLater(() -> startStopDisplay(e.getBooleanValue()));
          break;
        case PROGRAM_END_TIME_SECONDS:
          setEndTime((int) e.getDoubleValue());
          break;
        case CLEAR_PROGRAM:
          setEndTime(0);
          break;
        case PAUSE_CLICKED:
          JFXUtils.runLater(() -> pauseResumeDisplay(e.getBooleanValue()));
          break;
        case PROGRAM_NAME:
          JFXUtils.runLater(() -> name.setText(e.getStringValue()));
          break;
        default:
          break;
        }
      }
    });
  }

  private void pauseResumeDisplay(boolean b) {
    if (b) {
      tl.pause();
    } else {
      tl.play();
    }
  }

  private void startStopDisplay(boolean b) {
    if (!b) {
      tl.stop();
      setDisplay(true);
      return;
    }

    tl = new Timeline();
    int endValue = minutes * 60 + seconds;
    tl.getKeyFrames().add(new KeyFrame(Duration.seconds(endValue), new KeyValue(totalSeconds, endValue)));
    totalSeconds.addListener(new ChangeListener<Number>() {

      @Override
      public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        setCurrent();
        JFXUtils.runLater(() -> setLabelText());
      }
    });

    tl.play();
  }

  private void setEndTime(int seconds) {
    program.set(seconds > 0);

    minutes = seconds / 60;
    this.seconds = seconds % 60;

    JFXUtils.runLater(() -> setDisplay(program.get()));
  }

  private void setDisplay(boolean b) {
    label.setVisible(b);
    name.setVisible(b);
    if (!b) return;

    totalSeconds.set(0);

    setCurrent();

    setLabelText();
  }

  private void setCurrent() {
    currentMinutes = totalSeconds.get() / 60;
    currentSeconds = totalSeconds.get() % 60;
  }

  //@formatter:off
  private void setLabelText() {
    label.setText(
        df.format(currentMinutes) + 
        ":" + 
        df.format(currentSeconds) + 
        "/" +
        df.format(minutes) +
        ":" + 
        df.format(seconds));
  }
  //@formatter:on

}
