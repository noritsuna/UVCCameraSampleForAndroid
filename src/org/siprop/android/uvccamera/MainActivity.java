package org.siprop.android.uvccamera;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.view.Menu;

public class MainActivity extends Activity {

	private UVCCameraPreview uvcCameraView;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		

        FrameLayout previewCamera = (FrameLayout)findViewById(R.id.camera);
        uvcCameraView = new UVCCameraPreview(this);
        previewCamera.addView(uvcCameraView);
	}
	
	

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
