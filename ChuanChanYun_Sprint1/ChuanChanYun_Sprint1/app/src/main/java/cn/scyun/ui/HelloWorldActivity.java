package cn.scyun.ui;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import cn.scyun.R;

/**
 * 功能要求 1 的验证载体：Android Studio + SDK + AVD 跑通的第一个页面。
 *
 * <p>用纯代码搭一个最小 View 树（不写 XML），正好用来口述「java / res / Manifest 各放什么」：
 * 本页全部在 java 包里，注册页的界面则放在 res/layout，两种做法对照讲解。
 *
 * <p>它不是桌面入口，课堂演示用 adb 拉起：
 * <pre>adb shell am start -n cn.scyun/.ui.HelloWorldActivity</pre>
 */
public class HelloWorldActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setBackgroundColor(ContextCompat.getColor(this, R.color.scyun_bg));
        root.setPadding(48, 48, 48, 48);

        TextView hello = new TextView(this);
        hello.setText(R.string.hello_text);
        hello.setGravity(Gravity.CENTER);
        hello.setTextSize(20);
        hello.setLineSpacing(12, 1);
        hello.setTextColor(ContextCompat.getColor(this, R.color.scyun_primary));
        root.addView(hello);

        TextView badge = new TextView(this);
        badge.setText(R.string.hello_badge);
        badge.setGravity(Gravity.CENTER);
        badge.setTextSize(13);
        badge.setPadding(0, 32, 0, 0);
        badge.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        root.addView(badge);

        setContentView(root);
    }
}
