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
package net.sourceforge.entrainer.gui.laf;

import static net.sourceforge.entrainer.gui.laf.LAFConstants.SKINNABLE_LAF_CLASS_NAME;
import static net.sourceforge.entrainer.gui.laf.LAFConstants.SKIN_CLASS_NAME;

import java.awt.Component;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import net.sourceforge.entrainer.guitools.GuiUtil;
import net.sourceforge.entrainer.util.EntrainerClassLoader;
import net.sourceforge.entrainer.util.EntrainerRegister;

// TODO: Auto-generated Javadoc
/**
 * Code liberated from the Squirrel Sql Client
 * https://sourceforge.net/projects/squirrel-sql/
 * 
 * @author burton
 */
public class LAFRegister {

	private static EntrainerClassLoader loader;

	private static List<LookAndFeel> copa = new ArrayList<LookAndFeel>();

	private LAFRegister() {
		super();
	}

	/**
	 * Sets the look and feel.
	 *
	 * @param lafClass
	 *          the laf class
	 * @param components
	 *          the components
	 * @throws ClassNotFoundException
	 *           the class not found exception
	 * @throws IllegalAccessException
	 *           the illegal access exception
	 * @throws InstantiationException
	 *           the instantiation exception
	 * @throws UnsupportedLookAndFeelException
	 *           the unsupported look and feel exception
	 */
	public static void setLookAndFeel(String lafClass, Component... components) throws ClassNotFoundException,
			IllegalAccessException, InstantiationException, UnsupportedLookAndFeelException {
		setLookAndFeel(lafClass, null, components);
	}

	/**
	 * Sets the look and feel.
	 *
	 * @param lafClassName
	 *          the laf class name
	 * @param themePackName
	 *          the theme pack name
	 * @param components
	 *          the components
	 * @throws ClassNotFoundException
	 *           the class not found exception
	 * @throws IllegalAccessException
	 *           the illegal access exception
	 * @throws InstantiationException
	 *           the instantiation exception
	 * @throws UnsupportedLookAndFeelException
	 *           the unsupported look and feel exception
	 */
	public static void setLookAndFeel(String lafClassName, String themePackName, final Component[] components)
			throws ClassNotFoundException, IllegalAccessException, InstantiationException, UnsupportedLookAndFeelException {

		// If this is the Skin Look and Feel then load the current theme pack
		// and set the current skin.
		if (lafClassName.equals(SKINNABLE_LAF_CLASS_NAME)) {
			try {
				Class<?> skinLafClass = getLookAndFeel(lafClassName).getClass();
				Class<?> skinClass = loader.loadClass(SKIN_CLASS_NAME);
				Method loadThemePack = skinLafClass.getMethod("loadThemePack", new Class[] { String.class });
				Method setSkin = skinLafClass.getMethod("setSkin", new Class[] { skinClass });
				Object skin = loadThemePack.invoke(skinLafClass, new Object[] { themePackName });
				setSkin.invoke(skinLafClass, new Object[] { skin });
			} catch (Exception ex) {
				GuiUtil.handleProblem(ex);
			}
		}

		// Set Look and Feel and update the main frame to use it.
		UIManager.setLookAndFeel(getLookAndFeel(lafClassName));
		UIManager.getLookAndFeelDefaults().put("ClassLoader", loader);
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				for (Component c : components) {
					SwingUtilities.updateComponentTreeUI(c);
					c.validate();
				}
			}
		});
	}

	private static LookAndFeel getLookAndFeel(String lafClassName) {
		for (LookAndFeel laf : copa) {
			if (laf.getClass().getName().equals(lafClassName)) {
				return laf;
			}
		}

		return null;
	}

	/**
	 * Load all lafs.
	 *
	 * @throws URISyntaxException
	 *           the URI syntax exception
	 * @throws InstantiationException
	 *           the instantiation exception
	 * @throws IllegalAccessException
	 *           the illegal access exception
	 * @throws ClassNotFoundException
	 *           the class not found exception
	 */
	public static void loadAllLafs() throws URISyntaxException, InstantiationException, IllegalAccessException,
			ClassNotFoundException {

		List<URL> lafUrls = EntrainerRegister.getPackageUrls("lafs");

		loader = EntrainerRegister.loadClasses(lafUrls, LookAndFeel.class, copa);

		UIManager.LookAndFeelInfo[] defaults = UIManager.getInstalledLookAndFeels();
		installDefaultLafs(defaults);

		for (LookAndFeel laf : copa) {
			if (!isInstalled(laf, defaults)) {
				UIManager.installLookAndFeel(laf.getName(), laf.getClass().getName());
			}
		}

	}

	private static boolean isInstalled(LookAndFeel laf, LookAndFeelInfo[] defaults) {
		for (LookAndFeelInfo info : defaults) {
			if (info.getClassName().equals(laf.getClass().getName())) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private static void installDefaultLafs(UIManager.LookAndFeelInfo[] defaults) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		for (UIManager.LookAndFeelInfo info : defaults) {
			Class<LookAndFeel> laf = (Class<LookAndFeel>) loader.loadClass(info.getClassName());
			if (!copa.contains(laf)) {
				copa.add(laf.newInstance());
			}
		}
	}

}
