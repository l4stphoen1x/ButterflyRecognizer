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
import l4stphoen1x.butterflyrecognizer.Utils.Classifier.*;
import android.graphics.*;
import android.util.*;
import android.graphics.Bitmap.Config;
import android.view.View;
import l4stphoen1x.butterflyrecognizer.R;
import java.io.IOException;
import java.util.List;
public class ClassifierActivity extends l4stphoen1x.butterflyrecognizer.RealTimeActivity{
  private static final boolean MAINTAIN_ASPECT = true;
  private static final Size DESIRED_PREVIEW_SIZE = new Size(640, 480);
  private static final float TEXT_SIZE_DIP = 10;
  private Bitmap rgbFrameBitmap = null;
  private Bitmap croppedBitmap = null;
  private Classifier classifier;
  private Matrix frameToCropTransform;
  protected int getLayoutId(){
    return R.layout.camera_connection_fragment;}
  protected Size getDesiredPreviewFrameSize(){
    return DESIRED_PREVIEW_SIZE;}
  public void onPreviewSizeChosen(final Size size, final int rotation){
    final float textSizePx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());
    BorderedText borderedText = new BorderedText(textSizePx);
    borderedText.setTypeface(Typeface.MONOSPACE);
    recreateClassifier(getModel(), getDevice(), getNumThreads());
    if (classifier == null){
      return;}
    previewWidth = size.getWidth();
    previewHeight = size.getHeight();
    int sensorOrientation = rotation - getScreenOrientation();
    rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888);
    croppedBitmap = Bitmap.createBitmap(classifier.getImageSizeX(), classifier.getImageSizeY(), Config.ARGB_8888);
    frameToCropTransform = ImageUtils.getTransformationMatrix(previewWidth, previewHeight, classifier.getImageSizeX(), classifier.getImageSizeY(), sensorOrientation, MAINTAIN_ASPECT);
    Matrix cropToFrameTransform = new Matrix();
    frameToCropTransform.invert(cropToFrameTransform);}
  protected void processImage(){
    rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight);
    final Canvas canvas = new Canvas(croppedBitmap);
    canvas.drawBitmap(rgbFrameBitmap, frameToCropTransform, null);
    runInBackground(new Runnable(){
          public void run(){
            if (classifier != null){
              final List<Classifier.Recognition> results = classifier.recognizeImage(croppedBitmap);
              runOnUiThread(new Runnable(){
                    public void run(){
                      showResultsInBottomSheet(results);}});}
            readyForNextImage();}});}
  private void recreateClassifier(Model model, Device device, int numThreads){
    try{
      classifier = Classifier.create(this, model, device, numThreads);
    }catch (IOException e){
      e.printStackTrace();}}
  public void onClick(View view){}}