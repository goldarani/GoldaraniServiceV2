package ir.goldarani.service;

import android.app.*;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.*;
import java.text.NumberFormat;
import java.util.Locale;

public class MainActivity extends Activity {
    private int navy = Color.rgb(16,42,67), teal = Color.rgb(22,160,133),
            bg = Color.rgb(244,247,250), ink = Color.rgb(35,50,65), muted = Color.rgb(111,128,145), surface = Color.WHITE;
    private android.content.SharedPreferences prefs;
    private String userName = "";
    private int fontScale = 0;
    private boolean darkMode = false;
    private DB db;
    private LinearLayout root, body, top;
    private String page = "خانه";
    private final String[] pages = {"خانه","ثبت سرویس","ساختمان‌ها","ثبت خرابی","طلب‌ها","تنظیمات"};

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setStatusBarColor(navy);
        getWindow().setNavigationBarColor(navy);
        db = new DB(this);
        prefs = getSharedPreferences("goldarani_prefs", MODE_PRIVATE);
        userName = prefs.getString("user_name", "");
        darkMode = prefs.getBoolean("dark_mode", false);
        fontScale = prefs.getInt("font_scale", 0);
        applyTheme();
        if (userName.trim().isEmpty()) askUserName(true);
        else showPage("خانه");
    }

    private GradientDrawable shape(int color, int radius) {
        GradientDrawable d = new GradientDrawable(); d.setColor(color); d.setCornerRadius(radius); return d;
    }
    private TextView text(String s, int size, int color, boolean bold) {
        TextView t = new TextView(this); t.setText(s); t.setTextSize(Math.max(10, size + fontScale)); t.setTextColor(color);
        t.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL); t.setTypeface(Typeface.DEFAULT, bold?Typeface.BOLD:Typeface.NORMAL);
        t.setTextDirection(View.TEXT_DIRECTION_RTL); return t;
    }
    private LinearLayout column() {
        LinearLayout l = new LinearLayout(this); l.setOrientation(LinearLayout.VERTICAL); l.setLayoutDirection(View.LAYOUT_DIRECTION_RTL); return l;
    }
    private LinearLayout row() {
        LinearLayout l = new LinearLayout(this); l.setOrientation(LinearLayout.HORIZONTAL); l.setLayoutDirection(View.LAYOUT_DIRECTION_RTL); l.setGravity(Gravity.CENTER_VERTICAL); return l;
    }
    private void pad(View v, int x, int y) { v.setPadding(x,y,x,y); }
    private LinearLayout.LayoutParams lp(int w, int h) { return new LinearLayout.LayoutParams(w,h); }
    private void gap(LinearLayout l, int h) { View v = new View(this); l.addView(v, lp(1,h)); }
    private String money(long n) { return NumberFormat.getInstance(Locale.US).format(n) + " تومان"; }

    private void showPage(String p) {
        page=p;
        applyTheme(); root = column(); root.setBackgroundColor(bg);
        setContentView(root);
        top = column(); top.setBackgroundColor(navy); pad(top,20,18);
        TextView brand=text("سرویس‌یار گلدرانی",22,Color.WHITE,true); top.addView(brand);
        TextView sub=text("مدیریت سرویس، خرابی و حساب ساختمان‌ها",12,Color.rgb(191,215,230),false); top.addView(sub);
        root.addView(top,lp(-1,-2));
        HorizontalScrollView navScroll = new HorizontalScrollView(this); navScroll.setHorizontalScrollBarEnabled(false);
        LinearLayout nav=row(); nav.setBackgroundColor(surface); pad(nav,8,8);
        for(String item:pages) {
            TextView b=text(item,13,item.equals(p)?Color.WHITE:ink,true);
            b.setGravity(Gravity.CENTER); b.setPadding(16,12,16,12);
            b.setBackground(shape(item.equals(p)?teal:Color.rgb(239,243,247),28));
            LinearLayout.LayoutParams q=lp(-2,-2); q.setMargins(4,0,4,0); nav.addView(b,q);
            b.setOnClickListener(v->showPage(item));
        }
        navScroll.addView(nav); root.addView(navScroll,lp(-1,-2));
        ScrollView sc = new ScrollView(this); body=column(); pad(body,16,14); sc.addView(body);
        root.addView(sc,new LinearLayout.LayoutParams(-1,0,1));
        if(p.equals("خانه")) home();
        else if(p.equals("ثبت سرویس")) formService();
        else if(p.equals("ساختمان‌ها")) buildings();
        else if(p.equals("ثبت خرابی")) formFault();
        else if(p.equals("طلب‌ها")) debts();
        else if(p.equals("تنظیمات")) settings();
    }

    private void heading(String title, String subtitle) {
        body.addView(text(title,21,navy,true));
        if(subtitle!=null) body.addView(text(subtitle,13,muted,false));
        gap(body,12);
    }
    private LinearLayout card() {
        LinearLayout c=column(); c.setBackground(shape(surface,24)); pad(c,16,14);
        LinearLayout.LayoutParams p=lp(-1,-2); p.setMargins(0,0,0,12); body.addView(c,p); return c;
    }
    private void button(LinearLayout into, String label, int color, Runnable action) {
        TextView b=text(label,15,Color.WHITE,true); b.setGravity(Gravity.CENTER); b.setPadding(16,14,16,14); b.setBackground(shape(color,16));
        LinearLayout.LayoutParams p=lp(-1,-2); p.setMargins(0,6,0,4); into.addView(b,p); b.setOnClickListener(v->action.run());
    }
    private void home() {
        heading("سلام " + (userName.isEmpty()?"رفیق":userName) + "، خوش اومدی 👋","به سرویس‌یار گلدرانی خوش اومدی؛ کارهات رو مرتب و سریع مدیریت کن.");
        LinearLayout hero=card(); hero.setBackground(shape(navy,24));
        hero.addView(text("مدیریت آسون‌تر، حساب‌وکتاب مرتب‌تر",18,Color.WHITE,true));
        hero.addView(text("سرویس‌ها و پیگیری ساختمان‌ها را همین‌جا ثبت کن.",13,Color.rgb(205,222,235),false));
        gap(hero,8); button(hero,"＋ ثبت سرویس جدید",teal,()->showPage("ثبت سرویس"));
        long services=db.count("services"), faults=db.count("faults"), debt=db.sum("debts","amount");
        stat("🛠  سرویس‌های ثبت‌شده",String.valueOf(services),teal);
        stat("⚠️  خرابی‌های ثبت‌شده",String.valueOf(faults),Color.rgb(230,126,34));
        stat("💰  مجموع طلب‌ها",money(debt),Color.rgb(192,57,85));
        LinearLayout quick=card(); quick.addView(text("کارهای سریع",17,navy,true));
        button(quick,"ثبت خرابی جدید",Color.rgb(230,126,34),()->showPage("ثبت خرابی"));
        button(quick,"مشاهده طلب‌ها",Color.rgb(192,57,85),()->showPage("طلب‌ها"));
    }
    private void stat(String title,String value,int color) {
        LinearLayout c=card(); LinearLayout r=row();
        TextView dot=text("●",18,color,true); dot.setGravity(Gravity.CENTER); r.addView(dot,lp(30,42));
        LinearLayout details=column(); details.addView(text(title,14,muted,true)); details.addView(text(value,19,ink,true));
        r.addView(details,new LinearLayout.LayoutParams(0,-2,1)); c.addView(r);
    }

    private EditText field(LinearLayout box,String hint, boolean numeric) {
        EditText e=new EditText(this); e.setSingleLine(true); e.setTextSize(Math.max(13, 15 + fontScale)); e.setTextColor(ink); e.setHintTextColor(muted);
        e.setHint(hint); e.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL); e.setTextDirection(View.TEXT_DIRECTION_RTL);
        if(numeric) e.setInputType(2);
        e.setPadding(14,12,14,12); e.setBackground(shape(bg,14));
        LinearLayout.LayoutParams p=lp(-1,-2); p.setMargins(0,6,0,10); box.addView(e,p); return e;
    }
    private void saveForm(String title, String[] labels, boolean[] nums, String table, String[] keys, Runnable after) {
        LinearLayout form=card(); form.addView(text(title,18,navy,true));
        EditText[] es=new EditText[labels.length];
        for(int i=0;i<labels.length;i++) es[i]=field(form,labels[i],nums[i]);
        button(form,"ذخیره اطلاعات",teal,()->{
            for(int i=0;i<es.length;i++) if(es[i].getText().toString().trim().isEmpty()) {
                es[i].setError("این قسمت را وارد کن"); es[i].requestFocus(); return;
            }
            ContentValues cv=new ContentValues();
            for(int i=0;i<es.length;i++) cv.put(keys[i],es[i].getText().toString().trim());
            cv.put("created",System.currentTimeMillis());
            db.getWritableDatabase().insert(table,null,cv);
            Toast.makeText(this,"با موفقیت ثبت شد ✅",Toast.LENGTH_SHORT).show();
            ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(es[0].getWindowToken(),0);
            after.run();
        });
    }
    private void formService() {
        heading("ثبت سرویس جدید","مشخصات ساختمان و زمان سرویس را وارد کن.");
        saveForm("اطلاعات سرویس",new String[]{"نام ساختمان / مجتمع","نام مدیر یا مالک","شماره تماس","آدرس ساختمان","تاریخ سرویس (مثلاً ۱۴۰۵/۰۸/۰۱)","مبلغ سرویس (تومان)","توضیحات"},new boolean[]{false,false,true,false,false,true,false},
                "services",new String[]{"building","contact_name","phone","address","date","amount","notes"},()->showPage("ثبت سرویس"));
    }
    private void formFault() {
        heading("ثبت خرابی سرویس","خرابی‌ها را ثبت کن تا پیگیری‌شان راحت‌تر باشد.");
        saveForm("جزئیات خرابی",new String[]{"نام ساختمان","شماره تماس","تاریخ خرابی","شرح خرابی","وضعیت (مثلاً در انتظار / انجام شد)","هزینه (تومان)"},new boolean[]{false,true,false,false,false,true},
                "faults",new String[]{"building","phone","date","description","status","amount"},()->showPage("ثبت خرابی"));
    }
    private void buildings() {
        heading("ساختمان‌ها","فهرست بر اساس ساختمان‌هایی که ثبت کرده‌ای.");
        LinearLayout c=card();
        Cursor a=db.getReadableDatabase().rawQuery("SELECT building, MAX(phone), MAX(address), MAX(date) FROM services GROUP BY building ORDER BY building",null);
        Cursor b=db.getReadableDatabase().rawQuery("SELECT building, MAX(phone), '' AS address, MAX(date) FROM faults GROUP BY building",null);
        java.util.LinkedHashMap<String,String> items=new java.util.LinkedHashMap<>();
        while(a.moveToNext()) items.put(a.getString(0),safe(a,1)+"\n"+safe(a,2)+"\nآخرین سرویس: "+safe(a,3));
        while(b.moveToNext()) if(!items.containsKey(b.getString(0))) items.put(b.getString(0),safe(b,1)+"\n"+safe(b,2)+"\nثبت‌شده از خرابی‌ها");
        a.close(); b.close();
        if(items.isEmpty()) c.addView(text("هنوز ساختمانی ثبت نشده. از بخش «ثبت سرویس» شروع کن.",14,muted,false));
        else for(String name:items.keySet()) {
            c.addView(text("🏢  "+name,16,navy,true)); c.addView(text(items.get(name),12,muted,false)); gap(c,10);
            View line=new View(this); line.setBackgroundColor(bg); c.addView(line,lp(-1,1)); gap(c,8);
        }
        button(c,"＋ ثبت سرویس برای ساختمان",teal,()->showPage("ثبت سرویس"));
    }
    private String safe(Cursor c,int i) { String s=c.getString(i); return s==null?"—":s; }

    private void applyTheme() {
        if (darkMode) {
            navy = Color.rgb(13, 27, 42); teal = Color.rgb(26, 188, 156);
            bg = Color.rgb(22, 30, 40); ink = Color.rgb(235, 241, 247);
            muted = Color.rgb(174, 187, 200); surface = Color.rgb(34, 45, 58);
            getWindow().setStatusBarColor(navy); getWindow().setNavigationBarColor(navy);
        } else {
            navy = Color.rgb(16,42,67); teal = Color.rgb(22,160,133);
            bg = Color.rgb(244,247,250); ink = Color.rgb(35,50,65);
            muted = Color.rgb(111,128,145); surface = Color.WHITE;
            getWindow().setStatusBarColor(navy); getWindow().setNavigationBarColor(navy);
        }
    }
    private void askUserName(boolean firstRun) {
        EditText input = new EditText(this);
        input.setSingleLine(true); input.setTextSize(17); input.setHint("مثلاً امیرحسین");
        input.setText(userName); input.setPadding(28, 18, 28, 18);
        AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle(firstRun ? "خوش اومدی 👋" : "تغییر نام کاربر")
            .setMessage("دوست داری برنامه با چه اسمی صدات کنه؟")
            .setView(input)
            .setCancelable(!firstRun)
            .setNegativeButton(firstRun ? "خروج" : "انصراف", (d,w) -> { if (firstRun) finish(); })
            .setPositiveButton("ذخیره", null).create();
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String n = input.getText().toString().trim();
            if (n.isEmpty()) { input.setError("نامت رو وارد کن"); return; }
            userName = n; prefs.edit().putString("user_name", userName).apply();
            ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(input.getWindowToken(), 0);
            dialog.dismiss(); showPage("خانه");
        }));
        dialog.show();
    }
    private void settings() {
        heading("تنظیمات برنامه", "ظاهر و مشخصات سرویس‌یار رو مطابق سلیقه‌ات تنظیم کن.");
        LinearLayout profile = card();
        profile.addView(text("👤  حساب کاربری", 18, navy, true));
        profile.addView(text("نام فعلی: " + (userName.isEmpty()?"وارد نشده":userName), 14, muted, false));
        button(profile, "تغییر نام کاربر", teal, () -> askUserName(false));

        LinearLayout appearance = card();
        appearance.addView(text("🎨  ظاهر برنامه", 18, navy, true));
        appearance.addView(text("حالت روشن یا تاریک", 14, muted, false));
        Switch night = new Switch(this); night.setText("حالت شب"); night.setTextColor(ink); night.setTextSize(15); night.setChecked(darkMode);
        night.setPadding(0, 10, 0, 10); appearance.addView(night);
        night.setOnCheckedChangeListener((v, checked) -> {
            darkMode = checked; prefs.edit().putBoolean("dark_mode", darkMode).apply(); showPage("تنظیمات");
        });
        gap(appearance, 8);
        appearance.addView(text("اندازه نوشته‌ها", 14, muted, true));
        String[] sizes = {"کوچک", "معمولی", "درشت"};
        RadioGroup rg = new RadioGroup(this); rg.setOrientation(RadioGroup.HORIZONTAL); rg.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        for (int i=0;i<sizes.length;i++) {
            RadioButton rb = new RadioButton(this); rb.setText(sizes[i]); rb.setTextColor(ink); rb.setId(100+i); rg.addView(rb);
        }
        rg.check(100 + (fontScale < 0 ? 0 : fontScale > 0 ? 2 : 1)); appearance.addView(rg);
        button(appearance, "ذخیره ظاهر", teal, () -> {
            int checked=rg.getCheckedRadioButtonId(); fontScale = checked==100 ? -2 : checked==102 ? 3 : 0;
            prefs.edit().putInt("font_scale", fontScale).apply(); showPage("تنظیمات");
            Toast.makeText(this,"تنظیمات ذخیره شد",Toast.LENGTH_SHORT).show();
        });
        LinearLayout about = card();
        about.addView(text("🛠  سرویس‌یار گلدرانی", 17, navy, true));
        about.addView(text("نسخه ۱٫۱  •  مدیریت سرویس‌ها، ساختمان‌ها، خرابی‌ها و طلب‌ها", 13, muted, false));
    }

    private void debts() {
        heading("طلب‌ها","طلب‌ها را ثبت کن و بعد از تسویه، مورد مربوط را حذف کن.");
        saveForm("ثبت طلب جدید",new String[]{"نام ساختمان / شخص","شرح طلب","مبلغ طلب (تومان)","تاریخ سررسید"},new boolean[]{false,false,true,false},
                "debts",new String[]{"person","description","amount","due"},()->showPage("طلب‌ها"));
        LinearLayout summary=card(); summary.addView(text("مجموع طلب‌های ثبت‌شده",14,muted,true));
        summary.addView(text(money(db.sum("debts","amount")),23,Color.rgb(192,57,85),true));
        listRecords("debts",new String[]{"person","description","amount","due"},new String[]{"شخص / ساختمان","شرح","مبلغ","سررسید"},true);
    }
    private void listRecords(String table,String[] keys,String[] labels,boolean deletable) {
        Cursor c=db.getReadableDatabase().rawQuery("SELECT id,"+join(keys)+" FROM "+table+" ORDER BY id DESC",null);
        if(c.getCount()==0) { LinearLayout empty=card(); empty.addView(text("هنوز موردی ثبت نشده است.",14,muted,false)); c.close(); return; }
        while(c.moveToNext()) {
            final long id=c.getLong(0);
            LinearLayout box=card(); box.addView(text((deletable?"💰 ":"🧾 ")+safe(c,1),16,navy,true));
            for(int i=1;i<keys.length;i++) box.addView(text(labels[i]+": "+safe(c,i+1),13,muted,false));
            if(deletable) button(box,"حذف طلب",Color.rgb(192,57,85),()->new AlertDialog.Builder(this).setTitle("حذف طلب").setMessage("این طلب از فهرست حذف شود؟").setNegativeButton("انصراف",null).setPositiveButton("حذف", (d,w)->{db.getWritableDatabase().delete(table,"id=?",new String[]{String.valueOf(id)});showPage("طلب‌ها");}).show());
        }
        c.close();
    }
    private String join(String[] s) { String out=""; for(String x:s) out+=(out.isEmpty()?"":",")+x; return out; }

    private class DB extends SQLiteOpenHelper {
        DB(Context c){super(c,"goldarani_service.db",null,2);}
        public void onCreate(SQLiteDatabase d) {
            d.execSQL("CREATE TABLE services(id INTEGER PRIMARY KEY AUTOINCREMENT, building TEXT, contact_name TEXT, phone TEXT, address TEXT, date TEXT, amount TEXT, notes TEXT, created INTEGER)");
            d.execSQL("CREATE TABLE faults(id INTEGER PRIMARY KEY AUTOINCREMENT, building TEXT, phone TEXT, date TEXT, description TEXT, status TEXT, amount TEXT, created INTEGER)");
            d.execSQL("CREATE TABLE debts(id INTEGER PRIMARY KEY AUTOINCREMENT, person TEXT, description TEXT, amount TEXT, due TEXT, created INTEGER)");
        }
        public void onUpgrade(SQLiteDatabase d,int oldV,int newV){ if(oldV < 2) d.execSQL("DROP TABLE IF EXISTS invoices"); }
        long count(String table) { Cursor c=getReadableDatabase().rawQuery("SELECT COUNT(*) FROM "+table,null); c.moveToFirst(); long n=c.getLong(0); c.close(); return n; }
        long sum(String table,String col) {
            Cursor c=getReadableDatabase().rawQuery("SELECT "+col+" FROM "+table,null); long n=0;
            while(c.moveToNext()) { try { n+=Long.parseLong(c.getString(0).replace(",","").trim()); } catch(Exception ignored){} }
            c.close(); return n;
        }
    }
}
