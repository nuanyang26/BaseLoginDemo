package top.nuanyang26.utils;

// redis常用前缀
public class Const {
    public static final String JWT_BLACK_LIST = "jwt:blacklist:";
    public static final int ORDER_CORS = -102;
    public static final int ORDER_LIMIT = ORDER_CORS + 1;
    public static final String FLOW_LIMIT_COUNTER = "flow:counter"; // 计数器
    public static final String FLOW_LIMIT_BLOCK = "flow:block"; // 封禁

    public static final String VERIFY_EMAIL_LIMIT = "verify:email:limit";
    public static final String VERIFY_EMAIL_DATA = "verify:email:data";
    public static final long VERIFY_EMAIL_TIMEOUT = 3; // 本次验证的过期时间
    public static final int VERIFY_EMAIL_BLOCKTIME = 60; // 60秒内请求一次 --- 单次限流
}
