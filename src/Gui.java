import java.awt.EventQueue;
import java.awt.Image;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.Timer;
import java.awt.Point;

import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;

import java.awt.Color;

import javax.swing.JPanel;

import java.awt.Component;

import javax.swing.border.EtchedBorder;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.border.CompoundBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.UIManager;
import javax.swing.JTabbedPane;
import javax.swing.JScrollBar;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JProgressBar;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.border.EmptyBorder;

public class Gui {

    private JFrame frame;
    private JTextField Mem_Address;
    private JTextField Mem_value;
    private JTextField PSWH;
    private JTextField PSWL;
    private JTextField PC;
    private JTextField B;
    private JTextField C;
    private JTextField D;
    private JTextField E;
    private JTextField H;
    private JTextField L;
    private JTextField SP;
    private JTextField IR;
    private JTextField S;
    private JTextField Z;
    private JTextField AC;
    private JTextField P;
    private JTextField C_flag;

    private JTextField FLAG;
    private JTextField textField;
    private JTextField textField_1;
    private JTextField textField_2;

    Microprocessor up;
    Memory memory;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                     UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                    Gui window = new Gui();
                    window.frame.setVisible(true);
                    window.updateRegisters();

                    // updateRegisters();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void updateRegisters() {
        PC.setText( up.pc.hex());
        SP.setText( up.sp.hex());
        IR.setText( up.ir.hex());
        B.setText( up.register[0].hex());
        C.setText( up.register[1].hex());
        D.setText( up.register[2].hex());
        E.setText( up.register[3].hex());
        H.setText( up.register[4].hex());
        L.setText( up.register[5].hex());
        PSWH.setText( up.register[7].hex());
        PSWL.setText( up.flag.hex() );
        FLAG.setText( up.flag.value());
    }

    /**
     * Create the application.
     */
    public Gui() throws IOException {

        up = new Microprocessor();
        memory = new Memory(65536);
        Connector.connect(up,memory);
        memory.start();
        up.start();

        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() throws IOException {
        frame = new JFrame();
        frame.setBackground(UIManager.getColor("Button.background"));
        frame.setBounds(0, 0, 1200, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        frame.setResizable(false);

        JPanel tools_panel = new JPanel();
        tools_panel.setBounds(0, 0, 1201, 33);
        tools_panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        tools_panel.setLayout(null);
        frame.getContentPane().add(tools_panel);

        JScrollPane Message_scrollPane = new JScrollPane();
        Message_scrollPane.setBounds(0, 500, 1200, 199);
        Message_scrollPane.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        frame.getContentPane().add(Message_scrollPane);

        final JTextArea ErrorMessages = new JTextArea();
        ErrorMessages.setText("Error Pane!\n");
        ErrorMessages.setEditable(false);
        Message_scrollPane.setViewportView(ErrorMessages);

        JPanel Memory_panel = new JPanel();
        Memory_panel.setBounds(0, 32, 239, 466);
        Memory_panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        frame.getContentPane().add(Memory_panel);
        Memory_panel.setLayout(null);

        JLabel lblMemory = new JLabel("MEMORY");
        lblMemory.setBounds(81, 12, 70, 15);
        Memory_panel.add(lblMemory);

        Mem_Address = new JTextField();
        Mem_Address.setBounds(113, 62, 70, 25);
        Mem_Address.setToolTipText("Memory location");
        Mem_Address.setText("0000");
        Mem_Address.setHorizontalAlignment(JTextField.CENTER);
        Mem_Address.setEditable(false);
        Mem_Address.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
        Mem_Address.setBackground(UIManager.getColor("Button.highlight"));
        Memory_panel.add(Mem_Address);
        Mem_Address.setColumns(10);

        Mem_value = new JTextField();
        Mem_value.setBounds(113, 129, 70, 25);
        Mem_value.setToolTipText("Edit new value and click update");
        Mem_value.setText("0000");
        Mem_value.setHorizontalAlignment(JTextField.CENTER);
        Mem_value.setEditable(false);
        Mem_value.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
        Mem_value.setBackground(UIManager.getColor("Button.highlight"));
        Memory_panel.add(Mem_value);
        Mem_value.setColumns(10);

        JLabel lblLocation = new JLabel("Location");
        lblLocation.setBounds(32, 67, 70, 15);
        Memory_panel.add(lblLocation);

        JLabel lblValue = new JLabel("Value");
        lblValue.setBounds(32, 134, 70, 15);
        Memory_panel.add(lblValue);

        JPanel Reg_panel = new JPanel();
        Reg_panel.setBounds(996, 32, 204, 232);
        Reg_panel.setToolTipText("Current register status");
        Reg_panel.setBackground(UIManager.getColor("Button.background"));
        Reg_panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        frame.getContentPane().add(Reg_panel);
        Reg_panel.setLayout(null);

        JLabel lblRegisters = new JLabel("REGISTERS");
        lblRegisters.setBounds(63, 0, 90, 29);
        Reg_panel.add(lblRegisters);

        JLabel Psw_lbl = new JLabel("PSW");
        Psw_lbl.setBounds(43, 41, 38, 20);
        Reg_panel.add(Psw_lbl);

        JLabel Bc_lbl = new JLabel("BC");
        Bc_lbl.setBounds(43, 62, 38, 20);
        Reg_panel.add(Bc_lbl);

        JLabel De_lbl = new JLabel("DE");
        De_lbl.setBounds(43, 82, 38, 20);
        Reg_panel.add(De_lbl);

        JLabel Hl_lbl = new JLabel("HL");
        Hl_lbl.setBounds(43, 104, 38, 20);
        Reg_panel.add(Hl_lbl);

        JLabel Sp_lbl = new JLabel("SP");
        Sp_lbl.setBounds(43, 123, 38, 20);
        Reg_panel.add(Sp_lbl);

        JLabel Ir_lbl = new JLabel("IR");
        Ir_lbl.setBounds(43, 164, 38, 20);
        Reg_panel.add(Ir_lbl);

        JLabel PC_lbl = new JLabel("PC");
        PC_lbl.setBounds(43, 143, 38, 20);
        Reg_panel.add(PC_lbl);

        PSWH = new JTextField();
        PSWH.setBackground(UIManager.getColor("Button.background"));
        PSWH.setBorder(null);
        PSWH.setText("00");
        PSWH.setHorizontalAlignment(JTextField.CENTER);
        PSWH.setEditable(false);
        PSWH.setBounds(99, 42, 29, 19);
        Reg_panel.add(PSWH);
        PSWH.setColumns(10);

        PSWL = new JTextField();
        PSWL.setBackground(UIManager.getColor("Button.background"));
        PSWL.setBorder(null);
        PSWL.setHorizontalAlignment(JTextField.CENTER);
        PSWL.setText("00");
        PSWL.setEditable(false);
        PSWL.setColumns(10);
        PSWL.setBounds(140, 42, 29, 19);
        Reg_panel.add(PSWL);

        B = new JTextField();
        B.setBackground(UIManager.getColor("Button.background"));
        B.setBorder(null);
        B.setHorizontalAlignment(JTextField.CENTER);
        B.setText("00");
        B.setEditable(false);
        B.setColumns(10);
        B.setBounds(99, 63, 29, 19);
        Reg_panel.add(B);

        C = new JTextField();
        C.setBorder(null);
        C.setBackground(UIManager.getColor("Button.background"));
        C.setHorizontalAlignment(JTextField.CENTER);
        C.setText("00");
        C.setEditable(false);
        C.setColumns(10);
        C.setBounds(140, 63, 29, 19);
        Reg_panel.add(C);

        D = new JTextField();
        D.setBackground(UIManager.getColor("Button.background"));
        D.setBorder(null);
        D.setText("00");
        D.setHorizontalAlignment(JTextField.CENTER);
        D.setEditable(false);
        D.setColumns(10);
        D.setBounds(99, 83, 29, 19);
        Reg_panel.add(D);

        E = new JTextField();
        E.setBorder(null);
        E.setBackground(UIManager.getColor("Button.background"));
        E.setText("00");
        E.setHorizontalAlignment(JTextField.CENTER);
        E.setEditable(false);
        E.setColumns(10);
        E.setBounds(140, 83, 28, 19);
        Reg_panel.add(E);

        H = new JTextField();
        H.setBorder(null);
        H.setBackground(UIManager.getColor("Button.background"));
        H.setText("00");
        H.setHorizontalAlignment(JTextField.CENTER);
        H.setEditable(false);
        H.setColumns(10);
        H.setBounds(99, 105, 29, 19);
        Reg_panel.add(H);

        L = new JTextField();
        L.setBorder(null);
        L.setBackground(UIManager.getColor("Button.background"));
        L.setText("00");
        L.setHorizontalAlignment(JTextField.CENTER);
        L.setEditable(false);
        L.setColumns(10);
        L.setBounds(140, 105, 29, 19);
        Reg_panel.add(L);

        SP = new JTextField();
        SP.setBackground(UIManager.getColor("Button.background"));
        SP.setBorder(null);
        SP.setHorizontalAlignment(JTextField.CENTER);
        SP.setText("00");
        SP.setEditable(false);
        SP.setColumns(10);
        SP.setBounds(99, 124, 70, 19);
        Reg_panel.add(SP);

        IR = new JTextField();
        IR.setBackground(UIManager.getColor("Button.background"));
        IR.setBorder(null);
        IR.setHorizontalAlignment(JTextField.CENTER);
        IR.setText("00");
        IR.setEditable(false);
        IR.setColumns(10);
        IR.setBounds(120, 165, 29, 19);
        Reg_panel.add(IR);

        PC = new JTextField();
        PC.setBounds(99, 144, 70, 19);
        Reg_panel.add(PC);
        PC.setHorizontalAlignment(JTextField.CENTER);
        PC.setBorder(null);
        PC.setBackground(UIManager.getColor("Button.background"));
        PC.setText("00");
        PC.setEditable(false);
        PC.setColumns(10);

        FLAG = new JTextField();
        FLAG.setHorizontalAlignment(SwingConstants.CENTER);
        FLAG.setEditable(false);
        FLAG.setHorizontalAlignment(JTextField.CENTER);
        FLAG.setColumns(10);
        FLAG.setBorder(new EmptyBorder(0, 0, 0, 0));
        FLAG.setBackground(UIManager.getColor("Button.background"));
        FLAG.setBounds(53, 196, 124, 19);
        Reg_panel.add(FLAG);


        PSWH.setText( up.register[7].hex());
        PSWH.setHorizontalAlignment(JTextField.CENTER);

        PSWL.setText( up.flag.hex() );
        PSWL.setHorizontalAlignment(JTextField.CENTER);

        PC.setText( up.pc.hex());
        PC.setHorizontalAlignment(JTextField.CENTER);

        SP.setText( up.sp.hex());
        SP.setHorizontalAlignment(JTextField.CENTER);

        IR.setText( up.ir.hex());
        IR.setHorizontalAlignment(JTextField.CENTER);

        B.setText( up.register[0].hex());
        B.setHorizontalAlignment(JTextField.CENTER);

        C.setText( up.register[1].hex());
        C.setHorizontalAlignment(JTextField.CENTER);

        D.setText( up.register[2].hex());
        D.setHorizontalAlignment(JTextField.CENTER);

        E.setText( up.register[3].hex());
        E.setHorizontalAlignment(JTextField.CENTER);

        H.setText( up.register[4].hex());
        H.setHorizontalAlignment(JTextField.CENTER);

        L.setText( up.register[5].hex());
        L.setHorizontalAlignment(JTextField.CENTER);

        FLAG.setText( up.flag.value());
        FLAG.setHorizontalAlignment(JTextField.CENTER);



        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setBounds(244, 32, 750, 466);
        frame.getContentPane().add(tabbedPane);

        JScrollPane opcode_scrollPane = new JScrollPane();
        tabbedPane.addTab("OP-Code", null, opcode_scrollPane, null);

        final JTextArea Opcode = new JTextArea();
        // Opcode.setText("Op-Code text");
        opcode_scrollPane.setViewportView(Opcode);

        JScrollPane Hex_scrollPane = new JScrollPane();
        tabbedPane.addTab("HEX", null, Hex_scrollPane, null);

        final JTextArea Hex = new JTextArea();
        // Hex.setText("HEX code");
        Hex_scrollPane.setViewportView(Hex);



        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        panel.setBackground(UIManager.getColor("Button.background"));
        panel.setBounds(996, 259, 205, 239);
        frame.getContentPane().add(panel);

        JLabel label = new JLabel("PPI");
        label.setBounds(81, 12, 43, 15);
        panel.add(label);

        JLabel label_1 = new JLabel("Port A");
        label_1.setBounds(32, 59, 51, 15);
        panel.add(label_1);

        JLabel label_2 = new JLabel("Port B");
        label_2.setBounds(32, 101, 51, 15);
        panel.add(label_2);

        JLabel label_3 = new JLabel("Port C");
        label_3.setBounds(32, 148, 51, 15);
        panel.add(label_3);

        textField = new JTextField();
        textField.setEditable(false);
        textField.setColumns(10);
        textField.setBounds(101, 55, 70, 19);
        panel.add(textField);

        textField_1 = new JTextField();
        textField_1.setEditable(false);
        textField_1.setColumns(10);
        textField_1.setBounds(101, 102, 70, 19);
        panel.add(textField_1);

        textField_2 = new JTextField();
        textField_2.setEditable(false);
        textField_2.setColumns(10);
        textField_2.setBounds(101, 144, 70, 19);
        panel.add(textField_2);


        JButton button = new JButton("Update");
        button.setBounds(42, 184, 117, 25);
        panel.add(button);


        Image closeimage = ImageIO.read(this.getClass().getResource("/imgs/media-playback-stop.png"));
        Image stepimage = ImageIO.read(getClass().getResource("/imgs/media-skip-forward.png"));
        Image executeimage = ImageIO.read(getClass().getResource("/imgs/media-playback-start.png"));

        JButton SingleStep = new JButton();
        SingleStep.setBounds(200, 1, 30,30);
        tools_panel.add(SingleStep);
        SingleStep.setBorder(null);
        SingleStep.setToolTipText("Single Step");
        SingleStep.setIcon(new ImageIcon(stepimage));

        JButton execute = new JButton();
        execute.setBounds(167, 1, 30, 30);
        tools_panel.add(execute);
        execute.setBackground(null);
        execute.setToolTipText("Execute");
        execute.setBorder(null);
        execute.setIcon(new ImageIcon(executeimage));

        SingleStep.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {



                try {


                    if(!up.active && !up.trap) {

                        up.restartLocation = new Register16(0x8000);

                        Seperator tok = new Seperator(Opcode.getText(),false,up.restartLocation.hex());
                        Hex.setText("");
                        for( String[] file: tok.value() ) {
                            Parser asmParser = new Parser(file[1],false,"asm");
                            Integer address = Integer.parseInt(file[0],16);
                            memory.load(new Register16(address),asmParser.value());
                            Hex.setText(Hex.getText()+"@"+file[0] +"\n"+ asmParser.content()+"\n\n");
                            ErrorMessages.setText( ErrorMessages.getText()+"@"+file[0]+" Parse Successful!\n");
                        }
                        // Reset the microprocessor
                        up.trap = true;
                        up.resetin = true;
                    } else {
                        up.trap = true;
                        up.active = true;
                    }

                } catch (IOException ee) {
                    ErrorMessages.setText(ErrorMessages.getText()+ee.getMessage()+"\n");
                } catch (ParseException ee) {
                    ErrorMessages.setText(ErrorMessages.getText()+ee.getMessage()+"\n");
                }
            }
        });

        execute.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                try {
                    if(!up.active) {

                        up.restartLocation = new Register16(0x8000);

                        Seperator tok = new Seperator(Opcode.getText(),false,up.restartLocation.hex());
                        Hex.setText("");
                        for( String[] file: tok.value() ) {
                            Parser asmParser = new Parser(file[1],false,"asm");
                            Integer address = Integer.parseInt(file[0],16);
                            memory.load(new Register16(address),asmParser.value());
                            Hex.setText(Hex.getText()+"@"+file[0] +"\n"+ asmParser.content()+"\n\n");
                            ErrorMessages.setText( ErrorMessages.getText()+"@"+file[0]+" Parse Successful!\n");
                        }

                        // Reset the microprocessor
                        up.trap = false;
                        up.resetin = true;
                    } else {
                        up.active = false;
                    }

                } catch (IOException ee) {
                    ErrorMessages.setText(ErrorMessages.getText()+ee.getMessage()+"\n");
                } catch (ParseException ee) {
                    ErrorMessages.setText(ErrorMessages.getText()+ee.getMessage()+"\n");
                }
            }
        });


        ActionListener taskPerformer = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {

                // Update the registers
                updateRegisters();

                // Update the buttons
                if( up.active && !up.trap )
                    execute.setIcon(new ImageIcon(closeimage));
                else
                    execute.setIcon(new ImageIcon(executeimage));

            }
        };
        // 100 ms delay
        new Timer(100, taskPerformer).start();

    }
}



