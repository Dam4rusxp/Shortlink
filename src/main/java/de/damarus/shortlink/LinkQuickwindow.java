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

        // Using KeyboardFocusManager instead of KeyListener allows focus-independent shortcuts
        KeyboardFocusManager keyman = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        keyman.addKeyEventDispatcher(e -> {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_ESCAPE:
                    goAway();
                    return true;

                case KeyEvent.VK_C:
                    if ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
                        Shortlink.lastClipboard = tfLink.getText();
                        StringSelection selection = new StringSelection(tfLink.getText());
                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
                        goAway();
                    }
                    return true;

                case KeyEvent.VK_Q:
                    System.exit(0);
            }

            return false;
        });

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
