package com.techjini.android.paymnetlibrary.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.techjini.android.paymnetlibrary.R;
import com.techjini.android.paymnetlibrary.UnoPayParams;
import com.techjini.android.paymnetlibrary.Utils;
import com.techjini.android.paymnetlibrary.Wallet;
import com.techjini.android.paymnetlibrary.adapters.WalletAdapter;
import com.techjini.android.paymnetlibrary.interfaces.FragmentInteractionListener;
import com.techjini.android.paymnetlibrary.interfaces.GetWalletListCallBack;
import com.techjini.android.paymnetlibrary.network.UnoPayServerTransactions;
import com.techjini.android.paymnetlibrary.network.model.response.UnoPayResponse;
import com.techjini.android.paymnetlibrary.views.ListItemBaseView;
import com.techjini.android.paymnetlibrary.views.RecyclerListView;
import com.techjini.android.paymnetlibrary.views.SimpleDividerItemDecoration;

import java.util.ArrayList;


public class WalletList extends BaseFragment implements RecyclerListView.OnItemClickListener, GetWalletListCallBack {
    private OnWalletInteraction onWalletInteraction;
    private TextView mDefaultTextView;
    /*private LinearLayout mLoadingLayout;*/
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private WalletAdapter mWalletAdapter;
    private RecyclerListView mWalletsRecyclerView;
    private UnoPayServerTransactions unoPayServerTransactions;


    public static final String UNOPAY_PARAMS = "unopayParams";
    private UnoPayParams mUnoPayParams;

    public WalletList() {
        // Required empty public constructor
    }

    public static WalletList newInstance(UnoPayParams unoPayParams) {
        WalletList fragment = new WalletList();
        Bundle bundle = new Bundle();
        bundle.putSerializable(UNOPAY_PARAMS, unoPayParams);
        fragment.setArguments(bundle);
        return fragment;
    }

    private ProgressDialog progressDialog = null;


    private void showLoading() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching wallets");
        progressDialog.show();

    }

    private void hideLoading() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mUnoPayParams = (UnoPayParams) getArguments().getSerializable(UNOPAY_PARAMS);
            unoPayServerTransactions = new UnoPayServerTransactions(getContext(), this, mUnoPayParams.isProduction());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_wallet_list, container, false);

        mDefaultTextView = (TextView) rootView.findViewById(R.id.default_textview);
        mWalletsRecyclerView = (RecyclerListView) rootView.findViewById(R.id.wallet_list);
        mWalletsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mWalletsRecyclerView.setOnItemClickListener(this);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_view);
        mWalletsRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                getWallets();
            }
        });
        getWallets();
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnWalletInteraction) {
            onWalletInteraction = (OnWalletInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnWalletInteraction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onWalletInteraction = null;
    }

    public void getWallets() {

        if (Utils.isOnline(getContext())) {
            if (onWalletInteraction != null) {
                onWalletInteraction.isServerTransaction(true);
            }
            showLoading();
            unoPayServerTransactions.getOTPEnabledWallets(mUnoPayParams.getPartnerId(), mUnoPayParams.getMerchantSdkKey());
        } else {
            if (onWalletInteraction != null) {
                onWalletInteraction.isServerTransaction(false);
            }
            showDefaultView(true);
            setMessageOnDefaultView(getString(R.string.no_internet));
        }

    }


    private void showDefaultView(boolean show) {
        mDefaultTextView.setVisibility(show ? View.VISIBLE : View.GONE);
        mWalletsRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);

    }

    private void setMessageOnDefaultView(String message) {
        mDefaultTextView.setText(message);
    }


    @Override
    public void onGetWalletListSuccess(ArrayList<Wallet> wallets, UnoPayResponse unoPayResponse) {
        hideLoading();
        showDefaultView(false);
        if (onWalletInteraction != null) {
            onWalletInteraction.isServerTransaction(false);
        }
        mWalletAdapter = new WalletAdapter(getActivity(), wallets);
        mWalletsRecyclerView.setAdapter(mWalletAdapter);
    }

    @Override
    public void onGetWalletListFailure(String errorResponse) {
        //TODO handle the error scenario
        if (onWalletInteraction != null) {
            onWalletInteraction.isServerTransaction(false);
        }
        hideLoading();
        showDefaultView(true);
        setMessageOnDefaultView(errorResponse);
    }

    @Override
    public void onItemClick(ListItemBaseView view, int position) {
        Wallet wallet = mWalletAdapter.getItemAt(position);
        if (onWalletInteraction != null) {
            onWalletInteraction.onWalletSelected(wallet);
        }

    }


    public interface OnWalletInteraction extends FragmentInteractionListener {
        // TODO: Update argument type and name
        void onWalletSelected(Wallet wallet);

    }
}
