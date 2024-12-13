package com.hi.live.activity.purchase;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.android.billingclient.api.BillingClient;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.hi.live.R;
import com.hi.live.SessionManager__a;
import com.hi.live.adaptor.MoreCoinAdapter_a;
import com.hi.live.databinding.BottomSheetCardBinding;
import com.hi.live.databinding.BottomSheetPaymentBinding;
import com.hi.live.databinding.FragmentPlanListBinding;
import com.hi.live.models.PlanRoot;
import com.hi.live.models.RestResponse;
import com.hi.live.models.User;
import com.hi.live.retrofit.Const_a;
import com.hi.live.retrofit.RetrofitBuilder_a;
import com.razorpay.Checkout;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.model.StripeIntent;
import com.stripe.android.view.CardInputWidget;
import com.stripe.param.PaymentIntentCreateParams;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlanListFragment_a extends Fragment {
    private static final String STR_STRIPE = "stripe";
    private static final String STR_GP = "google pay";
    private static final String TAG = CoinPlanActivity_a.TAG + " planfrag";
    FragmentPlanListBinding binding;
    Checkout checkout = new Checkout();

    BottomSheetCardBinding bottomSheetCardBinding;
    String apiKey;
    String productId;
    private String paymentGateway;
    private SessionManager__a sessionManager;
    private String userId;
    private String planId;
    private Stripe stripe;
    private User user;
    private BottomSheetDialog bottomSheetDialog;

    private double totalAmount = 1;
    private String currency;
    private String country;
    private MyPlayStoreBilling myPlayStoreBilling;
    private boolean isConnected;


    public PlanListFragment_a() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_plan_list, container, false);
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        return binding.getRoot();
    }

    private void getData() {
        binding.pd.setVisibility(View.VISIBLE);
        Call<PlanRoot> call = RetrofitBuilder_a.create().getPlanListByPaymentGateway(Const_a.DEVKEY);
        call.enqueue(new Callback<PlanRoot>() {
            @Override
            public void onResponse(Call<PlanRoot> call, Response<PlanRoot> response) {
                if (response.code() == 200 && response.body().isStatus() && !response.body().getData().isEmpty()) {
//                    String currentCurrency = ((CoinPlanActivity_a) getActivity()).getCurrentCurrency();
                    MoreCoinAdapter_a moreCoinAdapter = new MoreCoinAdapter_a(response.body().getData(), country, dataItem -> {
                        binding.pd.setVisibility(View.VISIBLE);
                        openBottomSheet(dataItem);
                    });
                    binding.rvMoreCoins.setAdapter(moreCoinAdapter);
                }
                binding.pd.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<PlanRoot> call, Throwable t) {
//ll
            }
        });
    }


    private void openBottomSheet(PlanRoot.DataItem dataItem) {
        binding.pd.setVisibility(View.GONE);
        if (getActivity() == null) return;
        bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.CustomBottomSheetDialogTheme);
