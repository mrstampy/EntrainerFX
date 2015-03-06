/*
 * Copyright (C) 2008 - 2015 Burton Alexander
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
package net.sourceforge.entrainer.socket;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.concurrent.locks.ReentrantLock;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class EntrainerStateMessageMarshal.
 */
public class EntrainerStateMessageMarshal {
	private static final Logger log = Logger.getLogger(EntrainerStateMessageMarshal.class);

	private Marshaller marshal;
	private Unmarshaller unmarshal;

	private ReentrantLock marshalLock = new ReentrantLock(true);
	private ReentrantLock unmarshalLock = new ReentrantLock(true);

	/**
	 * Instantiates a new entrainer state message marshal.
	 */
	public EntrainerStateMessageMarshal() {
		try {
			JAXBContext context = JAXBContext.newInstance(EntrainerStateMessage.class);
			unmarshal = context.createUnmarshaller();
			marshal = context.createMarshaller();
			marshal.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshal.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Marshal message.
	 *
	 * @param message
	 *          the message
	 * @return the string
	 * @throws JAXBException
	 *           the JAXB exception
	 */
	public String marshalMessage(EntrainerStateMessage message) throws JAXBException {
		marshalLock.lock();
		try {
			StringWriter writer = new StringWriter();

			marshal.marshal(message, writer);

			return writer.toString();
		} finally {
			marshalLock.unlock();
		}
	}

	/**
	 * Unmarshal.
	 *
	 * @param message
	 *          the message
	 * @return the entrainer state message
	 * @throws JAXBException
	 *           the JAXB exception
	 */
	public EntrainerStateMessage unmarshal(Object message) throws JAXBException {
		unmarshalLock.lock();
		try {
			if (!(message instanceof String)) {
				log.error(message + " is not a string");
				return null;
			}

			StringReader reader = new StringReader((String) message);

			return (EntrainerStateMessage) unmarshal.unmarshal(reader);
		} finally {
			unmarshalLock.unlock();
		}
	}

}
