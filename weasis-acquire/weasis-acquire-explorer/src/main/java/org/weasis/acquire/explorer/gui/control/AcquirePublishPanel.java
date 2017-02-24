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
package org.weasis.acquire.explorer.gui.control;

import java.awt.Dimension;
import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.SwingWorker.StateValue;

import org.dcm4che3.net.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.weasis.acquire.explorer.Messages;
import org.weasis.acquire.explorer.PublishDicomTask;
import org.weasis.acquire.explorer.gui.dialog.AcquirePublishDialog;
import org.weasis.core.api.gui.task.CircularProgressBar;
import org.weasis.core.api.gui.util.JMVUtils;
import org.weasis.core.api.gui.util.WinUtil;
import org.weasis.core.api.util.FontTools;
import org.weasis.core.api.util.ThreadUtil;
import org.weasis.dicom.param.DicomNode;
import org.weasis.dicom.param.DicomState;

@SuppressWarnings("serial")
public class AcquirePublishPanel extends JPanel {
    private static final Logger LOGGER = LoggerFactory.getLogger(AcquirePublishPanel.class);

    private final JButton publishBtn = new JButton(Messages.getString("AcquirePublishPanel.publish")); //$NON-NLS-1$
    private final CircularProgressBar progressBar = new CircularProgressBar(0, 100);

    public static final ExecutorService PUBLISH_DICOM = ThreadUtil.buildNewSingleThreadExecutor("Publish Dicom"); //$NON-NLS-1$

    public AcquirePublishPanel() {
        // setBorder(new TitledBorder(null, "Publish", TitledBorder.LEADING, TitledBorder.TOP, null, null));

        publishBtn.addActionListener(e -> {
            final AcquirePublishDialog dialog = new AcquirePublishDialog(AcquirePublishPanel.this);
            JMVUtils.showCenterScreen(dialog, WinUtil.getParentWindow(AcquirePublishPanel.this));
        });

        publishBtn.setPreferredSize(new Dimension(150, 40));
        publishBtn.setFont(FontTools.getFont12Bold());

        add(publishBtn);
        add(progressBar);

        progressBar.setVisible(false);
    }

    public void publishDirDicom(File exportDirDicom, DicomNode destinationNode) {

        SwingWorker<DicomState, File> publishDicomTask = new PublishDicomTask(exportDirDicom, destinationNode);
        publishDicomTask.addPropertyChangeListener(evt -> {
            if ("progress" == evt.getPropertyName()) { //$NON-NLS-1$
                int progress = (Integer) evt.getNewValue();
                progressBar.setValue(progress);

            } else if ("state" == evt.getPropertyName()) { //$NON-NLS-1$

                if (StateValue.STARTED == evt.getNewValue()) {
                    publishBtn.setEnabled(false);
                    progressBar.setVisible(true);
                    progressBar.setValue(0);

                } else if (StateValue.DONE == evt.getNewValue()) {
                    try {
                        final DicomState dicomState = publishDicomTask.get();

                        if (dicomState.getStatus() != Status.Success) {
                            LOGGER.error("Dicom send error: {}", dicomState.getMessage()); //$NON-NLS-1$
                            JOptionPane.showOptionDialog(WinUtil.getParentWindow(AcquirePublishPanel.this),
                                String.format("DICOM send error: %s", dicomState.getMessage()), null, //$NON-NLS-1$
                                JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, null, null);
                        }
                        else if(dicomState.getProgress().getNumberOfFailedSuboperations() > 0){
                            LOGGER.error("{} dicom send operations failed", dicomState.getProgress().getNumberOfFailedSuboperations()); //$NON-NLS-1$
                            JOptionPane.showOptionDialog(WinUtil.getParentWindow(AcquirePublishPanel.this),
                                String.format("DICOM send error: %d operations", dicomState.getProgress().getNumberOfFailedSuboperations()), null, //$NON-NLS-1$
                                JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, null, null);
                        }
                    } catch (InterruptedException | ExecutionException doNothing) {
                    }
                    publishBtn.setEnabled(true);
                    progressBar.setVisible(false);
                }
            }
        });

        PUBLISH_DICOM.execute(publishDicomTask);
    }
}
