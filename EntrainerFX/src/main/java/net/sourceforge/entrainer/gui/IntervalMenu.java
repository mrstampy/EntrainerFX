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
package net.sourceforge.entrainer.gui;

import static net.sourceforge.entrainer.gui.IntervalMenuConstants.IMC_ADD_MENU_NAME;
import static net.sourceforge.entrainer.gui.IntervalMenuConstants.IMC_CUSTOM_MENU_NAME;
import static net.sourceforge.entrainer.gui.IntervalMenuConstants.IMC_DELETE_MENU_NAME;
import static net.sourceforge.entrainer.gui.IntervalMenuConstants.IMC_EIGHTH;
import static net.sourceforge.entrainer.gui.IntervalMenuConstants.IMC_ELEVENTH;
import static net.sourceforge.entrainer.gui.IntervalMenuConstants.IMC_FIFTH;
import static net.sourceforge.entrainer.gui.IntervalMenuConstants.IMC_FIRST;
import static net.sourceforge.entrainer.gui.IntervalMenuConstants.IMC_FOURTH;
import static net.sourceforge.entrainer.gui.IntervalMenuConstants.IMC_GOLDEN;
import static net.sourceforge.entrainer.gui.IntervalMenuConstants.IMC_GOLDEN_INVERSE;
import static net.sourceforge.entrainer.gui.IntervalMenuConstants.IMC_INTERVALS_MENU_NAME;
import static net.sourceforge.entrainer.gui.IntervalMenuConstants.IMC_NINTH;
import static net.sourceforge.entrainer.gui.IntervalMenuConstants.IMC_OCTAVE;
import static net.sourceforge.entrainer.gui.IntervalMenuConstants.IMC_REMOVE_MENU_NAME;
import static net.sourceforge.entrainer.gui.IntervalMenuConstants.IMC_SECOND;
import static net.sourceforge.entrainer.gui.IntervalMenuConstants.IMC_SEVENTH;
import static net.sourceforge.entrainer.gui.IntervalMenuConstants.IMC_SIXTH;
import static net.sourceforge.entrainer.gui.IntervalMenuConstants.IMC_TENTH;
import static net.sourceforge.entrainer.gui.IntervalMenuConstants.IMC_THIRD;
import static net.sourceforge.entrainer.mediator.MediatorConstants.CUSTOM_INTERVAL_ADD;
import static net.sourceforge.entrainer.mediator.MediatorConstants.CUSTOM_INTERVAL_REMOVE;
import static net.sourceforge.entrainer.mediator.MediatorConstants.INTERVAL_ADD;
import static net.sourceforge.entrainer.mediator.MediatorConstants.INTERVAL_REMOVE;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import net.sourceforge.entrainer.gui.jfx.JFXUtils;
import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.MediatorConstants;
import net.sourceforge.entrainer.mediator.ReceiverAdapter;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;
import net.sourceforge.entrainer.mediator.Sender;
import net.sourceforge.entrainer.mediator.SenderAdapter;
import net.sourceforge.entrainer.sound.AbstractSoundInterval;
import net.sourceforge.entrainer.xml.Settings;
import net.sourceforge.entrainer.xml.program.EntrainerProgramInterval;

// TODO: Auto-generated Javadoc
/**
 * This class allows intervals to be added to the base frequency. The chromatic
 * scale and Golden Ratios are included, and custom intervals can be added via
 * the {@link CustomInterval} dialog. All custom intervals are saved between
 * sessions, and can be deleted.
 * 
 * @author burton
 */
public class IntervalMenu extends Menu {

  /** The add. */
  protected Menu add;

  /** The remove. */
  protected Menu remove;

  /** The delete. */
  protected Menu delete = new Menu(IMC_DELETE_MENU_NAME);

  private Sender sender;

  /**
   * Instantiates a new interval menu.
   */
  public IntervalMenu() {
    super(IMC_INTERVALS_MENU_NAME);
    setAccelerator(new KeyCodeCombination(KeyCode.V, KeyCodeCombination.CONTROL_DOWN));
    init();
  }

