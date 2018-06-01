# In 6.x and 7.x gcc, libgfortran now needs libbacktrace.
# Enable building of this so that libgfortran builds correctly.

do_configure_prepend() {
    rm -rf ${B}/${TARGET_SYS}/libbacktrace/
    mkdir -p ${B}/${TARGET_SYS}/libbacktrace/
    cd ${B}/${TARGET_SYS}/libbacktrace/
    chmod a+x ${S}/libbacktrace/configure
    relpath=${@os.path.relpath("${S}", "${B}/${TARGET_SYS}")}
    ../$relpath/libbacktrace/configure ${CONFIGUREOPTS} ${EXTRA_OECONF}
    # Easiest way to stop bad RPATHs getting into the library since we have a
    # broken libtool here
    sed -i -e 's/hardcode_into_libs=yes/hardcode_into_libs=no/' ${B}/${TARGET_SYS}/libbacktrace/libtool
}

do_compile_prepend () {
    cd ${B}/${TARGET_SYS}/libbacktrace/
    oe_runmake MULTIBUILDTOP=${B}/${TARGET_SYS}/libbacktrace/
}
