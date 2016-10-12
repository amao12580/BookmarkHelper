package pro.kisscat.www.bookmarkhelper.util.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.List;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/10/10
 * Time:10:20
 */

public class JsonUtil {
    /**
     * 将对象转换成json
     *
     * @param src 对象
     * @return 返回json字符串
     */
    public static <T> String toJson(T src) {

        try {
            return src instanceof String ? (String) src : JSON.toJSONString(src);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将json通过类型转换成对象
     *
     * @param json  json字符串
     * @param clazz 泛型类型
     * @return 返回对象, 失败返回NULL
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return clazz.equals(String.class) ? (T) json : JSON.parseObject(json, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> List<T> toList(String json, Class<T> clazz) {
        return JSON.parseArray(json, clazz);
    }

    /**
     * 将json通过类型转换成对象
     *
     * @param json          json字符串
     * @param typeReference 引用类型
     * @return 返回对象
     */
    public static <T> T fromJson(String json, TypeReference<?> typeReference) {

        try {
            return (T) (typeReference.getType().equals(String.class) ? json
                    : JSON.parseObject(json, typeReference));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}