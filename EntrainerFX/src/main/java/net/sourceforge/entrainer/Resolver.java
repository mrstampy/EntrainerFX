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
package net.sourceforge.entrainer;

import java.io.File;
import java.io.FilenameFilter;
import java.util.concurrent.CountDownLatch;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import net.sourceforge.entrainer.esp.EspConnectionLoader;
import net.sourceforge.entrainer.gui.jfx.EntrainerFXResolveSplash;

import com.github.mrstampy.poisonivy.PoisonIvy;

// TODO: Auto-generated Javadoc
/**
 * The Class Resolver.
 */
public class Resolver implements Runnable {
	
	private String[] args = null;
	
	static {
		new JFXPanel();
	}
	
	/**
	 * Instantiates a new resolver.
	 *
	 * @param args the args
	 */
	Resolver(String... args) {
		this.args = args;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		execute(getEspResolver());
		execute(getResolver());
		moveLibUsb();
		System.exit(0);
	}
	
	// Fugly hack as the Jar spec doesn't handle wildcards in the classpath
	private void moveLibUsb() {
		File ivydir = new File("ivylib");
		
		File[] libusbs = ivydir.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith("libusb4java");
			}
		});
		
		if(libusbs.length == 1) {
			libusbs[0].renameTo(new File(ivydir, "libusb4java.jar"));
		} else {
			System.err.println("Cannot rename libusb4java: there are " + libusbs.length + " matching files");
		}
	}

	private void execute(PoisonIvy resolver) {
		try {
			showSplashScreen(resolver);
			resolver.execute();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	private PoisonIvy getResolver() {
		return args == null || args.length == 0 ? getDefaultResolver() : new PoisonIvy(args);
	}

	private PoisonIvy getDefaultResolver() {
		return new PoisonIvy("-ivy", "EntrainerFX.ivy.xml", "-f", "-mj", "EntrainerFX.jar");
	}
	
	private PoisonIvy getEspResolver() {
		File espDir = new File(EspConnectionLoader.ESP_DIR);
		if(!espDir.exists()) espDir.mkdir();
		
		return new PoisonIvy("-ivy", "EntrainerFX.ESP.ivy.xml", "-f", "-libdir", "./esp");
	}

	private void showSplashScreen(final PoisonIvy resolver) throws InterruptedException {
		final CountDownLatch cdl = new CountDownLatch(1);
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				resolver.addResolveListener(new EntrainerFXResolveSplash(true));
				cdl.countDown();
			}
		});
		
		cdl.await();
	}

}
