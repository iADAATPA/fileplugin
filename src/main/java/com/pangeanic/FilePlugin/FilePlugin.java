package com.pangeanic.FilePlugin;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.awt.datatransfer.*;
import java.awt.image.BufferedImage;
import static java.awt.image.ImageObserver.HEIGHT;
import java.util.*;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.BevelBorder;
import javax.imageio.ImageIO;
import javax.swing.table.TableColumnModel;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.json.*;
//import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Locale;
import java.util.ResourceBundle;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.table.JTableHeader;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.entity.StringEntity;

public class FilePlugin extends JFrame {

    static FilePlugin test;
    private static ResourceBundle mybundle;
    static boolean apiKeyOk = false;
    
    

    private static String hostAddress = "prod.pangeamt.com:8080/PGFile/v1";

    private static String apiKey = "000000";
    private static String user = "admin";
    private static boolean auto = true;
    private static String translatedFolder = "/Users/A/Documents/translated/";
    private static String tempFolder = "/Users/A/Documents/translated/";
    //private static String pretranslatedFolder="/Users/A/Documents/pretranslated/";
    /*
    static final List<String> allowedTypes = Arrays.asList("dtd","c","h","cpp","html","htm","xhtml","strings",
                               "properties","json","xslx","xltx","pptx","potx","docx","dotx","ods","ots",
                               "odg","otg","odp","otp","odt","ott","php","txt","po","lang","srt","sub",
                               "ttx","tmx","wix","xlf","xliff","xml","yml","yaml", "msg");
     */
    static final List<String> allowedTypes = Arrays.asList("html", "htm", "xhtml", "strings",
            "xlf", "xliff", "xlsx", "xltx", "pptx", "potx", "docx", "dotx", "ods", "ots",
            "odg", "otg", "odp", "otp", "odt", "ott", "txt", "msg", "eml", "pdf", "tmx");
    //private JDesktopPane dp = new JDesktopPane();
    private MyDesktopPane dp = new MyDesktopPane();
    private DefaultListModel listModel = new DefaultListModel();
    private JList list = new JList(listModel);
    private static int left;
    private static int top;

    private JCheckBoxMenuItem copyItem;
    private JCheckBoxMenuItem nullItem;
    private JCheckBoxMenuItem thItem;

    private String[] columnNames = {"File",
        "Process",
        "Source",
        "Target",
        "Status",
        "Download Link"};
    //private Object[][] data = {};
    private static int currentEngineId = 0;
    private static Engine currentEngine = null;
    private static String currentSrc = "-";
    private static String currentTgt = "-";
    private static String currentProcess = "Machine Translate";
    private static String currentGlossary = "NO GLOSSARY";
    private static int currentGlossaryId = 0;
    private static boolean checkLang = true;

    JTextField urlField = new JTextField();
    JTextField userName = new JTextField();
    JTextField apiKeyField = new JTextField();
    JPasswordField password = new JPasswordField();

    Object[] message_Credentials = {
        "Username:", userName,
        "API Key:", apiKeyField
    };

    JTextField downloadFolder = new JTextField();

    final JComponent[] inputsAccessPoint = new JComponent[]{
        new JLabel("Enter URL"),
        urlField
    };
    final JComponent[] inputsCredentials = new JComponent[]{
        new JLabel("Username"),
        userName,
        new JLabel("Password"),
        password,
        new JLabel("API Key"),
        apiKeyField
    };
    final JComponent[] inputsDownloadFolder = new JComponent[]{
        new JLabel("Enter Path"),
        downloadFolder
    };
    private static DefaultTableModel model = new DefaultTableModel();
    JTable table = new JTable(model);

    JLabel statusLabel;
     
    
           
    JLabel settingsLabel;
    JLabel settingsLabel2;

    static Splash splash;
    public static boolean splashShowing = true;

    private static Preferences prefs;

    List<Glossary> listGlos = new ArrayList<>();
    static List<Fic> listFics = new ArrayList<Fic>();
    // Create a couple of columns 

    //private JTable table = new JTable(data, columnNames);
    private JScrollPane scrollPane = new JScrollPane(table);

    private JMenu eng;
    private JMenuItem engMenuItem;
    private JMenuItem cfgMenuItem;
    private JMenuItem gloMenuItem;
    private JMenuItem prc2MenuItem;
    private JRadioButtonMenuItem glo2MenuItem;
    private JRadioButtonMenuItem srcMenuItem;
    private JRadioButtonMenuItem tgtMenuItem;
    private JRadioButtonMenuItem prcMenuItem;

    private int ficIndex = 0;

    JPanel statusPanel = new JPanel();

    private static Wget wget = new Wget();

    public void setEngine(Engine engine) {
        currentEngine = engine;
        currentEngineId = engine.myId;
        currentSrc = currentEngine.mySrc;
        currentTgt = currentEngine.myTgt;
        updateSettingsLabel();
        savePrefs();
        checkLang = true;
    }

    private class Fic {

        String name;
        File file;
        int myIndex;
        String myId;
        String mySrc = "";
        String myTgt = "";
        int myEngineId = 0;
        Engine myEngine = null;
        String status = "";
        int statusId = -1;
        MyCodes code;
        String link = "---";
        String processName = "";
        String translatedName = "";
        String glossaryName = "";
        int glossaryId = 0;
        boolean isZip = false;
        int ztot = 0;
        int zfinished = 0;
        int zfailed = 0;

        public long lastTryUpload = 0;
        public int numUploadTries = 0;

