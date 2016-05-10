package com.techjini.android.paymnetlibrary.interfaces;

import com.techjini.android.paymnetlibrary.Wallet;
import com.techjini.android.paymnetlibrary.network.model.response.UnoPayResponse;

import java.util.ArrayList;

/**
 * Created by Nitin S.Mesta on 2/5/16.
 * // Copyright (c) 2016 Techjini Solutions. All rights reserved.
 */
public interface GetWalletListCallBack extends UnopayServerCallBack{
    public void onGetWalletListSuccess(ArrayList<Wallet> wallets, UnoPayResponse unoPayResponse);
    public void onGetWalletListFailure(String errorResponse);
}
