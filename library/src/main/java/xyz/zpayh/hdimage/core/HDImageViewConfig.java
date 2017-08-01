/*
 *
 *  * Copyright 2017 陈志鹏
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package xyz.zpayh.hdimage.core;

import android.content.Context;
import android.util.Log;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import xyz.zpayh.hdimage.datasource.Interceptor;
import xyz.zpayh.hdimage.datasource.interceptor.AssetInterceptor;
import xyz.zpayh.hdimage.datasource.interceptor.ContentInterceptor;
import xyz.zpayh.hdimage.datasource.interceptor.FileInterceptor;
import xyz.zpayh.hdimage.datasource.interceptor.NetworkInterceptor;
import xyz.zpayh.hdimage.datasource.interceptor.ResourceInterceptor;
import xyz.zpayh.hdimage.util.Preconditions;

/**
 * 文 件 名: HDImageViewConfig
 * 创 建 人: 陈志鹏
 * 创建日期: 2017/7/29 14:19
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */

public class HDImageViewConfig {
    private static final String TAG = "HDImageViewConfig";
    //private final BitmapDataSource mBitmapDataSource;
    private final Interpolator mScaleAnimationInterpolator;
    private final Interpolator mTranslationAnimationInterpolator;

    private final List<Interceptor> mInterceptors;

    public static Builder newBuilder(Context context){
        return new Builder(context);
    }

    private HDImageViewConfig(Builder builder){
        mScaleAnimationInterpolator = builder.mScaleAnimationInterpolator == null ?
                new DecelerateInterpolator() : builder.mScaleAnimationInterpolator;
        mTranslationAnimationInterpolator = builder.mTranslationAnimationInterpolator == null ?
                new DecelerateInterpolator() : builder.mTranslationAnimationInterpolator;

        mInterceptors = new ArrayList<>();


        mInterceptors.add(new ResourceInterceptor(builder.mContext.getResources()));
        mInterceptors.add(new AssetInterceptor(builder.mContext.getAssets()));
        mInterceptors.add(new ContentInterceptor(builder.mContext));
        mInterceptors.add(new FileInterceptor());
       

        Interceptor interceptor = getInterceptor(builder.mContext);
        if (interceptor != null){
            mInterceptors.add(interceptor);
        }
        interceptor = getInterceptor();
        if (interceptor != null){
            mInterceptors.add(interceptor);
        }else{
            mInterceptors.add(new NetworkInterceptor(builder.mContext));
        }

        mInterceptors.addAll(builder.mInterceptors);
    }

    @SuppressWarnings({"unchecked", "deprecation"})
    private Interceptor getInterceptor(Context context) {
        Interceptor interceptor = null;

        try {
            Class<Interceptor> clazz =
                    (Class<Interceptor>) Class.forName("xyz.zpayh.hdimage.datasource.interceptor.GlideInterceptor");
            Constructor<Interceptor> constructor = clazz.getConstructor(Context.class);
            interceptor = constructor.newInstance(context);
            Log.d(TAG, "自动配置GlideInterceptor");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return interceptor;
    }

    @SuppressWarnings({"unchecked", "deprecation"})
    private Interceptor getInterceptor() {
        Interceptor interceptor = null;

        try {
            Class<Interceptor> clazz =
                    (Class<Interceptor>) Class.forName("xyz.zpayh.hdimage.datasource.interceptor.FrescoInterceptor");
            interceptor = clazz.newInstance();
            Log.d(TAG, "自动配置FrescoInterceptor");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return interceptor;
    }

    public Interpolator getScaleAnimationInterpolator() {
        return mScaleAnimationInterpolator;
    }

    public Interpolator getTranslationAnimationInterpolator() {
        return mTranslationAnimationInterpolator;
    }

    public List<Interceptor> getInterceptors() {
        return mInterceptors;
    }

    public static class Builder {

        //private BitmapDataSource mBitmapDataSource;
        private Interpolator mScaleAnimationInterpolator;
        private Interpolator mTranslationAnimationInterpolator;
        private Context mContext;
        private List<Interceptor> mInterceptors;
        private Builder(Context context){
            mContext = Preconditions.checkNotNull(context);
            mInterceptors = new ArrayList<>();
        }

        public Builder setScaleAnimationInterpolator(Interpolator scaleAnimationInterpolator) {
            mScaleAnimationInterpolator = scaleAnimationInterpolator;
            return this;
        }

        public Builder setTranslationAnimationInterpolator(Interpolator translationAnimationInterpolator) {
            mTranslationAnimationInterpolator = translationAnimationInterpolator;
            return this;
        }

        public Builder addInterceptor(Interceptor interceptor){
            mInterceptors.add(interceptor);
            return this;
        }

        public HDImageViewConfig build(){
            return new HDImageViewConfig(this);
        }
    }
}
