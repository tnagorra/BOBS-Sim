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
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.UIManager;
import javax.swing.JTabbedPane;
import javax.swing.JScrollBar;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.util.Vector;

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
import javax.swing.JTable;

public class Gui {

    private JFrame frame;

    private JTextField PC;
    private JTextField SP;
    private JTextField IR;
    private JTextField B;
    private JTextField C;
    private JTextField D;
    private JTextField E;
    private JTextField H;
    private JTextField L;
    private JTextField PSWH;
    private JTextField PSWL;
    private JTextField FLAG;

    private JTextField PortATxt_1;
    private JTextField PortBTxt_1;
    private JTextField PortCTxt_1;
    private JTextField PortATxt_2;
    private JTextField PortBTxt_2;
    private JTextField PortCTxt_2;

    private JTable table;

    Microprocessor up;
    Memory memory;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                    Gui window = new Gui();
                    window.frame.setVisible(true);
                    //  window.updateRegisters();

                    // updateRegisters();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void updateRegisters() {

        IR.setText( up.register[0].hex()); // B
        B.setText( up.register[1].hex()); // C
        C.setText( up.register[2].hex()); // D
        D.setText( up.register[3].hex()); // E
        E.setText( up.register[4].hex()); // H
        H.setText( up.register[5].hex()); // L

        PSWH.setText( up.ir.hex());         // IR
        L.setText( up.sp.hex());          // SP
        PSWL.setText( up.pc.hex());       // PC

        PC.setText( up.register[7].hex()); // PSWH
        SP.setText( up.flag.hex() );     // PSWL

