package payment.unopay.in.unopayconsumersdk;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.techjini.android.paymnetlibrary.UnoPayParams;
import com.techjini.android.paymnetlibrary.activities.UnoPayPayment;
import com.techjini.android.paymnetlibrary.constants.UPTransactionStatus;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private EditText mAmountET, mMobileNumberET;
    private Button mPayUsingUnoPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAmountET = (EditText) findViewById(R.id.amount);
        mMobileNumberET = (EditText) findViewById(R.id.mobile_number);
        mPayUsingUnoPay = (Button) findViewById(R.id.pay_using_unopay);
        mPayUsingUnoPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validateMandatoryFields()) {
                    UnoPayParams unoPayParams = new UnoPayParams();

                    unoPayParams.setAmount(Double.valueOf(mAmountET.getText().toString()));
                    unoPayParams.setAppName("Sample App");
                    unoPayParams.setEmail("ns.mesta@gmail.com");
                    unoPayParams.setMobileNumber(Long.parseLong(mMobileNumberET.getText().toString()));
                    unoPayParams.setPartnerId("b643f013cb9f3ccc9b44c0bd1ebbc669");
                    unoPayParams.setMerchantSdkKey("da35732a966ac2e96b99b5c640808ff0cdd4c18e");
                    unoPayParams.setName("Test");
                    unoPayParams.setOrderId(String.valueOf(System.currentTimeMillis()));
                    unoPayParams.setProduction(false);


                    Intent payUsingUnoPayIntent = new Intent(MainActivity.this, UnoPayPayment.class);
                    payUsingUnoPayIntent.putExtra(UnoPayPayment.PAYMENT_PARAMS, unoPayParams);
                    startActivityForResult(payUsingUnoPayIntent, 100);
                } else {
                    Toast.makeText(MainActivity.this, "Please fill mandatory data", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean validateMandatoryFields() {
        boolean isValid = true;

        if (mMobileNumberET.getText().toString().trim().length() < 10) {
            isValid = false;
            mMobileNumberET.setError(null);
            mMobileNumberET.setError("Please enter mobile number");
        }

        if (mAmountET.getText().toString().trim().length() == 0) {
            isValid = false;
            mAmountET.setError(null);
            mAmountET.setError("Amount needs to be entered!");
        }

        return isValid;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {

                String orderId = data.getStringExtra("ORDER_ID");
                String result = data.getStringExtra("RESULT");
                String resultStr = "Transaction status not available";
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    if (jsonObject != null) {
                        if (jsonObject.has("status")) {
                            int status = jsonObject.getInt("status");
                            if (status== UPTransactionStatus.SUCCESS.ordinal()) {
                                if (jsonObject.has("data")) {
                                    JSONObject dataObject = jsonObject.getJSONObject("data");
                                    if (dataObject != null) {

                                        resultStr="";
                                        if (dataObject.has("message")) {
                                            resultStr +="Message:"+dataObject.getString("message") + "\n";
                                        }
                                        if (dataObject.has("transactionId")) {
                                            resultStr +="TransactionId:"+dataObject.getString("transactionId") + "\n";
                                        }
                                        if (dataObject.has("requestedAmount")) {
                                            resultStr += "Requested Amount:"+dataObject.getString("requestedAmount") + "\n";

                                        }
                                        if (dataObject.has("merchantAmount")) {
                                            resultStr += "Merchant Amount:"+dataObject.getString("merchantAmount") + "\n";
                                        }

                                    }else {
                                        if (orderId != null) {
                                            resultStr=" Transaction was successful for the order :" + orderId + "But details not obtained .Please reach out support team if the amount was deducted from your account";
                                        } else {
                                            resultStr=" Transaction was successful.But details not obtained .Please reach out support team if the amount was deducted from your account";
                                        }
                                    }

                                } else {
                                    if (orderId != null) {
                                        resultStr=" Transaction was successful for the order :" + orderId + "But details not obtained .Please reach out support team if the amount was deducted from your account";
                                    } else {
                                        resultStr=" Transaction was successful.But details not obtained .Please reach out support team if the amount was deducted from your account";
                                    }
                                }

                            } else if(status==UPTransactionStatus.FAILED.ordinal()){
                                if (jsonObject.has("error")) {
                                    JSONObject errorObject=jsonObject.getJSONObject("error");
                                    resultStr=errorObject.getString("message");
                                    //we send request code which you can compare with @UPErrorCodes to check the type of error
                                    int errorCode=errorObject.getInt("code");
                                } else {
                                    if (orderId != null) {
                                        resultStr=" Transaction failed for the order :" + orderId + "Please reach out support team if the amount was deducted from your account";
                                    } else {
                                        resultStr="Oops transactions couldn't be processed.Please reach out support team if the amount was deducted from your account";
                                    }
                                }
                            }else if(status == UPTransactionStatus.CANCELLED.ordinal())
                            {
                                if (jsonObject.has("error")) {
                                    JSONObject errorObject=jsonObject.getJSONObject("error");
                                    resultStr=errorObject.getString("message");
                                    //we send request code which you can compare with @UPErrorCodes to check the type of error
                                    int errorCode=errorObject.getInt("code");
                                } else {
                                    if (orderId != null) {
                                        resultStr=" Transaction failed for the order :" + orderId + "Please reach out support team if the amount was deducted from your account";
                                    } else {
                                        resultStr="Oops transactions couldn't be processed.Please reach out support team if the amount was deducted from your account";
                                    }
                                }
                            }else {
                                // This will not happen as unopay is setting up all the status code
                                if (jsonObject.has("error")) {
                                    JSONObject errorObject=jsonObject.getJSONObject("error");
                                    resultStr=errorObject.getString("message");
                                    //we send request code which you can compare with @UPErrorCodes to check the type of error
                                    int errorCode=errorObject.getInt("code");
                                } else {
                                    if (orderId != null) {
                                        resultStr=" Transaction failed for the order :" + orderId + "Please reach out support team if the amount was deducted from your account";
                                    } else {
                                        resultStr="Oops transactions couldn't be processed.Please reach out support team if the amount was deducted from your account";
                                    }
                                }
                            }
                        } else {
                            if (orderId != null) {
                                resultStr=" Status is undefined for the order :" + orderId + "Please reach out support team if the amount was deducted from your account";
                            } else {
                                resultStr="Oops transactions couldn't be processed.Please reach out support team if the amount was deducted from your account";
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                showDialog(resultStr);


            }
        } else {

        }
    }

    public void showDialog(String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Transaction Status");
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.create();
        alertDialog.show();
    }
}
