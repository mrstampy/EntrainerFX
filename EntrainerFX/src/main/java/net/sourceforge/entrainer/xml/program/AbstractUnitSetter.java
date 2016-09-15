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
package net.sourceforge.entrainer.xml.program;

import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.MediatorConstants;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;
import net.sourceforge.entrainer.mediator.Sender;
import net.sourceforge.entrainer.mediator.SenderAdapter;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractUnitSetter.
 */
abstract class AbstractUnitSetter implements UnitSetter {
  private EntrainerProgramUnit unit;

  private Sender sender = new SenderAdapter();

  /**
   * Instantiates a new abstract unit setter.
   *
   * @param unit
   *          the unit
   */
  public AbstractUnitSetter(EntrainerProgramUnit unit) {
    super();
    setUnit(unit);
    initMediator();
  }

  private void initMediator() {
    EntrainerMediator.getInstance().addSender(sender);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.entrainer.xml.program.UnitSetter#getUnit()
   */
  public EntrainerProgramUnit getUnit() {
    return unit;
  }

  /**
   * Sets the unit.
   *
   * @param unit
   *          the new unit
   */
  public void setUnit(EntrainerProgramUnit unit) {
    this.unit = unit;
  }

  /**
   * Fire property change event.
   *
   * @param name
   *          the name
   * @param oldValue
   *          the old value
   * @param newValue
   *          the new value
   */
  protected void firePropertyChangeEvent(MediatorConstants name, double oldValue, double newValue) {
    ReceiverChangeEvent e = new ReceiverChangeEvent(this, newValue, name);
    sender.fireReceiverChangeEvent(e);
  }

}
