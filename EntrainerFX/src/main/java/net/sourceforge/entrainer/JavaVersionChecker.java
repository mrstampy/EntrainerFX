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

import java.text.DecimalFormat;
import java.util.StringTokenizer;

// TODO: Auto-generated Javadoc
/**
 * The Class JavaVersionChecker.
 */
public class JavaVersionChecker {

	/** The Constant MIN_VERSION. */
	public static final JavaVersion MIN_VERSION = new JavaVersion(1, 8, 0, 40);
	
	/** The Constant CURRENT. */
	public static final JavaVersion CURRENT;

	/** The Constant VERSION_OK. */
	public static final boolean VERSION_OK;

	static {
		CURRENT = JavaVersion.fromCurrent();

		VERSION_OK = CURRENT.isOk(MIN_VERSION);
	}

	/**
	 * The Class JavaVersion.
	 */
	public static class JavaVersion {
		private DecimalFormat releaseFormat = new DecimalFormat("00");
		
		/** The major. */
		public int major;
		
		/** The minor. */
		public int minor;
		
		/** The milli. */
		public int milli;
		
		/** The release. */
		public int release;

		/**
		 * Instantiates a new java version.
		 *
		 * @param major the major
		 * @param minor the minor
		 * @param milli the milli
		 * @param release the release
		 */
		public JavaVersion(int major, int minor, int milli, int release) {
			this.major = major;
			this.minor = minor;
			this.milli = milli;
			this.release = release;
		}

		/**
		 * Checks if is ok.
		 *
		 * @param jv the jv
		 * @return true, if is ok
		 */
		public boolean isOk(JavaVersion jv) {
			if(jv.major > major) return false;
			if(jv.major < major) return true;
			if(jv.minor > minor) return false;
			if(jv.minor < minor) return true;
			if(jv.milli > milli) return false;
			if(jv.milli < milli) return true;
			if(jv.release > release) return false;
			
			return true;
		}

		/**
		 * From current.
		 *
		 * @return the java version
		 */
		public static JavaVersion fromCurrent() {
			StringTokenizer toker = new StringTokenizer(System.getProperty("java.version"), ".");

			int major = Integer.parseInt(toker.nextToken());
			int minor = Integer.parseInt(toker.nextToken());

			StringTokenizer tokee = new StringTokenizer(toker.nextToken(), "_");

			int milli = Integer.parseInt(tokee.nextToken());
			int release = tokee.hasMoreTokens() ? Integer.parseInt(tokee.nextToken()) : 0;

			return new JavaVersion(major, minor, milli, release);
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return major + "." + minor + "." + milli + "_" + releaseFormat.format(release);
		}
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		System.out.println(VERSION_OK);
	}
}
