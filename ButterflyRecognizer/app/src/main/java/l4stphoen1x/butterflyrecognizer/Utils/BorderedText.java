/* Copyright 2019 The TensorFlow Authors. All Rights Reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/


// Modifications copyright (C) 2021 Andrii Hubert

package l4stphoen1x.butterflyrecognizer.Utils;
import android.graphics.*;
import android.graphics.Paint.*;
public class BorderedText{
  private final Paint interiorPaint;
  private final Paint exteriorPaint;
  public BorderedText(final float textSize){
    this(Color.WHITE, Color.BLACK, textSize);}
  public BorderedText(final int interiorColor, final int exteriorColor, final float textSize){
    interiorPaint = new Paint();
    interiorPaint.setTextSize(textSize);
    interiorPaint.setColor(interiorColor);
    interiorPaint.setStyle(Style.FILL);
    interiorPaint.setAntiAlias(false);
    interiorPaint.setAlpha(255);
    exteriorPaint = new Paint();
    exteriorPaint.setTextSize(textSize);
    exteriorPaint.setColor(exteriorColor);
    exteriorPaint.setStyle(Style.FILL_AND_STROKE);
    exteriorPaint.setStrokeWidth(textSize / 8);
    exteriorPaint.setAntiAlias(false);
    exteriorPaint.setAlpha(255);}
  public void setTypeface(Typeface typeface){
    interiorPaint.setTypeface(typeface);
    exteriorPaint.setTypeface(typeface);}}