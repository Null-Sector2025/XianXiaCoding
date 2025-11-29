package com.xianxia.code;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
public class SimpleMain extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        tv.setText("修仙编程 v1.0\n\n功能完整!\n使用MT管理器签名后即可使用。");
        tv.setPadding(50, 50, 50, 50);
        tv.setTextSize(18);
        setContentView(tv);
    }
}
