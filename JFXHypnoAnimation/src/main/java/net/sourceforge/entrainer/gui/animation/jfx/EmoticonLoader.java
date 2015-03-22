/*
 * Copyright (C) 2008 - 2013 Burton Alexander
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
package net.sourceforge.entrainer.gui.animation.jfx;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javafx.scene.image.Image;

/**
 * Emoticon pngs from http://openiconlibrary.sourceforge.net/
 * @author burton
 *
 */
public class EmoticonLoader {

  private List<Image> files = new ArrayList<Image>();

  private Random rand = new Random(System.currentTimeMillis());

  public EmoticonLoader() throws IOException, URISyntaxException {
    loadIcons();
  }

  public Image getRandomEmoticon() {
    int idx = rand.nextInt(files.size());

    return files.get(idx);
  }

  private void loadIcons() throws IOException, URISyntaxException {
    CodeSource src = EmoticonLoader.class.getProtectionDomain().getCodeSource();
    ZipFile jarFile = null;
    try {
      if (src != null) {
        URL jar = src.getLocation();
        jarFile = new ZipFile(new File(jar.toURI()));
        ZipInputStream zip = new ZipInputStream(jar.openStream());
        ZipEntry ze = null;

        while ((ze = zip.getNextEntry()) != null) {
          String entryName = ze.getName();
          if (entryName.endsWith(".png")) {
            files.add(new Image(jarFile.getInputStream(ze)));
          }
        }
      }
    } finally {
      if (jarFile != null) jarFile.close();
    }
  }

}
