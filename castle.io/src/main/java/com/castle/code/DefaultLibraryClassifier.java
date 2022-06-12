package com.castle.code;

import com.castle.io.FileNames;
import com.castle.util.os.Architecture;
import com.castle.util.os.KnownArchitecture;
import com.castle.util.os.KnownOperatingSystem;
import com.castle.util.os.Platform;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class DefaultLibraryClassifier implements LibraryClassifier {

    private static final byte[] PE_IMAGE_DOS_SIGNATURE = {0x4D, 0x5A}; /* MZ */
    private static final byte[] PE_IMAGE_NT_SIGNATURE = {0x50, 0x45}; /* PE */
    private static final int PE_OPTIONAL_HEADER_OFFSET = 60;

    private static final byte[] ELF_MAGIC = "\177ELF".getBytes(StandardCharsets.UTF_8);
    private static final int ELF_HEADER_MACHINE_OFFSET = 18;

    @Override
    public Platform targetPlatform(Path path) throws IOException {
        if (isPe(path)) {
            Architecture architecture = getArchFromPe(path);
            return new Platform(KnownOperatingSystem.WINDOWS, architecture);
        }

        if (isElf(path)) {
            Architecture architecture = getArchFromElf(path);
            return new Platform(KnownOperatingSystem.LINUX, architecture);
        }

        return new Platform(KnownOperatingSystem.UNKNOWN, KnownArchitecture.UNKNOWN);
    }

    private static boolean isPe(Path path) {
        String ext = FileNames.extension(path);
        return ext.equals("dll");
    }

    private static Architecture getArchFromPe(Path path) throws IOException {
        try (SeekableByteChannel channel = Files.newByteChannel(path, StandardOpenOption.READ)) {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            channel.read(buffer);
            buffer.flip();
            buffer.order(ByteOrder.LITTLE_ENDIAN);

            if (!areSame(buffer, 0, PE_IMAGE_DOS_SIGNATURE)) {
                throw new IOException("bad DOS signature");
            }

            int offsetToFileHeader = buffer.getInt(PE_OPTIONAL_HEADER_OFFSET);
            if (!areSame(buffer, offsetToFileHeader, PE_IMAGE_NT_SIGNATURE)) {
                throw new IOException("bad NT signature");
            }

            try {
                int machineType = Short.toUnsignedInt(buffer.getShort(offsetToFileHeader + 4));
                return PeMachineType.fromCode(machineType).arch();
            } catch (EnumConstantNotPresentException e) {
                return KnownArchitecture.UNKNOWN;
            }
        }
    }

    private static boolean isElf(Path path) {
        String ext = FileNames.extension(path);
        return ext.equals("so");
    }

    private static Architecture getArchFromElf(Path path) throws IOException {
        try (SeekableByteChannel channel = Files.newByteChannel(path, StandardOpenOption.READ)) {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            channel.read(buffer);
            buffer.flip();
            buffer.order(ByteOrder.LITTLE_ENDIAN);

            if (!areSame(buffer, 0, ELF_MAGIC)) {
                throw new IOException("bad ELF signature");
            }

            try {
                int machineType = Short.toUnsignedInt(buffer.getShort(ELF_HEADER_MACHINE_OFFSET));
                return ElfMachineType.fromCode(machineType).arch();
            } catch (EnumConstantNotPresentException e) {
                return KnownArchitecture.UNKNOWN;
            }
        }
    }

    private static boolean areSame(ByteBuffer buffer, int offset, byte[] arr) {
        for (int i = 0; i < arr.length; i++) {
            byte value = buffer.get(offset + i);
            if (value != arr[i]) {
                return false;
            }
        }

        return true;
    }

    enum PeMachineType {
        MACHINE_AMD64(0x8664, KnownArchitecture.AMD64),
        MACHINE_IA64(0x0200, KnownArchitecture.IA64),
        MACHINE_I386(0x014c, KnownArchitecture.I386),
        MACHINE_POWERPC(0x01f0, KnownArchitecture.PPC),
        MACHINE_ARM64(0x01c5, KnownArchitecture.AARCH64),
        ;

        private final int mCode;
        private final Architecture mMatchingArch;

        PeMachineType(int code, Architecture matchingArch) {
            mCode = code;
            mMatchingArch = matchingArch;
        }

        public int code() {
            return mCode;
        }

        public Architecture arch() {
            return mMatchingArch;
        }

        public static PeMachineType fromCode(int code) {
            for (PeMachineType type : values()) {
                if (type.code() == code) {
                    return type;
                }
            }

            throw new EnumConstantNotPresentException(PeMachineType.class, String.valueOf(code));
        }
    }

    enum ElfMachineType {
        MACHINE_X8664(62, KnownArchitecture.AMD64),
        MACHINE_IA64(50, KnownArchitecture.IA64),
        MACHINE_INTEL386(3, KnownArchitecture.I386),
        MACHINE_PPC(20, KnownArchitecture.PPC),
        MACHINE_AARCH64(183, KnownArchitecture.AARCH64),
        ;

        private final int mCode;
        private final Architecture mMatchingArch;

        ElfMachineType(int code, Architecture matchingArch) {
            mCode = code;
            mMatchingArch = matchingArch;
        }

        public int code() {
            return mCode;
        }

        public Architecture arch() {
            return mMatchingArch;
        }

        public static ElfMachineType fromCode(int code) {
            for (ElfMachineType type : values()) {
                if (type.code() == code) {
                    return type;
                }
            }

            throw new EnumConstantNotPresentException(ElfMachineType.class, String.valueOf(code));
        }
    }
}
