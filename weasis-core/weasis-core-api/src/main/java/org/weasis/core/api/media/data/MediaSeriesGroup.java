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
package org.weasis.core.api.media.data;

public interface MediaSeriesGroup extends Tagable {

    TagW getTagID();

    boolean matchIdValue(Object valueID);

    TagW getTagElement(int id);

    void dispose();

    void addMergeIdValue(Object valueID);
}
