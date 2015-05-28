package com.skhalid.hapity.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.skhalid.hapity.CameraPreview;
import com.skhalid.hapity.R;

import java.io.IOException;

public class VideoFragment extends Fragment {
	private Camera mCamera;
	private CameraPreview mPreview;
	private MediaRecorder mediaRecorder;
	private Button capture, switchCamera;
	private Context myContext;
	private LinearLayout cameraPreview;
	private boolean cameraFront = false;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.video_fragment, container, false);
		myContext = getActivity();
        cameraPreview = (LinearLayout) rootView.findViewById(R.id.camera_preview);

        mPreview = new CameraPreview(myContext, mCamera);
        cameraPreview.addView(mPreview);

		return rootView;

	}

	private int findFrontFacingCamera() {
		int cameraId = -1;
		// Search for the front facing camera
		int numberOfCameras = Camera.getNumberOfCameras();
		for (int i = 0; i < numberOfCameras; i++) {
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(i, info);
			if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
				cameraId = i;
				cameraFront = true;
				break;
			}
		}
		return cameraId;
	}

	private int findBackFacingCamera() {
		int cameraId = -1;
		// Search for the back facing camera
		// get the number of cameras
		int numberOfCameras = Camera.getNumberOfCameras();
		// for every camera check
		for (int i = 0; i < numberOfCameras; i++) {
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(i, info);
			if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
				cameraId = i;
				cameraFront = false;
				break;
			}
		}
		return cameraId;
	}

	public void onResume() {
		super.onResume();
		if (!hasCamera(myContext)) {
			Toast toast = Toast.makeText(myContext, "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
			toast.show();
			getActivity().finish();
		}
		if (mCamera == null) {
			// if the front facing camera does not exist
			if (findFrontFacingCamera() < 0) {
				Toast.makeText(getActivity(), "No front facing camera found.", Toast.LENGTH_LONG).show();
				switchCamera.setVisibility(View.GONE);
			}
			mCamera = Camera.open(findBackFacingCamera());

			mPreview.refreshCamera(mCamera);
//            if (!prepareMediaRecorder()) {
//                Toast.makeText(getActivity(), "Fail in prepareMediaRecorder()!\n - Ended -", Toast.LENGTH_LONG).show();
//                getActivity().finish();
//            }
//            // work on UiThread for better performance
////				runOnUiThread(new Runnable() {
////					public void run() {
//            // If there are stories, add them to the table
//
//            				getActivity().runOnUiThread(new Runnable() {
//                                public void run() {
//                                    // If there are stories, add them to the table
//
//                                    try {
//                                        mediaRecorder.start();
//                                    } catch (final Exception ex) {
//                                        // Log.i("---","Exception in thread");
//                                    }
//                                }
//                            });
		}
	}



	OnClickListener switchCameraListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// get the number of cameras
			if (!recording) {
				int camerasNumber = Camera.getNumberOfCameras();
				if (camerasNumber > 1) {
					// release the old camera instance
					// switch camera, from the front and the back and vice versa

					releaseCamera();
					chooseCamera();
				} else {
					Toast toast = Toast.makeText(myContext, "Sorry, your phone has only one camera!", Toast.LENGTH_LONG);
					toast.show();
				}
			}
		}
	};

	public void chooseCamera() {
		// if the camera preview is the front
		if (cameraFront) {
			int cameraId = findBackFacingCamera();
			if (cameraId >= 0) {
				// open the backFacingCamera
				// set a picture callback
				// refresh the preview

				mCamera = Camera.open(cameraId);
				// mPicture = getPictureCallback();
				mPreview.refreshCamera(mCamera);
			}
		} else {
			int cameraId = findFrontFacingCamera();
			if (cameraId >= 0) {
				// open the backFacingCamera
				// set a picture callback
				// refresh the preview

				mCamera = Camera.open(cameraId);
				// mPicture = getPictureCallback();
				mPreview.refreshCamera(mCamera);
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		// when on Pause, release camera in order to be used from other
		// applications
//        mediaRecorder.stop(); // stop the recording
//        releaseMediaRecorder();
        releaseCamera();
	}

	private boolean hasCamera(Context context) {
		// check if the device has camera
		if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			return true;
		} else {
			return false;
		}
	}

	boolean recording = false;
	OnClickListener captrureListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (recording) {
				// stop recording and release camera
				mediaRecorder.stop(); // stop the recording
				releaseMediaRecorder(); // release the MediaRecorder object
				Toast.makeText(getActivity(), "Video captured!", Toast.LENGTH_LONG).show();
				recording = false;
			} else {
				if (!prepareMediaRecorder()) {
                    Toast.makeText(getActivity(), "Fail in prepareMediaRecorder()!\n - Ended -", Toast.LENGTH_LONG).show();
                    getActivity().finish();
                }
                // work on UiThread for better performance
//				runOnUiThread(new Runnable() {
//					public void run() {
                // If there are stories, add them to the table

                try {
                    mediaRecorder.start();
                } catch (final Exception ex) {
                    // Log.i("---","Exception in thread");
                }
//					}
//				});

				recording = true;
			}
		}
	};

	private void releaseMediaRecorder() {
		if (mediaRecorder != null) {
			mediaRecorder.reset(); // clear recorder configuration
			mediaRecorder.release(); // release the recorder object
			mediaRecorder = null;
			mCamera.lock(); // lock camera for later use
		}
	}

	private boolean prepareMediaRecorder() {

		mediaRecorder = new MediaRecorder();

		mCamera.unlock();
		mediaRecorder.setCamera(mCamera);

		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

		mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));
        String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/youraudiofile.mp4";
		mediaRecorder.setOutputFile(mFileName);
		mediaRecorder.setMaxDuration(600000); // Set max duration 60 sec.
		mediaRecorder.setMaxFileSize(50000000); // Set max file size 50M

		try {
			mediaRecorder.prepare();
		} catch (IllegalStateException e) {
			releaseMediaRecorder();
			return false;
		} catch (IOException e) {
			releaseMediaRecorder();
			return false;
		}
		return true;

	}

	private void releaseCamera() {
		// stop and release camera
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
	}
}