        FLAG.setText( up.flag.value()); // FLAG
    }

    /**
     * Create the application.
     */
    public Gui() throws IOException {

        up = new Microprocessor();
        memory = new Memory(65536);
        Connector.connect(up, memory);
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

        final JPanel tools_panel = new JPanel();
        tools_panel.setBounds(0, 0, 1201, 33);
        tools_panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        frame.getContentPane().add(tools_panel);

        final JScrollPane Message_scrollPane = new JScrollPane();
        Message_scrollPane.setBounds(212, 500, 718, 172);
        Message_scrollPane.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        frame.getContentPane().add(Message_scrollPane);

        final JTextArea ErrorMessages = new JTextArea();
        ErrorMessages.setText("Error Pane!\n");
        ErrorMessages.setEditable(false);
        Message_scrollPane.setViewportView(ErrorMessages);

      final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setBounds(212, 32, 719, 466);
        frame.getContentPane().add(tabbedPane);

      final JScrollPane opcode_scrollPane = new JScrollPane();
        tabbedPane.addTab("OP-Code", null, opcode_scrollPane, null);

        final JTextArea Opcode = new JTextArea();
        // Opcode.setText("Op-Code text");
        opcode_scrollPane.setViewportView(Opcode);

      final JScrollPane Hex_scrollPane = new JScrollPane();
        tabbedPane.addTab("HEX", null, Hex_scrollPane, null);

        final JTextArea Hex = new JTextArea();
        // Hex.setText("HEX code");
        Hex_scrollPane.setViewportView(Hex);


        final Image closeimage = ImageIO.read(this.getClass().getResource("imgs/stop1.png"));
        final Image stepimage = ImageIO.read(getClass().getResource("imgs/SS1.png"));
        final Image executeimage = ImageIO.read(getClass().getResource("imgs/exec2.png"));
        tools_panel.setLayout(null);

      final JButton SingleStep = new JButton();
        SingleStep.setBounds(200, 1, 30, 30);
        tools_panel.add(SingleStep);
        SingleStep.setBorder(null);
        SingleStep.setToolTipText("Single Step");
        SingleStep.setIcon(new ImageIcon(stepimage));

        final JButton execute = new JButton();
        execute.setBounds(167, 1, 30, 30);
        tools_panel.add(execute);
        execute.setBackground(null);
        execute.setToolTipText("Execute");
        execute.setBorder(null);
        execute.setIcon(new ImageIcon(executeimage));

        JPanel RegisterPanel = new JPanel();
        RegisterPanel.setToolTipText("Current register status");
        RegisterPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        RegisterPanel.setBackground(UIManager.getColor("Button.background"));
        RegisterPanel.setBounds(10, 32, 204, 191);
        frame.getContentPane().add(RegisterPanel);
        RegisterPanel.setLayout(null);

        JLabel label = new JLabel("REGISTERS");
        label.setBounds(63, 0, 90, 29);
        RegisterPanel.add(label);

        JLabel label_1 = new JLabel("PSW");
        label_1.setBounds(43, 41, 38, 20);
        RegisterPanel.add(label_1);

        JLabel label_2 = new JLabel("BC");
        label_2.setBounds(43, 62, 38, 20);
        RegisterPanel.add(label_2);

        JLabel label_3 = new JLabel("DE");
        label_3.setBounds(43, 82, 38, 20);
        RegisterPanel.add(label_3);

        JLabel label_4 = new JLabel("HL");
        label_4.setBounds(43, 104, 38, 20);
        RegisterPanel.add(label_4);

        JLabel label_5 = new JLabel("SP");
        label_5.setBounds(43, 123, 38, 20);
        RegisterPanel.add(label_5);

        JLabel label_6 = new JLabel("IR");
        label_6.setBounds(43, 164, 38, 20);
        RegisterPanel.add(label_6);

        JLabel label_7 = new JLabel("PC");
        label_7.setBounds(43, 143, 38, 20);
        RegisterPanel.add(label_7);

        PC = new JTextField();
        PC.setBounds(99, 42, 29, 19);
        PC.setText("00");
        PC.setHorizontalAlignment(SwingConstants.CENTER);
        PC.setEditable(false);
        PC.setColumns(10);
        PC.setBorder(null);
        PC.setBackground(UIManager.getColor("Button.background"));
        RegisterPanel.add(PC);

        SP = new JTextField();
        SP.setBounds(140, 42, 29, 19);
        SP.setText("00");
        SP.setHorizontalAlignment(SwingConstants.CENTER);
        SP.setEditable(false);
        SP.setColumns(10);
        SP.setBorder(null);
        SP.setBackground(UIManager.getColor("Button.background"));
        RegisterPanel.add(SP);

        IR = new JTextField();
        IR.setBounds(99, 63, 29, 19);
        IR.setText("00");
        IR.setHorizontalAlignment(SwingConstants.CENTER);
        IR.setEditable(false);
        IR.setColumns(10);
        IR.setBorder(null);
        IR.setBackground(UIManager.getColor("Button.background"));
        RegisterPanel.add(IR);

        B = new JTextField();
        B.setBounds(140, 63, 29, 19);
        B.setText("00");
        B.setHorizontalAlignment(SwingConstants.CENTER);
        B.setEditable(false);
        B.setColumns(10);
        B.setBorder(null);
        B.setBackground(UIManager.getColor("Button.background"));
        RegisterPanel.add(B);

        C = new JTextField();
        C.setBounds(99, 83, 29, 19);
        C.setText("00");
        C.setHorizontalAlignment(SwingConstants.CENTER);
        C.setEditable(false);
        C.setColumns(10);
        C.setBorder(null);
        C.setBackground(UIManager.getColor("Button.background"));
        RegisterPanel.add(C);

        D = new JTextField();
        D.setBounds(140, 83, 28, 19);
        D.setText("00");
        D.setHorizontalAlignment(SwingConstants.CENTER);
        D.setEditable(false);
        D.setColumns(10);
        D.setBorder(null);
        D.setBackground(UIManager.getColor("Button.background"));
        RegisterPanel.add(D);

        E = new JTextField();
        E.setBounds(99, 105, 29, 19);
        E.setText("00");
        E.setHorizontalAlignment(SwingConstants.CENTER);
        E.setEditable(false);
        E.setColumns(10);
        E.setBorder(null);
        E.setBackground(UIManager.getColor("Button.background"));
        RegisterPanel.add(E);

        H = new JTextField();
        H.setBounds(140, 105, 29, 19);
        H.setText("00");
        H.setHorizontalAlignment(SwingConstants.CENTER);
        H.setEditable(false);
        H.setColumns(10);
        H.setBorder(null);
        H.setBackground(UIManager.getColor("Button.background"));
        RegisterPanel.add(H);

        L = new JTextField();
        L.setBounds(99, 124, 70, 19);
        L.setText("00");
        L.setHorizontalAlignment(SwingConstants.CENTER);
        L.setEditable(false);
        L.setColumns(10);
        L.setBorder(null);
        L.setBackground(UIManager.getColor("Button.background"));
        RegisterPanel.add(L);

        PSWH = new JTextField();
        PSWH.setBounds(120, 165, 29, 19);
        PSWH.setText("00");
        PSWH.setHorizontalAlignment(SwingConstants.CENTER);
        PSWH.setEditable(false);
        PSWH.setColumns(10);
        PSWH.setBorder(null);
        PSWH.setBackground(UIManager.getColor("Button.background"));
        RegisterPanel.add(PSWH);

        PSWL = new JTextField();
        PSWL.setBounds(99, 144, 70, 19);
        PSWL.setText("00");
        PSWL.setHorizontalAlignment(SwingConstants.CENTER);
        PSWL.setEditable(false);
        PSWL.setColumns(10);
        PSWL.setBorder(null);
        PSWL.setBackground(UIManager.getColor("Button.background"));
        RegisterPanel.add(PSWL);

        FLAG = new JTextField();
        FLAG.setBounds(44, 22, 124, 19);
        FLAG.setText("");
        FLAG.setHorizontalAlignment(SwingConstants.CENTER);
        FLAG.setEditable(false);
        FLAG.setColumns(10);
        FLAG.setBorder(new EmptyBorder(0, 0, 0, 0));
        FLAG.setBackground(UIManager.getColor("Button.background"));
        RegisterPanel.add(FLAG);



        JPanel Ppi1Panel = new JPanel();
        Ppi1Panel.setLayout(null);
        Ppi1Panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        Ppi1Panel.setBackground(UIManager.getColor("Button.background"));
        Ppi1Panel.setBounds(9, 224, 205, 179);
        frame.getContentPane().add(Ppi1Panel);

        JLabel Ppilbl_1 = new JLabel("PPI");
        Ppilbl_1.setBounds(78, 4, 43, 15);
        Ppi1Panel.add(Ppilbl_1);

        JLabel PortAlbl_1 = new JLabel("Port A");
        PortAlbl_1.setBounds(32, 31, 51, 15);
        Ppi1Panel.add(PortAlbl_1);

        JLabel PortBlbl_1 = new JLabel("Port B");
        PortBlbl_1.setBounds(32, 73, 51, 15);
        Ppi1Panel.add(PortBlbl_1);

        JLabel PortClbl_1 = new JLabel("Port C");
        PortClbl_1.setBounds(32, 120, 51, 15);
        Ppi1Panel.add(PortClbl_1);

        PortATxt_1 = new JTextField();
        PortATxt_1.setEditable(false);
        PortATxt_1.setColumns(10);
        PortATxt_1.setBounds(101, 27, 70, 19);
        Ppi1Panel.add(PortATxt_1);

        PortBTxt_1 = new JTextField();
        PortBTxt_1.setEditable(false);
        PortBTxt_1.setColumns(10);
        PortBTxt_1.setBounds(101, 74, 70, 19);
        Ppi1Panel.add(PortBTxt_1);

        PortCTxt_1 = new JTextField();
        PortCTxt_1.setEditable(false);
        PortCTxt_1.setColumns(10);
        PortCTxt_1.setBounds(101, 116, 70, 19);
        Ppi1Panel.add(PortCTxt_1);

      final JButton Updatebtn_1 = new JButton("Update");
        Updatebtn_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
            }
        });
        Updatebtn_1.setBounds(42, 147, 117, 25);
        Ppi1Panel.add(Updatebtn_1);

        JPanel Ppi2_panel = new JPanel();
        Ppi2_panel.setLayout(null);
        Ppi2_panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        Ppi2_panel.setBackground(UIManager.getColor("Button.background"));
        Ppi2_panel.setBounds(9, 403, 205, 179);
        frame.getContentPane().add(Ppi2_panel);

        JLabel Ppilbl_2 = new JLabel("PPI");
        Ppilbl_2.setBounds(78, 4, 43, 15);
        Ppi2_panel.add(Ppilbl_2);

        JLabel PortAlbl_2 = new JLabel("Port A");
        PortAlbl_2.setBounds(32, 31, 51, 15);
        Ppi2_panel.add(PortAlbl_2);

        JLabel PortBlbl_2 = new JLabel("Port B");
        PortBlbl_2.setBounds(32, 73, 51, 15);
        Ppi2_panel.add(PortBlbl_2);

        JLabel PortClbl_2 = new JLabel("Port C");
        PortClbl_2.setBounds(32, 120, 51, 15);
        Ppi2_panel.add(PortClbl_2);

        PortATxt_2 = new JTextField();
        PortATxt_2.setEditable(false);
        PortATxt_2.setColumns(10);
        PortATxt_2.setBounds(101, 27, 70, 19);
        Ppi2_panel.add(PortATxt_2);

        PortBTxt_2 = new JTextField();
        PortBTxt_2.setEditable(false);
        PortBTxt_2.setColumns(10);
        PortBTxt_2.setBounds(101, 74, 70, 19);
        Ppi2_panel.add(PortBTxt_2);

        PortCTxt_2 = new JTextField();
        PortCTxt_2.setEditable(false);
        PortCTxt_2.setColumns(10);
        PortCTxt_2.setBounds(101, 116, 70, 19);
        Ppi2_panel.add(PortCTxt_2);

      final JButton Updatebtn_2 = new JButton("Update");
        Updatebtn_2.setBounds(42, 147, 117, 25);
        Ppi2_panel.add(Updatebtn_2);

      final JScrollPane MessageScrollPane = new JScrollPane();
        MessageScrollPane.setBounds(943, 62, 245, 605);
        frame.getContentPane().add(MessageScrollPane);


        //Memory table
        int RowNbr;
        Vector<String> colHdrs = new Vector<String>(2);
        colHdrs.addElement(new String("Address"));
        colHdrs.addElement(new String("Value"));
        RowNbr = 65536;



        DefaultTableModel model = new DefaultTableModel(RowNbr, colHdrs.size());
        model.setColumnIdentifiers(colHdrs);

        table = new JTable(model);
        table.setBounds(5, 5, 245, 620);
        MessageScrollPane.setViewportView(table);

        JLabel Memorylbl = new JLabel("Memory");
        Memorylbl.setBounds(1031, 36, 70, 15);
        frame.getContentPane().add(Memorylbl);

        JPanel InterruptPanel = new JPanel();
        InterruptPanel.setBounds(12, 583, 202, 89);
        frame.getContentPane().add(InterruptPanel);
        InterruptPanel.setLayout(null);

      final JButton Trapbtn = new JButton("TRAP");
        Trapbtn.setBounds(0, 0, 89, 22);
        InterruptPanel.add(Trapbtn);

      final JButton RST75 = new JButton(" RST 7.5");
        RST75.setBounds(101, 0, 89, 22);
        InterruptPanel.add(RST75);

      final JButton RST65 = new JButton("RST 6.5");
        RST65.setBounds(0, 25, 89, 22);
        InterruptPanel.add(RST65);

      final JButton RST55 = new JButton("RST 5.5");
        RST55.setBounds(101, 25, 89, 22);
        InterruptPanel.add(RST55);

      final JButton INTR = new JButton("INTR");
        INTR.setBounds(0, 50, 89, 22);
        InterruptPanel.add(INTR);

      final JButton INTA = new JButton("INTA");
        INTA.setBounds(101, 50, 89, 22);
        InterruptPanel.add(INTA);

        SingleStep.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {



                try {


                    if (!up.active && !up.trap) {

                        up.restartLocation = new Register16(0x8000);

                        Seperator tok = new Seperator(Opcode.getText(), false, up.restartLocation.hex());
                        Hex.setText("");
                        for ( String[] file : tok.value() ) {
                            Parser asmParser = new Parser(file[1], false, "asm");
                            Integer address = Integer.parseInt(file[0], 16);
                            memory.load(new Register16(address), asmParser.value());
                            Hex.setText(Hex.getText() + "@" + file[0] + "\n" + asmParser.content() + "\n\n");
                            ErrorMessages.setText( ErrorMessages.getText() + "@" + file[0] + " Parse Successful!\n");
                        }
                        // Reset the microprocessor
                        up.trap = true;
                        up.resetin = true;
                    } else {
                        up.trap = true;
                        up.active = true;
                    }

                } catch (IOException ee) {
                    ErrorMessages.setText(ErrorMessages.getText() + ee.getMessage() + "\n");
                } catch (ParseException ee) {
                    ErrorMessages.setText(ErrorMessages.getText() + ee.getMessage() + "\n");
                }
            }
        });

        execute.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                try {
                    if (!up.active) {

                        up.restartLocation = new Register16(0x8000);

                        Seperator tok = new Seperator(Opcode.getText(), false, up.restartLocation.hex());
                        Hex.setText("");
                        for ( String[] file : tok.value() ) {
                            Parser asmParser = new Parser(file[1], false, "asm");
                            Integer address = Integer.parseInt(file[0], 16);
                            memory.load(new Register16(address), asmParser.value());
                            Hex.setText(Hex.getText() + "@" + file[0] + "\n" + asmParser.content() + "\n\n");
                            ErrorMessages.setText( ErrorMessages.getText() + "@" + file[0] + " Parse Successful!\n");
                        }

                        // Reset the microprocessor
                        up.trap = false;
                        up.resetin = true;
                    } else {
                        up.active = false;
                    }

                } catch (IOException ee) {
                    ErrorMessages.setText(ErrorMessages.getText() + ee.getMessage() + "\n");
                } catch (ParseException ee) {
                    ErrorMessages.setText(ErrorMessages.getText() + ee.getMessage() + "\n");
                }
            }
        });


        ActionListener taskPerformer = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {

                // Update the registers
                updateRegisters();

                // Update the buttons
                if ( up.active && !up.trap )
                    execute.setIcon(new ImageIcon(closeimage));
                else
                    execute.setIcon(new ImageIcon(executeimage));

            }
        };
        // 100 ms delay
        new Timer(30, taskPerformer).start();

    }
}





