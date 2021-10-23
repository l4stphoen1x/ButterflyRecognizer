/* Copyright 2020 paul623
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Modifications copyright (C) 2021 Andrii Hubert

package l4stphoen1x.butterflyrecognizer.Utils;
import java.io.*;
import java.nio.*;
import java.util.*;
import android.graphics.*;
import android.os.Trace;
import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import java.nio.channels.FileChannel;
import androidx.annotation.NonNull;
import android.annotation.SuppressLint;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.gpu.GpuDelegate;
public abstract class Classifier{
  public enum Model{QUANTIZED}
  public enum Device {CPU, NNAPI, GPU}
  private static final int MAX_RESULTS = 3;
  private static final int DIM_BATCH_SIZE = 1;
  private static final int DIM_PIXEL_SIZE = 3;
  private final int[] intValues = new int[getImageSizeX() * getImageSizeY()];
  private MappedByteBuffer tfliteModel;
  private final List<String> labels;
  private GpuDelegate gpuDelegate = null;
  protected Interpreter tflite;
  protected ByteBuffer imgData;
  public static Classifier create(Activity activity, Model model, Device device, int numThreads) throws IOException{
      return new ClassifierFloatMobileNet(activity, device, numThreads);}
  public static class Recognition{
    private final String id;
    private final String title;
    private final Float confidence;
    private final RectF location;
    public Recognition(final String id, final String title, final Float confidence, final RectF location){
      this.id = id;
      this.title = title;
      this.confidence = confidence;
      this.location = location;}
    public String getId(){
      return id;}
    public String getTitle(){
      return title;}
    public Float getConfidence(){
      return confidence;}
    @NonNull
    @SuppressLint("DefaultLocale")
    public String toString(){
      String resultString = "";
      if (id != null) {
        resultString += "[" + id + "] ";}
      if (title != null){
        resultString += title + " ";}
      if (confidence != null){
        resultString += String.format("(%.1f%%) ", confidence * 100.0f);}
      if (location != null){
        resultString += location + " ";}
      return resultString.trim();}}
  protected Classifier(Activity activity, Device device, int numThreads) throws IOException{
    tfliteModel = loadModelFile(activity);
    Interpreter.Options tfliteOptions = new Interpreter.Options();
    switch (device){
      case NNAPI:
        tfliteOptions.setUseNNAPI(true);
        break;
      case GPU:
        gpuDelegate = new GpuDelegate();
        tfliteOptions.addDelegate(gpuDelegate);
        break;
      case CPU:
        break;}
    tfliteOptions.setNumThreads(numThreads);
    tflite = new Interpreter(tfliteModel, tfliteOptions);
    labels = loadLabelList(activity);
    imgData = ByteBuffer.allocateDirect(DIM_BATCH_SIZE * getImageSizeX() * getImageSizeY() * DIM_PIXEL_SIZE * getNumBytesPerChannel());
    imgData.order(ByteOrder.nativeOrder());}
  private List<String> loadLabelList(Activity activity) throws IOException{
    List<String> labels = new ArrayList<String>();
    BufferedReader reader = new BufferedReader(new InputStreamReader(activity.getAssets().open(getLabelPath())));
    String line;
    while ((line = reader.readLine()) != null){
      labels.add(line);}
    reader.close();
    return labels;}
  private MappedByteBuffer loadModelFile(Activity activity) throws IOException{
    AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(getModelPath());
    FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
    FileChannel fileChannel = inputStream.getChannel();
    long startOffset = fileDescriptor.getStartOffset();
    long declaredLength = fileDescriptor.getDeclaredLength();
    return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);}
  private void convertBitmapToByteBuffer(Bitmap bitmap){
    if (imgData == null){
      return;}
    imgData.rewind();
    bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
    int pixel = 0;
    for (int i = 0; i < getImageSizeX(); ++i){
      for (int j = 0; j < getImageSizeY(); ++j){
        final int val = intValues[pixel++];
        addPixelValue(val);}}}
  public List<Recognition> recognizeImage(final Bitmap bitmap){
    Trace.beginSection("recognizeImage");
    Trace.beginSection("preprocessBitmap");
    convertBitmapToByteBuffer(bitmap);
    Trace.endSection();
    Trace.beginSection("runInference");
    runInference();
    Trace.endSection();
    PriorityQueue<Recognition> pq = new PriorityQueue<Recognition>(3, new Comparator<Recognition>(){
              public int compare(Recognition lhs, Recognition rhs){
                return Float.compare(rhs.getConfidence(), lhs.getConfidence());}});
    for (int i = 0; i < labels.size(); ++i){
      labels.size();
      pq.add(new Recognition("" + i, labels.get(i), getNormalizedProbability(i), null));}
    final ArrayList<Recognition> recognitions = new ArrayList<Recognition>();
    int recognitionsSize = Math.min(pq.size(), MAX_RESULTS);
    for (int i = 0; i < recognitionsSize; ++i){
      recognitions.add(pq.poll());}
    Trace.endSection();
    return recognitions;}
  public void close(){
    if (tflite != null){
      tflite.close();
      tflite = null;}
    if (gpuDelegate != null){
      gpuDelegate.close();
      gpuDelegate = null;}
    tfliteModel = null;}
  public abstract int getImageSizeX();
  public abstract int getImageSizeY();
  protected abstract String getModelPath();
  protected abstract String getLabelPath();
  protected abstract int getNumBytesPerChannel();
  protected abstract void addPixelValue(int pixelValue);
  protected abstract float getNormalizedProbability(int labelIndex);
  protected abstract void runInference();
  protected int getNumLabels(){
    return labels.size();}}