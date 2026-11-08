package cn.scyun.util;

/**
 * 注册表单校验规则（教材第 2 章「常用控件 · 表单校验」）。
 *
 * <p>刻意写成不依赖 Android SDK 的纯 Java 类：
 * ① 校验规则与界面解耦，测试工程师可以直接阅读规则并逐条设计用例；
 * ② 迭代 2 增加登录页时，手机号规则可原样复用；
 * ③ 便于后续改成 JUnit 本地单元测试而不需要模拟器。
 *
 * <p>规则来自《移动应用开发实践作业设计》P7 与 BDD-01：
 * 手机号 11 位、以 1 开头且第二位为 3~9、密码不少于 6 位，失败由调用方用 setError() 提示。
 */
public final class RegisterValidator {

    public static final int OK = 0;
    public static final int ERR_PHONE_LENGTH = 1;
    public static final int ERR_PHONE_FORMAT = 2;
    public static final int ERR_PASSWORD = 3;

    /** 中国大陆手机号：1 开头 + 第二位 3~9 + 剩余 9 位数字，共 11 位 */
    private static final String PHONE_PATTERN = "1[3-9]\\d{9}";

    private RegisterValidator() {
    }

    /** 长度是否恰好 11 位（对应 BDD-01：输入 10 位 → 「请输入 11 位手机号」） */
    public static boolean isPhoneLengthValid(String phone) {
        return phone != null && phone.length() == 11;
    }

    /** 完整手机号校验：长度 + 号段 */
    public static boolean isPhoneValid(String phone) {
        return isPhoneLengthValid(phone) && phone.matches(PHONE_PATTERN);
    }

    /** 密码不少于 6 位 */
    public static boolean isPasswordValid(String password) {
        return password != null && password.length() >= 6;
    }

    /**
     * 按界面自上而下的顺序校验，一次只返回第一个错误，
     * 保证 setError 提示出现在用户最先需要修正的输入框上。
     */
    public static int validate(String phone, String password) {
        if (!isPhoneLengthValid(phone)) {
            return ERR_PHONE_LENGTH;
        }
        if (!isPhoneValid(phone)) {
            return ERR_PHONE_FORMAT;
        }
        if (!isPasswordValid(password)) {
            return ERR_PASSWORD;
        }
        return OK;
    }
}
