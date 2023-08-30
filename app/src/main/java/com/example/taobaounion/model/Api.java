package com.example.taobaounion.model;

import com.example.taobaounion.model.bean.Categories;
import com.example.taobaounion.model.bean.HomePagerContent;
import com.example.taobaounion.model.bean.OnSellContent;
import com.example.taobaounion.model.bean.SearchRecommend;
import com.example.taobaounion.model.bean.SearchResult;
import com.example.taobaounion.model.bean.TicketParams;
import com.example.taobaounion.model.bean.TicketResult;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface Api {

    @GET("discovery/categories")
    Call<Categories> getCategories();

    @GET
    Call<HomePagerContent> getHomePagerContent(@Url String url);

    @POST("tpwd")
    Call<TicketResult> getTicket(@Body TicketParams ticketParams);

    @GET
    Call<OnSellContent>  getOnSellPageContent(@Url String url);

    @GET("search/recommend")
    Call<SearchRecommend> getRecommendWords();

    @GET("search")
    Call<SearchResult> doSearch(@Query("page") int page, @Query("keyword") String keyword);
}
