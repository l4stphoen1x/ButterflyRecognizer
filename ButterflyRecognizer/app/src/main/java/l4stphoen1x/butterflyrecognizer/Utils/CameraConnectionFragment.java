/*
 * Copyright 2019 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Modifications copyright (C) 2021 Andrii Hubert

package l4stphoen1x.butterflyrecognizer.Utils;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.hardware.camera2.*;
import android.os.*;
import android.util.*;
import android.view.*;
import java.util.*;
import java.util.concurrent.*;
import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;
import android.widget.Toast;
import l4stphoen1x.butterflyrecognizer.R;
@SuppressLint("ValidFragment")
public class CameraConnectionFragment extends Fragment {
  private static final int MINIMUM_PREVIEW_SIZE = 320;
  private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
  private static final String FRAGMENT_DIALOG = "dialog";
  static{
    ORIENTATIONS.append(Surface.ROTATION_0, 90);
    ORIENTATIONS.append(Surface.ROTATION_90, 0);
    ORIENTATIONS.append(Surface.ROTATION_180, 270);
    ORIENTATIONS.append(Surface.ROTATION_270, 180);}
  private final Semaphore cameraOpenCloseLock = new Semaphore(1);
  private final OnImageAvailableListener imageListener;
  private final Size inputSize;
  private final int layout;
  private final ConnectionCallback cameraConnectionCallback;
  private final CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback(){
        public void onCaptureProgressed(final CameraCaptureSession session, final CaptureRequest request, final CaptureResult partialResult){}
        public void onCaptureCompleted(final CameraCaptureSession session, final CaptureRequest request, final TotalCaptureResult result){}};
  private String cameraId;
  private AutoFitTextureView textureView;
  private CameraCaptureSession captureSession;
  private CameraDevice cameraDevice;
  private Integer sensorOrientation;
  private Size previewSize;
  private HandlerThread backgroundThread;
  private Handler backgroundHandler;
  private final TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener(){
        public void onSurfaceTextureAvailable(final SurfaceTexture texture, final int width, final int height){
          openCamera(width, height);}
        public void onSurfaceTextureSizeChanged(final SurfaceTexture texture, final int width, final int height){
          configureTransform(width, height);}
        public boolean onSurfaceTextureDestroyed(final SurfaceTexture texture){
          return true;}
        public void onSurfaceTextureUpdated(final SurfaceTexture texture){}};
  private ImageReader previewReader;
  private CaptureRequest.Builder previewRequestBuilder;
  private CaptureRequest previewRequest;
  private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback(){
        public void onOpened(final CameraDevice cd){
          cameraOpenCloseLock.release();
          cameraDevice = cd;
          createCameraPreviewSession();}
        public void onDisconnected(final CameraDevice cd){
          cameraOpenCloseLock.release();
          cd.close();
          cameraDevice = null;}
        public void onError(final CameraDevice cd, final int error){
          cameraOpenCloseLock.release();
          cd.close();
          cameraDevice = null;
          final Activity activity = getActivity();
          if (null != activity){
            activity.finish();}}};
  @SuppressLint("ValidFragment")
  private CameraConnectionFragment(
      final ConnectionCallback connectionCallback,
      final OnImageAvailableListener imageListener,
      final int layout,
      final Size inputSize){
    this.cameraConnectionCallback = connectionCallback;
    this.imageListener = imageListener;
    this.layout = layout;
    this.inputSize = inputSize;}
  protected static Size chooseOptimalSize(final Size[] choices, final int width, final int height){
    final int minSize = Math.max(Math.min(width, height), MINIMUM_PREVIEW_SIZE);
    final Size desiredSize = new Size(width, height);
    boolean exactSizeFound = false;
    final List<Size> bigEnough = new ArrayList<Size>();
    final List<Size> tooSmall = new ArrayList<Size>();
    for (final Size option : choices){
      if (option.equals(desiredSize)){
        exactSizeFound = true;}
      if (option.getHeight() >= minSize && option.getWidth() >= minSize){
        bigEnough.add(option);
      }else{
        tooSmall.add(option);}}
    if (exactSizeFound){
      return desiredSize;}
    if (bigEnough.size() > 0){
      return Collections.min(bigEnough, new CompareSizesByArea());
    }else{
      return choices[0];}}
  public static CameraConnectionFragment newInstance(final ConnectionCallback callback, final OnImageAvailableListener imageListener, final int layout, final Size inputSize){
    return new CameraConnectionFragment(callback, imageListener, layout, inputSize);}
  private void showToast(final String text){
    final Activity activity = getActivity();
    if (activity != null){
      activity.runOnUiThread(
          new Runnable(){
            public void run(){
              Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();}});}}
  public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState){
    return inflater.inflate(layout, container, false);}
  public void onViewCreated(final View view, final Bundle savedInstanceState){
    textureView = (AutoFitTextureView) view.findViewById(R.id.texture);}
  public void onActivityCreated(final Bundle savedInstanceState){
    super.onActivityCreated(savedInstanceState);}
  public void onResume(){
    super.onResume();
    startBackgroundThread();
    if (textureView.isAvailable()){
      openCamera(textureView.getWidth(), textureView.getHeight());
    }else{
      textureView.setSurfaceTextureListener(surfaceTextureListener);}}
  public void onPause(){
    closeCamera();
    stopBackgroundThread();
    super.onPause();}
  public void setCamera(String cameraId){
    this.cameraId = cameraId;}
  private void setUpCameraOutputs(){
    final Activity activity = getActivity();
    final CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
    try{
      final CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
      final StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
      sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
      previewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), inputSize.getWidth(), inputSize.getHeight());
      final int orientation = getResources().getConfiguration().orientation;
      if (orientation == Configuration.ORIENTATION_LANDSCAPE){
        textureView.setAspectRatio(previewSize.getWidth(), previewSize.getHeight());
      }else{
        textureView.setAspectRatio(previewSize.getHeight(), previewSize.getWidth());}
    }catch (final CameraAccessException ignored){
    }catch (final NullPointerException e){
      ErrorDialog.newInstance(getString(R.string.camera_error)).show(getChildFragmentManager(), FRAGMENT_DIALOG);
      throw new RuntimeException(getString(R.string.camera_error));}
    cameraConnectionCallback.onPreviewSizeChosen(previewSize, sensorOrientation);}
  private void openCamera(final int width, final int height){
    setUpCameraOutputs();
    configureTransform(width, height);
    final Activity activity = getActivity();
    final CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
    try{
      if (!cameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)){
        throw new RuntimeException("Time out waiting to lock camera opening.");}
      manager.openCamera(cameraId, stateCallback, backgroundHandler);
    }catch (final CameraAccessException ignored){
    }catch (final InterruptedException e){
      throw new RuntimeException("Interrupted while trying to lock camera opening.", e);}}
  private void closeCamera(){
    try{
      cameraOpenCloseLock.acquire();
      if (null != captureSession){
        captureSession.close();
        captureSession = null;}
      if (null != cameraDevice){
        cameraDevice.close();
        cameraDevice = null;}
      if (null != previewReader){
        previewReader.close();
        previewReader = null;}
    }catch (final InterruptedException e){
      throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
    }finally{
      cameraOpenCloseLock.release();}}
  private void startBackgroundThread(){
    backgroundThread = new HandlerThread("ImageListener");
    backgroundThread.start();
    backgroundHandler = new Handler(backgroundThread.getLooper());}
  private void stopBackgroundThread(){
    backgroundThread.quitSafely();
    try{
      backgroundThread.join();
      backgroundThread = null;
      backgroundHandler = null;
    }catch (final InterruptedException ignored){}}
  private void createCameraPreviewSession(){
    try{
      final SurfaceTexture texture = textureView.getSurfaceTexture();
      assert texture != null;
      texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
      final Surface surface = new Surface(texture);
      previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
      previewRequestBuilder.addTarget(surface);
      previewReader = ImageReader.newInstance(previewSize.getWidth(), previewSize.getHeight(), ImageFormat.YUV_420_888, 2);
      previewReader.setOnImageAvailableListener(imageListener, backgroundHandler);
      previewRequestBuilder.addTarget(previewReader.getSurface());
      cameraDevice.createCaptureSession(Arrays.asList(surface, previewReader.getSurface()), new CameraCaptureSession.StateCallback(){
            public void onConfigured(final CameraCaptureSession cameraCaptureSession){
              if (null == cameraDevice){
                return;}
              captureSession = cameraCaptureSession;
              try{
                previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                previewRequest = previewRequestBuilder.build();
                captureSession.setRepeatingRequest(previewRequest, captureCallback, backgroundHandler);
              }catch (final CameraAccessException ignored){}}
            public void onConfigureFailed(final CameraCaptureSession cameraCaptureSession){
              showToast("Failed");}}, null);
    }catch (final CameraAccessException ignored){}}
  private void configureTransform(final int viewWidth, final int viewHeight){
    final Activity activity = getActivity();
    if (null == textureView || null == previewSize || null == activity){
      return;}
    final int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
    final Matrix matrix = new Matrix();
    final RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
    final RectF bufferRect = new RectF(0, 0, previewSize.getHeight(), previewSize.getWidth());
    final float centerX = viewRect.centerX();
    final float centerY = viewRect.centerY();
    if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation){
      bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
      matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
      final float scale = Math.max((float) viewHeight / previewSize.getHeight(), (float) viewWidth / previewSize.getWidth());
      matrix.postScale(scale, scale, centerX, centerY);
      matrix.postRotate(90 * (rotation - 2), centerX, centerY);
    }else if(Surface.ROTATION_180 == rotation){
      matrix.postRotate(180, centerX, centerY);}
    textureView.setTransform(matrix);}
  public interface ConnectionCallback{
    void onPreviewSizeChosen(Size size, int cameraRotation);}
  static class CompareSizesByArea implements Comparator<Size>{
    public int compare(final Size lhs, final Size rhs){
      return Long.signum((long) lhs.getWidth() * lhs.getHeight() - (long) rhs.getWidth() * rhs.getHeight());}}
  public static class ErrorDialog extends DialogFragment{
    private static final String ARG_MESSAGE = "message";
    public static ErrorDialog newInstance(final String message){
      final ErrorDialog dialog = new ErrorDialog();
      final Bundle args = new Bundle();
      args.putString(ARG_MESSAGE, message);
      dialog.setArguments(args);
      return dialog;}
    public Dialog onCreateDialog(final Bundle savedInstanceState){
      final Activity activity = getActivity();
      return new AlertDialog.Builder(activity).setMessage(getArguments().getString(ARG_MESSAGE)).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
                public void onClick(final DialogInterface dialogInterface, final int i){
                  activity.finish();}})
          .create();}}}
