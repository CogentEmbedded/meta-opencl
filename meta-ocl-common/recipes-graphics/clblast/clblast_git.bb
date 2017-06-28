SUMMARY = "CLBlast"
DESCRIPTION = "A modern, lightweight, performant and tunable OpenCL BLAS library written in C++11."

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/LICENSE;md5=aeb40f7c58956a1eb8441f0b51f900bb"

#    file://0001-Reduce-default-WGS3-and-WPT3-sizes.patch

SRC_URI = " \
    git://github.com/CNugteren/CLBlast.git \
    file://0001-Add-PowerVR-Rogue-GX6650-to-the-database.patch \
    file://0002-Add-library-versioning-support.patch \
"

SRCREV = "48f2682eb7ee72b0f9e6f2922569fcf352f8ce5f"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE += ""

DEPENDS += "virtual/opencl"

FILES_${PN}-dev += "${libdir}/cmake"
