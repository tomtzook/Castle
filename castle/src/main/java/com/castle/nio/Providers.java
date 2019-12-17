package com.castle.nio;

import java.nio.file.ProviderNotFoundException;
import java.nio.file.spi.FileSystemProvider;

public class Providers {

    private static final String ZIP_SCHEME = "jar";

    private Providers() { }

    public static FileSystemProvider providerForScheme(String scheme) {
        for (FileSystemProvider provider : FileSystemProvider.installedProviders()) {
            if (scheme.equalsIgnoreCase(provider.getScheme())) {
                return provider;
            }
        }

        throw new ProviderNotFoundException("Provider \"" + scheme + "\" not found");
    }

    public static FileSystemProvider zipProvider() {
        return providerForScheme(ZIP_SCHEME);
    }
}
