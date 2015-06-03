package org.lwjgl.opengl;

public class PotatoEGL {
	public static native long getAndroidDisplay();
	public static native long getAndroidReadSurface();
	public static native long getAndroidDrawSurface();
	public static native long getAndroidContext();
	public static native boolean eglMakeCurrent(long display, long drawSurface, long readSurface, long context);
	public static native boolean eglSwapBuffers(long display, long drawSurface);
	public static native long init();
	static {
		init();
	}
}
