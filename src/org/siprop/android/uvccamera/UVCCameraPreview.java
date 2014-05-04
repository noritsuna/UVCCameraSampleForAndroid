package org.siprop.android.uvccamera;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

class UVCCameraPreview extends SurfaceView implements SurfaceHolder.Callback, Runnable {

	private static final boolean DEBUG = true;
	private static final String TAG="UVCCamera";
	protected Context context;
	private SurfaceHolder holder;
    Thread mainLoop = null;
	private Bitmap bmp=null;

	private boolean cameraExists=false;
	private boolean shouldStop=false;
	
	// /dev/videoX with Required 666 permission
	private int cameraId=0;
	
	static final int IMG_WIDTH=320;
	static final int IMG_HEIGHT=240;

	// The following variables are used to draw camera images.
    private int winWidth=0;
    private int winHeight=0;
    private Rect rect;
    private int dw, dh;
    private float rate;
  
    // JNI functions
    public native int prepareCamera(int videoid);
    public native void processCamera();
    public native void stopCamera();
    public native void pixeltobmp(Bitmap bitmap);
    static {
        System.loadLibrary("UVCCamera");
    }
    
	public UVCCameraPreview(Context context) {
		super(context);
		this.context = context;
		if(DEBUG) Log.d(TAG,"CameraPreview constructed");
		setFocusable(true);
		
		holder = getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);	
		
		if(bmp==null){
			bmp = Bitmap.createBitmap(IMG_WIDTH, IMG_HEIGHT, Bitmap.Config.ARGB_8888);
		}
		// /dev/videoX
		int ret = prepareCamera(cameraId);
		
		if(ret!=-1) cameraExists = true;
		
        mainLoop = new Thread(this);
        mainLoop.start();		
	}

	public UVCCameraPreview(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		setFocusable(true);
		
		holder = getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);	
		
		if(bmp==null){
			bmp = Bitmap.createBitmap(IMG_WIDTH, IMG_HEIGHT, Bitmap.Config.ARGB_8888);
		}
		// /dev/videoX
		int ret = prepareCamera(cameraId);
		
		if(ret!=-1) cameraExists = true;
		
        mainLoop = new Thread(this);
        mainLoop.start();		
	}
	
    @Override
    public void run() {
        while (true && cameraExists) {
			rect = new Rect(0, 0, 640, 480);
        	
        	processCamera();

            //Get Bitmap Image from UVC Camera
        	pixeltobmp(bmp);
        	
            Canvas canvas = getHolder().lockCanvas();
            if (canvas != null)
            {
            	// draw camera bmp on canvas
            	canvas.drawBitmap(bmp,null,rect,null);

            	getHolder().unlockCanvasAndPost(canvas);
            }

            if(shouldStop){
            	shouldStop = false;  
            	break;
            }	        
        }
    }

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if(cameraExists){
			shouldStop = true;
			while(shouldStop){
				try{ 
					Thread.sleep(100); // wait for thread stopping
				}catch(Exception e){}
			}
		}
		stopCamera();
	}   
}
