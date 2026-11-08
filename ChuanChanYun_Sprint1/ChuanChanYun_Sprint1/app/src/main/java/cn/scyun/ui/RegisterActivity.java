package cn.scyun.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import cn.scyun.R;
import cn.scyun.data.IndustryCatalog;
import cn.scyun.util.RegisterValidator;

/**
 * 迭代 1（Sprint 1）·「川产云」注册页。
 *
 * <p>覆盖教材第 1–2 章：环境搭建、布局管理器、常用控件、表单校验。
 * 控件映射（与《移动应用开发实践作业设计》P7 UI Mock 一致）：
 * <ul>
 *   <li>EditText × 2：手机号（inputType=phone / maxLength=11）、密码（textPassword）</li>
 *   <li>RadioGroup：性别（男 / 女）</li>
 *   <li>CheckBox × 4：感兴趣的产业（可多选，至少选 1 个，对应 BDD-02）</li>
 *   <li>Spinner：所在城市，ArrayAdapter 载入本组产业调研得到的川内城市</li>
 *   <li>Button：点击做表单校验，失败 setError()，成功 Toast + 信息摘要弹窗</li>
 * </ul>
 *
 * <p>两套布局共用一个 Activity：默认加载 activity_register.xml（ConstraintLayout，
 * 嵌套 ≤2 层）；长按标题切换到 activity_register_linear.xml（LinearLayout 教学对比版）。
 * 切换与旋转屏幕都会完整保留表单内容，便于课堂连续演示 BDD 用例。
 *
 * <p>范围说明：本迭代不做登录跳转、不做数据持久化、不申请任何权限——
 * 这些分别是迭代 2 / 3 / 5 的知识点，此处保持工程干净。
 */
public class RegisterActivity extends AppCompatActivity {

    private static final String KEY_LINEAR = "s1_use_linear";
    private static final String KEY_PHONE = "s1_phone";
    private static final String KEY_PASSWORD = "s1_password";
    private static final String KEY_FEMALE = "s1_female";
    private static final String KEY_INDUSTRIES = "s1_industries";
    private static final String KEY_CITY = "s1_city";

    private boolean useLinear = false;
    private FormState pendingState;

