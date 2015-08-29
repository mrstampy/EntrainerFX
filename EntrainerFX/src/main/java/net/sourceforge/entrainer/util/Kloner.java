package net.sourceforge.entrainer.util;

import com.rits.cloning.Cloner;

public class Kloner {

	private static Cloner cloner = new Cloner();
	
	public static <T> T klone(T o) {
		return cloner.deepClone(o);
	}
}
