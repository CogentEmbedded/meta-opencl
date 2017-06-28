do_configure_append () {
    # Correct invalid libgcc symlinks with wrong target path.
    cd ${B}/$target/libgcc
    for d in enable-execute-stack.c gthr-default.h md-unwind-support.h sfp-machine.h unwind.h; do
        if [ ! -e "$d" ]; then
            ln -f -s ./../$(readlink $d) $d
        fi
    done
}

FILES_${PN}-dev += " \
    ${libdir}/gcc/${TARGET_SYS}/${BINV}/finclude \
"
