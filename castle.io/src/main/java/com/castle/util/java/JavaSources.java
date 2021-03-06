package com.castle.util.java;

import com.castle.annotations.Stateless;
import com.castle.nio.zip.Zip;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;

@Stateless
public class JavaSources {

    private JavaSources() {
    }

    /**
     * <p>
     * Get the <em>JAR</em> archive containing the given class
     * code file.
     * </p>
     *
     * @param cls class type
     *
     * @return the <em>JAR</em> as a {@link Zip} object.
     *
     * @throws Error if unable to find the <em>JAR</em>. Might occur if the
     *               class is not contained in a <em>JAR</em>.
     */
    public static Zip jarContaining(Class<?> cls) {
        try {
            Path path = new File(cls.getProtectionDomain().getCodeSource().getLocation()
                    .toURI()).toPath();

            return Zip.fromPath(path);
        } catch (URISyntaxException e) {
            throw new Error(e);
        }
    }
}
