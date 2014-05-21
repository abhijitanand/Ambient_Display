package tud.tk3.ambientdisplay.app.communication;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.TextView;

import org.umundo.core.Discovery;
import org.umundo.core.Message;
import org.umundo.core.Node;
import org.umundo.core.Publisher;
import org.umundo.core.Receiver;
import org.umundo.core.Subscriber;
import org.umundo.core.SubscriberStub;
import org.umundo.s11n.ITypedGreeter;
import org.umundo.s11n.TypedPublisher;

import tud.tk3.ambientdisplay.app.display.Display;
import tud.tk3.ambientdisplay.app.display.DisplayTopology;

/**
 * Created by martin on 12.05.14.
 */
public class Communicator {

    private Discovery disc;
    private Node node;
    private Publisher publisher;
    private Subscriber subscriber;

    private Context context;
    private AmbientDisplay display;
    private DisplayTopology topology;


    public static final String CHANNEL_NAME = "ambientdisplay";

    public static class Action {
        public static final String NAME = "ACTION";
        public static final String SCREEN_ADD = "ALOHA";
        public static final String SCREEN_REMOVE = "BYE";
        public static final String DISPLAY = "DISPLAY";
    }

    public static class Screen {
        public static final String ID_NAME = "ID";
        public static final String WIDTH_NAME = "WIDTH";
        public static final String HEIGHT_NAME = "HEIGHT";
        public static final String DENSITY_NAME = "DENSITY";
    }




    public class DisplayReceiver extends Receiver {
        public void receive(Message msg) {
            String content = "";
            for (String key : msg.getMeta().keySet()) {
                content += content + key + ": " + msg.getMeta(key) + "\n";
                Log.e("ambientdisplay", key + ": " + msg.getMeta(key));
            }

            String action = msg.getMeta().get(Action.NAME);
            if (action.compareTo(Action.SCREEN_ADD) == 0) {
                addScreen(msg);
            } else if (action.compareTo(Action.SCREEN_REMOVE) == 0) {
                removeScreen(msg);
            } else if (action.compareTo(Action.DISPLAY) == 0) {
                display.displayImage(msg.getData());
            }
        }
    }


    public class DisplayGreeter implements ITypedGreeter {

        @Override
        public void welcome(TypedPublisher typedPublisher, SubscriberStub subscriberStub) {
            publishScreen(typedPublisher);
        }

        @Override
        public void farewell(TypedPublisher typedPublisher, SubscriberStub subscriberStub) {
            removeScreen(typedPublisher.getUUID());
        }
    }



    public Communicator(Context ctx) {

        context = ctx;
        display = (AmbientDisplay) ctx;

        WifiManager wifi = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        if (wifi != null) {
            WifiManager.MulticastLock mcLock = wifi.createMulticastLock("mylock");
            mcLock.acquire();
            // mcLock.release();
        } else {
            Log.v("COMMUNICATOR", "Cannot get WifiManager");
        }

        System.loadLibrary("umundoNativeJava");

        disc = new Discovery(Discovery.DiscoveryType.MDNS);

        node = new Node();
        disc.add(node);

        publisher = new Publisher(CHANNEL_NAME);
        node.addPublisher(publisher);

        subscriber = new Subscriber(CHANNEL_NAME, new DisplayReceiver());
        node.addSubscriber(subscriber);


        topology = new DisplayTopology();
    }


    public void publishScreen(Publisher pub) {
        Message msg = new Message();
        msg.putMeta(Action.NAME, Action.SCREEN_ADD);
        msg.putMeta(Screen.ID_NAME, node.getUUID());

        msg.putMeta(Screen.WIDTH_NAME, ""+context.getResources().getDisplayMetrics().widthPixels);
        msg.putMeta(Screen.HEIGHT_NAME, ""+context.getResources().getDisplayMetrics().heightPixels);

        msg.putMeta(Screen.DENSITY_NAME, ""+context.getResources().getDisplayMetrics().densityDpi);
        pub.send(msg);
    }

    public void sendImage(byte[] data) {
        Message msg = new Message();
        msg.putMeta(Action.NAME, Action.DISPLAY);
        msg.setData(data);
        publisher.send(msg);
    }



    public void addScreen(Message msg) {
        String id = msg.getMeta(Communicator.Screen.ID_NAME);
        int width = Integer.parseInt(msg.getMeta(Communicator.Screen.WIDTH_NAME));
        int height = Integer.parseInt(msg.getMeta(Communicator.Screen.HEIGHT_NAME));
        int density = Integer.parseInt(msg.getMeta(Communicator.Screen.DENSITY_NAME));
        Display d = new Display(id, width, height, density);
        topology.displays.put(id, d);
    }

    private void removeScreen(Message msg) {
        Display d = topology.displays.get(msg.getMeta(Communicator.Screen.ID_NAME));
        topology.displays.remove(d);
    }

    private void removeScreen(String uuid) {
        Display d = topology.displays.get(uuid);
        topology.displays.remove(d);
    }
}
