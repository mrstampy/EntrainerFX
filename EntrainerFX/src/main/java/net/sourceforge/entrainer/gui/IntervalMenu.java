/*
 * Copyright (C) 2008 - 2014 Burton Alexander
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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import net.sourceforge.entrainer.guitools.GuiUtil;
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
public class IntervalMenu extends JMenu {

	private static final long serialVersionUID = 1L;

	/** The add. */
	protected JMenu add;

	/** The remove. */
	protected JMenu remove;

	/** The delete. */
	protected JMenu delete = new JMenu(IMC_DELETE_MENU_NAME);

	private Sender sender;

	/**
	 * Instantiates a new interval menu.
	 */
	public IntervalMenu() {
		super(IMC_INTERVALS_MENU_NAME);
		setMnemonic(KeyEvent.VK_V);
		init();
	}

	/**
	 * Gets the loaded intervals.
	 *
	 * @return the loaded intervals
	 */
	public List<EntrainerProgramInterval> getLoadedIntervals() {
		List<EntrainerProgramInterval> loaded = new ArrayList<EntrainerProgramInterval>();
		Component[] comps = remove.getMenuComponents();
		JMenuItem jmi;
		for (Component c : comps) {
			jmi = (JMenuItem) c;
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
		Component[] comps = delete.getMenuComponents();
		JMenuItem item;
		for (Component c : comps) {
			item = (JMenuItem) c;
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
		Component[] comps = remove.getMenuComponents();
		JMenuItem jmi;
		for (Component c : comps) {
			jmi = (JMenuItem) c;
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
				JMenuItem item = getInterval(add, s);
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
		add(getAddMenu());
		add(getRemoveMenu());
		add(delete);
		add(getCustomMenu());
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
				JMenuItem item;
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
						final JMenuItem adder = item;
						SwingUtilities.invokeLater(new Runnable() {

							@Override
							public void run() {
								addInterval(adder);
							}
						});
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

	private JMenuItem getCustomMenu() {
		JMenuItem menu = new JMenuItem(IMC_CUSTOM_MENU_NAME);
		menu.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				showCustomDialog();
			}

		});

		return menu;
	}

	private void showCustomDialog() {
		final CustomInterval ci = new CustomInterval(EntrainerFX.getInstance(), this);

		ci.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				if (ci.isOk()) {
					String displayString = ci.getDisplayString();
					if (!containsInterval(displayString)) {
						addInOrder(add, createMenuItem(displayString));
						addInOrder(delete, createDeleteItem(displayString));
						fireIntervalEvent(displayString, CUSTOM_INTERVAL_ADD);
					}
				}
			}
		});

		GuiUtil.showDialog(ci);
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
	protected boolean containsInterval(JMenu menu, String displayString) {
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
	protected JMenuItem getInterval(JMenu menu, String displayString) {
		Component[] comps = menu.getMenuComponents();
		JMenuItem jmi;
		for (Component c : comps) {
			jmi = (JMenuItem) c;
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

	private boolean isEquivalentFraction(JMenu menu, String s) {
		Component[] comps = menu.getMenuComponents();
		JMenuItem jmi;
		for (Component c : comps) {
			jmi = (JMenuItem) c;
			if (areEquivalentFractions(jmi.getText(), s)) {
				return true;
			}
		}

		return false;
	}

	private JMenu getRemoveMenu() {
		remove = new JMenu(IMC_REMOVE_MENU_NAME);

		return remove;
	}

	private JMenu getAddMenu() {
		add = new JMenu(IMC_ADD_MENU_NAME);

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
	protected JMenuItem createMenuItem(String interval) {
		final JMenuItem item = new JMenuItem(interval);

		item.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				if (isAddItem(item)) {
					addInterval(item);
				} else if (isRemoveItem(item)) {
					removeInterval(item);
				}
			}

		});

		return item;
	}

	/**
	 * Creates the delete item.
	 *
	 * @param interval
	 *          the interval
	 * @return the j menu item
	 */
	protected JMenuItem createDeleteItem(String interval) {
		final JMenuItem item = new JMenuItem(interval);

		item.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				deleteItem(item);
			}

		});

		return item;
	}

	private void deleteItem(JMenuItem item) {
		int choice = JOptionPane.showConfirmDialog(EntrainerFX.getInstance(),
				"Deleting " + item.getText() + ". Continue?",
				"Delete Interval",
				JOptionPane.OK_CANCEL_OPTION);
		if (choice == JOptionPane.OK_OPTION) {
			deleteItem(add, item.getText());
			deleteItem(remove, item.getText());
			delete.remove(item);
			fireIntervalEvent(item.getText(), CUSTOM_INTERVAL_REMOVE);
			itemRemoved(item);
		}
	}

	private void deleteItem(JMenu menu, String s) {
		Component[] comps = menu.getMenuComponents();
		JMenuItem jmi;
		for (Component c : comps) {
			jmi = (JMenuItem) c;
			if (jmi.getText().equals(s)) {
				menu.remove(jmi);
			}
		}
	}

	private boolean isItem(JMenu menu, JMenuItem item) {
		Component[] comps = menu.getMenuComponents();
		for (Component c : comps) {
			if (c == item) {
				return true;
			}
		}

		return false;
	}

	private boolean isRemoveItem(JMenuItem item) {
		return isItem(remove, item);
	}

	private boolean isAddItem(JMenuItem item) {
		return isItem(add, item);
	}

	/**
	 * Removes the interval.
	 *
	 * @param item
	 *          the item
	 */
	protected synchronized void removeInterval(JMenuItem item) {
		remove.remove(item);
		addInOrder(add, item);
		itemRemoved(item);
	}

	/**
	 * Adds the interval.
	 *
	 * @param item
	 *          the item
	 */
	protected synchronized void addInterval(JMenuItem item) {
		add.remove(item);
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
	protected JMenuItem getAddItem(String s) {
		return getInterval(add, s);
	}

	/**
	 * Gets the removes the item.
	 *
	 * @param s
	 *          the s
	 * @return the removes the item
	 */
	protected JMenuItem getRemoveItem(String s) {
		return getInterval(remove, s);
	}

	/**
	 * Item added.
	 *
	 * @param item
	 *          the item
	 */
	protected void itemAdded(JMenuItem item) {
		fireIntervalEvent(item.getText(), INTERVAL_ADD);
	}

	/**
	 * Item removed.
	 *
	 * @param item
	 *          the item
	 */
	protected void itemRemoved(JMenuItem item) {
		fireIntervalEvent(item.getText(), INTERVAL_REMOVE);
	}

	private void fireIntervalEvent(String interval, MediatorConstants parm) {
		ReceiverChangeEvent e = new ReceiverChangeEvent(this, interval, parm);
		sender.fireReceiverChangeEvent(e);
	}

	private void addInOrder(JMenu menu, JMenuItem item) {
		Component[] comps = menu.getMenuComponents();
		JMenuItem jmi;
		int i = 0;
		for (Component c : comps) {
			jmi = (JMenuItem) c;
			if (isGreaterThan(jmi.getText(), item.getText())) {
				break;
			}
			i++;
		}

		if (i < comps.length) {
			menu.add(item, i);
		} else {
			menu.add(item);
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

}