  /**
   * Gets the loaded intervals.
   *
   * @return the loaded intervals
   */
  public List<EntrainerProgramInterval> getLoadedIntervals() {
    List<EntrainerProgramInterval> loaded = new ArrayList<EntrainerProgramInterval>();
    List<MenuItem> comps = remove.getItems();
    for (MenuItem jmi : comps) {
      EntrainerProgramInterval interval = new EntrainerProgramInterval(jmi.getText());
      loaded.add(interval);
    }

    return loaded;
  }

  /**
   * Gets the custom intervals.
   *
   * @return the custom intervals
   */
  public List<EntrainerProgramInterval> getCustomIntervals() {
    List<EntrainerProgramInterval> custom = new ArrayList<EntrainerProgramInterval>();
    custom.addAll(getCustom());

    return custom;
  }

  /**
   * Load custom intervals.
   */
  public void loadCustomIntervals() {
    List<EntrainerProgramInterval> custom = Settings.getInstance().getCustomIntervals();
    for (EntrainerProgramInterval s : custom) {
      if (!containsInterval(s.getValue())) {
        addInOrder(add, createMenuItem(s.getValue()));
        addInOrder(delete, createDeleteItem(s.getValue()));
      }
    }
  }

  private List<EntrainerProgramInterval> getCustom() {
    List<EntrainerProgramInterval> custom = new ArrayList<EntrainerProgramInterval>();
    List<MenuItem> comps = delete.getItems();
    for (MenuItem item : comps) {
      EntrainerProgramInterval i = new EntrainerProgramInterval(item.getText());
      custom.add(i);
    }
    return custom;
  }

  /**
   * Removes the all intervals.
   *
   * @return the list
   */
  public List<String> removeAllIntervals() {
    List<String> loaded = new ArrayList<String>();
    List<MenuItem> comps = new ArrayList<>(remove.getItems());
    for (MenuItem jmi : comps) {
      removeInterval(jmi);
      loaded.add(jmi.getText());
    }

    return loaded;
  }

  /**
   * Load intervals.
   *
   * @param toLoad
   *          the to load
   */
  public void loadIntervals(List<String> toLoad) {
    removeAllIntervals();
    for (String s : toLoad) {
      if (!containsInterval(remove, s)) {
        MenuItem item = getInterval(add, s);
        if (item != null) {
          addInterval(item);
        } else {
          addInOrder(add, createMenuItem(s));
          addInOrder(delete, createDeleteItem(s));
          fireIntervalEvent(s, CUSTOM_INTERVAL_ADD);
        }
      }
    }
  }

  private void init() {
    getItems().add(getAddMenu());
    getItems().add(getRemoveMenu());
    getItems().add(delete);
    getItems().add(getCustomMenu());
    initImpl();
  }

  /**
   * Inits the impl.
   */
  protected void initImpl() {
    loadCustomIntervals();
    sender = new SenderAdapter();
    EntrainerMediator.getInstance().addSender(sender);

    EntrainerMediator.getInstance().addReceiver(new ReceiverAdapter(this) {

      @Override
      protected void processReceiverChangeEvent(ReceiverChangeEvent e) {
        MenuItem item;
        switch (e.getParm()) {
        case INTERVAL_ADD:
          item = getAddItem(e.getStringValue());
          if (item == null) {
            item = getRemoveItem(e.getStringValue());
            if (item == null) {
              addInOrder(remove, createMenuItem(e.getStringValue()));
              addInOrder(delete, createDeleteItem(e.getStringValue()));
              fireIntervalEvent(e.getStringValue(), CUSTOM_INTERVAL_ADD);
            }
          } else {
            final MenuItem adder = item;
            JFXUtils.runLater(() -> addInterval(adder));
          }
          break;
        case INTERVAL_REMOVE:
          item = getRemoveItem(e.getStringValue());

          if (item == null) break;

          removeInterval(item);
          addInOrder(add, item);

          break;
        default:
          break;
        }
      }

    });
  }

  /**
   * Clear mediator objects.
   */
  public void clearMediatorObjects() {
    EntrainerMediator.getInstance().removeReceiver(this);
    EntrainerMediator.getInstance().removeSender(sender);
  }

