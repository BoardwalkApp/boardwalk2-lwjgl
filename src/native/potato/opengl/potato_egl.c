#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <stdbool.h>
#include "potatobridge.h"

typedef bool EGLBoolean;
typedef void* EGLDisplay;
typedef void* EGLSurface;
typedef void* EGLContext;

static struct PotatoBridge* potatoBridge;
static EGLBoolean (*real_eglMakeCurrent)(EGLDisplay dpy, EGLSurface draw, EGLSurface read, EGLContext ctx);
static EGLBoolean (*real_eglSwapBuffers)(EGLDisplay dpy, EGLSurface draw);

#define GET_REAL(name) real_##name = potatoBridge->dlsym(eglhandle, #name)

static void setupBridge() {
	sscanf(getenv("POTATO_BRIDGE"), "%p", &potatoBridge);
	void* eglhandle = potatoBridge->dlopen("libEGL.so", 1 /* RTLD_LAZY */);
	GET_REAL(eglMakeCurrent);
	GET_REAL(eglSwapBuffers);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_PotatoEGL_init
  (JNIEnv *env, jclass clazz) {
	setupBridge();
}
JNIEXPORT jlong JNICALL Java_org_lwjgl_opengl_PotatoEGL_getAndroidContext
  (JNIEnv *env, jclass clazz) {
	return (jlong) potatoBridge->eglContext;
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_opengl_PotatoEGL_getAndroidReadSurface
  (JNIEnv *env, jclass clazz) {
	return (jlong) potatoBridge->eglReadSurface;
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_opengl_PotatoEGL_getAndroidDrawSurface
  (JNIEnv *env, jclass clazz) {
	return (jlong) potatoBridge->eglDrawSurface;
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_opengl_PotatoEGL_getAndroidDisplay
  (JNIEnv *env, jclass clazz) {
	return (jlong) potatoBridge->eglDisplay;
}

JNIEXPORT jboolean JNICALL Java_org_lwjgl_opengl_PotatoEGL_eglSwapBuffers
  (JNIEnv *env, jclass clazz, jlong display, jlong surface) {
	return real_eglSwapBuffers((EGLDisplay) display, (EGLSurface) surface);
}

JNIEXPORT jboolean JNICALL Java_org_lwjgl_opengl_PotatoEGL_eglMakeCurrent
  (JNIEnv *env, jclass clazz, jlong display, jlong draw, jlong read, jlong context) {
	return real_eglMakeCurrent((EGLDisplay) display, (EGLSurface) draw, (EGLSurface) read, (EGLContext) context);
}
