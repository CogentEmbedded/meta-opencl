SUMMARY = "Renesas OpenCL SDK"
#HOMEPAGE
#BUGTRACKER

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=61fcb6632817d78f66982168f3e5d77e"

DEPENDS += "virtual/opencl opencv protobuf protobuf-native glog boost zlib jpeg libpng"

PV = "1.0.5"
S = "${WORKDIR}/git/${P}"

SRC_URI = "git://adc.luxoft.COm/stash/scm/renocl/renocl_sdk.git;protocol=https;user=renocl_ext:renocl_ext;"

SRC_URI_append = " \
    file://opencv.patch \
    file://clblas.patch \
    file://caffe.patch \
"

SRCREV = "313a3314e1289eae20c14257eb72b99b44dfeb5a"

inherit cmake

THIRDPARTY_DIR = "${S}/thirdParty"
SAMPLE_DIR = "${S}/samples"
OUT_DIR = "${B}/__output"
RENESAS_OPENCL_SDK_ROOT = "/opt/renesas/${P}"

CXXFLAGS_append = " -fpermissive -DGLOG_NO_ABBREVIATED_SEVERITIES -DNDEBUG"
CFLAGS_append = " -DGLOG_NO_ABBREVIATED_SEVERITIES -DNDEBUG"
EXTRA_OECMAKE = "-DCMAKE_SKIP_BUILD_RPATH=true -DCMAKE_ARCHIVE_OUTPUT_DIRECTORY=${OUT_DIR} -DCMAKE_RUNTIME_OUTPUT_DIRECTORY=${OUT_DIR} -DCMAKE_LIBRARY_OUTPUT_DIRECTORY=${OUT_DIR}"

#EXTRA_OECMAKE_append = "-DENABLE_PRECOMPILED_HEADERS=OFF -DBUILD_JPEG=ON -DBUILD_PNG=ON"

do_configure_prepend() {
    cat > ${S}/CMakeLists.txt << EOF
# Renesas OpenCL SDK cmake project
cmake_minimum_required(VERSION 3.0)

set(ENV{OUT_DIR}, "${OUT_DIR}" CACHE PATH "Output directory")
set(ENV{CLBLAS_DIR} "${THIRDPARTY_DIR}/clBLAS" CACHE PATH "clBLAS directory")
set(ENV{ATLAS_BLAS_DIR} "${THIRDPARTY_DIR}/AtlasBlas" CACHE PATH "AtlasBlas directory")
set(ENV{SAMPLE_COMMON_DIR} "${SAMPLE_DIR}/common" CACHE PATH "Common samples directory")
set(ENV{B_FD_LBP_DIR} "${SAMPLE_DIR}/BasicFaceDetLBP" CACHE PATH "Basic LBP Face Detection directory")
set(ENV{OCL_FD_LBP_DIR} "${SAMPLE_DIR}/OpenCLFaceDetLBP" CACHE PATH "OpenCL LBP Face Detection directory")
set(ENV{B_FD_HAAR_DIR} "${SAMPLE_DIR}/BasicFaceDetHaar" CACHE PATH "Basic Haar Face Detection directory")
set(ENV{OCL_FD_HAAR_DIR} "${SAMPLE_DIR}/OpenCLFaceDetHaar" CACHE PATH "OpenCL HAAR Face Detection directory")
set(ENV{B_CAFFE_SEGNET_DIR} "${SAMPLE_DIR}/BasicCaffeSegNet" CACHE PATH "Basic Caffe SegNet directory")
set(ENV{OCL_CAFFE_SEGNET_DIR} "${SAMPLE_DIR}/OpenCLCaffeSegNet" CACHE PATH "OpenCL Caffe SegNet directory")

# Libraries
add_subdirectory("\$ENV{CLBLAS_DIR}/CUSTOMCMAKE")
add_subdirectory("\$ENV{ATLAS_BLAS_DIR}")
add_subdirectory("\$ENV{B_CAFFE_SEGNET_DIR}/caffeLib")
add_subdirectory("\$ENV{OCL_CAFFE_SEGNET_DIR}/caffeCLLib")

# Samples
add_subdirectory("\$ENV{B_FD_LBP_DIR}")
add_subdirectory("\$ENV{B_FD_HAAR_DIR}")
add_subdirectory("\$ENV{B_CAFFE_SEGNET_DIR}/caffeTest")
add_subdirectory("\$ENV{OCL_FD_LBP_DIR}")
add_subdirectory("\$ENV{OCL_FD_HAAR_DIR}")
add_subdirectory("\$ENV{OCL_CAFFE_SEGNET_DIR}/caffeCLTest")
EOF
}

do_compile_prepend() {
    mkdir -p ${OUT_DIR}
}

do_install() {
    install -d ${D}/${RENESAS_OPENCL_SDK_ROOT}

    cp -R --no-dereference --preserve=mode,links ${OUT_DIR} ${D}/${RENESAS_OPENCL_SDK_ROOT}
    find ${S} \! -name __\* -maxdepth 1 -mindepth 1 -exec cp -R --no-dereference --preserve=mode,links \{\} ${D}/${RENESAS_OPENCL_SDK_ROOT} \;
    echo "export RENESAS_OPENCL_SDK_ROOT=${RENESAS_OPENCL_SDK_ROOT}" >> ${D}/${RENESAS_OPENCL_SDK_ROOT}/environment
}

FILES_${PN} = "${RENESAS_OPENCL_SDK_ROOT}"
FILES_${PN}-dbg += "${RENESAS_OPENCL_SDK_ROOT}/__output/.debug"
