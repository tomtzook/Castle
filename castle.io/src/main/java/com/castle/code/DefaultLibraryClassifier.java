package com.castle.code;

import com.castle.formats.ElfImage;
import com.castle.formats.PeImage;
import com.castle.util.os.Architecture;
import com.castle.util.os.KnownArchitecture;
import com.castle.util.os.KnownOperatingSystem;
import com.castle.util.os.Platform;
import com.castle.util.throwables.ThrowableChain;
import com.castle.util.throwables.Throwables;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

public class DefaultLibraryClassifier implements LibraryClassifier {

    @Override
    public Platform targetPlatform(Path path) throws IOException {
        ThrowableChain chain = Throwables.newChain();

        try {
            Optional<PeImage> optional = PeImage.tryOpen(path);
            if (optional.isPresent()) {
                PeImage image = optional.get();
                Architecture architecture = image.machineType().arch();
                return new Platform(KnownOperatingSystem.WINDOWS, architecture);
            }
        } catch (IOException e) {
            // probably not a PE
            chain.chain(e);
        }

        try {
            Optional<ElfImage> optional = ElfImage.tryOpen(path);
            if (optional.isPresent()) {
                ElfImage image = optional.get();
                Architecture architecture = image.machineType().arch();
                return new Platform(KnownOperatingSystem.LINUX, architecture);
            }
        } catch (IOException e) {
            // probably not an ELF
            chain.chain(e);
        }

        if (chain.getTopThrowable().isPresent()) {
            chain.throwIfType(IOException.class);
        }

        return new Platform(KnownOperatingSystem.UNKNOWN, KnownArchitecture.UNKNOWN);
    }
}