        public Fic(File file) {
            System.out.println("Creating fic " + file.getName());
            this.name = file.getName();
            this.file = file;
            listModel.add(listModel.size(), this);
            this.code = MyCodes.PENDING_UPLOAD;
            this.status = this.code.getMsg();
            this.mySrc = currentSrc;
            this.myTgt = currentTgt;
            this.myEngineId = currentEngineId;
            this.myEngine = currentEngine;
            this.processName = currentProcess;
            this.glossaryName = currentGlossary;
            this.glossaryId = currentGlossaryId;
            this.translatedName = "";
            model.addRow(new Object[]{this.name, this.processName, this.mySrc, this.myTgt, this.status, ""});
            this.myIndex = ficIndex++;

            System.out.println("Created fic " + file.getName() + " index " + myIndex + "  SRC: " + this.mySrc + "  TGT: " + this.myTgt);
        }

        public Fic(String filename, String processname, String translatedName, String fileid, String src, String tgt, MyCodes code) {
            System.out.println("Re Creating fic " + filename);
            this.name = filename;
            //this.file=null;
            //listModel.add(listModel.size(), this);
            this.code = code;
            this.status = this.code.getMsg();
            this.mySrc = src;
            this.myTgt = tgt;
            this.myId = fileid;
            this.processName = processname;
            this.translatedName = translatedName;
            model.addRow(new Object[]{this.name, this.processName, this.mySrc, this.myTgt, this.status, ""});
            this.myIndex = ficIndex++;

            System.out.println("Re Created fic " + this.name + " index " + myIndex + "  SRC: " + this.mySrc + "  TGT: " + this.myTgt);
        }

        @Override
        public String toString() {
            return name;
        }

        public void select() {
            System.out.println("File " + name + " selected");
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public void setCode(MyCodes code) {
            System.out.println("setCode starts");
            this.code = code;
            System.out.println("setCode Msg: " + code.getMsg());
            setStatus(code.getMsg());
            if (isZip && code == MyCodes.TRANSLATING) {
                model.setValueAt(ztot + "/" + zfinished + "/" + zfailed, myIndex, 4);
            } else {
                model.setValueAt(code.getMsg(), myIndex, 4);
            }

        }

        public void setLink(String link) {
            this.link = link;
            model.setValueAt(link, myIndex, 5);

        }

        public void setId(String id) {
            this.myId = id;
        }

        public void setTranslatedName(String tName) {
            this.translatedName = tName;
        }
    }

    private TransferHandler handler = new TransferHandler() {
        public boolean canImport(TransferHandler.TransferSupport support) {
            if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                return false;
            }

            return true;
        }

        public boolean importData(TransferHandler.TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }

            Transferable t = support.getTransferable();

            try {
                java.util.List<File> l
                        = (java.util.List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);

                for (File f : l) {
                    //Doc doc = new Doc(f);
                    System.out.println("New Doc detected: " + f.getName());

                    //get file type and warn and abort if not accceptable
                    String extension = "";

                    int i = f.getName().lastIndexOf('.');
                    if (i >= 0) {
                        extension = f.getName().substring(i + 1);
                    }
                    System.out.println("Extension detected:" + extension);
                    if (!apiKeyOk) {
                        JOptionPane.showMessageDialog(null, "Sorry, configured API Key is not validated");
                        System.out.println("API Key not validated");

                    } else if (allowedTypes.contains(extension)) {
                        System.out.println("Extention allowed:" + extension);

                        //checklang
                        if (checkLang) {
                            String msg = "File(s) will be translated from " + currentSrc + " -> " + currentTgt + " (" + currentEngine.descr + ")";
                            if (currentGlossaryId > 0) {
                                msg = msg + "\nusing " + currentGlossary;
                            }
                            msg = msg + ".\n\nAre you sure?";
                            //ImageIcon icon = new ImageIcon(getClass().getResource("src/main/resources/pangeamt_icon.png"));
                            ImageIcon picture1 = new ImageIcon("src/main/resources/MTHubIco.PNG");
                            int result = JOptionPane.showConfirmDialog((Component) null, msg, "Confirmation", JOptionPane.INFORMATION_MESSAGE);// icon);    
                            if (result != JOptionPane.OK_OPTION) {
                                return false;
                            }
                        }
                        checkLang = false;

                        Fic fic = new Fic(f);
                        if (extension.equals("zip")) {
                            fic.isZip = true;
                        }
                        System.out.println("New Doc created " + fic.name + " added to list");
                        listFics.add(fic);

                    } else {
                        String at = "";
                        for (int j = 0; j < allowedTypes.size(); j++) {
                            if (j > 0) {
                                at = at + ", ";
                            }
                            if ((j + 1) % 10 == 0) {
                                at = at + "\n";
                            }
                            at = at + allowedTypes.get(j);
                        }
                        JOptionPane.showMessageDialog(null, "Can't accept " + f.getName() + "\nOnly allowed file types:\n" + at);
                        System.out.println("Extention NOT allowed:" + at);
                    }

                }
            } catch (UnsupportedFlavorException e) {
                System.out.println("UFE: " + e);
                return false;
            } catch (IOException e) {
                System.out.println("IOE: " + e);
                return false;
            }

