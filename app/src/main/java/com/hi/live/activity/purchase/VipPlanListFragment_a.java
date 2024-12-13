package com.hi.live.activity.purchase;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import com.hi.live.adaptor.VipPlanAdapter_a;
import com.hi.live.databinding.BottomSheetCardBinding;
import com.hi.live.databinding.BottomSheetPaymentBinding;
import com.hi.live.databinding.FragmentVipPlanListBinding;
import com.hi.live.models.BecomeVipMemberRoot;
import com.hi.live.models.User;
import com.hi.live.models.VipPlanRoot;
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

public class VipPlanListFragment_a extends Fragment {


    private static final String STR_GP = "google pay";
    private static final String STR_STRIPE = "stripe";
    private static final String STR_VIP = "You Are Vip Member";
    FragmentVipPlanListBinding binding;
    Checkout checkout = new Checkout();
    BottomSheetCardBinding bottomSheetCardBinding;
    String apiKey;
    private String paymentGateway;
    private SessionManager__a sessionManager;
    private String userId;
    private User user;
    private Stripe stripe;
    private String planId;

    private BottomSheetDialog bottomSheetDialog;

    private double totalAmount = 1;
    private String currency;
    private String country;
    private MyPlayStoreBilling myPlayStoreBilling;
    private boolean isConnected;

    public VipPlanListFragment_a() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_vip_plan_list, container, false);
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.mainLL.getWindowToken(), 0);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sessionManager = new SessionManager__a(getActivity());
        userId = sessionManager.getUser().getId();
        user = sessionManager.getUser();
        Log.d("TAG", "onActivityCreated: " + paymentGateway);
        binding.pd.setVisibility(View.VISIBLE);

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
                if (isPurchaseSuccess) {
                    callPurchaseDoneApi();
                }
            }
        });


        Checkout.preload(getActivity());
        checkout.setKeyID(sessionManager.getSetting().getRazorPayId().trim());

        apiKey = sessionManager.getSetting().getStripeSecreteKey();

        stripe = new Stripe(
                getActivity().getApplicationContext(),
                Objects.requireNonNull(sessionManager.getSetting().getStripePublishableKey())
        );


    }


    private void getData() {
        Call<VipPlanRoot> call = RetrofitBuilder_a.create().getVipPlans(Const_a.DEVKEY);
        call.enqueue(new Callback<VipPlanRoot>() {
            private void onButClick(VipPlanRoot.DataItem dataItem) {

                openBottomSheet(dataItem);

            }

            @Override
            public void onResponse(Call<VipPlanRoot> call, Response<VipPlanRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && !response.body().getData().isEmpty()) {

                        VipPlanAdapter_a moreCoinAdapter = new VipPlanAdapter_a(response.body().getData(), country, this::onButClick);
                        binding.rvMoreCoins.setAdapter(moreCoinAdapter);

                    }
                }
                binding.pd.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<VipPlanRoot> call, Throwable t) {
//ll
            }
        });
    }

    private void openBottomSheet(VipPlanRoot.DataItem dataItem) {


        if (getActivity() == null) return;

        bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.CustomBottomSheetDialogTheme);
