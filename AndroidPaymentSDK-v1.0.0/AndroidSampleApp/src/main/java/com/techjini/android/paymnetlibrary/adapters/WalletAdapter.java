package com.techjini.android.paymnetlibrary.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.techjini.android.paymnetlibrary.R;
import com.techjini.android.paymnetlibrary.Wallet;

import java.util.ArrayList;

/**
 * Created by Nitin S.Mesta on 27/4/16.
 * // Copyright (c) 2016 Techjini Solutions. All rights reserved.
 */
public class WalletAdapter extends RecyclerView.Adapter<WalletAdapter.WalletHolder> {

    private ArrayList<Wallet> mWallets;
    private Context mContext;


    public WalletAdapter(Context context,ArrayList<Wallet> wallets)
    {
        this.mContext=context;
        mWallets=wallets;
    }


    @Override
    public WalletHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView=LayoutInflater.from(mContext).inflate(R.layout.wallet_view,parent,false);
        WalletHolder walletHolder = new WalletHolder(rootView);
        return walletHolder;
    }

    @Override
    public void onBindViewHolder(WalletHolder holder, int position) {
        Wallet wallet=mWallets.get(position);
        holder.mWalletNameView.setText(wallet.getName());
        Picasso.with(mContext).load(wallet.getLogoUrl()).into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mWallets!=null?mWallets.size():0;
    }

    public Wallet getItemAt(int position) {
        return mWallets.get(position);
    }

    public class WalletHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mWalletNameView;
        public WalletHolder(View itemView) {
            super(itemView);
            mImageView= (ImageView) itemView.findViewById(R.id.wallet_image);
            mWalletNameView= (TextView) itemView.findViewById(R.id.wallet_name);
        }
    }
}
