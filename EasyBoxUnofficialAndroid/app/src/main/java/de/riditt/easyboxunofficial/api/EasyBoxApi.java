package de.riditt.easyboxunofficial.api;

import android.os.Handler;
import android.util.Log;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.riditt.easyboxunofficial.models.requests.LoginRequest;
import de.riditt.easyboxunofficial.models.requests.SessionKeepAliveRequest;
import de.riditt.easyboxunofficial.models.responses.LoginResponse;
import de.riditt.easyboxunofficial.utilities.StringUtilities;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EasyBoxApi {
    private String serverUrl;
    private String authKey;
    private String dmCookie;
    private OkHttpClient client;

    private final Handler keepAliveHandler = new Handler();
    private boolean keepKeepAliveHandlerAlive;

    private boolean connectionEstablished;
    private boolean initialized;
    private boolean loggedIn;

    public EasyBoxApi(OkHttpClient client) {
        this.client = client;
    }

    public boolean isConnectionEstablished() {
        return connectionEstablished;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public String getDmCookie() {
        return dmCookie;
    }

    public void stopKeepAlive() {
        // need to think of a better way to do this
        keepKeepAliveHandlerAlive = false;
    }

    public void initialize(String serverUrl, final EasyBoxApiListener.OnApiResultListener listener) {
        if (serverUrl.isEmpty()) {
            determineServerUrl(new EasyBoxApiListener.OnApiResultListener() {
                @Override
                public void onApiResult(boolean success, Object... results) {
                    if (success) {
                        EasyBoxApi.this.serverUrl = (String) results[0];
                        initialized = true;
                    }
                    listener.onApiResult(success);
                }
            });
        } else {
            this.serverUrl = serverUrl;
            initialized = true;
            listener.onApiResult(true);
        }
    }

    public void establishConnection(final EasyBoxApiListener.OnApiResultListener listener) {
        fetchAuthKey(new EasyBoxApiListener.OnApiResultListener() {
            @Override
            public void onApiResult(boolean success, Object... results) {
                if (!success) {
                    listener.onApiResult(false);
                    return;
                }
                // after getting the auth_key we need the dm_cookie of this session
                fetchDmCookie("login.html", new EasyBoxApiListener.OnApiResultListener() {
                    @Override
                    public void onApiResult(boolean success, Object... results) {
                        if (!success) {
                            listener.onApiResult(false);
                            return;
                        }
                        // now that we have that, we set up the keep-alive timer
                        keepAlive(new EasyBoxApiListener.OnApiResultListener() {
                            @Override
                            public void onApiResult(boolean success, Object... results) {
                                // wait until after we got the keepAlive response to send the message about the successful login
                                Log.d("EasyBoxApi", "keepAlive: response (" + success + ")");
                                connectionEstablished = success;
                                listener.onApiResult(success);
                            }
                        });
                        // do everything we just did but skip the listener.onApiResult() call
                        keepAliveHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (keepKeepAliveHandlerAlive) {
                                    Log.d("EasyBoxApi", "keepAlive: don't stop");
                                    keepAlive(new EasyBoxApiListener.OnApiResultListener() {
                                        @Override
                                        public void onApiResult(boolean success, Object... results) {
                                            Log.d("EasyBoxApi", "keepAlive: response (" + success + ")");
                                            if(!success) {
                                                stopKeepAlive();
                                                initialized = false;
                                                connectionEstablished = false;
                                            }
                                        }
                                    });
                                    keepAliveHandler.postDelayed(this, 10000);
                                } else {
                                    Log.d("EasyBoxApi", "keepAlive: stop");
                                }
                            }
                        }, 10000);
                    }
                });
            }
        });
    }

    public void checkForValidEasyBoxUrl(String serverUrl, final EasyBoxApiListener.OnApiResultListener listener) {
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
                listener.onApiResult(response.isSuccessful());
            }
        });
    }

    /*
    Tries different possible server URL values and checks if they belong to an EasyBox.
    Will call the listener with the boolean result and, if true, with the determined server URL.
     */
    private void determineServerUrl(final EasyBoxApiListener.OnApiResultListener listener) {
        // try two different default values
        checkForValidEasyBoxUrl("easy.box", new EasyBoxApiListener.OnApiResultListener() {
            @Override
            public void onApiResult(boolean success, Object... results) {
                if (success) {
                    listener.onApiResult(true, "easy.box");
                } else {
                    checkForValidEasyBoxUrl("192.168.2.1", new EasyBoxApiListener.OnApiResultListener() {
                        @Override
                        public void onApiResult(boolean success, Object... results) {
                            listener.onApiResult(success, success ? "192.168.2.1" : "");
                        }
                    });
                }
            }
        });
    }

    /*
    Both, gets and sets the authKey variable.
     */
    private void fetchAuthKey(final EasyBoxApiListener.OnApiResultListener listener) {
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
                authKey = match.group(1);
                listener.onApiResult(true);
            }
        });
    }

    /*
    Both, gets and sets the dmCookie variable.
     */
    public void fetchDmCookie(String pageName, final EasyBoxApiListener.OnApiResultListener listener) {
        Log.d("EasyBoxApi", "fetchDmCookie: enter");
        Request request = new Request.Builder()
                .url("http://" + serverUrl + "/main.cgi?page=" + pageName)
                .build();

        Log.d("EasyBoxApi", "fetchDmCookie: enter");
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
                dmCookie = match.group(1);
                listener.onApiResult(true);
            }
        });
        Log.d("EasyBoxApi", "fetchDmCookie: enter");
    }

    private void keepAlive(final EasyBoxApiListener.OnApiResultListener listener) {
        Log.d("EasyBoxApi", "keepAlive: enter");
        SessionKeepAliveRequest sessionKeepAliveRequest = new SessionKeepAliveRequest(serverUrl, dmCookie);
        Log.d("EasyBoxApi", "keepAlive: send");
        client.newCall(sessionKeepAliveRequest.getRequest()).enqueue(new Callback() {
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
        Log.d("EasyBoxApi", "keepAlive: exit");
    }

    public void login(String password, final EasyBoxApiListener.OnLoginResponseListener listener) {
        Log.d("EasyBoxApi", "login: enter");
        LoginRequest loginRequest = new LoginRequest(serverUrl, dmCookie, "vodafone", StringUtilities.md5(password + authKey).toUpperCase(), false);
        Log.d("EasyBoxApi", "login: send");
        client.newCall(loginRequest.getRequest()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                listener.onLoginResponse(new LoginResponse());
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                LoginResponse loginResponse;
                try {
                    Serializer serializer = new Persister(new AnnotationStrategy());
                    loginResponse = serializer.read(LoginResponse.class, response.body().string());
                    loggedIn = true;
                } catch (Exception e) {
                    Log.d("EasyBoxApi", "login: deserialization failed.");
                    e.printStackTrace();
                    loginResponse = new LoginResponse();
                }
                listener.onLoginResponse(loginResponse);
            }
        });
        Log.d("EasyBoxApi", "login: exit");
    }
}
