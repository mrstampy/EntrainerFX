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
package net.sourceforge.entrainer.esp;

import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.sourceforge.entrainer.mediator.EntrainerMediator;
import net.sourceforge.entrainer.mediator.MediatorConstants;
import net.sourceforge.entrainer.mediator.ReceiverChangeEvent;
import net.sourceforge.entrainer.mediator.Sender;
import net.sourceforge.entrainer.mediator.SenderAdapter;
import net.sourceforge.entrainer.util.EntrainerRegister;
import net.sourceforge.entrainer.util.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mrstampy.esp.dsp.lab.RawEspConnection;

// TODO: Auto-generated Javadoc
/**
 * The Class EspConnectionLoader.
 */
public class EspConnectionLoader {
	private static final Logger log = LoggerFactory.getLogger(EspConnectionLoader.class);

	/** The Constant ESP_DIR. */
	public static final String ESP_DIR = "esp";

	private List<RawEspConnection> connections = Collections.synchronizedList(new ArrayList<>());
	private List<Path> jarFiles = Collections.synchronizedList(new ArrayList<Path>());
	private Timer timer = new Timer("Raw Esp Connection Watch Timer", true);

	private Sender sender = new SenderAdapter();

	/**
	 * Instantiates a new esp connection loader.
	 */
	public EspConnectionLoader() {
		EntrainerMediator.getInstance().addSender(sender);
	}

	/**
	 * Gets the esp connections.
	 *
	 * @return the esp connections
	 */
	public List<RawEspConnection> getEspConnections() {
		return connections;
	}

	/**
	 * Checks if is empty.
	 *
	 * @return true, if is empty
	 */
	public boolean isEmpty() {
		return connections.isEmpty();
	}

	/**
	 * Load all connections.
	 */
	void loadAllConnections() {
		log.debug("Loading ESP Connections");

		timer.schedule(getTimerTask(Paths.get(ESP_DIR)), 0);

		List<Path> jarPaths = EntrainerRegister.getJarFilesInDirectory(ESP_DIR);

		jarPaths = getNewJarPaths(jarPaths);

		if (jarPaths.isEmpty()) {
			log.debug("No new ESP Connections to load");
			return;
		}

		List<URL> jarUrls = new ArrayList<>();

		try {
			for (Path path : jarPaths) {
				log.debug("Loading " + path);
				jarUrls.add(path.toUri().toURL());
			}

			EntrainerRegister.loadClasses(jarUrls, RawEspConnection.class, connections);
			sender.fireReceiverChangeEvent(new ReceiverChangeEvent(this, true, MediatorConstants.ESP_CONNECTIONS_RELOADED));
		} catch (Exception e) {
			log.error("Unexpected exception", e);
		}
	}

	private TimerTask getTimerTask(final Path directory) {
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				try {
					WatchKey key = directory.register(getWatchService(),
							StandardWatchEventKinds.ENTRY_CREATE,
							StandardWatchEventKinds.ENTRY_DELETE,
							StandardWatchEventKinds.ENTRY_MODIFY);

					List<WatchEvent<?>> events = key.pollEvents();
					while (events.isEmpty()) {
						log.debug("No changes to esp directory");
						Utils.snooze(5000);
						events = key.pollEvents();
					}

					boolean shouldLoad = false;
					for (WatchEvent<?> event : events) {
						if (StandardWatchEventKinds.ENTRY_CREATE.equals(event.kind())
								|| StandardWatchEventKinds.ENTRY_MODIFY.equals(event.kind())) {
							shouldLoad = true;
						}
						if (StandardWatchEventKinds.ENTRY_DELETE.equals(event.kind())) {
							Path jarFile = (Path) event.context();
							shouldLoad = true;
							fireConnectionRemoved(jarFile);
						}
					}

					if (shouldLoad) loadAllConnections();
				} catch (Throwable e) {
					log.error("Unexpected watch problem: ", e);
				}
			}
		};

		return task;
	}

	private void fireConnectionRemoved(Path jarFile) {
		// TODO Auto-generated method stub
	}

	private WatchService getWatchService() throws IOException {
		FileSystem system = FileSystems.getDefault();
		return system.newWatchService();
	}

	@SuppressWarnings("unchecked")
	private List<Path> getNewJarPaths(List<Path> jarPaths) {
		if (jarPaths.isEmpty()) return Collections.EMPTY_LIST;
		List<Path> paths = new ArrayList<Path>();

		if (jarFiles.isEmpty()) {
			jarFiles.addAll(jarPaths);
			paths.addAll(jarPaths);
		} else {
			for (Path path : jarPaths) {
				if (!jarFiles.contains(path)) {
					jarFiles.add(path);

					paths.add(path);
				}
			}
		}

		return paths;
	}
}