//        bottomSheetDialog.setOnShowListener(dialog -> {
//            BottomSheetDialog d = (BottomSheetDialog) dialog;
//            FrameLayout bottomSheet = (FrameLayout) d.findViewById(R.id.design_bottom_sheet);
//            if (bottomSheet != null) {
//                BottomSheetBehavior.from(bottomSheet)
//                        .setState(BottomSheetBehavior.STATE_EXPANDED);
//            }
//        });
        BottomSheetPaymentBinding bottomSheetPaymentBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.bottom_sheet_payment, null, false);
        bottomSheetDialog.setContentView(bottomSheetPaymentBinding.getRoot());
        bottomSheetDialog.show();
        bottomSheetPaymentBinding.btnclose.setOnClickListener(v ->
                bottomSheetDialog.dismiss());

        List<String> paymentGateways = ((CoinPlanActivity_a) getActivity()).getPaymentGateways();


        if (paymentGateways.contains(STR_GP)) {
            bottomSheetPaymentBinding.lytgooglepay.setVisibility(View.VISIBLE);

            bottomSheetPaymentBinding.lytgooglepay.setOnClickListener(v -> {
                Log.d(CoinPlanActivity_a.TAG, "openBottomSheet: gpay");
                paymentGateway = STR_GP;
                binding.pd.setVisibility(View.VISIBLE);
                buyItem(dataItem);
            });
        } else {
            bottomSheetPaymentBinding.lytgooglepay.setVisibility(View.GONE);
        }
        if (paymentGateways.contains("razor pay")) {

            bottomSheetPaymentBinding.lytrazorpay.setVisibility(View.VISIBLE);
            bottomSheetPaymentBinding.lytrazorpay.setOnClickListener(v -> {
                paymentGateway = "razor pay";
                buyItem(dataItem);
            });
        } else {
            bottomSheetPaymentBinding.lytrazorpay.setVisibility(View.GONE);
        }
        if (paymentGateways.contains(STR_STRIPE)) {
            bottomSheetPaymentBinding.lytstripe.setVisibility(View.VISIBLE);
            bottomSheetPaymentBinding.lytstripe.setOnClickListener(v -> {
                Log.d(TAG, "openBottomSheet: df " + totalAmount);
                if (totalAmount >= 1) {
                    Log.d(TAG, "openBottomSheet: gfdfdfdf ");
                    paymentGateway = STR_STRIPE;
                    buyItem(dataItem);
                } else {
                    Toast.makeText(getActivity(), "Please use another payment method", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            bottomSheetPaymentBinding.lytstripe.setVisibility(View.GONE);
        }


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        sessionManager = new SessionManager__a(getActivity());
        userId = sessionManager.getUser().getId();
        user = sessionManager.getUser();
        Log.d(TAG, "onActivityCreated: " + paymentGateway);

        currency = "INR";
        country = "IN";

        getData();

        myPlayStoreBilling = new MyPlayStoreBilling(getActivity(), new MyPlayStoreBilling.OnPurchaseComplete() {
            @Override
            public void onConnected(boolean isConnect) {
                if (isConnect) {
                    isConnected = true;
                }
            }

            @Override
            public void onPurchaseResult(boolean isPurchaseSuccess) {
                Log.d(TAG, "onPurchaseResult: susss ");
                if (isPurchaseSuccess) {
                    callPurchaseDoneApi();
                }
            }
        });

        Checkout.preload(getActivity());
        checkout.setKeyID(sessionManager.getSetting().getRazorPayId());

        apiKey = sessionManager.getSetting().getStripeSecreteKey();

        stripe = new Stripe(
                getActivity().getApplicationContext(),
                Objects.requireNonNull(sessionManager.getSetting().getStripePublishableKey())
        );


    }

    public void startPurchase(String planId) {

        myPlayStoreBilling.startPurchase(planId, BillingClient.SkuType.INAPP, true);

    }

    private void buyItem(PlanRoot.DataItem dataItem) {
        ((CoinPlanActivity_a) getActivity()).setSelectedPlanId(dataItem.getId(), false);

        bottomSheetDialog.dismiss();

        totalAmount = dataItem.getRupee();

        //   binding.pd.setVisibility(View.VISIBLE);
        planId = dataItem.getId();

        if (paymentGateway.equals(STR_GP)) {
            planId = dataItem.getId();
            productId = dataItem.getGoogleProductId();
            planId = dataItem.getId();
            Log.d(TAG, "buyItem: ");
            binding.pd.setVisibility(View.GONE);
            startPurchase(productId);
        } else if (paymentGateway.equals(STR_STRIPE)) {
            bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.CustomBottomSheetDialogTheme);
            bottomSheetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            bottomSheetCardBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.bottom_sheet_card, null, false);
            bottomSheetDialog.setContentView(bottomSheetCardBinding.getRoot());
            bottomSheetCardBinding.cardInputWidget.setPostalCodeEnabled(false);
            bottomSheetCardBinding.cardInputWidget.setPostalCodeRequired(false);

            bottomSheetCardBinding.btnclose.setOnClickListener(v -> {
                bottomSheetDialog.dismiss();
            });

            bottomSheetCardBinding.tvamount.setText(String.valueOf(dataItem.getRupee()) + " " + currency);
            bottomSheetCardBinding.tvcoin.setText(String.valueOf(dataItem.getCoin()));
            bottomSheetCardBinding.btnPay.setOnClickListener(v -> {
                binding.pd.setVisibility(View.VISIBLE);
                Log.d(TAG, "buyItem: as");
                if (totalAmount >= 1) {
                    Log.d(TAG, "buyItem: dssdss");
                    PaymentMethodCreateParams params = bottomSheetCardBinding.cardInputWidget.getPaymentMethodCreateParams();
                    if (params != null) {
                        new MyTask().execute();
                        Log.d(TAG, "confirmPayment: ");
                        bottomSheetDialog.dismiss();
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.entercarddetails), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Please use another payment method", Toast.LENGTH_SHORT).show();
                }
            });
            bottomSheetDialog.show();
        } else {

            if (checkout != null) {
                checkout.setImage(R.drawable.coin);
                try {
                    Activity activity = getActivity();
                    JSONObject options = new JSONObject();
                    options.put("name", sessionManager.getUser().getName());
                    options.put("description", "user id: " + sessionManager.getUser().getId());
                    options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
                    options.put("currency", currency);

                    // Log.d(TAG, "razorpay : rate2= " + dataItem.getRupee());
                    options.put("amount", 100 * totalAmount);

//                  options.put("prefill.email", Const_a.EMAIL);
//                  options.put("prefill.contact", "0000000000");
                    checkout.open(activity, options);
                } catch (Exception e) {
                    Log.e(TAG, "Error in submitting payment details", e);
                }
            }
        }

    }

    private void callPurchaseDoneApi() {
        binding.pd.setVisibility(View.VISIBLE);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", userId);
        jsonObject.addProperty("plan_id", planId);
        Call<RestResponse> call = RetrofitBuilder_a.create().purchaseCoin(Const_a.DEVKEY, jsonObject);
        call.enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus()) {
                        if (isAdded()) {
                            if (!requireActivity().isFinishing()) {
                                Toast.makeText(requireActivity(), "Purchased", Toast.LENGTH_SHORT).show();
                                if (bottomSheetDialog != null) {
                                    bottomSheetDialog.dismiss();
                                }
                            }
                        }

                    } else {
                        // Toast.makeText(getActivity(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    }
                }
                binding.pd.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<RestResponse> call, Throwable t) {
                Toast.makeText(requireActivity(), "Something Went Wrong..", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        Log.d(TAG, "onActivityResult: ");
        stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void displayAlert(@NonNull String title,
                              @Nullable String message, boolean b) {
        binding.pd.setVisibility(View.GONE);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message);
        builder.setPositiveButton("Ok", (dialog, which) -> {
            if (b) {
                Toast.makeText(getActivity(), "Purchase Done", Toast.LENGTH_SHORT).show();
                callPurchaseDoneApi();
            } else {
                builder.create().dismiss();
            }
        });
        builder.create().show();
    }

    private static final class PaymentResultCallback
            implements ApiResultCallback<PaymentIntentResult> {


        private final WeakReference<Fragment> activityRef;

        public PaymentResultCallback(PlanListFragment_a planListFragmentG) {
            activityRef = new WeakReference<>(planListFragmentG);
            Log.d(TAG, "PaymentResultCallback: ");
        }

        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            PlanListFragment_a activity = (PlanListFragment_a) activityRef.get();
            if (activity == null) {
                return;
            }
            PaymentIntent paymentIntent = result.getIntent();
            StripeIntent.Status status = paymentIntent.getStatus();
            if (status == StripeIntent.Status.Succeeded) {

                // Payment completed successfully
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                Log.d(TAG, "onSuccess: payment== " + gson.toString());
                long amount = paymentIntent.getAmount() / 100;
                String message = "Amount: " + amount + " " + activity.currency + "\n Status: " + paymentIntent.getStatus().toString();

                activity.displayAlert(
                        "Payment completed",
                        message, true
                );
            } else if (status == StripeIntent.Status.RequiresPaymentMethod) {
                // Payment failed – allow retrying using a different payment method
                activity.displayAlert(
                        "Payment failed",
                        Objects.requireNonNull(paymentIntent.getLastPaymentError()).getMessage(), false
                );
            }
        }

        @Override
        public void onError(@NonNull Exception e) {
            PlanListFragment_a activity = (PlanListFragment_a) activityRef.get();
            if (activity == null) {
                return;
            }
            // Payment request failed – allow retrying using the same payment method
            Log.d(TAG, "onSuccess: error== " + e.toString());
            activity.displayAlert("Error", e.getMessage(), false);
        }
    }

    private class MyTask extends AsyncTask<String, String, String> {


        private String paymentIntentClientSecret;

        @Override
        protected String doInBackground(String... strings) {

            com.stripe.Stripe.apiKey = sessionManager.getSetting().getStripeSecreteKey();

            PaymentIntentCreateParams params1 =
                    PaymentIntentCreateParams.builder()
                            .setAmount((long) totalAmount * 100)
                            .setDescription(user.getId())
                            .setReceiptEmail(Const_a.EMAIL)
                            //   .putExtraParam("email",sessionManager.getUser().getData().getEmail())
                            .setShipping(
                                    PaymentIntentCreateParams.Shipping.builder()
                                            .setName(user.getName())
                                            .setPhone("0000000000")
                                            .setAddress(
                                                    PaymentIntentCreateParams.Shipping.Address.builder()
                                                            .setLine1("abc")
                                                            .setPostalCode("91761")
                                                            .setLine2("def")
                                                            .setCity("city")
                                                            .setState("sar")
                                                            .setCountry(country)
                                                            .build())
                                            .build())
                            .setCurrency(currency)
                            .addPaymentMethodType("card")
                            .build();

            com.stripe.model.PaymentIntent paymentIntent = null;

            try {
                paymentIntent = com.stripe.model.PaymentIntent.create(params1);
            } catch (com.stripe.exception.StripeException e) {
                e.printStackTrace();
                Log.d(TAG, "startCheckout: errr 64 " + e);
            }


            paymentIntentClientSecret = paymentIntent != null ? paymentIntent.getClientSecret() : null;
            Log.d(TAG, "doInBackground:0 " + paymentIntentClientSecret);

            Log.d(TAG, "doInBackground:1 " + paymentIntentClientSecret);
            return paymentIntentClientSecret;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            CardInputWidget cardInputWidget = bottomSheetCardBinding.cardInputWidget;
            cardInputWidget.setPostalCodeRequired(false);
            cardInputWidget.setPostalCodeEnabled(false);
            PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();

            if (params != null && paymentIntentClientSecret != null && !requireActivity().isFinishing()) {
                Log.d(TAG, "confirmPayment: " + params.toString());
                ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                        .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
                stripe.confirmPayment(PlanListFragment_a.this, confirmParams);

                Log.d(TAG, "onResponse: cps == " + confirmParams.getClientSecret());
            }
        }

    }


}



