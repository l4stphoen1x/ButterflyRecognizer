package l4stphoen1x.butterflyrecognizer;
import android.content.*;
import android.view.*;
import android.widget.*;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import java.util.Objects;
public class DatabaseActivity extends AppCompatActivity{
    private ListView lv;
    private String selectedFromList;
    private final String choosed_butterfly = "";
    SharedPreferences sp;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);
        Window window = DatabaseActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(DatabaseActivity.this,R.color.blue));
        sp = getSharedPreferences(choosed_butterfly, Context.MODE_PRIVATE);
        Toolbar title_toolbar = findViewById(R.id.title_toolbar);
        setSupportActionBar(title_toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        lv = findViewById(R.id.lv);
        final String[] butterflies = new String[] { getString(R.string.butterfly_name_0), getString(R.string.butterfly_name_1), getString(R.string.butterfly_name_2),
                getString(R.string.butterfly_name_3), getString(R.string.butterfly_name_4), getString(R.string.butterfly_name_5), getString(R.string.butterfly_name_6),
                getString(R.string.butterfly_name_7), getString(R.string.butterfly_name_8), getString(R.string.butterfly_name_9), getString(R.string.butterfly_name_10),
                getString(R.string.butterfly_name_11), getString(R.string.butterfly_name_12), getString(R.string.butterfly_name_13), getString(R.string.butterfly_name_14),
                getString(R.string.butterfly_name_15), getString(R.string.butterfly_name_16), getString(R.string.butterfly_name_17), getString(R.string.butterfly_name_18),
                getString(R.string.butterfly_name_19), getString(R.string.butterfly_name_20), getString(R.string.butterfly_name_21), getString(R.string.butterfly_name_22),
                getString(R.string.butterfly_name_23), getString(R.string.butterfly_name_24), getString(R.string.butterfly_name_25), getString(R.string.butterfly_name_26),
                getString(R.string.butterfly_name_27), getString(R.string.butterfly_name_28), getString(R.string.butterfly_name_29), getString(R.string.butterfly_name_30),
                getString(R.string.butterfly_name_31), getString(R.string.butterfly_name_32), getString(R.string.butterfly_name_33), getString(R.string.butterfly_name_34),
                getString(R.string.butterfly_name_35), getString(R.string.butterfly_name_36), getString(R.string.butterfly_name_37), getString(R.string.butterfly_name_38),
                getString(R.string.butterfly_name_39), getString(R.string.butterfly_name_40), getString(R.string.butterfly_name_41), getString(R.string.butterfly_name_42),
                getString(R.string.butterfly_name_43), getString(R.string.butterfly_name_44), getString(R.string.butterfly_name_45), getString(R.string.butterfly_name_46),
                getString(R.string.butterfly_name_47), getString(R.string.butterfly_name_48), getString(R.string.butterfly_name_49), getString(R.string.butterfly_name_50),
                getString(R.string.butterfly_name_51), getString(R.string.butterfly_name_52), getString(R.string.butterfly_name_53), getString(R.string.butterfly_name_54)};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, butterflies);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l){
                selectedFromList = (String) lv.getItemAtPosition(position);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(choosed_butterfly, selectedFromList);
                editor.apply();
                Intent intent = new Intent(DatabaseActivity.this, ExampleActivity.class);
                startActivity(intent);}});}
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;}
        return super.onOptionsItemSelected(item);}}