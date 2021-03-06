import Handshake.NoiseHandshake;
import ProtocolTree.*;
import Util.GorgeousLooper;
import jni.NoiseJni;
import org.whispersystems.libsignal.logging.Log;
import org.whispersystems.libsignal.logging.SignalProtocolLogger;
import org.whispersystems.libsignal.logging.SignalProtocolLoggerProvider;

import javax.swing.*;
import java.awt.event.*;
import java.net.*;
import java.util.Date;

public class MainDialog extends JDialog implements SignalProtocolLogger, GorgeousEngine.GorgeousEngineDelegate {
    private static final String TAG = MainDialog.class.getSimpleName();
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JButton signal_test;
    private JButton logout;
    GorgeousEngine engine_;


    public MainDialog() {
        GorgeousLooper.Instance().Init();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
        }

        setLocationRelativeTo(null);
        SignalProtocolLoggerProvider.setProvider(this);
        String os = System.getProperty("os.name");
        if (os.startsWith("Linux")) {
            System.load(System.getProperty("user.dir") + "/jni/libNoiseJni.so");
        } else if (os.startsWith("Windows")) {
            System.load(System.getProperty("user.dir") + "\\jni\\libNoiseJni.dll");
        }  else if (os.startsWith("Mac")) {
            System.load(System.getProperty("user.dir") + "/jni/libNoiseJni.dylib");
        }

        long instance = NoiseJni.CreateInstance();
        NoiseJni.DestroyInstance(instance);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);


        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        signal_test.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                /*NoiseHandshake.Proxy proxy = new NoiseHandshake.Proxy();
                proxy.server = "127.0.0.1";
                proxy.port = 10808;*/
                engine_ = new GorgeousEngine(System.getProperty("user.dir") + "\\out\\axolotl.db", MainDialog.this, null,System.getProperty("user.dir") + "/out");
                boolean start = engine_.StartEngine();
                if (!start) {
                    Log.i(TAG, "start engine error");
                }
            }
        });
        logout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                engine_.StopEngine();
            }
        });
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        MainDialog dialog = new MainDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    @Override
    public void log(int priority, String tag, String message) {
        System.out.println(new Date().toString() +  message);
    }

    @Override
    public void OnLogin(int code, ProtocolTreeNode desc) {
        Log.i(TAG, "OnLogin:" + code + " desc:" + desc);
    }

    @Override
    public void OnDisconnect(String desc) {
        Log.i(TAG, "OnDisconnect:" + desc);
    }

    @Override
    public void OnSync(ProtocolTreeNode content) {

    }

    @Override
    public void OnPacketResponse(String type, ProtocolTreeNode content) {

    }
}
