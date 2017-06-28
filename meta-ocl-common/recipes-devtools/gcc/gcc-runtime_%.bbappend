do_install_append() {
    # These files are handled by libgfortran package
    rm -f ${D}${libdir}/libgfortran.*
    rm -f ${D}${libdir}/gcc/${TARGET_SYS}/${BINV}/libgfortran*
    rm -f ${D}${libdir}/gcc/${TARGET_SYS}/${BINV}/libcaf_single.*
    rm -rf ${D}${libdir}/gcc/${TARGET_SYS}/${BINV}/finclude
}
