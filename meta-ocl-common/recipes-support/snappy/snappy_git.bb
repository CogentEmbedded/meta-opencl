SUMMARRY = "Snappy"
DESCRIPTION = "Snappy, a fast compressor/decompressor."

SRC_URI = "git://github.com/google/snappy.git"
SRCREV = "2d99bd14d471664758e4dfdf81b44f413a7353fd"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://${S}/COPYING;md5=f62f3080324a97b3159a7a7e61812d0c"

S = "${WORKDIR}/git"

inherit autotools pkgconfig
