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
import android.app.Activity;
import java.io.IOException;
public class ClassifierFloatMobileNet extends Classifier{
  private static final float IMAGE_MEAN = 127.5f;
  private static final float IMAGE_STD = 127.5f;
  private final float[][] labelProbArray;
  public ClassifierFloatMobileNet(Activity activity, Device device, int numThreads) throws IOException{
    super(activity, device, numThreads);
    labelProbArray = new float[1][getNumLabels()];}
  public int getImageSizeX(){
    return 224;}
  public int getImageSizeY(){
    return 224;}
  protected String getModelPath(){
    return "model.tflite";}
  protected String getLabelPath(){
    return "labels.txt";}
  protected int getNumBytesPerChannel(){
    return 4;}
  protected void addPixelValue(int pixelValue){
    imgData.putFloat((((pixelValue >> 16) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
    imgData.putFloat((((pixelValue >> 8) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
    imgData.putFloat(((pixelValue & 0xFF) - IMAGE_MEAN) / IMAGE_STD);}
  protected float getNormalizedProbability(int labelIndex){
    return labelProbArray[0][labelIndex];}
  protected void runInference(){
    tflite.run(imgData, labelProbArray);}}