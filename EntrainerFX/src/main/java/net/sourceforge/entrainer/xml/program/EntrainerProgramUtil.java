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

import java.io.File;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

// TODO: Auto-generated Javadoc
/**
 * The Class EntrainerProgramUtil.
 */
public class EntrainerProgramUtil {

  private static Marshaller marshaller;
  private static Unmarshaller unmarshaller;

  static {
    try {
      JAXBContext context = JAXBContext.newInstance(EntrainerProgram.class);
      marshaller = context.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      unmarshaller = context.createUnmarshaller();
    } catch (JAXBException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Unmarshal.
   *
   * @param fileName
   *          the file name
   * @return the entrainer program
   */
  public static EntrainerProgram unmarshal(String fileName) {
    File file = new File(fileName);

    return unmarshal(file);
  }

  /**
   * Unmarshal.
   *
   * @param file
   *          the file
   * @return the entrainer program
   */
  public static EntrainerProgram unmarshal(File file) {
    EntrainerProgram program;
    try {
      if (file != null && file.exists()) {
        program = (EntrainerProgram) unmarshaller.unmarshal(file);
      } else {
        program = new EntrainerProgram();
      }
      program.setFile(file);

      return program;
    } catch (JAXBException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Marshal.
   *
   * @param program
   *          the program
   * @param fileName
   *          the file name
   */
  public static void marshal(EntrainerProgram program, String fileName) {
    File file = new File(fileName);

    try {
      marshaller.marshal(program, file);
    } catch (JAXBException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Marshal.
   *
   * @param program
   *          the program
   * @return the string
   */
  public static String marshal(EntrainerProgram program) {
    StringWriter writer = new StringWriter();

    try {
      marshaller.marshal(program, writer);
      return writer.toString();
    } catch (JAXBException e) {
      throw new RuntimeException(e);
    }
  }
}
