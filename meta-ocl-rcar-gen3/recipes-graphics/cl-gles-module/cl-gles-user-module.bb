require include/gles-control.inc

DESCRIPTION = "PowerVR CL GLES3 GPU user module"
LICENSE = "CLOSED"

PN = "cl-gles-user-module"
PR = "r0"

COMPATIBLE_MACHINE = "(r8a7795|r8a7796|r8a77965|r8a77990)"
PACKAGE_ARCH = "${MACHINE_ARCH}"

S = "${WORKDIR}/rogue"
GLES = "gsx"

SRC_URI_r8a7795 = "file://r8a77951_linux_gsx_binaries_cl_gles.tar.bz2"
SRC_URI_r8a7796 = "file://r8a77960_linux_gsx_binaries_cl_gles.tar.bz2"
SRC_URI_r8a77965 = "file://r8a77965_linux_gsx_binaries_cl_gles.tar.bz2"
SRC_URI_r8a77990 = "file://r8a77990_linux_gsx_binaries_cl_gles.tar.bz2"
SRC_URI_append = " \
    file://change-shell.patch \
    file://rc.pvr.service \
"

inherit update-rc.d systemd

INITSCRIPT_NAME = "pvrinit"
INITSCRIPT_PARAMS = "start 7 5 2 . stop 62 0 1 6 ."
SYSTEMD_SERVICE_${PN} = "rc.pvr.service"

do_populate_lic[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    # Install configuration files
    install -d ${D}/${sysconfdir}/init.d
    install -m 644 ${S}/${sysconfdir}/powervr.ini ${D}/${sysconfdir}
    install -m 755 ${S}/${sysconfdir}/init.d/rc.pvr ${D}/${sysconfdir}/init.d/
    install -d ${D}/${sysconfdir}/udev/rules.d
    install -m 644 ${S}/${sysconfdir}/udev/rules.d/72-pvr-seat.rules ${D}/${sysconfdir}/udev/rules.d/

    # Install header files
    install -d ${D}/${includedir}/CL
    install -m 644 ${S}/${includedir}/CL/*.h ${D}/${includedir}/CL/
    install -d ${D}/${includedir}/EGL
    install -m 644 ${S}/${includedir}/EGL/*.h ${D}/${includedir}/EGL/
    install -d ${D}/${includedir}/GLES2
    install -m 644 ${S}/${includedir}/GLES2/*.h ${D}/${includedir}/GLES2/
    install -d ${D}/${includedir}/GLES3
    install -m 644 ${S}/${includedir}/GLES3/*.h ${D}/${includedir}/GLES3/
    install -d ${D}/${includedir}/KHR
    install -m 644 ${S}/${includedir}/KHR/khrplatform.h ${D}/${includedir}/KHR/khrplatform.h

    # Install pre-builded binaries
    install -d ${D}/${libdir}
    install -m 755 ${S}/${libdir}/*.so ${D}/${libdir}/
    install -d ${D}/${exec_prefix}/local/bin
    install -m 755 ${S}/${exec_prefix}/local/bin/dlcsrv_REL ${D}/${exec_prefix}/local/bin/dlcsrv_REL
    install -m 755 ${S}/${exec_prefix}/local/bin/ocl_unit_test ${D}/${exec_prefix}/local/bin/
    install -d ${D}/lib/firmware
    install -m 644 ${S}/lib/firmware/* ${D}/lib/firmware/

    # Install pkgconfig
    install -d ${D}/${libdir}/pkgconfig
    install -m 644 ${S}/${libdir}/pkgconfig/*.pc ${D}/${libdir}/pkgconfig/

    # Create symbolic link
    cd ${D}/${libdir}
    ln -sf libEGL.so libEGL.so.1
    ln -sf libGLESv2.so libGLESv2.so.2
    ln -sf libPVROCL.so libOpenCL.so

    if [ "${USE_GLES_WAYLAND}" = "1" ]; then
        # Set the "WindowSystem" parameter for wayland
        if [ "${GLES}" = "gsx" ]; then
            sed -i -e "s/WindowSystem=libpvrDRM_WSEGL.so/WindowSystem=libpvrWAYLAND_WSEGL.so/g" \
                ${D}/${sysconfdir}/powervr.ini
        fi
    fi

    # Install systemd service
    if [ ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)} ]; then
        install -d ${D}/${systemd_system_unitdir}/
        install -m 644 ${WORKDIR}/rc.pvr.service ${D}/${systemd_system_unitdir}/
        install -d ${D}/${exec_prefix}/bin
        install -m 755 ${S}/${sysconfdir}/init.d/rc.pvr ${D}/${exec_prefix}/bin/pvrinit
    fi

    if [ ${@bb.utils.contains('DISTRO_FEATURES', 'surroundview', 'true', 'false', d)} ]; then
        echo "WseglNumBuffers=4" >> ${D}/${sysconfdir}/powervr.ini
    fi
}

PACKAGES = "\
    ${PN} \
    ${PN}-dev \
"

FILES_${PN} = " \
    ${sysconfdir}/* \
    ${libdir}/* \
    /lib/firmware/rgx.fw* \
    ${exec_prefix}/local/bin/dlcsrv_REL \
    ${exec_prefix}/bin/* \
"

FILES_${PN}-dev = " \
    ${includedir}/* \
    ${libdir}/pkgconfig/* \
    ${exec_prefix}/local/bin/ocl_unit_test \
"

PROVIDES += "virtual/libgles2 virtual/egl virtual/opencl opencl-headers"

PROVIDES += "gles-user-module"
RPROVIDES_${PN} += "gles-user-module"
RREPLACES_${PN} += "gles-user-module"
RCONFLICTS_${PN} += "gles-user-module"

RPROVIDES_${PN} += " \
    ${GLES}-user-module \
    libgles2-mesa \
    libgles2-mesa-dev \
    libgles2 \
    libgles2-dev \
    libegl \
    libegl1 \
    libOpenCL.so()(64bit) \
"

DEPENDS += "libdrm"

RDEPENDS_${PN} = " \
    kernel-module-gles \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'libgbm wayland-kms', '', d)} \
"

INSANE_SKIP_${PN} = "ldflags build-deps file-rdeps dev-so"
INSANE_SKIP_${PN}-dev = "ldflags build-deps file-rdeps"
INSANE_SKIP_${PN} += "arch"
INSANE_SKIP_${PN}-dev += "arch"
INSANE_SKIP_${PN}-dbg = "arch"

# Skip debug strip of do_populate_sysroot()
INHIBIT_SYSROOT_STRIP = "1"

# Skip debug split and strip of do_package()
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_PACKAGE_STRIP = "1"
