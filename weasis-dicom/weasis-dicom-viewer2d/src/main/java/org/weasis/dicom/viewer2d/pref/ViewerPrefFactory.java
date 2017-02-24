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
package org.weasis.dicom.viewer2d.pref;

import java.util.Hashtable;

import org.weasis.core.api.gui.Insertable;
import org.weasis.core.api.gui.Insertable.Type;
import org.weasis.core.api.gui.PreferencesPageFactory;
import org.weasis.core.api.gui.util.AbstractItemDialogPage;

@org.osgi.service.component.annotations.Component(service = PreferencesPageFactory.class, immediate = false)
public class ViewerPrefFactory implements PreferencesPageFactory {

    @Override
    public AbstractItemDialogPage createInstance(Hashtable<String, Object> properties) {
        return new ViewerPrefView();
    }

    @Override
    public void dispose(Insertable component) {
    }

    @Override
    public boolean isComponentCreatedByThisFactory(Insertable component) {
        return component instanceof ViewerPrefView;
    }

    @Override
    public Type getType() {
        return Insertable.Type.PREFERENCES;
    }

}
