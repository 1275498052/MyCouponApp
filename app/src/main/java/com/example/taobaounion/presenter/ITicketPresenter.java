package com.example.taobaounion.presenter;

import com.example.taobaounion.base.IBasePresenter;
import com.example.taobaounion.view.ITicketPagerCallback;

public  interface ITicketPresenter extends IBasePresenter<ITicketPagerCallback> {

    void getTicket(String title, String url, String cover);
}