//        bottomSheetDialog.setOnShowListener(dialog -> {
//            BottomSheetDialog d = (BottomSheetDialog) dialog;
//            FrameLayout bottomSheet = (FrameLayout) d.findViewById(R.id.design_bottom_sheet);
//            BottomSheetBehavior.from(bottomSheet)
//                    .setState(BottomSheetBehavior.STATE_EXPANDED);
//        });
        BottomSheetPaymentBinding bottomSheetPaymentBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.bottom_sheet_payment, null, false);
        bottomSheetDialog.setContentView(bottomSheetPaymentBinding.getRoot());
        bottomSheetDialog.show();
        bottomSheetPaymentBinding.btnclose.setOnClickListener(v -> bottomSheetDialog.dismiss());
        List<String> pg = ((CoinPlanActivity_a) getActivity()).getPaymentGateways();
        if (pg.contains(STR_GP)) {
            bottomSheetPaymentBinding.lytgooglepay.setVisibility(View.VISIBLE);

            bottomSheetPaymentBinding.lytgooglepay.setOnClickListener(v -> {
                paymentGateway = STR_GP;
                buyItem(dataItem);
            });
        } else {
            bottomSheetPaymentBinding.lytgooglepay.setVisibility(View.GONE);
        }
        if (pg.contains("razor pay")) {

            bottomSheetPaymentBinding.lytrazorpay.setVisibility(View.VISIBLE);
            bottomSheetPaymentBinding.lytrazorpay.setOnClickListener(v -> {
                paymentGateway = "razor pay";
                buyItem(dataItem);
            });
        } else {
            bottomSheetPaymentBinding.lytrazorpay.setVisibility(View.GONE);
        }
        if (pg.contains(STR_STRIPE)) {
            bottomSheetPaymentBinding.lytstripe.setVisibility(View.VISIBLE);
            bottomSheetPaymentBinding.lytstripe.setOnClickListener(v -> {

                if (totalAmount >= 1) {
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

    public void startPurchase(String planId) {
        myPlayStoreBilling.startPurchase(planId, BillingClient.SkuType.INAPP, true);
    }

    private void buyItem(VipPlanRoot.DataItem dataItem) {
        bottomSheetDialog.dismiss();
        totalAmount = dataItem.getPrice();
        planId = dataItem.getId();
        ((CoinPlanActivity_a) getActivity()).setSelectedPlanId(dataItem.getId(), true);

        if (paymentGateway.equals(STR_GP)) {

            planId = dataItem.getId();

            Log.d("TAG", "buyItem: product id " + dataItem.getProductid());
            startPurchase(dataItem.getProductid());

        } else if (paymentGateway.equals(STR_STRIPE)) {

            bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.CustomBottomSheetDialogTheme);
            bottomSheetCardBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.bottom_sheet_card, null, false);
            bottomSheetDialog.setContentView(bottomSheetCardBinding.getRoot());
            bottomSheetCardBinding.cardInputWidget.setPostalCodeEnabled(false);
            bottomSheetCardBinding.cardInputWidget.setPostalCodeRequired(false);

            bottomSheetCardBinding.btnclose.setOnClickListener(v -> bottomSheetDialog.dismiss());
            bottomSheetCardBinding.tvamount.setText(String.valueOf(totalAmount) + " " + currency);
            bottomSheetCardBinding.tvcoin.setText(String.valueOf(dataItem.getTime()));
            bottomSheetCardBinding.btnPay.setOnClickListener(v -> {
                Log.d("TAG", "buyItem: amounta " + totalAmount);
                if (totalAmount >= 1) {
                    PaymentMethodCreateParams params = bottomSheetCardBinding.cardInputWidget.getPaymentMethodCreateParams();
                    if (params != null) {
                        new MyTask().execute();
                        Log.d("TAG", "confirmPayment: ");
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


                    Log.d("TAG", "razorpay : rate2= " + totalAmount);
                    Log.d("TAG", "razorpay : rate2= " + currency);
                    options.put("amount", (int) 100 * totalAmount);


//                    options.put("prefill.email", Const_a.EMAIL);
//                    options.put("prefill.contact", "0000000000");
                    checkout.open(activity, options);


                } catch (Exception e) {
                    Log.e("TAG", "Error in submitting payment details", e);
                }
            }

        }


    }


    private void callPurchaseDoneApi() {
        binding.pd.setVisibility(View.VISIBLE);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", userId);
        jsonObject.addProperty("plan_id", planId);
        Call<BecomeVipMemberRoot> call = RetrofitBuilder_a.create().becomeVip(Const_a.DEVKEY, jsonObject);
        call.enqueue(new Callback<BecomeVipMemberRoot>() {
            @Override
            public void onResponse(Call<BecomeVipMemberRoot> call, Response<BecomeVipMemberRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && response.body().getUser() != null) {
                        try {
                            Toast.makeText(getActivity(), STR_VIP, Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        sessionManager.saveUser(response.body().getUser());
                        if (bottomSheetDialog != null) {
                            bottomSheetDialog.dismiss();
                        }
                    } else {
                        // Toast.makeText(getActivity(), STR_VIP, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Toast.makeText(getActivity(), STR_VIP, Toast.LENGTH_SHORT).show();
                }
                binding.pd.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<BecomeVipMemberRoot> call, Throwable t) {
                Toast.makeText(getActivity(), "Something Went Wrong..", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        Log.d("TAG", "onActivityResult: ");
        stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));
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

        public PaymentResultCallback(VipPlanListFragment_a planListFragment) {
            activityRef = new WeakReference<>(planListFragment);
            Log.d("TAG", "PaymentResultCallback: ");
        }

        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            VipPlanListFragment_a activity = (VipPlanListFragment_a) activityRef.get();
            if (activity == null) {
                return;
            }
            PaymentIntent paymentIntent = result.getIntent();
            StripeIntent.Status status = paymentIntent.getStatus();
            if (status == StripeIntent.Status.Succeeded) {

                // Payment completed successfully
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                Log.d("TAG", "onSuccess: payment== " + gson.toString());
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
            VipPlanListFragment_a activity = (VipPlanListFragment_a) activityRef.get();
            if (activity == null) {
                return;
            }
            // Payment request failed – allow retrying using the same payment method
            Log.d("TAG", "onSuccess: error== " + e.toString());
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
                Log.d("TAG", "startCheckout: errr 64 " + e);
            }


            paymentIntentClientSecret = paymentIntent != null ? paymentIntent.getClientSecret() : null;
            Log.d("TAG", "doInBackground:0 " + paymentIntentClientSecret);

            Log.d("TAG", "doInBackground:1 " + paymentIntentClientSecret);
            return paymentIntentClientSecret;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            CardInputWidget cardInputWidget = bottomSheetCardBinding.cardInputWidget;
            cardInputWidget.setPostalCodeRequired(false);
            cardInputWidget.setPostalCodeEnabled(false);
            PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();

            if (params != null && paymentIntentClientSecret != null) {
                Log.d("TAG", "confirmPayment: " + params.toString());
                ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                        .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
                stripe.confirmPayment(VipPlanListFragment_a.this, confirmParams);

                Log.d("TAG", "onResponse: cps == " + confirmParams.getClientSecret());
            }
        }

    }

}