    private EditText etPhone;
    private EditText etPassword;
    private RadioGroup rgGender;
    private CheckBox[] industryBoxes;
    private Spinner spCity;
    private TextView tvTitle;
    private TextView tvLabelIndustry;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            useLinear = savedInstanceState.getBoolean(KEY_LINEAR, false);
            pendingState = FormState.readFrom(savedInstanceState);
        }
        render();
    }

    /** 按当前布局版本重建视图树；两套 XML 的控件 id 完全一致，所以业务代码只有一份 */
    private void render() {
        setContentView(useLinear
                ? R.layout.activity_register_linear
                : R.layout.activity_register);
        bindViews();
    }

    private void bindViews() {
        tvTitle = findViewById(R.id.tv_reg_title);
        tvLabelIndustry = findViewById(R.id.tv_label_industry);
        etPhone = findViewById(R.id.et_reg_phone);
        etPassword = findViewById(R.id.et_reg_password);
        rgGender = findViewById(R.id.rg_gender);
        spCity = findViewById(R.id.sp_city);
        btnSubmit = findViewById(R.id.btn_register_submit);

        industryBoxes = new CheckBox[]{
                findViewById(R.id.cb_industry_electronics),
                findViewById(R.id.cb_industry_food),
                findViewById(R.id.cb_industry_equipment),
                findViewById(R.id.cb_industry_medical)
        };

        // Spinner + ArrayAdapter：数据源来自本组真实产业素材常量类
        ArrayAdapter<String> cities = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, IndustryCatalog.CITIES);
        cities.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCity.setAdapter(cities);
        spCity.setSelection(IndustryCatalog.DEFAULT_CITY_INDEX);

        // 点击事件（Lambda）：迭代 1 只用一种，迭代 2 会补 XML 绑定 / 长按
        btnSubmit.setOnClickListener(v -> attemptRegister());

        // 长按标题切换布局：课堂上不必改代码即可对比两套布局的嵌套层级
        tvTitle.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                switchLayout();
                return true;
            }
        });
    }

    /** 切换布局前先留存表单状态，重建后再回填，避免演示过程中丢失输入 */
    private void switchLayout() {
        FormState snapshot = captureState();
        useLinear = !useLinear;
        render();
        applyState(snapshot);
        Toast.makeText(this,
                useLinear ? R.string.toast_layout_linear : R.string.toast_layout_constraint,
                Toast.LENGTH_SHORT).show();
    }

    /** 表单校验主流程：手机号 → 密码 → 产业兴趣 → 成功 */
    private void attemptRegister() {
        clearErrors();

        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString();

        switch (RegisterValidator.validate(phone, password)) {
            case RegisterValidator.ERR_PHONE_LENGTH:
                // BDD-01：输入 10 位手机号 → 输入框显示「请输入 11 位手机号」
                etPhone.setError(getString(R.string.error_phone));
                etPhone.requestFocus();
                return;
            case RegisterValidator.ERR_PHONE_FORMAT:
                etPhone.setError(getString(R.string.error_phone_format));
                etPhone.requestFocus();
                return;
            case RegisterValidator.ERR_PASSWORD:
                etPassword.setError(getString(R.string.error_password));
                etPassword.requestFocus();
                return;
            default:
                break;
        }

        List<String> selected = selectedIndustries();
        if (selected.isEmpty()) {
            // 至少勾选 1 个产业方向（BDD-02 的前置条件）
            industryBoxes[0].setError(getString(R.string.error_industry));
            industryBoxes[0].requestFocus();
            Toast.makeText(this, R.string.error_industry, Toast.LENGTH_SHORT).show();
            return;
        }

        String gender = rgGender.getCheckedRadioButtonId() == R.id.rb_female
                ? getString(R.string.gender_female) : getString(R.string.gender_male);
        Object cityItem = spCity.getSelectedItem();
        String city = cityItem == null
                ? IndustryCatalog.CITIES[IndustryCatalog.DEFAULT_CITY_INDEX]
                : cityItem.toString();

        showSuccess(phone, gender, TextUtils.join("、", selected), city);
    }

    /** BDD-02 / BDD-03：Toast「注册成功」+ 摘要与所填内容逐项一致 */
    private void showSuccess(String phone, String gender, String industries, String city) {
        Toast.makeText(this, R.string.toast_register_success, Toast.LENGTH_SHORT).show();

        String summary = getString(R.string.dialog_summary_head) + "\n\n"
                + getString(R.string.summary_phone, phone) + "\n"
                + getString(R.string.summary_gender, gender) + "\n"
                + getString(R.string.summary_industry, industries) + "\n"
                + getString(R.string.summary_city, city) + "\n\n"
                + getString(R.string.summary_foot);

        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_summary_title)
                .setMessage(summary)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_positive, null)
                .show();
    }

    private List<String> selectedIndustries() {
        List<String> result = new ArrayList<>();
        int[] labels = {
                R.string.industry_electronics,
                R.string.industry_food,
                R.string.industry_equipment,
                R.string.industry_medical
        };
        for (int i = 0; i < industryBoxes.length; i++) {
            if (industryBoxes[i].isChecked()) {
                result.add(getString(labels[i]));
            }
        }
        return result;
    }

    private void clearErrors() {
        etPhone.setError(null);
        etPassword.setError(null);
        tvLabelIndustry.setError(null);
        for (CheckBox box : industryBoxes) {
            box.setError(null);
        }
    }

    // ── 状态保存：旋转屏幕 / 切换布局都不丢输入（现场演示不打断） ──────────────

    private FormState captureState() {
        FormState s = new FormState();
        if (etPhone == null) {
            return s;
        }
        s.phone = etPhone.getText().toString();
        s.password = etPassword.getText().toString();
        s.female = rgGender.getCheckedRadioButtonId() == R.id.rb_female;
        s.industries = new boolean[industryBoxes.length];
        for (int i = 0; i < industryBoxes.length; i++) {
            s.industries[i] = industryBoxes[i].isChecked();
        }
        s.cityIndex = spCity.getSelectedItemPosition();
        return s;
    }

    private void applyState(FormState s) {
        if (s == null) {
            return;
        }
        etPhone.setText(s.phone);
        etPassword.setText(s.password);
        rgGender.check(s.female ? R.id.rb_female : R.id.rb_male);
        if (s.industries != null) {
            for (int i = 0; i < industryBoxes.length && i < s.industries.length; i++) {
                industryBoxes[i].setChecked(s.industries[i]);
            }
        }
        if (s.cityIndex >= 0 && s.cityIndex < IndustryCatalog.CITIES.length) {
            spCity.setSelection(s.cityIndex);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (pendingState != null) {
            applyState(pendingState);
            pendingState = null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_LINEAR, useLinear);
        captureState().writeTo(outState);
    }

    /** 仅用于「同一 Activity 内」传递表单快照，不落盘、不跨页面，符合迭代 1 的知识范围 */
    private static final class FormState {
        String phone = "";
        String password = "";
        boolean female = false;
        boolean[] industries = new boolean[4];
        int cityIndex = IndustryCatalog.DEFAULT_CITY_INDEX;

        void writeTo(Bundle out) {
            out.putString(KEY_PHONE, phone);
            out.putString(KEY_PASSWORD, password);
            out.putBoolean(KEY_FEMALE, female);
            out.putBooleanArray(KEY_INDUSTRIES, industries);
            out.putInt(KEY_CITY, cityIndex);
        }

        static FormState readFrom(Bundle in) {
            FormState s = new FormState();
            s.phone = in.getString(KEY_PHONE, "");
            s.password = in.getString(KEY_PASSWORD, "");
            s.female = in.getBoolean(KEY_FEMALE, false);
            boolean[] saved = in.getBooleanArray(KEY_INDUSTRIES);
            if (saved != null && saved.length == s.industries.length) {
                s.industries = saved;
            }
            s.cityIndex = in.getInt(KEY_CITY, IndustryCatalog.DEFAULT_CITY_INDEX);
            return s;
        }
    }
}
