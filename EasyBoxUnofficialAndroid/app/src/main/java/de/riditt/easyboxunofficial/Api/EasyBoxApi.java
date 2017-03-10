package de.riditt.easyboxunofficial.Api;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.riditt.easyboxunofficial.Utilities;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class EasyBoxApi {
    public interface OnApiResultListener {
        void onApiResult(boolean success, Object... results);
    }

    private String serverUrl;
    private String authKey;
    private String dmCookie;
    private final OkHttpClient client;

    private final Handler keepAliveHandler = new Handler();
    private boolean stopKeepAliveTask = false;

    public EasyBoxApi() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        client = new OkHttpClient.Builder()
                .cookieJar(new CookieJar() {
                    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

                    @Override
                    public void saveFromResponse(HttpUrl fullUrl, List<Cookie> cookies) {
                        cookieStore.put(fullUrl.host(), cookies);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl fullUrl) {
                        List<Cookie> cookies = cookieStore.get(fullUrl.host());
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                    }
                })
                .addInterceptor(logging)
                .build();
    }

    public void EstablishConnection(String serverUrl, final OnApiResultListener listener) {
        if (serverUrl.isEmpty()) {
            return;
            // TODO: fix the DetermineServerUrl function
            //this.serverUrl = DetermineServerUrl();
        } else {
            this.serverUrl = serverUrl;
        }

        GetAuthKey(new OnApiResultListener() {
            @Override
            public void onApiResult(boolean success, Object... results) {
                if (!success) {
                    listener.onApiResult(false);
                    return;
                }
                // after getting the auth_key we need the dm_cookie of this session
                GetDmCookie("login.html", new OnApiResultListener() {
                    @Override
                    public void onApiResult(boolean success, Object... results) {
                        if (!success) {
                            listener.onApiResult(false);
                            return;
                        }
                        // now that we have that, we set up the keep-alive timer
                        keepAliveHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("EasyBoxApi", "KeepAlive: don't stop");
                                KeepAlive(new OnApiResultListener() {
                                    @Override
                                    public void onApiResult(boolean success, Object... results) {
                                        Log.d("EasyBoxApi", "KeepAlive: response (" + success + ")");
                                        // wait until after we got the KeepAlive response to send the message about the successful login
                                        listener.onApiResult(true);
                                    }
                                });
                                // do everything we just did but skip the listener.onApiResult() call
                                keepAliveHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!stopKeepAliveTask) {
                                            Log.d("EasyBoxApi", "KeepAlive: don't stop");
                                            KeepAlive(new OnApiResultListener() {
                                                @Override
                                                public void onApiResult(boolean success, Object... results) {
                                                    Log.d("EasyBoxApi", "KeepAlive: response (" + success + ")");
                                                }
                                            });
                                            keepAliveHandler.postDelayed(this, 10000);
                                        } else {
                                            Log.d("EasyBoxApi", "KeepAlive: stop");
                                        }
                                    }
                                }, 10000);
                            }
                        }, 0);
                    }
                });
            }
        });
    }

    private String DetermineServerUrl() {
        try {
            // TODO: this does not work since it's throwing an exception about network operations in the main thread
            // (I always thought that's only for actual HTTP connections ._.)
            String ipAddress = Inet4Address.getByName("easy.box").getHostAddress();
            if (ipAddress.isEmpty()) {
                return "192.168.2.1";
            } else {
                return ipAddress;
            }
        } catch (UnknownHostException e) {
            // time for some well-reasoned guessing!
            // okay, let's just use the default value
            return "192.168.2.1";
        }
    }

    /*
    Both, gets and sets the authKey variable.
     */
    private void GetAuthKey(final OnApiResultListener listener) {
        Request request = new Request.Builder()
                .url("http://" + serverUrl + "/main.cgi?js=rg_config.js")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                listener.onApiResult(false);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unsuccessful response: " + response);
                }
                String body = response.body().string();
                Matcher match = Pattern.compile(".*auth_key = '(\\d+)';.*", Pattern.DOTALL).matcher(body);
                if (!match.find()) {
                    listener.onApiResult(false);
                }
                authKey =  match.group(1);
                listener.onApiResult(true);
            }
        });
    }

    /*
    Both, gets and sets the dmCookie variable.
     */
    private void GetDmCookie(String pageName, final OnApiResultListener listener) {
        Log.d("EasyBoxApi", "GetDmCookie: enter");
        Request request = new Request.Builder()
                .url("http://" + serverUrl + "/main.cgi?page=" + pageName)
                .build();

        Log.d("EasyBoxApi", "GetDmCookie: enter");
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                listener.onApiResult(false);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unsuccessful response: " + response);
                }
                String body = response.body().string();
                Matcher match = Pattern.compile(".*var dm_cookie='([A-Z0-9]{32})';.*", Pattern.DOTALL).matcher(body);
                if (!match.find()) {
                    listener.onApiResult(false);
                }
                dmCookie =  match.group(1);
                listener.onApiResult(true);
            }
        });
        Log.d("EasyBoxApi", "GetDmCookie: enter");
    }

    private Request CreateSoapRequest(String soapEnvelope, String soapAction) {
        return new Request.Builder()
                .url("http://" + serverUrl + "/data_model.cgi")
                .addHeader("SOAPServer", "")
                .addHeader("SOAPAction", soapAction)
                .post(RequestBody.create(MediaType.parse("text/xml"), soapEnvelope))
                .build();
    }

    private void KeepAlive(final OnApiResultListener listener) {
        Log.d("EasyBoxApi", "KeepAlive: enter");
        String soapEnvelope = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "    <soapenv:Header>\n" +
                "        <DMCookie>" + dmCookie + "</DMCookie>\n" +
                "        <SessionNotRefresh>1</SessionNotRefresh>\n" +
                "    </soapenv:Header>\n" +
                "    <soapenv:Body>\n" +
                "        <cwmp:SessionKeepAlive xmlns=\"\">\n" +
                "            <SessionKeepAlive></SessionKeepAlive>\n" +
                "        </cwmp:SessionKeepAlive>\n" +
                "    </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        Log.d("EasyBoxApi", "KeepAlive: send");
        client.newCall(CreateSoapRequest(soapEnvelope, "cwmp:SessionKeepAlive")).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                listener.onApiResult(false);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                listener.onApiResult(response.isSuccessful());
            }
        });
        Log.d("EasyBoxApi", "KeepAlive: exit");
    }

    public void Login(String password, final OnApiResultListener listener) {
        Log.d("EasyBoxApi", "Login: enter");
        String soapEnvelope = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "    <soapenv:Header>\n" +
                "        <DMCookie>" + dmCookie + "</DMCookie>\n" +
                "    </soapenv:Header>\n" +
                "    <soapenv:Body>\n" +
                "        <cwmp:Login xmlns = \"\">\n" +
                "            <ParameterList>\n" +
                "                <Username>vodafone</Username>\n" +
                "                <Password>" + Utilities.md5(password + authKey) + "</Password>\n" +
                "                <AllowRelogin>0</AllowRelogin>\n" +
                "            </ParameterList>\n" +
                "        </cwmp:Login>\n" +
                "    </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        Log.d("EasyBoxApi", "Login: send");
        client.newCall(CreateSoapRequest(soapEnvelope, "cwmp:Login")).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                listener.onApiResult(false);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // after logging in, the app gives us a new dm_cookie value which we need to get
                // from the app.html page here
                GetDmCookie("app.html", new OnApiResultListener() {
                    @Override
                    public void onApiResult(boolean success, Object... results) {
                        listener.onApiResult(response.isSuccessful());
                    }
                });
            }
        });
        Log.d("EasyBoxApi", "Login: exit");
    }
}
