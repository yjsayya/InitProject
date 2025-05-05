package init.project.global.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonUtil {

    private static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping() // <, > 같은 HTML 문자 이스케이핑 방지
            .serializeNulls()      // null도 JSON으로 직렬화
            .setPrettyPrinting()   // 보기 좋게 출력 (선택)
            .create();

    public static Gson getGson() {
        return GSON;
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }

    public static String toJson(Object object) {
        return GSON.toJson(object);
    }

}