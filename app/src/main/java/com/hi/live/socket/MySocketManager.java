package com.hi.live.socket;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.hi.live.BuildConfig;
import com.hi.live.SessionManager__a;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.engineio.client.transports.WebSocket;

public class MySocketManager {

    private static final String TAG = "SocketManager_1";
    public static int lisenerCount = 0;
    private final List<LiveHandler> liveHandlers = new ArrayList<>();

    private final List<SocketConnectHandler> socketConnectHandlers = new ArrayList<>();
    private final List<ChatHandler> chatHandlers = new ArrayList<>();
    private final List<CallHandler> callHandlers = new ArrayList<>();
    public boolean lastCallCancalled = false;
    public boolean globalConnecting = false;
    public boolean globalConnected = false;
    Handler handler = new Handler();

    SessionManager__a sessionManager;
    String userId;
    Socket socket;
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (socket != null) {
                Log.d(TAG, "run: SOCKET CONNECTE = " + socket.connected());
            }
            handler.postDelayed(this, 3000);
        }
    };

    public MySocketManager() {

    }

    public static MySocketManager getInstance() {
        return Holder.INSTANCE;
    }

    public void createGlobal(Context applicationContext) {

        if (MySocketManager.getInstance().getSocet() != null) {
            if (MySocketManager.getInstance().getSocet().connected()) {
                return;
            }
        }

        Log.d("{{{{{{{{{{{{{{{{{{{{{TAG}}}}}}}}}}}}}}}}}}}}}", "createGlobal: ");

        sessionManager = new SessionManager__a(applicationContext.getApplicationContext());

        if (sessionManager.getUser() == null) {
            Log.d(TAG, "createGlobal: not Logged yet");
            return;
        }

        userId = sessionManager.getUser().getId();

        Log.d(TAG, "initGlobalSocket: init " + userId);
        IO.Options options = IO.Options.builder()
                // IO factory options
                .setForceNew(false)
                .setMultiplex(true)
                // low-level engine options
                .setTransports(new String[]{WebSocket.NAME})
                .setUpgrade(false)
                .setRememberUpgrade(false)
                .setPath("/socket.io/")
                .setQuery("globalRoom=" + "globalRoom:" + userId + "")
                .setExtraHeaders(null)
                // Manager options
                .setReconnection(true)
                .setReconnectionAttempts(Integer.MAX_VALUE)
                .setReconnectionDelay(1000)
                .setReconnectionDelayMax(5000)
                .setRandomizationFactor(0.5)
//                .setTimeout(3000)
                // Socket options
                .setAuth(null)
                .build();

        URI uri = URI.create(BuildConfig.BASE_URL);
        socket = IO.socket(uri, options);
        socket.connect();

        Log.d(TAG, "createGlobal: SSS97  " + socket.connected());

        socket.io().on("reconnect", args1 -> {

            for (SocketConnectHandler connectHandler : socketConnectHandlers) {
                connectHandler.onReconnected(args1);
            }

            Log.d(TAG, "reconnected: 111   listner count>> " + lisenerCount);
            Intent intent = new Intent();
            intent.setAction("com.ttyo.ONLINE");
            intent.putExtra("from", "socketmanager_reconnnect");
            applicationContext.sendBroadcast(intent);

        });

        socket.io().on("reconnection_attempt", args -> {
            Log.d(TAG, "reconnection_attempt :111 ");
            for (SocketConnectHandler connectHandler : socketConnectHandlers) {
                connectHandler.onReconnecting();
            }
        });

        socket.io().on("reconnected", args1 -> {
            Log.d(TAG, "reconnected: 1111  listner count>> " + lisenerCount);
            for (SocketConnectHandler connectHandler : socketConnectHandlers) {
                connectHandler.onReconnected(args1);
            }
        });

        socket.on(Socket.EVENT_DISCONNECT, args1 -> {
            Log.d(TAG, "createGlobal: event disconnect " + args1[0].toString());
            Log.d(TAG, "createGlobal: event disconnect lenghth>" + args1.length);
            globalConnected = false;
            globalConnecting = false;

            for (SocketConnectHandler connectHandler : socketConnectHandlers) {
                connectHandler.onDisconnect();
                Log.d(TAG, "createGlobal: onDeisconnected");
            }

            Intent intent1 = new Intent();
            intent1.setAction("com.ttyo.OFFLINE");
            intent1.putExtra("from", "socketmanager");
            applicationContext.sendBroadcast(intent1);
        });

        socket.once(Socket.EVENT_CONNECT, args -> {
            Log.d(TAG, "connected: globelSoket");
            globalConnected = true;
            lastCallCancalled = false;

            for (SocketConnectHandler connectHandler : socketConnectHandlers) {
                connectHandler.onConnect();
                Log.d(TAG, "createGlobal: onconnect");
            }

            socket.io().on("reconnect", args1 -> {
                Log.d(TAG, "reconnect: 222   ");
            });

            socket.io().on("reconnected", args1 -> {
                Log.d(TAG, "reconnected: 222  listner count>> " + lisenerCount);
            });

            socket.io().on("reconnection_attempt", args1 -> {
                Log.d(TAG, "reconnection_attempt:222 ");
            });

            socket.on(SocketConst.MY_FILTER, args1 -> {
                Log.d(TAG, "createGlobal: onsimplerfilter " + args1[0].toString());
                for (LiveHandler liveHandler : liveHandlers) {
                    liveHandler.onSimpleFilter(args1);
                }
            });


            socket.on(SocketConst.MY_GIF, args1 -> {
                Log.d(TAG, "createGlobal: onGif " + args1[0].toString());
                for (LiveHandler liveHandler : liveHandlers) {
                    liveHandler.onGif(args1);
                }
            });

            socket.on(SocketConst.MY_LIVE_COMMENT, args1 -> {
                Log.d(TAG, "createGlobal: onComment " + args1[0].toString());
                for (LiveHandler liveHandler : liveHandlers) {
                    liveHandler.onComment(args1);
                }
            });

            socket.on(SocketConst.MY_GIFT, args1 -> {
                Log.d(TAG, "createGlobal: MY_GIFT " + args1[0].toString());
                for (LiveHandler liveHandler : liveHandlers) {
                    liveHandler.onGift(args1);
                }
            });

            socket.on(SocketConst.MY_VIEW, args1 -> {
                Log.d(TAG, "createGlobal: onView " + args1[0].toString());
                for (LiveHandler liveHandler : liveHandlers) {
                    liveHandler.onView(args1);
                }
            });

            socket.on(SocketConst.MY_REFRESH, args1 -> {
                Log.d(TAG, "createGlobal: onView " + args1[0].toString());
                for (LiveHandler liveHandler : liveHandlers) {
                    liveHandler.onRefresh(args1);
                }
            });

            socket.on(SocketConst.MY_STICKER, args1 -> {
                Log.d(TAG, "createGlobal: onView " + args1[0].toString());
                for (LiveHandler liveHandler : liveHandlers) {
                    liveHandler.onSticker(args1);
                }
            });

            socket.on(SocketConst.MY_EMOJI, args1 -> {
                Log.d(TAG, "createGlobal: onView " + args1[0].toString());
                for (LiveHandler liveHandler : liveHandlers) {
                    liveHandler.onEmoji(args1);
                }
            });


            socket.on(SocketConst.EVENT_CHAT, args1 -> {
                Log.d(TAG, "createGlobal: onChat " + args1[0].toString());
                for (ChatHandler chatHandler : chatHandlers) {
                    chatHandler.onChat(args1);
                }
            });

            socket.on(SocketConst.EVENT_CALL_REQUEST, args1 -> {
                Log.d(TAG, "createGlobal: onCallRequest  " + args1[0].toString());
                for (CallHandler callHandler : callHandlers) {
                    callHandler.onCallRequest(args1);
                }
            });
            socket.on(SocketConst.EVENT_ISBUSY, args1 -> {
                Log.d(TAG, "createGlobal: EVENT_ISBUSY  " + args1[0].toString());
                for (CallHandler callHandler : callHandlers) {
                    callHandler.onIsBusy(args1);
                }
            });
            socket.once(SocketConst.EVENT_MAKE_CALL, args1 -> {
                for (CallHandler callHandler : callHandlers) {
                    callHandler.onMakeCall(args1);
                }
            });

            socket.on(SocketConst.EVENT_CALL_REQUEST, args1 -> {
                Log.d(TAG, "createGlobal: onCallRequest  " + args1[0].toString());
                for (CallHandler callHandler : callHandlers) {
                    callHandler.onGiftRequest(args1);
                }
            });

            socket.on(SocketConst.EVENT_CALL_ANSWER, args1 -> {
                /*for (CallHandler callHandler : callHandlers) {
                    callHandler.onCallAnswer(args1);
                }*/
                for (int i = 0; i < callHandlers.size(); i++) {
                    CallHandler callHandler = callHandlers.get(i);
                    callHandler.onCallAnswer(args1);
                }
            });

            socket.on(SocketConst.EVENT_CALL_RECIVE, args1 -> {
                Log.d(TAG, "createGlobal: onCallRecive " + args1[0].toString());
                for (CallHandler callHandler : callHandlers) {
                    callHandler.onCallReceive(args1);
                }
            });

            socket.on(SocketConst.EVENT_CALL_CONFIRMED, args1 -> {
                Log.d(TAG, "createGlobal: onCallComfirm: " + args1[0].toString());
                for (CallHandler callHandler : callHandlers) {
                    callHandler.onCallConfirm(args1);
                }
            });

            socket.on(SocketConst.EVENT_CALL_CANCEL, args1 -> {
                Log.d(TAG, "createGlobal: onCallCancel: " + args1[0].toString());
                for (CallHandler callHandler : callHandlers) {
                    callHandler.onCallCancel(args1);
                }
            });

            socket.on(SocketConst.EVENT_CALL_DISCONNECT, args1 -> {
                Log.d(TAG, "createGlobal: onCallDisconnect: " + args1[0].toString());
                for (CallHandler callHandler : callHandlers) {
                    callHandler.onCallDisconnect(args1);
                }
            });

            socket.on(SocketConst.MY_COMMENT, args1 -> {
                Log.d(TAG, "createGlobal: onCallDisconnect: " + args1[0].toString());
                for (CallHandler callHandler : callHandlers) {
                    callHandler.onComment(args1);
                }
            });

            socket.on(SocketConst.MY_V_GIFT, args1 -> {
                Log.d(TAG, "createGlobal: MY_V_GIFT: " + args1[0].toString());
                for (CallHandler callHandler : callHandlers) {
                    callHandler.onVgift(args1);
                }
            });

            socket.on(SocketConst.MY_GIFT_REQUEST, args1 -> {
                Log.d(TAG, "createGlobal: MY_GIFT_REQUEST: " + args1[0].toString());
                for (CallHandler callHandler : callHandlers) {
                    callHandler.onGiftRequest(args1);
                }
            });


            handler.removeCallbacksAndMessages(null);
            handler.postDelayed(timerRunnable, 3000);
        });

    }

    public void addSocketConnectHandler(SocketConnectHandler socketConnectHandler) {
        socketConnectHandlers.add(socketConnectHandler);
    }

    public void addLiveListener(LiveHandler liveHandler) {
        liveHandlers.add(liveHandler);
    }

    public void removeLiveListener(LiveHandler liveHandler) {
        liveHandlers.remove(liveHandler);
    }

    public void addChatListener(ChatHandler chatListener) {
        chatHandlers.add(chatListener);
    }

    public void removeChatListener(ChatHandler chatListener) {
        chatHandlers.remove(chatListener);
    }

    public void addCallListener(CallHandler callHandler) {
        callHandlers.add(callHandler);
    }

    public void removeCallListener(CallHandler callHandler) {
        callHandlers.remove(callHandler);
    }

    public Socket getSocet() {
        return socket;
    }

    public void removeSocketListener(SocketConnectHandler socketConnectHandler) {
        socketConnectHandlers.remove(socketConnectHandler);
    }

    private static final class Holder {
        private static final MySocketManager INSTANCE = new MySocketManager();
    }

}
