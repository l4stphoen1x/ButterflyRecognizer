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

package l4stphoen1x.butterflyrecognizer;
import android.view.*;
import android.widget.*;
import android.os.*;
import android.media.*;
import android.hardware.camera2.*;
import l4stphoen1x.butterflyrecognizer.Utils.Classifier.*;
import l4stphoen1x.butterflyrecognizer.Utils.*;
import java.util.*;
import android.Manifest;
import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image.Plane;
import android.media.ImageReader.OnImageAvailableListener;
import android.util.Size;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import java.nio.ByteBuffer;
public abstract class RealTimeActivity extends AppCompatActivity implements OnImageAvailableListener, Camera.PreviewCallback, View.OnClickListener{
  private static final int PERMISSIONS_REQUEST = 1;
  private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
  protected int previewWidth = 0;
  protected int previewHeight = 0;
  private Handler handler;
  private HandlerThread handlerThread;
  private boolean useCamera2API;
  private boolean isProcessingFrame = false;
  private final byte[][] yuvBytes = new byte[3][];
  private int[] rgbBytes = null;
  private int yRowStride;
  private Runnable postInferenceCallback;
  private Runnable imageConverter;
  private LinearLayout gestureLayout;
  private BottomSheetBehavior sheetBehavior;
  protected TextView recognitionTextView, recognition1TextView, recognition2TextView, recognitionValueTextView, recognition1ValueTextView, recognition2ValueTextView;
  public boolean onOptionsItemSelected(MenuItem item){
    if (item.getItemId() == android.R.id.home){
      onBackPressed();
      return true;}
    return super.onOptionsItemSelected(item);}
  protected void onCreate(final Bundle savedInstanceState){
    super.onCreate(null);
    Window window = RealTimeActivity.this.getWindow();
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    window.setStatusBarColor(ContextCompat.getColor(RealTimeActivity.this,R.color.blue));
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    setContentView(R.layout.activity_realtime);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setDisplayShowTitleEnabled(false);
    if (hasPermission()){
      setFragment();
    }else{
      requestPermission();}
    RelativeLayout bottomSheetLayout = findViewById(R.id.bottom_sheet_layout);
    gestureLayout = findViewById(R.id.gesture_layout);
    sheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
    ViewTreeObserver vto = gestureLayout.getViewTreeObserver();
    vto.addOnGlobalLayoutListener(
        new ViewTreeObserver.OnGlobalLayoutListener(){
          public void onGlobalLayout(){
            gestureLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            int height = gestureLayout.getMeasuredHeight();
            sheetBehavior.setPeekHeight(height);}});
    sheetBehavior.setHideable(false);
    recognitionTextView = findViewById(R.id.detected_item);
    recognitionValueTextView = findViewById(R.id.detected_item_value);
    recognition1TextView = findViewById(R.id.detected_item1);
    recognition1ValueTextView = findViewById(R.id.detected_item1_value);
    recognition2TextView = findViewById(R.id.detected_item2);
    recognition2ValueTextView = findViewById(R.id.detected_item2_value);}
  protected int[] getRgbBytes(){
    imageConverter.run();
    return rgbBytes;}
  public void onPreviewFrame(final byte[] bytes, final Camera camera){
    if (isProcessingFrame){
      return;}
    try{
      if (rgbBytes == null){
        Camera.Size previewSize = camera.getParameters().getPreviewSize();
        previewHeight = previewSize.height;
        previewWidth = previewSize.width;
        rgbBytes = new int[previewWidth * previewHeight];
        onPreviewSizeChosen(new Size(previewSize.width, previewSize.height), 90);}
    }catch (final Exception e){
      return;}
    isProcessingFrame = true;
    yuvBytes[0] = bytes;
    yRowStride = previewWidth;
    imageConverter = new Runnable(){
          public void run(){
            ImageUtils.convertYUV420SPToARGB8888(bytes, previewWidth, previewHeight, rgbBytes);}};
    postInferenceCallback = new Runnable(){
          public void run(){
            camera.addCallbackBuffer(bytes);
            isProcessingFrame = false;}};
    processImage();}
  public void onImageAvailable(final ImageReader reader){
    if (previewWidth == 0 || previewHeight == 0){
      return;}
    if (rgbBytes == null){
      rgbBytes = new int[previewWidth * previewHeight];}
    try{
      final Image image = reader.acquireLatestImage();
      if (image == null){
        return;}
      if (isProcessingFrame){
        image.close();
        return;}
      isProcessingFrame = true;
      Trace.beginSection("imageAvailable");
      final Plane[] planes = image.getPlanes();
      fillBytes(planes, yuvBytes);
      yRowStride = planes[0].getRowStride();
      final int uvRowStride = planes[1].getRowStride();
      final int uvPixelStride = planes[1].getPixelStride();
      imageConverter = new Runnable(){
            public void run(){
              ImageUtils.convertYUV420ToARGB8888(yuvBytes[0], yuvBytes[1], yuvBytes[2], previewWidth, previewHeight, yRowStride, uvRowStride, uvPixelStride, rgbBytes);}};
      postInferenceCallback = new Runnable(){
            public void run(){
              image.close();
              isProcessingFrame = false;}};
      processImage();
    }catch (final Exception e){
      Trace.endSection();
      return;}
    Trace.endSection();}
  public synchronized void onStart(){
    super.onStart();}
  public synchronized void onResume(){
    super.onResume();
    handlerThread = new HandlerThread("inference");
    handlerThread.start();
    handler = new Handler(handlerThread.getLooper());}
  public synchronized void onPause(){
    handlerThread.quitSafely();
    try{
      handlerThread.join();
      handlerThread = null;
      handler = null;
    }catch (final InterruptedException ignored){}
    super.onPause();}
  public synchronized void onStop(){
    super.onStop();}
  public synchronized void onDestroy(){
    super.onDestroy();}
  protected synchronized void runInBackground(final Runnable r){
    if (handler != null){
      handler.post(r);}}
  public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults){
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == PERMISSIONS_REQUEST){
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
        setFragment();
      }else{
        requestPermission();}}}
  private boolean hasPermission(){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
      return checkSelfPermission(PERMISSION_CAMERA) == PackageManager.PERMISSION_GRANTED;
    }else{
      return true;}}
  private void requestPermission(){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
      if (shouldShowRequestPermissionRationale(PERMISSION_CAMERA)){
        Toast.makeText(RealTimeActivity.this, "Camera permission is required for this demo", Toast.LENGTH_LONG).show();}
      requestPermissions(new String[] {PERMISSION_CAMERA}, PERMISSIONS_REQUEST);}}
  private boolean isHardwareLevelSupported(CameraCharacteristics characteristics){
    int deviceLevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
    if (deviceLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY){
      return false;}
    return android.hardware.camera2.CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_FULL <= deviceLevel;}
  private String chooseCamera(){
    final CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
    try{
      for (final String cameraId : manager.getCameraIdList()){
        final CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
        final Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
        if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT){
          continue;}
        final StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        if (map == null){
          continue;}
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
          useCamera2API = (facing == CameraCharacteristics.LENS_FACING_EXTERNAL) || isHardwareLevelSupported(characteristics);}
        return cameraId;}
    }catch (CameraAccessException ignored){}
    return null;}
  protected void setFragment(){
    String cameraId = chooseCamera();
    Fragment fragment;
    CameraConnectionFragment camera2Fragment = CameraConnectionFragment.newInstance(new CameraConnectionFragment.ConnectionCallback(){
      public void onPreviewSizeChosen(final Size size, final int rotation){
        previewHeight = size.getHeight();
        previewWidth = size.getWidth();
        RealTimeActivity.this.onPreviewSizeChosen(size, rotation);}}, this, getLayoutId(), getDesiredPreviewFrameSize());
    camera2Fragment.setCamera(cameraId);
    fragment = camera2Fragment;
    getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();}
  protected void fillBytes(final Plane[] planes, final byte[][] yuvBytes){
    for (int i = 0; i < planes.length; ++i){
      final ByteBuffer buffer = planes[i].getBuffer();
      if (yuvBytes[i] == null){
        yuvBytes[i] = new byte[buffer.capacity()];}
      buffer.get(yuvBytes[i]);}}
  protected void readyForNextImage(){
    if (postInferenceCallback != null){
      postInferenceCallback.run();}}
  @SuppressLint("SwitchIntDef")
  protected int getScreenOrientation(){
    switch (getWindowManager().getDefaultDisplay().getRotation()){
      case Surface.ROTATION_270:
        return 270;
      case Surface.ROTATION_180:
        return 180;
      case Surface.ROTATION_90:
        return 90;
      default:
        return 0;}}
  @SuppressLint({"SetTextI18n", "DefaultLocale"})
  protected void showResultsInBottomSheet(List<Recognition> results){
    if (results != null && results.size() >= 3){
      Recognition recognition = results.get(0);
      if (recognition != null){
        if (recognition.getTitle() != null) recognitionTextView.setText(recognition.getTitle());
        if (recognition.getConfidence() != null)
          recognitionValueTextView.setText(String.format("%.2f", (100 * recognition.getConfidence())) + "%");}
      Recognition recognition1 = results.get(1);
      if (recognition1 != null){
        if (recognition1.getTitle() != null) recognition1TextView.setText(recognition1.getTitle());
        if (recognition1.getConfidence() != null)
          recognition1ValueTextView.setText(String.format("%.2f", (100 * recognition1.getConfidence())) + "%");}
      Recognition recognition2 = results.get(2);
      if (recognition2 != null){
        if (recognition2.getTitle() != null) recognition2TextView.setText(recognition2.getTitle());
        if (recognition2.getConfidence() != null)
          recognition2ValueTextView.setText(String.format("%.2f", (100 * recognition2.getConfidence())) + "%");}}}
  protected Model getModel(){
    return Model.QUANTIZED;}
  protected Device getDevice(){
    return Device.CPU;}
  protected int getNumThreads(){
    return 5;}
  protected abstract void processImage();
  protected abstract void onPreviewSizeChosen(final Size size, final int rotation);
  protected abstract int getLayoutId();
  protected abstract Size getDesiredPreviewFrameSize();}