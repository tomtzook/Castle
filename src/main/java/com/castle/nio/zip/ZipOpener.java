package com.castle.nio.zip;

import java.io.IOException;

public interface ZipOpener {

    OpenZip open(ZipReferenceCounter zipReferenceCounter) throws IOException;
}
