package l4stphoen1x.butterflyrecognizer;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.*;
import android.widget.*;
import l4stphoen1x.butterflyrecognizer.Utils.*;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import java.util.Objects;
public class MainActivity extends AppCompatActivity{
    private final String unbug = "";
    SharedPreferences sp;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Window window = MainActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(MainActivity.this,R.color.blue));
        Toolbar title_toolbar = findViewById(R.id.title_toolbar);
        Toolbar database_toolbar = findViewById(R.id.database_toolbar);
        ImageView realtime_view = findViewById(R.id.realtime_view);
        ImageView camera_view = findViewById(R.id.camera_view);
        ImageView gallery_view = findViewById(R.id.gallery_view);
        setSupportActionBar(title_toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        sp = getSharedPreferences(unbug, Context.MODE_PRIVATE);
        realtime_view.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this, ClassifierActivity.class);
                startActivity(intent);}});
        camera_view.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(unbug, "0");
                editor.apply();
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(intent);}});
        gallery_view.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(unbug, "0");
                editor.apply();
                Intent intent = new Intent(MainActivity.this, GalleryActivity.class);
                startActivity(intent);}});
        database_toolbar.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this, DatabaseActivity.class);
                startActivity(intent);}});}}