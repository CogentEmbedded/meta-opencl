SUMMARY = "clBLAS"
DESCRIPTION = "Software library containing BLAS functions written in OpenCL"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/LICENSE;md5=2ee41112a44fe7014dce33e26468ba93"

SRC_URI = " \
    git://github.com/clMathLibraries/clBLAS.git \
    file://clt.tar.bz2 \
    file://prebuilt-clt.patch \
"

SRCREV = "cf9113982fdfc994297d372785ce76eb80911af2"

S = "${WORKDIR}/git/src"

inherit cmake pythonnative

EXTRA_OECMAKE += "-DSUFFIX_LIB= -DUSE_SYSTEM_GTEST=ON -DBUILD_TEST=OFF -DPREBUILT_CLT_PATH=${WORKDIR}/clt"

DEPENDS += "virtual/opencl"

FILES_${PN}-dev += "${libdir}/cmake"
