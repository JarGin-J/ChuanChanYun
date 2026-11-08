package cn.scyun.data;

/**
 * 本组（蜀芯小组）为迭代 1 准备的真实产业素材，集中放在一处便于后续迭代替换。
 *
 * <p>所选方向：电子信息（四川六大优势产业之一）。
 * 依据四川电子信息产业集群的实际分布选择注册页「所在城市」下拉项：
 * 成都（高新区、天府软件园、双流综保区）、绵阳（中国科技城）、宜宾（智能终端产业园）、
 * 遂宁（成渝配套电子电路）、德阳、乐山、泸州、自贡。
 * 前四个城市为川内电子信息产业主要承载地，默认落在成都。
 *
 * <p>迭代 2 起，注册收集到的产业偏好与城市会经 Intent 传向首页，
 * 迭代 3 起决定资讯内容；本迭代只负责采集与校验，不做持久化。
 */
public final class IndustryCatalog {

    /** 本组所选产业方向 */
    public static final String FOCUS_INDUSTRY = "电子信息";

    /** 方向说明，写在作业卡与说明文档中，便于答辩时交代素材来源 */
    public static final String FOCUS_NOTE =
            "成都高新区 / 天府软件园 · 集成电路与新型显示产业链";

    /** 注册页「所在城市」Spinner 的数据源 */
    public static final String[] CITIES = {
            "成都", "绵阳", "宜宾", "遂宁", "德阳", "乐山", "泸州", "自贡"
    };

    /** 默认选中项：本组调研所在地 */
    public static final int DEFAULT_CITY_INDEX = 0;

    private IndustryCatalog() {
        // 常量类，禁止实例化
    }
}
