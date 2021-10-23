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

package l4stphoen1x.butterflyrecognizer;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.*;
import android.view.*;
import android.widget.*;
import com.yanzhenjie.album.*;
import com.yanzhenjie.album.api.widget.Widget;
import java.io.*;
import java.util.*;
import l4stphoen1x.butterflyrecognizer.Utils.*;
import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
public class GalleryActivity extends AppCompatActivity{
    private TextView butterfly_label, accuracy_label, butterfly_output, accuracy_output, info_output;
    private ImageView img;
    private Toolbar prediction_toolbar;
    private Toolbar info_toolbar;
    private String current_butterfly;
    private final String unbug = "";
    private static GalleryActivity instance;
    SharedPreferences sp;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        Window window = GalleryActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(GalleryActivity.this,R.color.blue));
        Toolbar chooseimg_toolbar = findViewById(R.id.chooseimg_toolbar);
        prediction_toolbar = findViewById(R.id.prediction_toolbar);
        info_toolbar = findViewById(R.id.info_toolbar);
        setSupportActionBar(chooseimg_toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        img = findViewById(R.id.img);
        butterfly_label = findViewById(R.id.butterfly_label);
        accuracy_label = findViewById(R.id.accuracy_label);
        butterfly_output = findViewById(R.id.butterfly_output);
        accuracy_output = findViewById(R.id.accuracy_output);
        info_output = findViewById(R.id.info_output);
        sp = getSharedPreferences(unbug, Context.MODE_PRIVATE);
        String unbug_str = sp.getString(unbug, "");
        Album.initialize(AlbumConfig.newBuilder(GalleryActivity.this).setAlbumLoader(new MediaLoader()).build());
        if (unbug_str.equals("1")){
        }else{
            Album.image(this) // Image selection.
                    .singleChoice()
                    .widget(Widget.newDarkBuilder(this)
                            .title("Wybierz obraz")
                            .statusBarColor(getResources().getColor(R.color.blue))
                            .toolBarColor(getResources().getColor(R.color.blue))
                            .bucketItemCheckSelector(Color.GRAY, getResources().getColor(R.color.blue))
                            .navigationBarColor(Color.WHITE)
                            .build())
                    .camera(false)
                    .columnCount(2)
                    .afterFilterVisibility(false) // Show the filtered files, but they are not available.
                    .onResult(new Action<ArrayList<AlbumFile>>() {
                        @Override
                        public void onAction(@NonNull ArrayList<AlbumFile> result) {
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString(unbug, "1");
                            editor.apply();
                            for(AlbumFile i:result){
                                    initEveryThing(ImageUtils.getBitmapByPath(GalleryActivity.this,i.getPath()));
                                    Bitmap myBitmap = BitmapFactory.decodeFile(i.getPath());
                                    Bitmap resized = Bitmap.createScaledBitmap(myBitmap, 1080, 900, false);
                                    img.setImageBitmap(resized);
                                    show_design();}}})
                    .onCancel(new Action<String>() {
                        @Override
                        public void onAction(@NonNull String result) {
                        }
                    })
                    .start();}
        chooseimg_toolbar.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Album.image(GalleryActivity.this) // Image selection.
                        .singleChoice()
                        .widget(Widget.newDarkBuilder(GalleryActivity.this)
                                .title("Wybierz obraz")
                                .statusBarColor(getResources().getColor(R.color.blue))
                                .toolBarColor(getResources().getColor(R.color.blue))
                                .bucketItemCheckSelector(Color.GRAY, getResources().getColor(R.color.blue))
                                .navigationBarColor(Color.WHITE)
                                .build())
                        .camera(false)
                        .columnCount(2)
                        .afterFilterVisibility(false) // Show the filtered files, but they are not available.
                        .onResult(new Action<ArrayList<AlbumFile>>() {
                            @Override
                            public void onAction(@NonNull ArrayList<AlbumFile> result) {
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString(unbug, "1");
                                editor.apply();
                                for(AlbumFile i:result){
                                    initEveryThing(ImageUtils.getBitmapByPath(GalleryActivity.this,i.getPath()));
                                    Bitmap myBitmap = BitmapFactory.decodeFile(i.getPath());
                                    Bitmap resized = Bitmap.createScaledBitmap(myBitmap, 1080, 900, false);
                                    img.setImageBitmap(resized);
                                    show_design();}}})
                        .onCancel(new Action<String>() {
                            @Override
                            public void onAction(@NonNull String result) {
                            }
                        })
                        .start();}});}
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;}
        return super.onOptionsItemSelected(item);}
    public void show_design(){
        prediction_toolbar.setVisibility(View.VISIBLE);
        butterfly_label.setVisibility(View.VISIBLE);
        accuracy_label.setVisibility(View.VISIBLE);
        butterfly_output.setVisibility(View.VISIBLE);
        accuracy_output.setVisibility(View.VISIBLE);
        info_toolbar.setVisibility(View.VISIBLE);
        info_output.setVisibility(View.VISIBLE);}
    public void show_result(){
        String butterfly_detected;
        if (current_butterfly.equals(getString(R.string.butterfly_name_0))){
            butterfly_detected = getString(R.string.butterfly_info_0);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_1))){
            butterfly_detected = getString(R.string.butterfly_info_1);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_2))){
            butterfly_detected = getString(R.string.butterfly_info_2);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_3))){
            butterfly_detected = getString(R.string.butterfly_info_3);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_4))){
            butterfly_detected = getString(R.string.butterfly_info_4);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_5))){
            butterfly_detected = getString(R.string.butterfly_info_5);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_6))){
            butterfly_detected = getString(R.string.butterfly_info_6);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_7))){
            butterfly_detected = getString(R.string.butterfly_info_7);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_8))){
            butterfly_detected = getString(R.string.butterfly_info_8);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_9))){
            butterfly_detected = getString(R.string.butterfly_info_9);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_10))){
            butterfly_detected = getString(R.string.butterfly_info_10);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_11))){
            butterfly_detected = getString(R.string.butterfly_info_11);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_12))){
            butterfly_detected = getString(R.string.butterfly_info_12);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_13))){
            butterfly_detected = getString(R.string.butterfly_info_13);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_14))){
            butterfly_detected = getString(R.string.butterfly_info_14);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_15))){
            butterfly_detected = getString(R.string.butterfly_info_15);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_16))){
            butterfly_detected = getString(R.string.butterfly_info_16);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_17))){
            butterfly_detected = getString(R.string.butterfly_info_17);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_18))){
            butterfly_detected = getString(R.string.butterfly_info_18);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_19))){
            butterfly_detected = getString(R.string.butterfly_info_19);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_20))){
            butterfly_detected = getString(R.string.butterfly_info_20);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_21))){
            butterfly_detected = getString(R.string.butterfly_info_21);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_22))){
            butterfly_detected = getString(R.string.butterfly_info_22);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_23))){
            butterfly_detected = getString(R.string.butterfly_info_23);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_24))){
            butterfly_detected = getString(R.string.butterfly_info_24);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_25))){
            butterfly_detected = getString(R.string.butterfly_info_25);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_26))){
            butterfly_detected = getString(R.string.butterfly_info_26);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_27))){
            butterfly_detected = getString(R.string.butterfly_info_27);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_28))){
            butterfly_detected = getString(R.string.butterfly_info_28);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_29))){
            butterfly_detected = getString(R.string.butterfly_info_29);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_30))){
            butterfly_detected = getString(R.string.butterfly_info_30);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_31))){
            butterfly_detected = getString(R.string.butterfly_info_31);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_32))){
            butterfly_detected = getString(R.string.butterfly_info_32);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_33))){
            butterfly_detected = getString(R.string.butterfly_info_33);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_34))){
            butterfly_detected = getString(R.string.butterfly_info_34);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_35))){
            butterfly_detected = getString(R.string.butterfly_info_35);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_36))){
            butterfly_detected = getString(R.string.butterfly_info_36);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_37))){
            butterfly_detected = getString(R.string.butterfly_info_37);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_38))){
            butterfly_detected = getString(R.string.butterfly_info_38);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_39))){
            butterfly_detected = getString(R.string.butterfly_info_39);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_40))){
            butterfly_detected = getString(R.string.butterfly_info_40);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_41))){
            butterfly_detected = getString(R.string.butterfly_info_41);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_42))){
            butterfly_detected = getString(R.string.butterfly_info_42);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_43))){
            butterfly_detected = getString(R.string.butterfly_info_43);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_44))){
            butterfly_detected = getString(R.string.butterfly_info_44);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_45))){
            butterfly_detected = getString(R.string.butterfly_info_45);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_46))){
            butterfly_detected = getString(R.string.butterfly_info_46);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_47))){
            butterfly_detected = getString(R.string.butterfly_info_47);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_48))){
            butterfly_detected = getString(R.string.butterfly_info_48);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_49))){
            butterfly_detected = getString(R.string.butterfly_info_49);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_50))){
            butterfly_detected = getString(R.string.butterfly_info_50);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_51))){
            butterfly_detected = getString(R.string.butterfly_info_51);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_52))){
            butterfly_detected = getString(R.string.butterfly_info_52);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_53))){
            butterfly_detected = getString(R.string.butterfly_info_53);
            info_output.setText(butterfly_detected);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_54))){
            butterfly_detected = getString(R.string.butterfly_info_54);
            info_output.setText(butterfly_detected);
        }else{
            butterfly_detected = getString(R.string.butterfly_info_unknown);
            info_output.setText(butterfly_detected);}}
    @SuppressLint("SetTextI18n")
    public void initEveryThing(Bitmap bitmap){
        try{
            Classifier classifier = new ClassifierFloatMobileNet(this, Classifier.Device.CPU, 5);
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            if (width == 224 | height == 224){
                List<Classifier.Recognition> results= classifier.recognizeImage(bitmap);
                if(results.size()==0){
                    Toast.makeText(GalleryActivity.this, getString(R.string.image_not_found), Toast.LENGTH_SHORT).show();
                }else{
                    float abc = results.get(0).getConfidence();
                    abc = abc*100;
                    butterfly_output.setText(results.get(0).getTitle());
                    current_butterfly = results.get(0).getTitle();
                    accuracy_output.setText(abc+"%");
                    show_result();}}
            else{
                List<Classifier.Recognition> results= classifier.recognizeImage(ImageUtils.scaleImage(bitmap,224,224));
                if(results.size()==0){
                    Toast.makeText(GalleryActivity.this, getString(R.string.image_not_found), Toast.LENGTH_SHORT).show();
                }else{
                    float abc = results.get(0).getConfidence();
                    abc = abc*100;
                    butterfly_output.setText(results.get(0).getTitle());
                    current_butterfly = results.get(0).getTitle();
                    accuracy_output.setText(abc+"%");
                    show_result();}}
        }catch (IOException e){
            e.printStackTrace();}}}