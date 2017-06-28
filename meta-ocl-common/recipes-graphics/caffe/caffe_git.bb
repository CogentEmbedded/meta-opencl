SUMMARY = "Caffe"
DESCRIPTION = "Caffe is a deep learning framework made with expression, speed, and modularity in mind."
HOMEPAGE = "http://caffe.berkeleyvision.org"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=91d560803ea3d191c457b12834553991"

SRC_URI = "git://github.com/BVLC/caffe.git;branch=opencl"

SRCREV = "e0f77c3b5f4837615f05b097ba3f2a05d7413e58"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE += "-DCMAKE_INSTALL_RPATH_USE_LINK_PATH=FALSE -DBLAS=open -DBUILD_python=OFF -DUSE_GREENTEA=ON -DUSE_HDF5=OFF"

PACKAGECONFIG ??= "opencv"
PACKAGECONFIG[opencv] = "-DUSE_OPENCV=ON,-DUSE_OPENCV=OFF,opencv"
PACKAGECONFIG[leveldb] = "-DUSE_LEVELDB=ON,-DUSE_LEVELDB=OFF,leveldb"
PACKAGECONFIG[lmdb] = "-DUSE_LMDB=ON,-DUSE_LMDB=OFF,lmdb"
PACKAGECONFIG[clblas] = "-DUSE_CLBLAS=ON -DUSE_CLBLAST=OFF,-DUSE_CLBLAS=OFF,clblas"
PACKAGECONFIG[clblast] = "-DUSE_CLBLAST=ON -DUSE_CLBLAS=OFF,-DUSE_CLBLAST=OFF,clblast"

DEPENDS += "virtual/opencl opencv viennacl openblas boost protobuf protobuf-native glog gflags snappy"

PACKAGES += "\
    ${PN}-python \
"

FILES_${PN}-dev += " \
    ${datadir} \
"

FILES_${PN}-python += " \
    ${prefix}/python \
"
