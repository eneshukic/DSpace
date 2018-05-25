/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dspace.app.mediafilter;

import org.dspace.content.Item;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

/**
 *
 * @author Enes This class is used for the multi-page tiff file preview
 * generation. Preview is created from the first page
 */
public class ImageMagickTifPreviewFilter extends ImageMagickPreviewFilter {

    @Override
    public InputStream getDestinationStream(Item currentItem, InputStream source, boolean verbose)
            throws Exception {
        File f = inputStreamToTempFile(source, "imptifpreview", ".tif");
        File f2 = null;
        File f3 = null;
        try {
            f2 = getImageFile(f, 0, verbose);
            f3 = getPreviewFile(f2, verbose);
            byte[] bytes = Files.readAllBytes(f3.toPath());
            return new ByteArrayInputStream(bytes);
        } finally {
            //noinspection ResultOfMethodCallIgnored
            f.delete();
            if (f2 != null) {
                //noinspection ResultOfMethodCallIgnored
                f2.delete();
            }
            if (f3 != null) {
                //noinspection ResultOfMethodCallIgnored
                f3.delete();
            }
        }
    }

}