  private MenuItem getCustomMenu() {
    MenuItem menu = new MenuItem(IMC_CUSTOM_MENU_NAME);
    menu.setOnAction(e -> showCustomDialog());

    return menu;
  }

  private void showCustomDialog() {
    CustomInterval ci = new CustomInterval(this);

    Dialog<ButtonType> dci = new Dialog<>();
    dci.setTitle("Custom Interval");
    dci.setDialogPane(ci);
    dci.setResizable(false);
    dci.initOwner(EntrainerFX.getInstance().getStage());

    Optional<ButtonType> bt = dci.showAndWait();

    if (bt.isPresent() && bt.get() == ButtonType.OK && ci.validated()) {
      String displayString = ci.getDisplayString();
      if (!containsInterval(displayString)) {
        addInOrder(add, createMenuItem(displayString));
        addInOrder(delete, createDeleteItem(displayString));
        fireIntervalEvent(displayString, CUSTOM_INTERVAL_ADD);
      }
    }
  }

  /**
   * Contains interval.
   *
   * @param displayString
   *          the display string
   * @return true, if successful
   */
  protected boolean containsInterval(String displayString) {
    return containsInterval(add, displayString) || containsInterval(remove, displayString);
  }

  /**
   * Contains interval.
   *
   * @param menu
   *          the menu
   * @param displayString
   *          the display string
   * @return true, if successful
   */
  protected boolean containsInterval(Menu menu, String displayString) {
    return getInterval(menu, displayString) != null;
  }

  /**
   * Gets the interval.
   *
   * @param menu
   *          the menu
   * @param displayString
   *          the display string
   * @return the interval
   */
  protected MenuItem getInterval(Menu menu, String displayString) {
    List<MenuItem> comps = menu.getItems();
    for (MenuItem jmi : comps) {
      if (jmi.getText().equals(displayString) || areEquivalentFractions(jmi.getText(), displayString)) {
        return jmi;
      }
    }

    return null;
  }

  private boolean areEquivalentFractions(String s1, String s2) {
    return AbstractSoundInterval.getInterval(s1) == AbstractSoundInterval.getInterval(s2);
  }

  /**
   * Checks if is equivalent fraction.
   *
   * @param s
   *          the s
   * @return true, if is equivalent fraction
   */
  public boolean isEquivalentFraction(String s) {
    return isEquivalentFraction(add, s) || isEquivalentFraction(remove, s);
  }

  private boolean isEquivalentFraction(Menu menu, String s) {
    List<MenuItem> comps = menu.getItems();
    for (MenuItem jmi : comps) {
      if (areEquivalentFractions(jmi.getText(), s)) {
        return true;
      }
    }

    return false;
  }

  private Menu getRemoveMenu() {
    remove = new Menu(IMC_REMOVE_MENU_NAME);

    return remove;
  }

  private Menu getAddMenu() {
    add = new Menu(IMC_ADD_MENU_NAME);

    addInOrder(add, createMenuItem(IMC_FIRST));
    addInOrder(add, createMenuItem(IMC_SECOND));
    addInOrder(add, createMenuItem(IMC_THIRD));
    addInOrder(add, createMenuItem(IMC_FOURTH));
    addInOrder(add, createMenuItem(IMC_FIFTH));
    addInOrder(add, createMenuItem(IMC_SIXTH));
    addInOrder(add, createMenuItem(IMC_SEVENTH));
    addInOrder(add, createMenuItem(IMC_EIGHTH));
    addInOrder(add, createMenuItem(IMC_NINTH));
    addInOrder(add, createMenuItem(IMC_TENTH));
    addInOrder(add, createMenuItem(IMC_ELEVENTH));
    addInOrder(add, createMenuItem(IMC_OCTAVE));
    addInOrder(add, createMenuItem(IMC_GOLDEN));
    addInOrder(add, createMenuItem(IMC_GOLDEN_INVERSE));

    return add;
  }

  /**
   * Creates the menu item.
   *
   * @param interval
   *          the interval
   * @return the j menu item
   */
  protected MenuItem createMenuItem(String interval) {
    final MenuItem item = new MenuItem(interval);

    item.setOnAction(e -> addOrRemove(item));

    return item;
  }

