package com.k2archer.common.utils;

import com.google.gson.Gson;
import com.k2archer.common.BaseResponseBody;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class GsonResponseParse<T> implements ParameterizedType {
//    private final UtilsLog lg = UtilsLog.getLogger(GsonResponsePasare.class);

    public T deal(String response) {
//            Type gsonType = new ParameterizedType() {//...};
// 不建议该方式，推荐采用GsonResponseParse实现ParameterizedType.因为getActualTypeArguments这里涉及获取GsonResponsePasare的泛型集合
        Type gsonType = this;

        BaseResponseBody<T> commonResponse = new Gson().fromJson(response, gsonType);
//        lg.e("Data is : " + commonResponse.getData(), "Class Type is : " + commonResponse.getData().getClass().toString());
        return commonResponse.getData();
    }

    @Override
    public Type[] getActualTypeArguments() {
        Class clz = this.getClass();
        //这里必须注意在外面使用new GsonResponseParse<GsonResponseParse.DataInfo>(){};实例化时必须带上{},否则获取到的superclass为Object
        Type superclass = clz.getGenericSuperclass(); //getGenericSuperclass()获得带有泛型的父类
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return parameterized.getActualTypeArguments();
    }

    @Override
    public Type getOwnerType() {
        return null;
    }

    @Override
    public Type getRawType() {
        return BaseResponseBody.class;
    }
}
