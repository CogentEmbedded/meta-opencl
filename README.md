OpenCL support
==============

The *meta-opencl* layers provide the following packages:

 * meta-ocl-rcar-gen3 layer:
   * cl-gles-user-module - OpenCL/OpenGL user module for Renesas R-Car R8A7795 SoC boards
 * meta-ocl-common layer:
   * libgfortran - GNU Fortran library (fixes)
   * opencv - Open Computer Vision library (fixes)
   * gflags - command line flags processing library
   * snappy - fast compressor/decompressor
   * lmdb - Lightning Memory-Mapped Database
   * openblas - optimized linear algebra library
   * viennacl - linear algebra library for computations on many-core architectures
   * clblas - library containing BLAS functions written in OpenCL
   * clblast - OpenCL BLAS library written in C++11
   * caffe - deep learning framework
   * renesas-opencl-sdk - separate set of Caffe/clBlas/ATLAS libraries and samples
   provided by Luxoft

*Notes:*

 * OpenCL is currently supported on Renesas R-Car R8A7795 SoC boards.
 * Luxoft renesas-opencl-sdk provides separate set of Caffe and clBlas
   libraries, and does not depend on caffe or clblas packages.

Enabling OpenCL support in Yocto for R-Car Gen3 boards
======================================================

Set up Yocto as usual, and use the following steps to enable OpenCL support before starting the build.

1. Copy OpenCL/OpenGL binaries to the meta-opencl/meta-ocl-rcar-gen3 layer:

	cp r8a7795_linux_gsx_binaries_cl_gles3.tar.bz2 meta-opencl/meta-ocl-rcar-gen3/recipes-graphics/cl-gles-module/cl-gles-user-module/

2. Add meta-python layer to *BBLAYERS* in bblayers.conf file:

	${TOPDIR}/../meta-openembedded/meta-python \

3. Add meta-opencl layers to *BBLAYERS* in bblayers.conf file:

	${TOPDIR}/../meta-opencl/meta-ocl-common \
	${TOPDIR}/../meta-opencl/meta-ocl-rcar-gen3 \

4. Replace all occurrences of gles-user-module with cl-gles-user-module,
   and add *PREFERRED_PROVIDER_virtual/opencl* variable equal to "cl-gles-user-module" in local.conf:

	PREFERRED_PROVIDER_virtual/libgles2 = "cl-gles-user-module"
	PREFERRED_PROVIDER_virtual/egl = "cl-gles-user-module"
	PREFERRED_PROVIDER_virtual/opencl = "cl-gles-user-module"

5. Enable cl-gles-user-module instead of gles-user-module in local.conf:

	PREFERRED_PROVIDER_gles-user-module = "cl-gles-user-module"

Sample configuration files are available at *meta-opencl/meta-ocl-rcar-gen3/docs/sample/conf*.

Enabling Caffe support in Yocto
===============================

Once OpenCL is enabled, enable Caffe support by adding the following line to local.conf:

	IMAGE_INSTALL_append = " caffe"

Caffe can be configured with *PACKAGECONFIG* variable.
The following configuration features are supported:

 * opencv - enable OpenCV support (ON)
 * clblas - build using clBLAS instead of ViennaCL (OFF)
 * clblast - build using CLBlast instead of ViennaCL (OFF)
 * lmdb - enable LMDB support (OFF)
 * leveldb - enable LevelDB support (OFF)

Only OpenCV support is enabled by default.
In case you need to use clBLAS instead of ViennaCL,
add the following configuration variable to local.conf file:

	PACKAGECONFIG_append_pn-caffe = " clblas"

If CLBlast support is needed, the following variable should be used:

	PACKAGECONFIG_append_pn-caffe = " clblast"

In case database support is need, use the following variable in local.conf:

	PACKAGECONFIG_append_pn-caffe = " leveldb lmdb"

Any number of features can be combined using *PACKAGECONFIG* variable,
however clBLAS and CLBlast features are mutually exclusive, for example:

	PACKAGECONFIG_append_pn-caffe = " clblas leveldb lmdb"

Finally, edit local.conf to add packages necessary for downloading and
unpacking Caffe sample models:

	IMAGE_INSTALL_append = " git python-argparse python-pyyaml python-six"

Enabling OpenCL SDK by Luxoft
=============================

Edit local.conf to add Renesas OpenCL SDK by Luxoft:

	IMAGE_INSTALL_append += " renesas-opencl-sdk"

Building Yocto images
=====================

Start the build as usual:

	bitbake core-image-weston

If the target board is to be used for natively building OpenCL applications,
it might be useful to follow additional configuration steps below.

