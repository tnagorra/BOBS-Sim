import java.awt.Font;
import java.awt.Image;
import javax.swing.BorderFactory;

import java.awt.EventQueue;

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

public class Gui
{

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
    private JTextField ppiTextA2;
    private JTextField ppiTextB2;
    private JTextField ppiTextC2;

    private JTable table;

    Microprocessor up;
    Memory memory;
    Ppi ppi1;
    Ppi ppi2;

    /**
     * Launch the application.
     */
    public static void main(String[] args)
    {

        EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                try
                {
                    // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                    Gui window = new Gui();
                    window.frame.setVisible(true);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    public void updateRegisters()
    {

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
    public Gui() throws IOException
    {

        // Initializing Devices
        up = new Microprocessor();
        memory = new Memory(65536);
        ppi1 = new Ppi(new Register8(0x40));
        ppi2 = new Ppi(new Register8(0x80));

        // Connecting Memory and Ppi
        memory.up = up;
        ppi1.up = up;
        ppi2.up = up;

        // Starting Memory and Ppi and Microprocessor
        memory.start();
        ppi1.start();
        ppi2.start();

        up.start();

        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() throws IOException
    {

        // Load Fonts
        final Font monospace = new Font("Ubuntu Mono", Font.BOLD, 18);
        final Font serif = new Font("Courier", Font.PLAIN, 12);

        /*
        // Load images
        final Image closeimage = ImageIO.read(this.getClass().getResource("imgs/stop1.png"));
        final Image stepimage = ImageIO.read(getClass().getResource("imgs/SS1.png"));
        final Image executeimage = ImageIO.read(getClass().getResource("imgs/exec2.png"));
        */

        // Initialize main frame
        frame = new JFrame();
        frame.setBackground(UIManager.getColor("Button.background"));
        frame.setBounds(0, 0, 1100 - 118, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        frame.setResizable(false);


        // Error Message Pane

        final JScrollPane messageScrollPane = new JScrollPane();
        messageScrollPane.setBounds(218, 498, 600, 168);
        frame.getContentPane().add(messageScrollPane);

        final JTextArea errorMessage = new JTextArea();
        errorMessage.setEditable(false);
        errorMessage.setFont(serif);
        errorMessage.setBorder(BorderFactory.createEmptyBorder(10, 10, 15, 10));
        messageScrollPane.setViewportView(errorMessage);


        // Opcode And Hex Pane
        final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setBounds(218, 32, 600, 466);
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        frame.getContentPane().add(tabbedPane);

        final JScrollPane opcodeScrollPane = new JScrollPane();
        tabbedPane.addTab("Mnemonics", null, opcodeScrollPane, null);

        final JTextArea Opcode = new JTextArea();
        Opcode.setFont(monospace);
        Opcode.setBorder(BorderFactory.createEmptyBorder(10, 10, 15, 10));
        opcodeScrollPane.setViewportView(Opcode);

        final JScrollPane hexScrollPane = new JScrollPane();
        tabbedPane.addTab("Opcode", null, hexScrollPane, null);

        final JTextArea Hex = new JTextArea();
        hexScrollPane.setViewportView(Hex);
        Hex.setFont(monospace);
        Hex.setBorder(BorderFactory.createEmptyBorder(10, 10, 15, 10));


        // Registers

        final JPanel registerPanel = new JPanel();
        registerPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        registerPanel.setBackground(UIManager.getColor("Button.background"));
        registerPanel.setBounds(8, 34, 204, 191);
        frame.getContentPane().add(registerPanel);
        registerPanel.setLayout(null);

        JLabel label = new JLabel("REGISTERS");
        label.setBounds(63, 0, 90, 29);
        registerPanel.add(label);

        JLabel labelPsw = new JLabel("PSW");
        labelPsw.setBounds(43, 41, 38, 20);
        registerPanel.add(labelPsw);

        JLabel labelBc = new JLabel("BC");
        labelBc.setBounds(43, 62, 38, 20);
        registerPanel.add(labelBc);

        JLabel labelDe = new JLabel("DE");
        labelDe.setBounds(43, 82, 38, 20);
        registerPanel.add(labelDe);

        JLabel labelHl = new JLabel("HL");
        labelHl.setBounds(43, 104, 38, 20);
        registerPanel.add(labelHl);

        JLabel labelSp = new JLabel("SP");
        labelSp.setBounds(43, 123, 38, 20);
        registerPanel.add(labelSp);

        JLabel labelIr = new JLabel("IR");
        labelIr.setBounds(43, 164, 38, 20);
        registerPanel.add(labelIr);

        JLabel labelPc = new JLabel("PC");
        labelPc.setBounds(43, 143, 38, 20);
        registerPanel.add(labelPc);

        PC = new JTextField();
        PC.setBounds(99, 42, 29, 19);
        PC.setText("00");
        PC.setHorizontalAlignment(SwingConstants.CENTER);
        PC.setEditable(false);
        PC.setColumns(10);
        PC.setBorder(null);
        PC.setBackground(UIManager.getColor("Button.background"));
        registerPanel.add(PC);

        SP = new JTextField();
        SP.setBounds(140, 42, 29, 19);
        SP.setText("00");
        SP.setHorizontalAlignment(SwingConstants.CENTER);
        SP.setEditable(false);
        SP.setColumns(10);
        SP.setBorder(null);
        SP.setBackground(UIManager.getColor("Button.background"));
        registerPanel.add(SP);

        IR = new JTextField();
        IR.setBounds(99, 63, 29, 19);
        IR.setText("00");
        IR.setHorizontalAlignment(SwingConstants.CENTER);
        IR.setEditable(false);
        IR.setColumns(10);
        IR.setBorder(null);
        IR.setBackground(UIManager.getColor("Button.background"));
        registerPanel.add(IR);

        B = new JTextField();
        B.setBounds(140, 63, 29, 19);
        B.setText("00");
        B.setHorizontalAlignment(SwingConstants.CENTER);
        B.setEditable(false);
        B.setColumns(10);
        B.setBorder(null);
        B.setBackground(UIManager.getColor("Button.background"));
        registerPanel.add(B);

        C = new JTextField();
        C.setBounds(99, 83, 29, 19);
        C.setText("00");
        C.setHorizontalAlignment(SwingConstants.CENTER);
        C.setEditable(false);
        C.setColumns(10);
        C.setBorder(null);
        C.setBackground(UIManager.getColor("Button.background"));
        registerPanel.add(C);

        D = new JTextField();
        D.setBounds(140, 83, 28, 19);
        D.setText("00");
        D.setHorizontalAlignment(SwingConstants.CENTER);
        D.setEditable(false);
        D.setColumns(10);
        D.setBorder(null);
        D.setBackground(UIManager.getColor("Button.background"));
        registerPanel.add(D);

        E = new JTextField();
        E.setBounds(99, 105, 29, 19);
        E.setText("00");
        E.setHorizontalAlignment(SwingConstants.CENTER);
        E.setEditable(false);
        E.setColumns(10);
        E.setBorder(null);
        E.setBackground(UIManager.getColor("Button.background"));
        registerPanel.add(E);

        H = new JTextField();
        H.setBounds(140, 105, 29, 19);
        H.setText("00");
        H.setHorizontalAlignment(SwingConstants.CENTER);
        H.setEditable(false);
        H.setColumns(10);
        H.setBorder(null);
        H.setBackground(UIManager.getColor("Button.background"));
        registerPanel.add(H);

        L = new JTextField();
        L.setBounds(99, 124, 70, 19);
        L.setText("00");
        L.setHorizontalAlignment(SwingConstants.CENTER);
        L.setEditable(false);
        L.setColumns(10);
        L.setBorder(null);
        L.setBackground(UIManager.getColor("Button.background"));
        registerPanel.add(L);

        PSWH = new JTextField();
        PSWH.setBounds(120, 165, 29, 19);
        PSWH.setText("00");
        PSWH.setHorizontalAlignment(SwingConstants.CENTER);
        PSWH.setEditable(false);
        PSWH.setColumns(10);
        PSWH.setBorder(null);
        PSWH.setBackground(UIManager.getColor("Button.background"));
        registerPanel.add(PSWH);

        PSWL = new JTextField();
        PSWL.setBounds(99, 144, 70, 19);
        PSWL.setText("00");
        PSWL.setHorizontalAlignment(SwingConstants.CENTER);
        PSWL.setEditable(false);
        PSWL.setColumns(10);
        PSWL.setBorder(null);
        PSWL.setBackground(UIManager.getColor("Button.background"));
        registerPanel.add(PSWL);

        FLAG = new JTextField();
        FLAG.setBounds(44, 22, 124, 19);
        FLAG.setText("");
        FLAG.setHorizontalAlignment(SwingConstants.CENTER);
        FLAG.setEditable(false);
        FLAG.setColumns(10);
        FLAG.setBorder(new EmptyBorder(0, 0, 0, 0));
        FLAG.setBackground(UIManager.getColor("Button.background"));
        registerPanel.add(FLAG);


        // PPI 2
        final JPanel ppiPanel1 = new JPanel();
        ppiPanel1.setLayout(null);
        ppiPanel1.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        ppiPanel1.setBackground(UIManager.getColor("Button.background"));
        ppiPanel1.setBounds(8, 405, 205, 120);
        frame.getContentPane().add(ppiPanel1);

        final JLabel ppiLabel2 = new JLabel("PPI @40");
        ppiLabel2.setBounds(78, 4, 80, 15);
        ppiPanel1.add(ppiLabel2);

        final JLabel ppiLabelA2 = new JLabel("Port A");
        ppiLabelA2.setBounds(20, 30, 50, 15);
        ppiPanel1.add(ppiLabelA2);

        final JLabel ppiLabelB2 = new JLabel("Port B");
        ppiLabelB2.setBounds(20, 60, 50, 15);
        ppiPanel1.add(ppiLabelB2);

        final JLabel ppiLabelC2 = new JLabel("Port C");
        ppiLabelC2.setBounds(20, 90, 50, 15);
        ppiPanel1.add(ppiLabelC2);

        ppiTextA2 = new JTextField();
        ppiTextA2.setEditable(true);
        ppiTextA2.setColumns(10);
        ppiTextA2.setBounds(80, 30, 30, 19);
        ppiTextA2.setText("00");
        ppiTextA2.setHorizontalAlignment(SwingConstants.CENTER);
        ppiPanel1.add(ppiTextA2);

        ppiTextB2 = new JTextField();
        ppiTextB2.setEditable(false);
        ppiTextB2.setColumns(10);
        ppiTextB2.setBounds(80, 60, 30, 19);
        ppiTextB2.setText("00");
        ppiTextB2.setHorizontalAlignment(SwingConstants.CENTER);
        ppiPanel1.add(ppiTextB2);

        ppiTextC2 = new JTextField();
        ppiTextC2.setEditable(false);
        ppiTextC2.setColumns(10);
        ppiTextC2.setBounds(80, 90, 30, 19);
        ppiTextC2.setText("00");
        ppiTextC2.setHorizontalAlignment(SwingConstants.CENTER);
        ppiPanel1.add(ppiTextC2);

        final JButton ppiBtnA2 = new JButton("Stb");
        ppiBtnA2.setBounds(125, 30, 60, 19);
        ppiPanel1.add(ppiBtnA2);

        final JButton ppiBtnB2 = new JButton("Stb");
        ppiBtnB2.setBounds(125, 60, 60, 19);
        ppiPanel1.add(ppiBtnB2);

        final JButton ppiBtnC2 = new JButton("Stb");
        ppiBtnC2.setBounds(125, 90, 60, 19);
        ppiPanel1.add(ppiBtnC2);


        ppiBtnA2.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                int value = Integer.parseInt(ppiTextA2.getText(),16);
                int address = 0x80;
                ppi2.set( new Register8(address), new Register8(value) );
            }
        });

        ppiBtnB2.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                int value = Integer.parseInt(ppiTextB2.getText(),16);
                int address = 0x81;
                ppi2.set( new Register8(address), new Register8(value) );
            }
        });