  /**
   * Creates the delete item.
   *
   * @param interval
   *          the interval
   * @return the j menu item
   */
  protected MenuItem createDeleteItem(String interval) {
    final MenuItem item = new MenuItem(interval);

    item.setOnAction(e -> deleteItem(item));

    return item;
  }

  private void deleteItem(MenuItem item) {
    Alert alert = new Alert(AlertType.CONFIRMATION, "Deleting " + item.getText() + ". Continue?", ButtonType.OK,
        ButtonType.CANCEL);
    alert.setTitle("Delete Interval");
    alert.initOwner(EntrainerFX.getInstance().getStage());

    Optional<ButtonType> button = alert.showAndWait();
    if (button.isPresent() && button.get() == ButtonType.OK) {
      deleteItem(add, item.getText());
      deleteItem(remove, item.getText());
      delete.getItems().remove(item);
      fireIntervalEvent(item.getText(), CUSTOM_INTERVAL_REMOVE);
      itemRemoved(item);
    }
  }

  private void deleteItem(Menu menu, String s) {
    List<MenuItem> comps = menu.getItems();
    MenuItem del = null;
    for (MenuItem jmi : comps) {
      if (jmi.getText().equals(s)) {
        del = jmi;
      }
    }

    if (del != null) comps.remove(del);
  }

  private boolean isItem(Menu menu, MenuItem item) {
    List<MenuItem> comps = menu.getItems();
    for (MenuItem c : comps) {
      if (c == item) {
        return true;
      }
    }

    return false;
  }

  private boolean isRemoveItem(MenuItem item) {
    return isItem(remove, item);
  }

  private boolean isAddItem(MenuItem item) {
    return isItem(add, item);
  }

  /**
   * Removes the interval.
   *
   * @param item
   *          the item
   */
  protected synchronized void removeInterval(MenuItem item) {
    remove.getItems().remove(item);
    addInOrder(add, item);
    itemRemoved(item);
  }

  /**
   * Adds the interval.
   *
   * @param item
   *          the item
   */
  protected synchronized void addInterval(MenuItem item) {
    add.getItems().remove(item);
    addInOrder(remove, item);
    itemAdded(item);
  }

  /**
   * Gets the adds the item.
   *
   * @param s
   *          the s
   * @return the adds the item
   */
  protected MenuItem getAddItem(String s) {
    return getInterval(add, s);
  }

  /**
   * Gets the removes the item.
   *
   * @param s
   *          the s
   * @return the removes the item
   */
  protected MenuItem getRemoveItem(String s) {
    return getInterval(remove, s);
  }

  /**
   * Item added.
   *
   * @param item
   *          the item
   */
  protected void itemAdded(MenuItem item) {
    fireIntervalEvent(item.getText(), INTERVAL_ADD);
  }

  /**
   * Item removed.
   *
   * @param item
   *          the item
   */
  protected void itemRemoved(MenuItem item) {
    fireIntervalEvent(item.getText(), INTERVAL_REMOVE);
  }

  private void fireIntervalEvent(String interval, MediatorConstants parm) {
    ReceiverChangeEvent e = new ReceiverChangeEvent(this, interval, parm);
    sender.fireReceiverChangeEvent(e);
  }

  private void addInOrder(Menu menu, MenuItem item) {
    List<MenuItem> comps = menu.getItems();
    int i = 0;
    for (MenuItem jmi : comps) {
      if (isGreaterThan(jmi.getText(), item.getText())) {
        break;
      }
      i++;
    }

    if (i < comps.size()) {
      menu.getItems().add(i, item);
    } else {
      menu.getItems().add(item);
    }
  }

  private boolean isGreaterThan(String existing, String current) {
    int intExisting = getNumerator(existing);
    int intCurrent = getNumerator(current);

    return intExisting > intCurrent;
  }

  private int getNumerator(String s) {
    return AbstractSoundInterval.getNumerator(s);
  }

  private void addOrRemove(final MenuItem item) {
    if (isAddItem(item)) {
      addInterval(item);
    } else if (isRemoveItem(item)) {
      removeInterval(item);
    }
  }

}
