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

/**
 * Created by martin on 12.05.14.
 */
public class Communicator {

    private Discovery disc;
    private Node node;
    private Publisher publisher;
    private Subscriber subscriber;

    private Context context;

    public static class Actions {
        public static final String SCREEN_ADD = "ALOHA";
        public static final String SCREEN_REMOVE = "BYE";
        public static final String DISPLAY = "DISPLAY";

    }

    public class DisplayReceiver extends Receiver {
        public void receive(Message msg) {
            String content = "";
            for (String key : msg.getMeta().keySet()) {
                content += content + key + ": " + msg.getMeta(key) + "\n";
                Log.i("ambientdisplay", key + ": " + msg.getMeta(key));
            }
            //((MainActivity)context).display(content);
            publishScreen();

            String action = msg.getMeta().get("ACTION");
            if (action.compareTo(Actions.SCREEN_ADD) == 0) {

            } else if (action.compareTo(Actions.SCREEN_REMOVE) == 0) {

            } else if (action.compareTo(Actions.DISPLAY) == 0) {

            }
        }
    }

    public Communicator(Context ctx) {

        context = ctx;

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

        publisher = new Publisher("ambientdisplay");
        node.addPublisher(publisher);

        subscriber = new Subscriber("ambientdisplay", new DisplayReceiver());
        node.addSubscriber(subscriber);
    }


    public void publishScreen() {
        Message msg = new Message();
        msg.putMeta("ACTION", "ALOHA");
        msg.putMeta("IP", node.getIP());
        msg.putMeta("ARGUMENT", "5");
        publisher.send(msg);
    }

    public void sendImage(byte[] data) {
        Message msg = new Message();
        msg.putMeta("ACTION", "DISPLAY");
        msg.setData(data);
        publisher.send(msg);
    }

}