        ppiBtnC2.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                int value = Integer.parseInt(ppiTextC2.getText(),16);
                int address = 0x82;
                ppi2.set( new Register8(address), new Register8(value) );
            }
        });


        //Memory table

        final JScrollPane memoryScrollPane = new JScrollPane();
        memoryScrollPane.setBounds(943 - 118, 62, 150, 605);
        frame.getContentPane().add(memoryScrollPane);

        final JLabel memoryLabel = new JLabel("MEMORY");
        memoryLabel.setBounds(985 - 118, 36, 70, 15);
        frame.getContentPane().add(memoryLabel);

        Vector<String> colHdrs = new Vector<String>(2);
        colHdrs.addElement(new String("Address"));
        colHdrs.addElement(new String("Value"));

        final int rows = 65536;
        final DefaultTableModel model = new DefaultTableModel(rows, colHdrs.size());
        model.setColumnIdentifiers(colHdrs);
        for (int i = 0; i < rows; i++)
            model.setValueAt(new Register16(i).hex() , i , 0);

        table = new JTable(model);
        table.setBounds(5, 5, 245, 620);
        memoryScrollPane.setViewportView(table);

        // Buttons

        final JPanel toolsPanel = new JPanel();
        toolsPanel.setBounds(0, 0, 1100 - 118, 33);
        toolsPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        toolsPanel.setLayout(null);
        frame.getContentPane().add(toolsPanel);

        final JButton execute = new JButton("Execute");
        execute.setBounds(10, 3, 90, 24);
        // execute.setBorder(null);
        // execute.setToolTipText("Execute");
        // execute.setIcon(new ImageIcon(executeimage));
        toolsPanel.add(execute);

        final JButton singleStep = new JButton("Step");
        singleStep.setBounds(100, 3, 70, 24);
        toolsPanel.add(singleStep);


        final JButton RST75 = new JButton("7.5");
        RST75.setBounds(190, 3, 70, 24);
        toolsPanel.add(RST75);

        final JButton RST65 = new JButton("6.5");
        RST65.setBounds(260, 3, 70, 24);
        toolsPanel.add(RST65);

        final JButton RST55 = new JButton("5.5");
        RST55.setBounds(330, 3, 70, 24);
        toolsPanel.add(RST55);

        final JButton INTR = new JButton("INTR");
        INTR.setBounds(400, 3, 70, 24);
        toolsPanel.add(INTR);

        final JButton trapBtn = new JButton("TRAP");
        trapBtn.setBounds(470, 3, 70, 24);
        toolsPanel.add(trapBtn);


        trapBtn.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                up.trap = true;
                errorMessage.setText("Status: Trap sent.\n" + errorMessage.getText() );
            }
        });

        RST75.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                up.r7 = true;
                errorMessage.setText("Status: Rst 7.5 sent.\n" + errorMessage.getText() );
            }
        });
        RST65.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                up.r6 = true;
                errorMessage.setText("Status: Rst 6.5 sent.\n" + errorMessage.getText());
            }
        });
        RST55.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                up.r5 = true;
                errorMessage.setText( "Status: Rst 5.5 sent.\n" + errorMessage.getText() );
            }
        });
        INTR.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                up.intr = true;
                errorMessage.setText( "Status: Intr sent.\n" + errorMessage.getText());
                errorMessage.setText( "Error: Programmable Interrupt Controller not found.\n" + errorMessage.getText() );
            }
        });

        singleStep.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {

                try
                {

                    if (!up.active && !up.trap)
                    {

                        up.restartLocation = new Register16(0x8000);

                        Seperator tok = new Seperator(Opcode.getText(), false, up.restartLocation.hex());
                        Hex.setText("");
                        for ( String[] file : tok.value() )
                        {
                            Parser asmParser = new Parser(file[1], false, "asm");
                            Integer address = Integer.parseInt(file[0], 16);
                            memory.load(new Register16(address), asmParser.value());
                            Hex.setText(Hex.getText() + "@" + file[0] + "\n" + asmParser.content() + "\n\n");
                            errorMessage.setText( "Message: Parsing completed @" + file[0] + ".\n" + errorMessage.getText());
                        }
                        // Reset the microprocessor
                        up.trap = true;
                        up.resetin = true;
                    }
                    else
                    {
                        up.trap = true;
                        up.active = true;
                    }

                }
                catch (IOException ee)
                {
                    errorMessage.setText("Error: " + ee.getMessage() + "\n" + errorMessage.getText() );
                }
                catch (ParseException ee)
                {
                    errorMessage.setText("Error: " + ee.getMessage() + "\n" + errorMessage.getText() );
                }
            }
        });

        execute.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {

                try
                {
                    if (!up.active)
                    {

                        up.restartLocation = new Register16(0x8000);

                        Seperator tok = new Seperator(Opcode.getText(), false, up.restartLocation.hex());
                        Hex.setText("");
                        for ( String[] file : tok.value() )
                        {
                            Parser asmParser = new Parser(file[1], false, "asm");
                            Integer address = Integer.parseInt(file[0], 16);
                            memory.load(new Register16(address), asmParser.value());
                            Hex.setText(Hex.getText() + "@" + file[0] + "\n" + asmParser.content() + "\n\n");
                            errorMessage.setText( "Message: Parsing completed @" + file[0] + ".\n" + errorMessage.getText());
                        }

                        // Reset the microprocessor
                        up.trap = false;
                        up.resetin = true;
                    }
                    else
                    {
                        up.active = false;
                    }

                }
                catch (IOException ee)
                {
                    errorMessage.setText("Error: " + ee.getMessage() + "\n" + errorMessage.getText() );
                }
                catch (ParseException ee)
                {
                    errorMessage.setText("Error: " + ee.getMessage() + "\n" + errorMessage.getText() );
                }
            }
        });


        ActionListener taskPerformer = new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {

                // Update the registers
                updateRegisters();

                // Update the buttons
                if ( up.active && !up.trap )
                    execute.setText("Stop");
                else
                    execute.setText("Execute");

                // Update memory

                // May cause inaccuracy when using "up.active"
                if ( up.active || up.trap ) {
                    for (int i = 0; i < rows; i++)
                    {
                        Register16 address = new Register16(i);
                        model.setValueAt( memory.get(address).hex() , i , 1);
                    }
                }

                /*
                // Update PPI
                if(ppi2.portAIOfunc())
                ppiTextA2.setEditable(true);
                else {
                ppiTextA2.setEditable(false);
                ppiTextA2.setText( ppi2.get( new Register8(0x80) ).hex() );
                }
                */

            }

        };
        // 100 ms delay
        new Timer(30, taskPerformer).start();

    }
}

