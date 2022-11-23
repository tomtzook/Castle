package com.castle.formats;

import com.castle.util.Buffers;
import com.castle.util.os.Architecture;
import com.castle.util.os.KnownArchitecture;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

public class PeImage {

    public static final byte[] PE_IMAGE_DOS_SIGNATURE = {0x4D, 0x5A}; /* MZ */
    public static final byte[] PE_IMAGE_NT_SIGNATURE = {0x50, 0x45}; /* PE */
    public static final int PE_OPTIONAL_HEADER_OFFSET = 60;

    public enum MachineType {
        MACHINE_AMD64(0x8664, KnownArchitecture.AMD64),
        MACHINE_IA64(0x0200, KnownArchitecture.IA64),
        MACHINE_I386(0x014c, KnownArchitecture.I386),
        MACHINE_POWERPC(0x01f0, KnownArchitecture.PPC),
        MACHINE_ARM64(0x01c5, KnownArchitecture.AARCH64),
        ;

        private final int mCode;
        private final Architecture mMatchingArch;

        MachineType(int code, Architecture matchingArch) {
            mCode = code;
            mMatchingArch = matchingArch;
        }

        public int code() {
            return mCode;
        }

        public Architecture arch() {
            return mMatchingArch;
        }

        public static MachineType fromCode(int code) {
            for (MachineType type : values()) {
                if (type.code() == code) {
                    return type;
                }
            }

            throw new EnumConstantNotPresentException(MachineType.class, String.valueOf(code));
        }
    }

    private final ByteBuffer mHeaders;
    private final int mOffsetToFileHeader;

    private PeImage(ByteBuffer headers) throws BadFormatException {
        if (!Buffers.areSame(headers, 0, PE_IMAGE_DOS_SIGNATURE)) {
            throw new BadFormatException("bad DOS signature");
        }

        int offsetToFileHeader = headers.getInt(PE_OPTIONAL_HEADER_OFFSET);
        if (!Buffers.areSame(headers, offsetToFileHeader, PE_IMAGE_NT_SIGNATURE)) {
            throw new BadFormatException("bad NT signature");
        }

        mHeaders = headers;
        mOffsetToFileHeader = offsetToFileHeader;
    }

    public MachineType machineType() throws BadFormatException {
        int machineType = Short.toUnsignedInt(mHeaders.getShort(mOffsetToFileHeader + 4));
        try {
            return MachineType.fromCode(machineType);
        } catch (EnumConstantNotPresentException e) {
            throw new BadFormatException("unknown machineType " + machineType, e);
        }
    }

    public static PeImage open(Path path) throws IOException {
        try (SeekableByteChannel channel = Files.newByteChannel(path, StandardOpenOption.READ)) {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            channel.read(buffer);
            buffer.flip();
            buffer.order(ByteOrder.LITTLE_ENDIAN);

            return new PeImage(buffer);
        }
    }

    public static Optional<PeImage> tryOpen(Path path) throws IOException {
        try {
            return Optional.of(open(path));
        } catch (BadFormatException e) {
            // not a PE then
            return Optional.empty();
        }
    }
}
