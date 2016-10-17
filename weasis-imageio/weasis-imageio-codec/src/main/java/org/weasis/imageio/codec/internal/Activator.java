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
package org.weasis.imageio.codec.internal;

import javax.imageio.ImageIO;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ImageWriterSpi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.weasis.core.api.image.util.JAIUtil;

import com.sun.media.imageioimpl.common.ImageioUtil;
import com.sun.media.imageioimpl.plugins.bmp.BMPImageReaderSpi;
import com.sun.media.imageioimpl.plugins.bmp.BMPImageWriterSpi;
import com.sun.media.imageioimpl.plugins.gif.GIFImageWriterSpi;
import com.sun.media.imageioimpl.plugins.jpeg.CLibJPEGImageReaderSpi;
import com.sun.media.imageioimpl.plugins.jpeg.CLibJPEGImageWriterSpi;
import com.sun.media.imageioimpl.plugins.jpeg2000.J2KImageReaderCodecLibSpi;
import com.sun.media.imageioimpl.plugins.jpeg2000.J2KImageReaderSpi;
import com.sun.media.imageioimpl.plugins.jpeg2000.J2KImageWriterCodecLibSpi;
import com.sun.media.imageioimpl.plugins.jpeg2000.J2KImageWriterSpi;
import com.sun.media.imageioimpl.plugins.png.CLibPNGImageReaderSpi;
import com.sun.media.imageioimpl.plugins.png.CLibPNGImageWriterSpi;
import com.sun.media.imageioimpl.plugins.pnm.PNMImageReaderSpi;
import com.sun.media.imageioimpl.plugins.pnm.PNMImageWriterSpi;
import com.sun.media.imageioimpl.plugins.raw.RawImageReaderSpi;
import com.sun.media.imageioimpl.plugins.raw.RawImageWriterSpi;
import com.sun.media.imageioimpl.plugins.tiff.TIFFImageReaderSpi;
import com.sun.media.imageioimpl.plugins.tiff.TIFFImageWriterSpi;
import com.sun.media.imageioimpl.plugins.wbmp.WBMPImageReaderSpi;
import com.sun.media.imageioimpl.plugins.wbmp.WBMPImageWriterSpi;
import com.sun.media.imageioimpl.stream.ChannelImageInputStreamSpi;
import com.sun.media.imageioimpl.stream.ChannelImageOutputStreamSpi;
import com.sun.media.jai.imageioimpl.ImageReadWriteSpi;

public class Activator implements BundleActivator {

    @Override
    public void start(final BundleContext bundleContext) throws Exception {
        // Do not use cache. Images must be download locally before reading them.
        ImageIO.setUseCache(false);

        // SPI Issue Resolution
        // Register imageio SPI with the classloader of this bundle
        // and unregister imageio SPI if imageio.jar is also in the jre/lib/ext folder

        Class[] jaiCodecs = { ChannelImageInputStreamSpi.class, ChannelImageOutputStreamSpi.class,
            J2KImageReaderSpi.class, J2KImageReaderCodecLibSpi.class, WBMPImageReaderSpi.class, BMPImageReaderSpi.class,
            PNMImageReaderSpi.class, RawImageReaderSpi.class, TIFFImageReaderSpi.class, J2KImageWriterSpi.class,
            J2KImageWriterCodecLibSpi.class, WBMPImageWriterSpi.class, BMPImageWriterSpi.class, GIFImageWriterSpi.class,
            PNMImageWriterSpi.class, RawImageWriterSpi.class, TIFFImageWriterSpi.class };

        for (Class c : jaiCodecs) {
            ImageioUtil.registerServiceProvider(c);
        }

        // Set priority to these codec which have better performance to the one in JRE
        ImageioUtil.registerServiceProviderInHighestPriority(CLibJPEGImageReaderSpi.class, ImageReaderSpi.class, "jpeg");
        ImageioUtil.registerServiceProviderInHighestPriority(CLibJPEGImageWriterSpi.class, ImageWriterSpi.class, "jpeg");
        ImageioUtil.registerServiceProviderInHighestPriority(CLibPNGImageReaderSpi.class, ImageReaderSpi.class, "png");
        ImageioUtil.registerServiceProviderInHighestPriority(CLibPNGImageWriterSpi.class, ImageWriterSpi.class, "png");

        // TODO Should be in properties?
        // Unregister sun native jpeg codec
        // ImageioUtil.unRegisterServiceProvider(registry, CLibJPEGImageReaderSpi.class);

        // Register the ImageRead and ImageWrite operation for JAI
        new ImageReadWriteSpi().updateRegistry(JAIUtil.getOperationRegistry());
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {

        Class[] jaiCodecs = { ChannelImageInputStreamSpi.class, ChannelImageOutputStreamSpi.class,
            CLibJPEGImageReaderSpi.class, CLibPNGImageReaderSpi.class, J2KImageReaderSpi.class,
            J2KImageReaderCodecLibSpi.class, WBMPImageReaderSpi.class, BMPImageReaderSpi.class, PNMImageReaderSpi.class,
            RawImageReaderSpi.class, TIFFImageReaderSpi.class, CLibJPEGImageWriterSpi.class,
            CLibPNGImageWriterSpi.class, J2KImageWriterSpi.class, J2KImageWriterCodecLibSpi.class,
            WBMPImageWriterSpi.class, BMPImageWriterSpi.class, GIFImageWriterSpi.class, PNMImageWriterSpi.class,
            RawImageWriterSpi.class, TIFFImageWriterSpi.class };

        for (Class c : jaiCodecs) {
            ImageioUtil.unRegisterServiceProvider(c);
        }

    }

}
