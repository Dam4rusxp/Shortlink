package de.damarus.shortlink;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.net.MalformedURLException;

public class LinkQuickwindow extends JFrame {

    ModifieableLink currentLink;

    private JTextField tfLink;
    private JPanel paramPanel;

    public LinkQuickwindow(ModifieableLink link) throws HeadlessException, MalformedURLException {
        this();
        displayLink(link);
    }

    public LinkQuickwindow() {
        super("Shortlink");

        tfLink = new JTextField();
        tfLink.setEditable(false);
        tfLink.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                ((JTextField) e.getSource()).grabFocus();
            }
        });
        add(tfLink, BorderLayout.NORTH);

        paramPanel = new JPanel();
        paramPanel.setLayout(new FlowLayout());
        add(paramPanel);

        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

        // Set frame-global shortcuts using KeyBinding API 
        InputMap inputMap = tfLink.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = tfLink.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "goaway");
        actionMap.put("goaway", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goAway();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK), "copy");
        actionMap.put("copy", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Shortlink.lastClipboard = tfLink.getText();
                StringSelection selection = new StringSelection(tfLink.getText());
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
                goAway();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0), "quit");
        actionMap.put("quit", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // Hide the window quickly, when the user is not interested e.g. clicks outside
        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowLostFocus(WindowEvent e) {
                goAway();
            }
        });
    }

    public void displayLink(ModifieableLink link) throws MalformedURLException {
        currentLink = link;

        if (link == null) {
            setVisible(false);
            return;
        }

        tfLink.setText(link.toString());
        tfLink.selectAll();

        paramPanel.removeAll();
        for (ModifieableLink.UrlParameter param : link.getParameters()) {
            JCheckBox cBox = new JCheckBox(param.getKey(), param.isEnabled());
            cBox.addItemListener(e -> {
                param.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
                tfLink.setText(link.toString());
            });
            paramPanel.add(cBox);
        }

        if (link.hasFragment()) {
            JCheckBox cBox = new JCheckBox("#", link.isFragmentEnabled());
            cBox.addItemListener(e -> {
                link.setFragmentEnabled(e.getStateChange() == ItemEvent.SELECTED);
                tfLink.setText(link.toString());
            });
            paramPanel.add(cBox);
        }
        pack();
        setSize(500, getHeight());

        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
        setLocation(insets.left + 25, 25);
        setVisible(true);
        requestFocusInWindow();
    }

    private void goAway() {
        dispatchEvent(new WindowEvent(LinkQuickwindow.this, WindowEvent.WINDOW_CLOSING));
    }
}
