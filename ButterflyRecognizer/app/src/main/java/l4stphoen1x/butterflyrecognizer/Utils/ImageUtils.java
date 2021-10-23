package l4stphoen1x.butterflyrecognizer.Utils;
import android.content.*;
import android.graphics.*;
import android.os.*;
import java.io.*;
import android.provider.MediaStore;
import android.net.Uri;
import android.database.Cursor;
import android.annotation.SuppressLint;
import l4stphoen1x.butterflyrecognizer.R;
public class ImageUtils{

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

  public static Bitmap getBitmapByPath(Context context, String path){
    if(path==null||path.equals("")){
      return BitmapFactory.decodeResource(context.getResources(), R.drawable.error_loading);}
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
      return getBitmapFromUri(context,getImageContentUri(context,path));
    }else{
      return getBitmapFromSrc(path);}}
  private static Bitmap getBitmapFromUri(Context context, Uri uri){
    try{
      ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r");
      FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
      Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
      parcelFileDescriptor.close();
      return image;
    }catch (Exception e){
      e.printStackTrace();}
    return null;}
  private static Uri getImageContentUri(Context context, String path){
    @SuppressLint("Recycle") Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            new String[] { MediaStore.Images.Media._ID }, MediaStore.Images.Media.DATA + "=? ",
            new String[] { path }, null);
    if (cursor != null && cursor.moveToFirst()){
      @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
      Uri baseUri = Uri.parse("content://media/external/images/media");
      return Uri.withAppendedPath(baseUri, "" + id);
    }else{
      if (new File(path).exists()){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATA, path);
        return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
      }else{
        return null;}}}
  public static Bitmap scaleImage(Bitmap bm, int newWidth, int newHeight){
    if (bm == null){
      return null;}
    int width = bm.getWidth();
    int height = bm.getHeight();
    float scaleWidth = ((float) newWidth) / width;
    float scaleHeight = ((float) newHeight) / height;
    Matrix matrix = new Matrix();
    matrix.postScale(scaleWidth, scaleHeight);
    Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
    if (!bm.isRecycled()){
      bm.recycle();}
    return newbm;}
  public static Bitmap getBitmapFromSrc(String src){
    return scaleImage(BitmapFactory.decodeFile(src),224,224);}
  static final int kMaxChannelValue = 262143;
  @SuppressWarnings("unused")

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

  public static int getYUVByteSize(final int width, final int height){
    final int ySize = width * height;
    final int uvSize = ((width + 1) / 2) * ((height + 1) / 2) * 2;
    return ySize + uvSize;}
  public static void convertYUV420SPToARGB8888(byte[] input, int width, int height, int[] output){
    final int frameSize = width * height;
    for (int j = 0, yp = 0; j < height; j++){
      int uvp = frameSize + (j >> 1) * width;
      int u = 0;
      int v = 0;
      for (int i = 0; i < width; i++, yp++){
        int y = 0xff & input[yp];
        if ((i & 1) == 0) {
          v = 0xff & input[uvp++];
          u = 0xff & input[uvp++];}
        output[yp] = YUV2RGB(y, u, v);}}}
  private static int YUV2RGB(int y, int u, int v){
    y = Math.max((y - 16), 0);
    u -= 128;
    v -= 128;
    int y1192 = 1192 * y;
    int r = (y1192 + 1634 * v);
    int g = (y1192 - 833 * v - 400 * u);
    int b = (y1192 + 2066 * u);
    r = r > kMaxChannelValue ? kMaxChannelValue : (Math.max(r, 0));
    g = g > kMaxChannelValue ? kMaxChannelValue : (Math.max(g, 0));
    b = b > kMaxChannelValue ? kMaxChannelValue : (Math.max(b, 0));
    return 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);}
  public static void convertYUV420ToARGB8888(byte[] yData, byte[] uData, byte[] vData, int width, int height, int yRowStride, int uvRowStride, int uvPixelStride, int[] out){
    int yp = 0;
    for (int j = 0; j < height; j++){
      int pY = yRowStride * j;
      int pUV = uvRowStride * (j >> 1);
      for (int i = 0; i < width; i++){
        int uv_offset = pUV + (i >> 1) * uvPixelStride;
        out[yp++] = YUV2RGB(0xff & yData[pY + i], 0xff & uData[uv_offset], 0xff & vData[uv_offset]);}}}
  public static Matrix getTransformationMatrix(final int srcWidth, final int srcHeight, final int dstWidth, final int dstHeight, final int applyRotation, final boolean maintainAspectRatio){
    final Matrix matrix = new Matrix();
    if (applyRotation != 0){
      matrix.postTranslate(-srcWidth / 2.0f, -srcHeight / 2.0f);
      matrix.postRotate(applyRotation);}
    final boolean transpose = (Math.abs(applyRotation) + 90) % 180 == 0;
    final int inWidth = transpose ? srcHeight : srcWidth;
    final int inHeight = transpose ? srcWidth : srcHeight;
    if (inWidth != dstWidth || inHeight != dstHeight){
      final float scaleFactorX = dstWidth / (float) inWidth;
      final float scaleFactorY = dstHeight / (float) inHeight;
      if (maintainAspectRatio){
        final float scaleFactor = Math.max(scaleFactorX, scaleFactorY);
        matrix.postScale(scaleFactor, scaleFactor);
      }else{
        matrix.postScale(scaleFactorX, scaleFactorY);}}
    if (applyRotation != 0){
      matrix.postTranslate(dstWidth / 2.0f, dstHeight / 2.0f);}
    return matrix;}}