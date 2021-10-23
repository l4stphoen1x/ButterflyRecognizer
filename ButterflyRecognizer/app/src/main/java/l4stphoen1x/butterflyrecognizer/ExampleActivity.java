package l4stphoen1x.butterflyrecognizer;
import android.content.*;
import android.graphics.*;
import android.view.*;
import android.widget.*;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import java.util.Objects;
public class ExampleActivity extends AppCompatActivity{
    private TextView info_tv, toolbar_tv;
    private ImageView img;
    private final String choosed_butterfly = "";
    SharedPreferences sp;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        Window window = ExampleActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(ExampleActivity.this,R.color.blue));
        sp = getSharedPreferences(choosed_butterfly, Context.MODE_PRIVATE);
        toolbar_tv = (TextView)findViewById(R.id.toolbar_tv);
        Toolbar title_toolbar = findViewById(R.id.title_toolbar);
        setSupportActionBar(title_toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        img = findViewById(R.id.img);
        info_tv = findViewById(R.id.info_tv);
        show_result();}
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;}
        return super.onOptionsItemSelected(item);}
    public void show_result(){
        String current_butterfly = sp.getString(choosed_butterfly, "");
        toolbar_tv.setText(current_butterfly);
        Bitmap myBitmap;
        String butterfly_detected;
        if (current_butterfly.equals(getString(R.string.butterfly_name_0))){
            butterfly_detected = getString(R.string.butterfly_info_0);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_00);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_1))){
            butterfly_detected = getString(R.string.butterfly_info_1);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_01);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_2))){
            butterfly_detected = getString(R.string.butterfly_info_2);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_02);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_3))){
            butterfly_detected = getString(R.string.butterfly_info_3);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_03);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_4))){
            butterfly_detected = getString(R.string.butterfly_info_4);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_04);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_5))){
            butterfly_detected = getString(R.string.butterfly_info_5);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_05);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_6))){
            butterfly_detected = getString(R.string.butterfly_info_6);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_06);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_7))){
            butterfly_detected = getString(R.string.butterfly_info_7);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_07);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_8))){
            butterfly_detected = getString(R.string.butterfly_info_8);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_08);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_9))){
            butterfly_detected = getString(R.string.butterfly_info_9);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_09);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_10))){
            butterfly_detected = getString(R.string.butterfly_info_10);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_10);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_11))){
            butterfly_detected = getString(R.string.butterfly_info_11);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_11);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_12))){
            butterfly_detected = getString(R.string.butterfly_info_12);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_12);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_13))){
            butterfly_detected = getString(R.string.butterfly_info_13);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_13);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_14))){
            butterfly_detected = getString(R.string.butterfly_info_14);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_14);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_15))){
            butterfly_detected = getString(R.string.butterfly_info_15);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_15);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_16))){
            butterfly_detected = getString(R.string.butterfly_info_16);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_16);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_17))){
            butterfly_detected = getString(R.string.butterfly_info_17);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_17);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_18))){
            butterfly_detected = getString(R.string.butterfly_info_18);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_18);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_19))){
            butterfly_detected = getString(R.string.butterfly_info_19);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_19);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_20))){
            butterfly_detected = getString(R.string.butterfly_info_20);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_20);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_21))){
            butterfly_detected = getString(R.string.butterfly_info_21);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_21);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_22))){
            butterfly_detected = getString(R.string.butterfly_info_22);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_22);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_23))){
            butterfly_detected = getString(R.string.butterfly_info_23);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_23);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_24))){
            butterfly_detected = getString(R.string.butterfly_info_24);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_24);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_25))){
            butterfly_detected = getString(R.string.butterfly_info_25);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_25);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_26))){
            butterfly_detected = getString(R.string.butterfly_info_26);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_26);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_27))){
            butterfly_detected = getString(R.string.butterfly_info_27);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_27);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_28))){
            butterfly_detected = getString(R.string.butterfly_info_28);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_28);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_29))){
            butterfly_detected = getString(R.string.butterfly_info_29);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_29);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_30))){
            butterfly_detected = getString(R.string.butterfly_info_30);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_30);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_31))){
            butterfly_detected = getString(R.string.butterfly_info_31);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_31);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_32))){
            butterfly_detected = getString(R.string.butterfly_info_32);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_32);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_33))){
            butterfly_detected = getString(R.string.butterfly_info_33);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_33);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_34))){
            butterfly_detected = getString(R.string.butterfly_info_34);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_34);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_35))){
            butterfly_detected = getString(R.string.butterfly_info_35);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_35);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_36))){
            butterfly_detected = getString(R.string.butterfly_info_36);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_36);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_37))){
            butterfly_detected = getString(R.string.butterfly_info_37);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_37);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_38))){
            butterfly_detected = getString(R.string.butterfly_info_38);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_38);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_39))){
            butterfly_detected = getString(R.string.butterfly_info_39);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_39);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_40))){
            butterfly_detected = getString(R.string.butterfly_info_40);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_40);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_41))){
            butterfly_detected = getString(R.string.butterfly_info_41);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_41);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_42))){
            butterfly_detected = getString(R.string.butterfly_info_42);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_42);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_43))){
            butterfly_detected = getString(R.string.butterfly_info_43);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_43);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_44))){
            butterfly_detected = getString(R.string.butterfly_info_44);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_44);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_45))){
            butterfly_detected = getString(R.string.butterfly_info_45);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_45);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_46))){
            butterfly_detected = getString(R.string.butterfly_info_46);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_46);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_47))){
            butterfly_detected = getString(R.string.butterfly_info_47);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_47);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_48))){
            butterfly_detected = getString(R.string.butterfly_info_48);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_48);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_49))){
            butterfly_detected = getString(R.string.butterfly_info_49);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_49);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_50))){
            butterfly_detected = getString(R.string.butterfly_info_50);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_50);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_51))){
            butterfly_detected = getString(R.string.butterfly_info_51);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_51);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_52))){
            butterfly_detected = getString(R.string.butterfly_info_52);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_52);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_53))){
            butterfly_detected = getString(R.string.butterfly_info_53);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_53);
        }else if(current_butterfly.equals(getString(R.string.butterfly_name_54))){
            butterfly_detected = getString(R.string.butterfly_info_54);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_54);
        }else{
            butterfly_detected = getString(R.string.butterfly_info_unknown);
            info_tv.setText(butterfly_detected);
            myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mark);}
        Bitmap resized = Bitmap.createScaledBitmap(myBitmap, 1080, 900, false);
        img.setImageBitmap(resized);}}