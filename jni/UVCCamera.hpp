#include <jni.h>
#include <android/log.h>
#include <android/bitmap.h>

#include <string.h>

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>

#include <fcntl.h>              /* low-level i/o */
#include <unistd.h>
#include <errno.h>
#include <malloc.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <sys/time.h>
#include <sys/mman.h>
#include <sys/ioctl.h>

#include <asm/types.h>          /* for videodev2.h */

#include <linux/videodev2.h>
#include "uvcvideo.h"
#include "video.h"

#define  LOG_TAG    "UVCCamera"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

#define CLEAR(x) memset (&(x), 0, sizeof (x))

#define IMG_WIDTH 320
#define IMG_HEIGHT 240

#define ERROR_LOCAL -1
#define SUCCESS_LOCAL 0

struct buffer {
        void *                  start;
        size_t                  length;
};

/**
 * For V4L
*/
static char            dev_name[16];
static int              fd              = -1;
struct buffer *         buffers         = NULL;
static unsigned int     n_buffers       = 0;


 /**
  * For yuyv_to_rgb24
  */
int *rgb = NULL;
int *ybuf = NULL;


#ifdef __cplusplus
extern "C" {
#endif

int errnoexit(const char *s);

int xioctl(int fd, int request, void *arg);

int opendevice(int videoid);
int initdevice(void);
int initmmap(void);
int startcapturing(void);

int readframeonce(void);
int readframe(void);

int stopcapturing(void);
int uninitdevice(void);
int closedevice(void);

void yuyv_to_rgb24 (unsigned char *src);

jint Java_org_siprop_android_uvccamera_UVCCameraPreview_prepareCamera( JNIEnv* env,jobject thiz, jint videoid);
jint Java_org_siprop_android_uvccamera_UVCCameraPreview_prepareCameraWithBase( JNIEnv* env,jobject thiz, jint videoid, jint videobase);
void Java_org_siprop_android_uvccamera_UVCCameraPreview_processCamera( JNIEnv* env,jobject thiz);
void Java_org_siprop_android_uvccamera_UVCCameraPreview_stopCamera(JNIEnv* env,jobject thiz);
void Java_org_siprop_android_uvccamera_UVCCameraPreview_pixeltobmp( JNIEnv* env,jobject thiz,jobject bitmap);

#ifdef __cplusplus
}
#endif

