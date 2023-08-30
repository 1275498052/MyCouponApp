package com.example.taobaounion.utils;

public class UrlUtils {
    public static String createHomePagerUrl(int materialId, int page) {
        return "discovery/" + materialId + "/" + page;
    }

    public static String getCoverPath(String url, int coverSize) {
        return "https:" + url + "_" + coverSize + "x" + coverSize + ".jpg";
    }

    public static String getCoverPath(String pict_url) {
        if(pict_url.startsWith("http") || pict_url.startsWith("https")) {
            return pict_url;
        } else {
            return "https:" + pict_url;
        }
    }

    public static String getOnSellPageUrl(int currentPage) {
        return "onSell/" + currentPage;
    }

}
