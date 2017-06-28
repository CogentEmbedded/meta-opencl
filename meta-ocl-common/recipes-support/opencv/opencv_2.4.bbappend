# Add gstreamer support
PACKAGECONFIG[gstreamer] = "-DWITH_GSTREAMER=ON,-DWITH_GSTREAMER=OFF,gstreamer1.0 gstreamer1.0-plugins-base,"

# Remove libav since it's not supported. While at it enable gstreamer
PACKAGECONFIG ??= "eigen jpeg png tiff v4l libv4l gstreamer \
                   ${@bb.utils.contains("DISTRO_FEATURES", "x11", "gtk", "", d)} \
"
