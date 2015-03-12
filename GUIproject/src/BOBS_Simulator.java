import java.awt.EventQueue;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.JTextArea;
import javax.swing.JInternalFrame;
import javax.swing.Box;
import javax.swing.JSplitPane;
import javax.swing.JFormattedTextField;
import javax.swing.border.LineBorder;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.border.EtchedBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.UIManager;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.CompoundBorder;


public class BOBS_Simulator {

    private JFrame frame;
    private JTable table;
    private JTable table_1;
    private JPanel panel;
    private JTextField txtAddress;
    private JTextField txtValue;
    private JTextField A_value;
    private JTextField B_value;
    private JTextField C_value;
    private JTextField D_value;
    private JTextField E_value;
    private JTextField H_value;
    private JTextField L_value;
    private JTextField PSWH_value;
    private JTextField PSWL_value;
    private JTextField PCH_value;
    private JTextField PCL_value;
    private JTextField SPH_value;
    private JTextField SPL_value;
    private JTextField S_value;
    private JTextField Z_value;
    private JTextField AC_value;
    private JTextField P_value;
    private JTextField flagC_value;
    private JTextField mem_location;
    private JTextField mem_value;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName());
                    BOBS_Simulator window = new BOBS_Simulator();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public BOBS_Simulator() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame("BOBS Simulator");
        frame.setBounds(100,100,900,600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JPanel registerpanel = new JPanel();
        registerpanel.setBounds(12, 25, 142, 229);
        registerpanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        registerpanel.setToolTipText("Current Register Status");
        frame.getContentPane().add(registerpanel);
        registerpanel.setLayout(null);

        JLabel reg_label = new JLabel("Registers");
        reg_label.setBounds(30, 0, 77, 28);
        registerpanel.add(reg_label);

        JLabel reg_A_label = new JLabel("A");
        reg_A_label.setBounds(12, 30, 38, 28);
        registerpanel.add(reg_A_label);

        JLabel reg_BC_label = new JLabel("BC");
        reg_BC_label.setBounds(12, 58, 38, 28);
        registerpanel.add(reg_BC_label);

        JLabel reg_HL_label = new JLabel("HL");
        reg_HL_label.setBounds(12, 114, 38, 28);
        registerpanel.add(reg_HL_label);

        JLabel reg_PSW_label = new JLabel("PSW");
        reg_PSW_label.setBounds(12, 142, 38, 28);
        registerpanel.add(reg_PSW_label);

        JLabel reg_PC_label = new JLabel("PC");
        reg_PC_label.setBounds(12, 170, 38, 28);
        registerpanel.add(reg_PC_label);

        JLabel reg_SP_label = new JLabel("SP");
        reg_SP_label.setBounds(12, 198, 38, 28);
        registerpanel.add(reg_SP_label);

        JLabel reg_DE_label = new JLabel("DE");
        reg_DE_label.setBounds(12, 86, 38, 28);
        registerpanel.add(reg_DE_label);

        A_value = new JTextField("00");
        A_value.setBorder(null);
        A_value.setBounds(78, 35, 38, 19);
        A_value.setEditable(false);
        registerpanel.add(A_value);
        A_value.setColumns(10);

        B_value = new JTextField("00");
        B_value.setBorder(null);
        B_value.setEditable(false);
        B_value.setBounds(54, 63, 38, 19);
        registerpanel.add(B_value);
        B_value.setColumns(10);

        C_value = new JTextField("00");
        C_value.setBorder(null);
        C_value.setEditable(false);
        C_value.setColumns(10);
        C_value.setBounds(100, 63, 38, 19);
        registerpanel.add(C_value);

        D_value = new JTextField("00");
        D_value.setBorder(null);
        D_value.setEditable(false);
        D_value.setColumns(10);
        D_value.setBounds(54, 90, 38, 19);
        registerpanel.add(D_value);

        E_value = new JTextField("00");
        E_value.setBorder(null);
        E_value.setEditable(false);
        E_value.setColumns(10);
        E_value.setBounds(100, 90, 38, 19);
        registerpanel.add(E_value);

        H_value = new JTextField("00");
        H_value.setBorder(null);
        H_value.setEditable(false);
        H_value.setColumns(10);
        H_value.setBounds(54, 117, 38, 19);
        registerpanel.add(H_value);

        L_value = new JTextField("00");
        L_value.setBorder(null);
        L_value.setEditable(false);
        L_value.setColumns(10);
        L_value.setBounds(100, 117, 38, 19);
        registerpanel.add(L_value);

        PSWH_value = new JTextField("00");
        PSWH_value.setBorder(null);
        PSWH_value.setEditable(false);
        PSWH_value.setColumns(10);
        PSWH_value.setBounds(54, 147, 38, 19);
        registerpanel.add(PSWH_value);

        PSWL_value = new JTextField("00");
        PSWL_value.setBorder(null);
        PSWL_value.setEditable(false);
        PSWL_value.setColumns(10);
        PSWL_value.setBounds(100, 147, 38, 19);
        registerpanel.add(PSWL_value);

        PCH_value = new JTextField("00");
        PCH_value.setBorder(null);
        PCH_value.setEditable(false);
        PCH_value.setColumns(10);
        PCH_value.setBounds(54, 172, 38, 19);
        registerpanel.add(PCH_value);

        PCL_value = new JTextField("00");
        PCL_value.setBorder(null);
        PCL_value.setEditable(false);
        PCL_value.setColumns(10);
        PCL_value.setBounds(100, 172, 38, 19);
        registerpanel.add(PCL_value);

        SPH_value = new JTextField("00");
        SPH_value.setBorder(null);
        SPH_value.setEditable(false);
        SPH_value.setColumns(10);
        SPH_value.setBounds(54, 198, 38, 19);
        registerpanel.add(SPH_value);

        SPL_value = new JTextField("00");
        SPL_value.setBorder(null);
        SPL_value.setEditable(false);
        SPL_value.setColumns(10);
        SPL_value.setBounds(100, 198, 38, 19);
        registerpanel.add(SPL_value);

        JPanel flagpanel = new JPanel();
        flagpanel.setBounds(155, 25, 97, 229);
        flagpanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        flagpanel.setToolTipText("Current Flag Status");
        frame.getContentPane().add(flagpanel);
        flagpanel.setLayout(null);

        JLabel flag_label = new JLabel("Flags");
        flag_label.setBounds(29, 5, 38, 15);
        flagpanel.add(flag_label);

        JLabel flag_S_label = new JLabel("S");
        flag_S_label.setBounds(12, 29, 21, 20);
        flagpanel.add(flag_S_label);

        JLabel flag_Z_label = new JLabel("Z");
        flag_Z_label.setBounds(12, 56, 21, 15);
        flagpanel.add(flag_Z_label);

        JLabel lblAc = new JLabel("AC");
        lblAc.setBounds(12, 83, 28, 15);
        flagpanel.add(lblAc);

        JLabel flag_P_label = new JLabel("P");
        flag_P_label.setBounds(12, 110, 28, 15);
        flagpanel.add(flag_P_label);

        JLabel flag_C_label = new JLabel("C");
        flag_C_label.setBounds(12, 137, 28, 15);
        flagpanel.add(flag_C_label);

        S_value = new JTextField("0");
        S_value.setBorder(null);
        S_value.setBounds(39, 29, 38, 19);
        S_value.setEditable(false);
        S_value.setColumns(10);
        flagpanel.add(S_value);

        Z_value = new JTextField("0");
        Z_value.setBorder(null);
        Z_value.setBounds(39, 54, 38, 19);
        Z_value.setEditable(false);
        Z_value.setColumns(10);
        flagpanel.add(Z_value);

        AC_value = new JTextField("0");
        AC_value.setBorder(null);
        AC_value.setBounds(39, 81, 38, 19);
        AC_value.setEditable(false);
        AC_value.setColumns(10);
        flagpanel.add(AC_value);

        P_value = new JTextField("0");
        P_value.setBorder(null);
        P_value.setBounds(39, 108, 38, 19);
        P_value.setEditable(false);
        P_value.setColumns(10);
        flagpanel.add(P_value);

        flagC_value = new JTextField("0");
        flagC_value.setBorder(null);
        flagC_value.setBounds(39, 135, 38, 19);
        flagC_value.setEditable(false);
        flagC_value.setColumns(10);
        flagpanel.add(flagC_value);

        JScrollPane code_scrollPanel = new JScrollPane();
        code_scrollPanel.setBounds(353, 25, 196, 433);
        frame.getContentPane().add(code_scrollPanel);

        JTextArea code_text_area = new JTextArea();
        code_text_area.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {

            }
        });
        code_text_area.setText("Enter Program Here");
        code_scrollPanel.setViewportView(code_text_area);

        JPanel io_panel = new JPanel();
        io_panel.setBounds(12, 275, 320, 105);
        io_panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        frame.getContentPane().add(io_panel);
        io_panel.setLayout(null);

        txtAddress = new JTextField();
        txtAddress.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }
        });
        txtAddress.setToolTipText("Change the port address here");
        txtAddress.setBounds(12, 30, 100, 30);
        txtAddress.setText("00");
        io_panel.add(txtAddress);
        txtAddress.setColumns(10);

        txtValue = new JTextField();
        txtValue.setToolTipText("Enter new port value");
        txtValue.setBounds(150, 30, 150, 30);
        txtValue.setText("00");
        io_panel.add(txtValue);
        txtValue.setColumns(10);

        JLabel lblIoPorts = new JLabel("I/O Ports");
        lblIoPorts.setBounds(120, 9, 80, 18);
        io_panel.add(lblIoPorts);

        JButton Enter = new JButton("Update Port Value");
        Enter.setBounds(70, 70, 200, 25);
        io_panel.add(Enter);

        panel = new JPanel();
        panel.setBounds(12, 415, 320, 105);
        panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        frame.getContentPane().add(panel);
        panel.setLayout(null);

        JLabel lblMemory = new JLabel("Memory");
        lblMemory.setBounds(126, 12, 70, 15);
        panel.add(lblMemory);

        JButton btnNewButton = new JButton("Update Memory");
        btnNewButton.setBounds(70, 75, 200, 25);
        panel.add(btnNewButton);

        mem_location = new JTextField("00");
        mem_location.setToolTipText("Change the memory location to view here");
        mem_location.setBounds(12, 37, 100, 30);
        panel.add(mem_location);
        mem_location.setColumns(10);

        mem_value = new JTextField("00");
        mem_value.setToolTipText("Edit new value");
        mem_value.setBounds(150, 37, 150, 30);
        panel.add(mem_value);
        mem_value.setColumns(10);

        final JScrollPane opcode_scrollpane = new JScrollPane();
        opcode_scrollpane.setVisible(false);
        opcode_scrollpane.setBounds(626, 25, 196, 433);
        frame.getContentPane().add(opcode_scrollpane);

        final JTextArea Opcode_area = new JTextArea();
        Opcode_area.setVisible(false);
        Opcode_area.setText("Op-code");
        opcode_scrollpane.setViewportView(Opcode_area);

        JPanel exec_panel = new JPanel();
        exec_panel.setBounds(350, 462, 600, 100);
        frame.getContentPane().add(exec_panel);

        JButton btnExecute = new JButton("EXECUTE");
        btnExecute.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }
        });
        btnExecute.setBounds(202, 12, 117, 25);
        btnExecute.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                String exec_command = arg0.getActionCommand();
                if(exec_command.equals("EXECUTE")){
                    opcode_scrollpane.setVisible(true);
                    Opcode_area.setVisible(true);

                }
            }
        });
        exec_panel.setLayout(null);
        exec_panel.add(btnExecute);

        JButton btnSingleStep = new JButton("SINGLE STEP");
        btnSingleStep.setBounds(181, 49, 164, 25);
        exec_panel.add(btnSingleStep);

        JButton btnPrevious = new JButton("Previous");
        btnPrevious.setBounds(68, 49, 100, 25);
        exec_panel.add(btnPrevious);

        JButton btnNext = new JButton("Next");
        btnNext.setBounds(356, 49, 100, 25);
        btnNext.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });
        exec_panel.add(btnNext);


    }
}
