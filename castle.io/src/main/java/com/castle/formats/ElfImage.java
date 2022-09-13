package com.castle.formats;

import com.castle.util.Buffers;
import com.castle.util.os.Architecture;
import com.castle.util.os.KnownArchitecture;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

public class ElfImage {

    public static final byte[] ELF_MAGIC = "\177ELF".getBytes(StandardCharsets.UTF_8);
    public static final int ELF_HEADER_MACHINE_OFFSET = 18;

    public enum MachineType {
        MACHINE_X8664(62, KnownArchitecture.AMD64),
        MACHINE_IA64(50, KnownArchitecture.IA64),
        MACHINE_INTEL386(3, KnownArchitecture.I386),
        MACHINE_PPC(20, KnownArchitecture.PPC),
        MACHINE_AARCH64(183, KnownArchitecture.AARCH64),
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

    public ElfImage(ByteBuffer headers) throws BadFormatException {
        if (!Buffers.areSame(headers, 0, ELF_MAGIC)) {
            throw new BadFormatException("bad ELF signature");
        }

        mHeaders = headers;
    }

    public MachineType machineType() throws BadFormatException {
        int machineType = Short.toUnsignedInt(mHeaders.getShort(ELF_HEADER_MACHINE_OFFSET));
        try {
            return MachineType.fromCode(machineType);
        } catch (EnumConstantNotPresentException e) {
            throw new BadFormatException("unknown machineType " + machineType, e);
        }
    }

    public static ElfImage open(Path path) throws IOException {
        try (SeekableByteChannel channel = Files.newByteChannel(path, StandardOpenOption.READ)) {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            channel.read(buffer);
            buffer.flip();
            buffer.order(ByteOrder.LITTLE_ENDIAN);

            return new ElfImage(buffer);
        }
    }

    public static Optional<ElfImage> tryOpen(Path path) throws IOException {
        try {
            return Optional.of(open(path));
        } catch (BadFormatException e) {
            // not an elf then
            return Optional.empty();
        }
    }
}
