package com.cloud4magic.freecast.utils;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Date   2017/7/14
 * Editor  Misuzu
 */

public interface ApiService {

    /**
     * 获取下载链接
     */
    @GET
    public Observable<ResponseBody> getDownloadLink(@Url String url);

    /**
     * 下载文件
     */
    @GET
    public Observable<ResponseBody> downloadFile(@Url String url);
}