1. Enable Fortran support in local.conf:

	FORTRAN_forcevariable = ",fortran"
	RUNTIMETARGET_append_pn-gcc-runtime = " libquadmath libgfortran"
	IMAGE_INSTALL_append = " gfortran gfortran-symlinks libgfortran libgfortran-dev"

2. Enable cmake in local.conf:

	IMAGE_INSTALL_append = " cmake"

3. Enable boost development libraries in local.conf:

	IMAGE_INSTALL_append = " boost-dev boost-staticdev"

4. Enable other development libraries in local.conf:

	IMAGE_INSTALL_append = " gflags-dev snappy-dev protobuf-dev leveldb-dev lmdb-dev opencv-dev opencv-apps"

5. Enable BLAS development libraries in local.conf:

	IMAGE_INSTALL_append = " caffe-dev clblas-dev clblast-dev viennacl-dev openblas-dev"

6. Disable cpio.gz image generation in local.conf to avoid cpio 2GB image size limitation issues:

	IMAGE_FSTYPES_remove = "cpio.gz"

7. Build SDK image:

	bitbake core-image-weston-sdk

Testing OpenCL on R-Car Gen3 boards
===================================

Use the following command to start OpenCL unit tests on the board:

	ocl_unit_test

The application should perform the unit tests, and print results to the console.
The last message should indicate the number of passed tests:

	Finished 31 tests in 171.5 seconds: 31 passed, 0 failed (100.00%)

*Note:* OpenCL unit test is included in the cl-gles-module-dev package
which is added to core-image-weston-sdk by default.

Testing ViennaCL
================

Use the following command to display ViennaCL information:

	viennacl-info

Make sure that GPU device type is used:

	Type:                          GPU

Testing Caffe
=============

Before Caffe classification test can be started, Caffe model has to be downloaded and unpacked.
The next two steps can be done on the target board directly, provided the board is connected
to the internet, and DNS is properly setup. Otherwise, the model can be downloaded to the host
system and copied to the target root filesystem later.

1. Download Caffe sources:

	git clone -b opencl -n git://github.com/BVLC/caffe.git
	cd caffe
	git checkout -b tmp e0f77c3b5f4837615f05b097ba3f2a05d7413e58

2. Download and unpack Caffe model:

	./scripts/download_model_binary.py models/bvlc_reference_caffenet
	./data/ilsvrc12/get_ilsvrc_aux.sh

3. Run Caffe classification test on the target board from Caffe source directory:

	classification \
	models/bvlc_reference_caffenet/deploy.prototxt \
	models/bvlc_reference_caffenet/bvlc_reference_caffenet.caffemodel \
	data/ilsvrc12/imagenet_mean.binaryproto \
	data/ilsvrc12/synset_words.txt \
	examples/images/cat.jpg

The test should detect the cat in the image, and produce the following output to the console:

	Use GPU with device ID 0
	---------- Prediction for examples/images/cat.jpg ----------
	0.3134 - "n02123045 tabby, tabby cat"
	0.2380 - "n02123159 tiger cat"
	0.1235 - "n02124075 Egyptian cat"
	0.1004 - "n02119022 red fox, Vulpes vulpes"
	0.0715 - "n02127052 lynx, catamount"

Testing Renesas OpenCL SDK by Luxoft
====================================

1. Export *RENESAS_OPENCL_SDK_ROOT* directory, for example:

	export RENESAS_OPENCL_SDK_ROOT=/opt/renesas/renesas-opencl-sdk-1.0.5

2. Create SDK working directory:

	mkdir -p /home/root/RenoclSDK

3. Copy binaries and libs from *RENESAS_OPENCL_SDK_ROOT* to SDK working directory:

	cp $RENESAS_OPENCL_SDK_ROOT/__output/* /home/root/RenoclSDK

4. Copy resources from *RENESAS_OPENCL_SDK_ROOT* to SDK working directory:

	cp -r $RENESAS_OPENCL_SDK_ROOT/Resources /home/root/RenoclSDK

5. Export *LD_LIBRARY_PATH*:

	export LD_LIBRARY_PATH=/home/root/RenoclSDK:$LD_LIBRARY_PATH

6. Change current working directory to the SDK working directory:

	cd /home/root/RenoclSDK

7. Run Face Detection Samples, for example:

	./BasicFaceDetHaar -image Resources/images/singleFace.jpg
	./OpenCLFaceDetHaar -image Resources/images/singleFace.jpg

8. Run Deep Learning Samples, for example:

	./BasicCaffeSegNet -image Resources/Segnet/0001TP_008550.png
	./OpenCLCaffeSegNet -image Resources/Segnet/0001TP_008550.png

For further details, please refer to the documentation available at *$RENESAS_OPENCL_SDK_ROOT/documents*.