            return true;
        }
    };

    private static void incr() {
        left += 30;
        top += 30;
        if (top == 150) {
            top = 0;
        }
    }

    
    private int sendGetStatus() throws ClientProtocolException, IOException {
        int retstatus = 200;
        for (Fic fic : listFics) {
             if (fic.myId != null) if (fic.code.id<120 && fic.code.id>-1) {
                 System.out.println("FIC FOUND: " + fic.myId + " " + fic.name + " Status: " + fic.code.id );
                 
                 //get status
                CloseableHttpClient httpclient = HttpClients.createDefault();

                try {
                    HttpPost request = new HttpPost("https://" + hostAddress + "/aretrievefiletranslation");
                    String paramsString="{\"token\":\""+ apiKey +"\",\"guid\":\""+ fic.myId +"\"} ";
                    System.out.println("Sending REST to " + "https://" + hostAddress + "/aretrievefiletranslation" + " Using: " + paramsString);
                    StringEntity params =new StringEntity(paramsString);
                    request.addHeader("content-type", "application/json");
                    request.addHeader("Accept","application/json");
                    request.setEntity(params);
                    CloseableHttpResponse response = httpclient.execute(request);
                    int status = response.getStatusLine().getStatusCode();
                    System.out.println("REST answered: " + response.toString() + " statusCode: " + status);
                    // handle response here...
                    HttpEntity entity = response.getEntity();
                    String responseString = EntityUtils.toString(entity);
                    System.out.println("REST rs: " + responseString);
                    JSONObject jsonObject = new JSONObject(responseString);
                    boolean success = jsonObject.getBoolean("success");
                    System.out.println("success: " + success);
                    
                    if (success) {
                        System.out.println("success detected");
                        JSONObject data = jsonObject.getJSONObject("data");
                        System.out.println("data answer parsed");
                        String encoded=data.getString("file");
                        if (encoded.length()<5){
                            int result = Integer.parseInt((String)data.get("file"));
                            int statusFile=result;
                            System.out.println("File not finished, status: " + statusFile);
                            MyCodes code = MyCodes.getCode(statusFile);
                            fic.setCode(code);    
                        } else {
                            //file can be downloaded
                            System.out.println("File can be downloaded");
                            
                            byte[] filedata = Base64.getDecoder().decode(encoded);

                            try { 
                               System.out.println("Writing to: " + translatedFolder + fic.name);
                               OutputStream stream = new FileOutputStream(translatedFolder + fic.name);      
                               stream.write(filedata);
                               System.out.println("File dowloaded to : " + translatedFolder + fic.name);
                               MyCodes code = MyCodes.getCode(120);
                               System.out.println("Setting code to 120");
                               fic.setCode(code);
                               System.out.println("Code is now 120");
                               stream.close();
                               System.out.println("Stream closed");
                            }
                            catch (Exception e) 
                            {
                               System.err.println("Couldn't write to file...");
                            }
                        }

                    } else {
                        //File not finished
                        System.out.println("error answer!");
                        JSONObject error = jsonObject.getJSONObject("error");

                        int statusFile=error.getInt("code");
                        System.out.println("File not finished, status: " + statusFile);
                        //MyCodes code = MyCodes.getCode(statusFile);
                        //fic.setCode(code);
                    }
                    
                    
                }catch (Exception ex) {
                    // handle exception here
                } finally {
                    httpclient.close();
                }                 
             }
        }


        return retstatus;
    }

    private static int sendRest(Fic doc) throws ClientProtocolException, IOException {

        System.out.println("sendRest for " + doc.name);
        String ext1 = FilenameUtils.getExtension(doc.name);
        
        String encodedFile = null;
        FileInputStream fileInputStreamReader = new FileInputStream(doc.file);
        byte[] bytes = new byte[(int)doc.file.length()];
        fileInputStreamReader.read(bytes);
        encodedFile = new String(Base64.getEncoder().encode(bytes));

        StringEntity payload = new StringEntity("{\"fileType\":\"" + ext1 + "\",\"source\":\"" + doc.mySrc + "\",\"target\":\"" + doc.myTgt+ "\",\"token\":\"" + apiKey + "\",\"file\":\"" + encodedFile + "\"}");
        System.out.println("Sending post for fileType: " + ext1 + " " + doc.mySrc + ">" + doc.myTgt + " at url: http://" + hostAddress + "/atranslatefile");

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost request = new HttpPost("https://" + hostAddress + "/atranslatefile");

        request.addHeader("content-type", "application/json");
        request.addHeader("Accept","application/json");
        request.setEntity(payload);
        CloseableHttpResponse response = client.execute(request);
        int status = response.getStatusLine().getStatusCode();
        System.out.println("REST answered: " + response.toString() + " statusCode: " + status);
        // handle response here...
        HttpEntity entity = response.getEntity();
        String responseString = EntityUtils.toString(entity);
        System.out.println("REST rs: " + responseString);
        JSONObject jsonObject = new JSONObject(responseString);
        JSONObject data = jsonObject.getJSONObject("data");
        String fileId= data.getString("guid");

        
        System.out.println("File Id: " + fileId);
        doc.setCode(MyCodes.UPLOADED);
        doc.setId(fileId);
        client.close();
        return status;
    }


    public static void setColumnWidths(JTable table, int... widths) {
        TableColumnModel columnModel = table.getColumnModel();
        for (int i = 0; i < widths.length; i++) {
            if (i < columnModel.getColumnCount()) {
                columnModel.getColumn(i).setMaxWidth(widths[i]);
            } else {
                break;
            }
        }
    }


    private int sendRestGrants() throws ClientProtocolException, IOException {
        Engine.engines.clear();

        System.out.println("Sending get RestGrant to:" + "http://" + hostAddress + "/describesuppliers/" + apiKey );
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://" + hostAddress + "/describesuppliers/" + apiKey);
        CloseableHttpResponse response = httpclient.execute(httpGet);

        int status = response.getStatusLine().getStatusCode();
        if (status==200) apiKeyOk=true;
        System.out.println("REST answered statusCode: " + status);
        System.out.println("REST answered: " + response.toString());
        HttpEntity entity = response.getEntity();
        String responseString = EntityUtils.toString(entity);
        System.out.println("REST rs: " + responseString);
        JSONObject jsonObject = new JSONObject(responseString);
        JSONObject data = jsonObject.getJSONObject("data");
        JSONArray suppliers = data.getJSONArray("suppliers");
        int engineId=0;
        for (int i = 0, size = suppliers.length(); i < size; i++) {
            JSONObject supplier = suppliers.getJSONObject(i);
            String supplierName= supplier.getString("name");
            JSONArray engines = supplier.getJSONArray("engines");
                for (int j = 0, sizeengines = engines.length(); j < sizeengines; j++) {
                    engineId++;
                    JSONObject engineObj = engines.getJSONObject(j);
                    String name = engineObj.getString("name");
                    String src = engineObj.getString("source");
                    String tgt = engineObj.getString("target");
                    String domain = engineObj.getString("domain");
                    Engine e = new Engine(engineId, domain, "", src, tgt, supplierName + "*" + name + " " + domain);
                    if (currentEngineId == engineId) {
                        currentEngine = Engine.getEngine(currentEngineId);
                    }
                    engMenuItem = new JMenuItem(e.getTitle());
                    engMenuItem.setMnemonic(KeyEvent.VK_R);
                    eng.add(engMenuItem);
                    engMenuItem.addActionListener(engActionListener);

                }
            } 
        httpclient.close();
        boolean found = false;
        for (Engine engine : Engine.engines) {
            System.out.println("Engine " + engine.myId + ": " + engine.descr);
        }
        if (!Engine.engines.contains(currentEngine) && Engine.engines.size() > 0) {
            System.out.println("Current Engine not found");
            currentEngine = Engine.engines.get(0);
            currentEngineId = currentEngine.myId;
            currentSrc = currentEngine.mySrc;
            currentTgt = currentEngine.myTgt;
            updateSettingsLabel();
        } else {
            System.out.println("Current Engine found: " + currentEngineId);
        }
        if (Engine.engines.size() > 5) {
            eng.removeAll();
            engMenuItem = new JMenuItem("Choose from List");
            engMenuItem.setMnemonic(KeyEvent.VK_R);
            //JMenuItem item2 = new JMenuItem("Opcion 2");
            eng.add(engMenuItem);
            engMenuItem.addActionListener(engActionListener);
            //eng.add(item2);
            //item2.addActionListener(engActionListener2);         
        }
        updateSettingsLabel();
        savePrefs();
        return status;
    }


    void updateSettingsLabel() {
        if (!apiKeyOk) {
            settingsLabel.setText("Configured API Key is not validated");
        } else {
           
                //JLabel j1 = new JLabel(new ImageIcon(myPicture));
                settingsLabel.setForeground(Color.WHITE);
                
                settingsLabel.setBounds(top + 10, top, 650, 60);
                
                //settingsLabel.setText("<html>Drop files here to " + currentProcess + " " + currentSrc + " -> " + currentTgt + " " + currentEngine.descr + " using " + currentGlossary + " [" + currentGlossaryId + "]" + "<br>Languages Selected: From " + currentSrc.toUpperCase() + " to " + currentTgt.toUpperCase() + "</html>");
                settingsLabel.setText("<html>Drop files here to translate " + currentSrc + " -> " + currentTgt + " " + currentEngine.descr + "<br/>Languages Selected: From " + currentSrc.toUpperCase() + " to " + currentTgt.toUpperCase() + "</html>");
            
        }
    }

    public FilePlugin() {
        super("MT-Hub FilePlugin App. V0");

        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        getContentPane().add(statusPanel, BorderLayout.SOUTH);
        statusPanel.setPreferredSize(new Dimension(getContentPane().getWidth(), 20));
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        statusLabel = new JLabel("status");
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusPanel.add(statusLabel);

        settingsLabel = new JLabel("Loading Settings");

        setJMenuBar(createDummyMenuBar());
        //getContentPane().add(createDummyToolBar(), BorderLayout.NORTH);
        if (model.getColumnCount() >=6) {
            ;
        }else{
            model.addColumn("File name");
        model.addColumn("Process");
        model.addColumn("SRC");
        model.addColumn("TGT");
        model.addColumn("Status");
        model.addColumn("Download Link");
        System.out.println("----------------------");
        System.out.println(model.getColumnCount());
        
        }
        

        setColumnWidths(table, 200, 120, 40, 40, 100, 400);

        JTableHeader header = table.getTableHeader();
        //header.setBackground(Color.gray);
        //header.setFont(new Font("Dialog", 13));

        //JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, list, dp);
        JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, dp);
        sp.setDividerLocation(260);
        getContentPane().add(sp);
        settingsLabel.setPreferredSize(new Dimension(700, 20));
        settingsLabel.setBounds(new Rectangle(new Point(10, 10), settingsLabel.getPreferredSize()));
        dp.add(settingsLabel);

        updateSettingsLabel();

        table.setFillsViewportHeight(true);

        list.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        list.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }

                Fic val = (Fic) list.getSelectedValue();
                //Doc val = (Doc)list.getSelectedValue();
                if (val != null) {
                    val.select();
                }
            }
        });

        final TransferHandler th = list.getTransferHandler();
        list.setTransferHandler(th);
        setTransferHandler(null);

        dp.setTransferHandler(handler);
        //setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("src/main/resources/pangeamt_icon.png")));

        setIconImage(Toolkit.getDefaultToolkit().getImage("src/main/resources/MTHubIco.PNG"));

    }

    boolean started = false;
    //initialization functions of plugin here

    private void start() {
        System.out.println("Start() called, cleaning files");
        //clean everything
        listFics.clear();
        ficIndex = 0;
        int rowCount = model.getRowCount();
        //Remove rows one by one from the end of the table
        for (int i = rowCount - 1; i >= 0; i--) {
            model.removeRow(i);
        }

        System.out.println("Sending jobs request");
        statusLabel.setText("Connecting to server now...");

        try {
            int status = sendRestGrants();
            if (status != 200) {
                statusLabel.setText("Error connecting to server, retrying...");
            } else {
                started = true;
                statusLabel.setText("Server connected");
                //splash.splash.close();

            }
        } catch (Exception ee) {
            System.out.println("Excpt: " + ee);
            statusLabel.setText("Error connecting to server, retrying...");
        }

    }

    private void init() {

        System.out.println("Starting background timer");
        final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                //System.out.println("Timer triggered @ " + new Date() + " " + System.currentTimeMillis());
                try {
                    if (!started) {
                        start();
                    }
                    uploadPendingFiles();
                    //getCurrentFileStatus();
                } catch (Throwable t) {  // Catch Throwable rather than Exception (a subclass).
                    System.out.println("Caught exception in ScheduledExecutorService. StackTrace:\n" + t.getStackTrace());
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
        System.out.println("Started background timer");
    }

    private long lastStatus = 0;

    private void getCurrentFileStatus() {
        long now = System.currentTimeMillis();
        boolean error = false;
        //statusLabel.setText("Checking jobs status now?");
        if (now > lastStatus + 10 * 1000) {
            statusLabel.setText("Checking jobs status");
            //System.out.println("Checking status " + " @ " + new Date());
            lastStatus = now;

            try {
                int status = sendGetStatus();
                if (status != 200) {
                    error = true;
                }
            } catch (Exception ee) {
                error = true;
                System.out.println("Exception getting status" + ee);
            }
            if (!error) {
                statusLabel.setText("Connection to " + hostAddress + " OK");
            } else {
                statusLabel.setText("Error refreshing status");
            }
        }
    }

    private void uploadPendingFiles() {
        long now = System.currentTimeMillis();
        boolean uploaded = false;
        for (Fic fic : listFics) {
            if (fic.code == MyCodes.PENDING_UPLOAD && now - fic.lastTryUpload > Math.min(60 * 60 * 1000, fic.numUploadTries * 1000)) {
                System.out.println("Uploading " + fic.name + " @ " + new Date());
                fic.numUploadTries++;
                fic.lastTryUpload = now;
                try {
                    fic.setCode(MyCodes.UPLOADING);
                    sendRest(fic);
                    uploaded = true;
                } catch (Exception ee) {
                    System.out.println("Exception uploading " + ee);
                    fic.setCode(MyCodes.PENDING_UPLOAD);
                }
            } else {
                //System.out.println("No further action for " + fic.name);
            }
            if (!uploaded) {
                getCurrentFileStatus();
            }
        }

    }

    private static void createAndShowGUI(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }

        test = new FilePlugin();

        test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        test.setSize(800, 600);
        test.setLocationRelativeTo(null);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (!apiKey.equals("000000")) {
                    test.init();
                }
            }
        });

    }

    public static void main(final String[] args) {
        // en_US
        FilePlugin pgbclient = new FilePlugin();
        // Setup the Preferences for this application, by class.
        prefs = Preferences.userNodeForPackage(FilePlugin.class);
        loadPrefs();

        Locale.setDefault(new Locale("en", "EN"));

        System.out.println("Current Locale: " + Locale.getDefault());
        mybundle = ResourceBundle.getBundle("fileplugin");

        /*
        Locale.setDefault(new Locale("es", "ES"));

        // read MyLabels_ms_MY.properties
        System.out.println("Current Locale: " + Locale.getDefault());
        mybundle = ResourceBundle.getBundle("fileplugin");
        System.out.println("PENDING UPLOAD: " +  MyCodes.PENDING_UPLOAD.getMsg());
         */
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //Turn off metal's use of bold fonts
                UIManager.put("swing.boldMetal", Boolean.FALSE);
                createAndShowGUI(args);
            }
        });

        splash = new Splash(test);
        /*
        while (splashShowing)
            try { 
                Thread.sleep(1000); 
            } catch (Exception ee) {}
         */
        test.setVisible(true);
        test.list.requestFocus();
        if (apiKey.equals("000000")) {
            //String result = (String) JOptionPane.showInputDialog(null,"Enter the API Key","Set Credentials",  JOptionPane.OK_CANCEL_OPTION, null, null, apiKey);

            pgbclient.get_credentials();

            /*
                    if (result != null) {
                        System.out.println("You entered " +
                                result );
                        apiKey=result;
                        savePrefs();
                        test.init();
                    } else {
                        System.out.println("User canceled / closed the dialog, result = " + result);
                    }
             */
        }

    }

    public void get_credentials() {
        System.out.println("--- Starting PGB & Get Credentials");
         //String result = (String) JOptionPane.showInputDialog(null,"Enter the API Key","Set Credentials",  JOptionPane.OK_CANCEL_OPTION, null, null, apiKey);
         JTextField userN = new JTextField(user);
         JTextField apiKeyF = new JTextField(apiKey);
          Object[] message_Credentials1 = {
        "Username:", userN,
        "API Key:", apiKeyF
    };
        int option = JOptionPane.showConfirmDialog(null, message_Credentials1, "Set Credentials", JOptionPane.OK_CANCEL_OPTION );
        if (option == JOptionPane.OK_OPTION) {
            user = userN.getText();
            apiKey = apiKeyF.getText();

            System.out.println("You entered user: " + user + " APIKey: " + apiKey);
            // apiKey=result;
            System.out.println("Refresh Files now");
            savePrefs();
            test.init();

        } else {
            System.out.println("User canceled / closed the dialog, result = " + user + " " + apiKey);
        }
    }

    private static void loadPrefs() {
        System.out.println("Loading Preferences");
        hostAddress = prefs.get("AccessPoint", "www.pangeanic-online.com:5095");
        apiKey = prefs.get("ApiKey", "000000");
        user = prefs.get("UserName", "admin");
        translatedFolder = prefs.get("TranslationFolder", "//");
        tempFolder = prefs.get("TempFolder", "//");
        currentSrc = prefs.get("SrcLang", "IT");
        currentTgt = prefs.get("TgtLang", "EN");
        currentEngineId = prefs.getInt("EngineId", 0);
        //currentEngine=Engine.getEngine(currentEngineId);
        currentProcess = prefs.get("Process", "Machine Translate");
        currentGlossary = prefs.get("Glossary", "NO GLOSSARY");
        currentGlossaryId = prefs.getInt("GlossaryId", 0);
        System.out.println("Preferences loaded, Address: " + hostAddress + " Key: " + apiKey + " Username:" + user
                + " OutFolder: " + translatedFolder
                + " TempFolder: " + tempFolder
                + " Engine: " + currentEngineId);
    }

    private static void savePrefs() {
        System.out.println("Saving Preferences: " + hostAddress + " " + apiKey + " " + translatedFolder);
        prefs.put("AccessPoint", hostAddress);
        prefs.put("ApiKey", apiKey);
        prefs.put("UserName", user);
        prefs.put("TranslationFolder", translatedFolder);
        prefs.put("TempFolder", tempFolder);
        prefs.put("SrcLang", currentSrc);
        prefs.put("TgtLang", currentTgt);
        prefs.putInt("EngineId", currentEngineId);
        prefs.put("Process", currentProcess);
        prefs.put("Glossary", currentGlossary);
        prefs.putInt("GlossaryId", currentGlossaryId);

    }

    ActionListener cfgActionListener = new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
            JMenuItem source = (JMenuItem) (ae.getSource());
            String s = "Curren CFG: " + source.getText();
            System.out.println(s);
            //currentSrc=source.getText();
            //updateSettingsLabel();
            if (source.getText().equals("Set Access Point")) {
                //int result = JOptionPane.showConfirmDialog(null,inputsAccessPoint,"Access Point Setting",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE,null);
                //int result = JOptionPane.showConfirmDialog(null, inputsAccessPoint, "Access Point Setting", JOptionPane.PLAIN_MESSAGE);

                String result = (String) JOptionPane.showInputDialog(null,
                        "Enter the url where PangeaBox server is accessible\n(for instance prod.pangeamt.com:8080/NexAPI/v1)\n", " Access Point", JOptionPane.OK_CANCEL_OPTION, null,
                        null, hostAddress);

                if (result != null) {
                    System.out.println("You entered " + result);
                    hostAddress = result;
                    test.start();
                } else {
                    System.out.println("User canceled / closed the dialog, result = " + result);
                }

            } else if (source.getText().equals("Set Credentials")) {
                //String result = (String) JOptionPane.showInputDialog(null, "Enter the API Key"," Credentials",  JOptionPane.OK_CANCEL_OPTION, null, null, apiKey);

                int option = JOptionPane.showConfirmDialog(null, message_Credentials, "Credentials", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    user = userName.getText();
                    apiKey = apiKeyField.getText();

                    System.out.println("You entered USER: " + user + " APIKey: " + apiKey);
                    // apiKey=result;
                    System.out.println("Refresh Files now");
                    test.start();
                } else {
                    System.out.println("User canceled / closed the dialog, result = " + user + " " + apiKey);
                }

            } else if (source.getText().equals("Download Folder")) {
                String choosertitle = "Select Destination Path";
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new java.io.File(translatedFolder));
                chooser.setDialogTitle(choosertitle);
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                //
                // disable the "All files" option.
                //
                chooser.setAcceptAllFileFilterUsed(false);
                //    
                if (chooser.showOpenDialog(test) == JFileChooser.APPROVE_OPTION) {
                    System.out.println("getCurrentDirectory(): "
                            + chooser.getCurrentDirectory());
                    System.out.println("getSelectedFile() : "
                            + chooser.getSelectedFile());
                    translatedFolder = chooser.getSelectedFile() + "/";//+ File.pathSeparator;//"/";
                } else {
                    System.out.println("No Selection ");
                }
            } else if (source.getText().equals("Temp Folder")) {
                String choosertitle = "Select Temporal Path";
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new java.io.File(tempFolder));
                chooser.setDialogTitle(choosertitle);
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                //
                // disable the "All files" option.
                //
                chooser.setAcceptAllFileFilterUsed(false);
                //    
                if (chooser.showOpenDialog(test) == JFileChooser.APPROVE_OPTION) {
                    System.out.println("getCurrentDirectory(): "
                            + chooser.getCurrentDirectory());
                    System.out.println("getSelectedFile() : "
                            + chooser.getSelectedFile());
                    tempFolder = chooser.getSelectedFile() + File.pathSeparator; //"/";
                } else {
                    System.out.println("No Selection ");
                }
            }

            savePrefs();
        }

        public void itemStateChanged(ItemEvent e) {
            JMenuItem source = (JMenuItem) (e.getSource());
            String s = "Item event detected." + "    Event source: " + source.getText() + "    New state: " + ((e.getStateChange() == ItemEvent.SELECTED) ? "selected" : "unselected");
            System.out.println(s);
        }
    };

    ActionListener engActionListener = new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
            JMenuItem source = (JMenuItem) (ae.getSource());
            String s = "Selected E: " + source.getText();
            System.out.println(s);
            if (source.getText().equals("Choose from List")) {
                //EngineDialog.createAndShowGUI(this);
                EngineDialog eDlg = new EngineDialog(test);
                eDlg.setVisible(true);
            } else {
                for (Engine e : Engine.engines) {
                    if (e.getTitle().equals(source.getText())) {
                        currentEngine = e;
                        currentEngineId = e.myId;
                        currentSrc = e.mySrc;
                        currentTgt = e.myTgt;
                    }
                }
                updateSettingsLabel();
                savePrefs();
                checkLang = true;
            }
        }

        public void itemStateChanged(ItemEvent e) {
            JMenuItem source = (JMenuItem) (e.getSource());
            String s = "Item event detected." + "    Event source: " + source.getText() + "    New state: " + ((e.getStateChange() == ItemEvent.SELECTED) ? "selected" : "unselected");
            System.out.println(s);
        }
    };

    ActionListener srcActionListener = new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
            JMenuItem source = (JMenuItem) (ae.getSource());
            String s = "Curren SRC: " + source.getText();
            System.out.println(s);
            currentSrc = source.getText();
            updateSettingsLabel();
            savePrefs();
            checkLang = true;
        }

        public void itemStateChanged(ItemEvent e) {
            JMenuItem source = (JMenuItem) (e.getSource());
            String s = "Item event detected." + "    Event source: " + source.getText() + "    New state: " + ((e.getStateChange() == ItemEvent.SELECTED) ? "selected" : "unselected");
            System.out.println(s);
        }
    };
    ActionListener tgtActionListener = new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
            JMenuItem source = (JMenuItem) (ae.getSource());
            String s = "Current TGT: " + source.getText();
            System.out.println(s);
            currentTgt = source.getText();
            updateSettingsLabel();
            savePrefs();
            checkLang = true;

        }

        public void itemStateChanged(ItemEvent e) {
            JMenuItem source = (JMenuItem) (e.getSource());
            String s = "Item event detected." + "    Event source: " + source.getText() + "    New state: " + ((e.getStateChange() == ItemEvent.SELECTED) ? "selected" : "unselected");
            System.out.println(s);
        }
    };
    ActionListener prcActionListener = new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
            JMenuItem source = (JMenuItem) (ae.getSource());
            String s = "Current Process: " + source.getText();
            System.out.println(s);
            if (source.getText().equals("Clear Old Files")) {
                start();

            } else {
                currentProcess = source.getText();
                updateSettingsLabel();
                savePrefs();
            }

        }

        public void itemStateChanged(ItemEvent e) {
            JMenuItem source = (JMenuItem) (e.getSource());
            String s = "Item event detected." + "    Event source: " + source.getText() + "    New state: " + ((e.getStateChange() == ItemEvent.SELECTED) ? "selected" : "unselected");
            System.out.println(s);
        }
    };

            ActionListener actionListenerRadio = new ActionListener() {
      
        public void actionPerformed(ActionEvent actionEvent) {
            AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
            boolean selected = abstractButton.getModel().isSelected();
                System.out.println("Auto Mode: "+ selected);
                auto = selected;
      }
    };

    private JToolBar createDummyToolBar() {
        JToolBar tb = new JToolBar();
        JButton b;
        b = new JButton("New");
        b.setRequestFocusEnabled(false);
        tb.add(b);
        b = new JButton("Open");
        b.setRequestFocusEnabled(false);
        tb.add(b);
        b = new JButton("Save");
        b.setRequestFocusEnabled(false);
        tb.add(b);
        b = new JButton("Print");
        b.setRequestFocusEnabled(false);
        tb.add(b);
        b = new JButton("Preview");
        b.setRequestFocusEnabled(false);
        tb.add(b);
        tb.setFloatable(false);
        return tb;
    }

    private JMenuBar createDummyMenuBar() {
        JMenuBar mb = new JMenuBar();

        JMenu cfg = new JMenu("Configuration");
        mb.add(cfg);

        ButtonGroup cfgGroup = new ButtonGroup();
        cfgMenuItem = new JMenuItem("Set Access Point");
        cfgMenuItem.setMnemonic(KeyEvent.VK_R);
        cfgGroup.add(cfgMenuItem);
        cfg.add(cfgMenuItem);
        cfgMenuItem.addActionListener(cfgActionListener);

        cfgMenuItem = new JMenuItem("Set Credentials");
        cfgMenuItem.setMnemonic(KeyEvent.VK_R);
        cfgGroup.add(cfgMenuItem);
        cfg.add(cfgMenuItem);
        cfgMenuItem.addActionListener(cfgActionListener);

        cfgMenuItem = new JMenuItem("Download Folder");
        cfgMenuItem.setMnemonic(KeyEvent.VK_R);
        cfgGroup.add(cfgMenuItem);
        cfg.add(cfgMenuItem);
        cfgMenuItem.addActionListener(cfgActionListener);

        /*
        cfgMenuItem = new JMenuItem("Temp Folder");
        cfgMenuItem.setMnemonic(KeyEvent.VK_R);
        cfgGroup.add(cfgMenuItem);
        cfg.add(cfgMenuItem);
        cfgMenuItem.addActionListener(cfgActionListener);
         */
        eng = new JMenu("Translation Engine");
        mb.add(eng);

        JMenu prc = new JMenu("File Process");
        
        //mb.add(prc);

        ButtonGroup prcGroup = new ButtonGroup();

        prcMenuItem = new JRadioButtonMenuItem("Machine Translate");
        prcMenuItem.setMnemonic(KeyEvent.VK_O);
        prcGroup.add(prcMenuItem);
        prc.add(prcMenuItem);
        prcMenuItem.addActionListener(prcActionListener);
        prcMenuItem.setSelected(false);
        prcMenuItem.setEnabled(false);
        if (currentProcess.equals("Machine Translate")) {
            prcMenuItem.setSelected(true);
        }
        
        JCheckBox check = new JCheckBox("   Automatic Mode", auto);
        
        check.setSize(100, 100);
        prc.add(check);
        
  
        check.addActionListener(actionListenerRadio);
        
        

        prcMenuItem = new JRadioButtonMenuItem("Generic MTranslate");
        prcMenuItem.setMnemonic(KeyEvent.VK_O);
        prcGroup.add(prcMenuItem);
        prc.add(prcMenuItem);
        prcMenuItem.setEnabled(true);
        prcMenuItem.addActionListener(prcActionListener);
        prcMenuItem.setSelected(true);
        if (currentProcess.equals("Generic MTranslate")) {
            prcMenuItem.setSelected(true);
        }

        prcMenuItem = new JRadioButtonMenuItem("Translation + Summarization");
        prcMenuItem.setMnemonic(KeyEvent.VK_O);
        prcGroup.add(prcMenuItem);
        prc.add(prcMenuItem);
        prcMenuItem.setEnabled(false);
        prcMenuItem.addActionListener(prcActionListener);
        prcMenuItem.setSelected(false);
        if (currentProcess.equals("Translation + Summarization")) {
            prcMenuItem.setSelected(true);
        }

        prcMenuItem = new JRadioButtonMenuItem("Translation + Postedition");
        prcMenuItem.setMnemonic(KeyEvent.VK_O);
        prcGroup.add(prcMenuItem);
        prc.add(prcMenuItem);
        prcMenuItem.setEnabled(false);
        prcMenuItem.addActionListener(prcActionListener);
        prcMenuItem.setSelected(false);
        if (currentProcess.equals("Translation + Postedition")) {
            prcMenuItem.setSelected(true);
        }

        prcMenuItem = new JRadioButtonMenuItem("Human Translation");
        prcMenuItem.setMnemonic(KeyEvent.VK_O);
        prcGroup.add(prcMenuItem);
        prc.add(prcMenuItem);
        prcMenuItem.setEnabled(false);
        prcMenuItem.addActionListener(prcActionListener);
        prcMenuItem.setSelected(false);
        if (currentProcess.equals("Human Translation")) {
            prcMenuItem.setSelected(true);
        }

        prcMenuItem = new JRadioButtonMenuItem("Domain Detection");
        prcMenuItem.setMnemonic(KeyEvent.VK_O);
        prcGroup.add(prcMenuItem);
        prc.add(prcMenuItem);
        prcMenuItem.setEnabled(false);
        prcMenuItem.addActionListener(prcActionListener);
        prcMenuItem.setSelected(false);
        if (currentProcess.equals("Domain Detection")) {
            prcMenuItem.setSelected(true);
        }

        prcMenuItem = new JRadioButtonMenuItem("Indexation");
        prcMenuItem.setMnemonic(KeyEvent.VK_O);
        prcGroup.add(prcMenuItem);
        prc.add(prcMenuItem);
        prcMenuItem.setEnabled(false);
        prcMenuItem.addActionListener(prcActionListener);
        prcMenuItem.setSelected(false);
        if (currentProcess.equals("Indexation")) {
            prcMenuItem.setSelected(true);
        }

        prcMenuItem = new JRadioButtonMenuItem("Summarization");
        prcMenuItem.setMnemonic(KeyEvent.VK_O);
        prcGroup.add(prcMenuItem);
        prc.add(prcMenuItem);
        prcMenuItem.setEnabled(false);
        prcMenuItem.addActionListener(prcActionListener);
        prcMenuItem.setSelected(false);
        if (currentProcess.equals("Summarization")) {
            prcMenuItem.setSelected(true);
        }

        prcMenuItem = new JRadioButtonMenuItem("Sentiment Analysis");
        prcMenuItem.setMnemonic(KeyEvent.VK_O);
        prcGroup.add(prcMenuItem);
        prc.add(prcMenuItem);
        prcMenuItem.setEnabled(false);
        prcMenuItem.addActionListener(prcActionListener);
        prcMenuItem.setSelected(false);
        if (currentProcess.equals("Sentiment Analysis")) {
            prcMenuItem.setSelected(true);
        }

        prcMenuItem = new JRadioButtonMenuItem("Get-a-Quote");
        prcMenuItem.setMnemonic(KeyEvent.VK_O);
        prcGroup.add(prcMenuItem);
        prc.add(prcMenuItem);
        prcMenuItem.setEnabled(false);
        prcMenuItem.addActionListener(prcActionListener);
        prcMenuItem.setSelected(false);
        if (currentProcess.equals("Get-a-Quote")) {
            prcMenuItem.setSelected(true);
        }

        prc2MenuItem = new JMenuItem("Clear Old Files");
        prc2MenuItem.setMnemonic(KeyEvent.VK_O);
        prcGroup.add(prc2MenuItem);
        prc.add(prc2MenuItem);
        prc2MenuItem.setEnabled(true);
        prc2MenuItem.addActionListener(prcActionListener);
        prc2MenuItem.setSelected(false);


        return mb;
    }

    private JMenu createDummyMenu(String str) {
        JMenu menu = new JMenu(str);
        JMenuItem item = new JMenuItem("[Empty]");
        item.setEnabled(false);
        menu.add(item);
        return menu;
    }

    public enum MyCodes {
        UNKNOWN(-1, "unknown"),
        PENDING_UPLOAD(0, "pending_upload"),
        UPLOADING(5, "uploading"),
        UPLOADED(10, "uploaded"),
        PREPROCESSING(20, "preprocessing"),
        TRANSLATING(30, "translating"),
        POSTPROCESING(40, "postprocessing"),
        FINISHED(100, "finished"),
        downloaded(110, "downloaded"),
        DOWNLOADED(120, "downloaded"),
        FAILED(-10, "failed"),;
        private final int id;
        private final String msg;

        MyCodes(int id, String msg) {
            this.id = id;
            this.msg = msg;
        }

        public int getId() {
            return this.id;
        }

        public String getMsg() {
            return mybundle.getString(this.msg);
        }

        // Reverse-lookup map for getting a day from an abbreviation
        private static final Map<Integer, MyCodes> lookup = new HashMap<Integer, MyCodes>();

        static {
            for (MyCodes d : MyCodes.values()) {
                lookup.put((Integer) d.getId(), d);
            }
        }

        public static MyCodes getCode(int i) {
            return lookup.get((Integer) i);
        }

    }

    public void glossDialog(File f) {
        JTextField xField = new JTextField(5);
        JTextField yField = new JTextField(5);

        JPanel myPanel = new JPanel();
        myPanel.add(new JLabel("x:"));
        myPanel.add(xField);
        myPanel.add(Box.createHorizontalStrut(15)); // a spacer
        myPanel.add(new JLabel("y:"));
        myPanel.add(yField);

        int result = JOptionPane.showConfirmDialog(null, myPanel,
                "Please Enter X and Y Values", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            System.out.println("x value: " + xField.getText());
            System.out.println("y value: " + yField.getText());
        }
    }

    class MyDesktopPane extends JDesktopPane {

        Image img;

        public MyDesktopPane() {
            try {
                URL url = getClass().getResource("/drophere.png");
                img = ImageIO.read(url);
            } catch (Exception e) {
            }//do nothing
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (img != null) {
                g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);
            } else {
                g.drawString("Image not found", 50, 50);
            }
        }
    }
}
