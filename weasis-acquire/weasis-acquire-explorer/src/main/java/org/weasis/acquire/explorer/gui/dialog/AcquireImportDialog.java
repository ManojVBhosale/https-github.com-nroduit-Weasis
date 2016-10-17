/*******************************************************************************
 * Copyright (c) 2016 Weasis Team and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Nicolas Roduit - initial API and implementation
 *******************************************************************************/
package org.weasis.acquire.explorer.gui.dialog;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import org.osgi.framework.FrameworkUtil;
import org.osgi.service.prefs.Preferences;
import org.weasis.acquire.explorer.AcquireManager;
import org.weasis.acquire.explorer.AcquisitionView;
import org.weasis.acquire.explorer.core.bean.Serie;
import org.weasis.acquire.explorer.gui.list.AcquireThumbnailListPane;
import org.weasis.core.api.gui.util.JMVUtils;
import org.weasis.core.api.media.data.ImageElement;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.api.service.BundlePreferences;
import org.weasis.core.api.util.StringUtil;

public class AcquireImportDialog extends JDialog implements PropertyChangeListener {
    private static final long serialVersionUID = -8736946182228791444L;

    private static final String P_MAX_RANGE = "maxMinuteRange";

    private final AcquireThumbnailListPane<? extends MediaElement> mainPanel;

    private final Object[] options = { "Validate", "Cancel" };
    private final static String REVALIDATE = "ReValidate";

    private final JTextField serieName = new JTextField();
    private final ButtonGroup btnGrp = new ButtonGroup();

    private final JRadioButton btn1 = new JRadioButton("Do not group");
    private final JRadioButton btn2 = new JRadioButton("Group by date");
    private final JRadioButton btn3 = new JRadioButton("Group by name");
    private final JSpinner spinner;

    private Serie serieType = Serie.DEFAULT_SERIE;
    private JOptionPane optionPane;

    private List<ImageElement> mediaList;

    public AcquireImportDialog(AcquireThumbnailListPane<? extends MediaElement> mainPanel,
        List<ImageElement> mediaList) {
        super();
        this.mainPanel = mainPanel;
        this.mediaList = mediaList;

        int maxRange = 60;
        Preferences prefs =
            BundlePreferences.getDefaultPreferences(FrameworkUtil.getBundle(this.getClass()).getBundleContext());
        if (prefs != null) {
            Preferences p = prefs.node(AcquisitionView.PREFERENCE_NODE);
            maxRange = p.getInt(P_MAX_RANGE, maxRange);
        }
        spinner = new JSpinner(new SpinnerNumberModel(maxRange, 1, 5256000, 5));

        optionPane = new JOptionPane(initPanel(), JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null,
            options, options[0]);
        optionPane.addPropertyChangeListener(this);
        setContentPane(optionPane);
        setModal(true);
        setLocationRelativeTo(null);
        pack();
    }

    private JPanel initPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(0, 0, 20, 10));
        panel.setLayout(new GridBagLayout());

        JLabel question = new JLabel("Select how to group the images" + StringUtil.COLON);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0, 0, 15, 0);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.anchor = GridBagConstraints.WEST;
        panel.add(question, c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.anchor = GridBagConstraints.WEST;
        panel.add(btn1, c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = GridBagConstraints.RELATIVE;
        c.anchor = GridBagConstraints.WEST;
        panel.add(btn2, c);

        JMVUtils.setPreferredWidth(spinner, 75);
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 2;
        c.gridwidth = GridBagConstraints.RELATIVE;
        c.anchor = GridBagConstraints.WEST;
        panel.add(spinner, c);
        installFocusListener(spinner);

        c = new GridBagConstraints();
        c.insets = new Insets(0, 2, 0, 0);
        c.gridx = 2;
        c.gridy = 2;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("(max range in minutes)"), c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.WEST;
        panel.add(btn3, c);

        JMVUtils.setPreferredWidth(serieName, 150);
        serieName.addFocusListener(new FocusListener() {

            @Override
            public void focusLost(FocusEvent e) {
                // Do nothing
            }

            @Override
            public void focusGained(FocusEvent e) {
                btnGrp.setSelected(btn3.getModel(), true);
            }
        });
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 3;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.WEST;
        panel.add(serieName, c);

        btnGrp.add(btn1);
        btnGrp.add(btn2);
        btnGrp.add(btn3);
        btnGrp.setSelected(btn1.getModel(), true);

        return panel;
    }

    public void installFocusListener(JSpinner spinner) {
        JComponent spinnerEditor = spinner.getEditor();
        if (spinnerEditor != null) {
            Component c = spinnerEditor.getComponent(0);
            if (c != null) {
                c.addFocusListener(new FocusListener() {

                    @Override
                    public void focusLost(FocusEvent e) {
                        // Do nothing
                    }

                    @Override
                    public void focusGained(FocusEvent e) {
                        btnGrp.setSelected(btn2.getModel(), true);
                    }
                });
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Object action = evt.getNewValue();
        if (action != null) {
            boolean close = true;
            if (action.equals(options[0])) {
                if (btnGrp.getSelection().equals(btn1.getModel())) {
                    serieType = Serie.DEFAULT_SERIE;
                } else if (btnGrp.getSelection().equals(btn2.getModel())) {
                    serieType = Serie.DATE_SERIE;
                } else {
                    if (serieName.getText() != null && !serieName.getText().isEmpty()) {
                        serieType = new Serie(serieName.getText());
                    } else {
                        JOptionPane.showMessageDialog(this, "PLease provide a name for the Serie",
                            "The Serie name cannot be empty", JOptionPane.ERROR_MESSAGE);
                        optionPane.setValue(REVALIDATE);
                        close = false;
                    }
                }
                if (close) {
                    Integer max = (Integer) spinner.getValue();
                    AcquireManager.importImages(serieType, mediaList, max);
                    mainPanel.getCentralPane().setSelectedAndGetFocus();
                    Preferences prefs = BundlePreferences
                        .getDefaultPreferences(FrameworkUtil.getBundle(this.getClass()).getBundleContext());
                    if (prefs != null) {
                        Preferences p = prefs.node(AcquisitionView.PREFERENCE_NODE);
                        BundlePreferences.putIntPreferences(p, P_MAX_RANGE, max);
                    }
                }
            } else if (action.equals(REVALIDATE)) {
                close = false;
            }

            if (close) {
                clearAndHide();
            }
        }
    }

    public void clearAndHide() {
        serieName.setText(null);
        setVisible(false);
    }
}
