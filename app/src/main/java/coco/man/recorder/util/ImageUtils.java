package coco.man.recorder.util;

import android.graphics.ImageFormat;

import androidx.camera.core.ImageProxy;

import java.nio.ByteBuffer;

/**
 * coco man
 * 2023/3/23
 **/
public class ImageUtils {

    public static byte[] getBytes(ImageProxy imageProxy, int rotationDegrees, int mWidth, int mHeight) {
        ByteBuffer yuvI420 = null;
        //获取图像格式
        int format = imageProxy.getFormat();
        //根据CameraX的官方文档，CameraX返回的数据格式：YUV_420_888
        if (format != ImageFormat.YUV_420_888) {
            //异常处理，如果有厂商修改了CameraX返回的数据格式。
        }
        //I420的数据格式，4个Y共用一组UV，其中Y数据的大小是width * height，
        // U V数据的大小都是 (width/2) * (height/2)

        //这个数组的第0个元素保存的是Y数据，data[0].getBuffer(); Y分量数据时连续存储的，
        //这个数组的第1个元素保存的是U数据，U V分量可能出现交叉存储，
        //这个数组的第2个元素保存的是V数据，
        //如果按照上面的方式，简单的获取到Y U V分量的字节数组，然后拼接到一起，在多数情况下可能是正常的，
        // 但是不能兼容多种camera分辨率，因为涉及补位的问题,要去考虑行步长planes[0].getRowStride()。
        // 当Camera的分辨率不同时，补位数据的长度就会不一样，planes[0].getRowStride()行步长也就会不一样。
        ImageProxy.PlaneProxy[] planes = imageProxy.getPlanes();
        //一个YUV420数据，需要的字节大小，
        int size = imageProxy.getWidth() * imageProxy.getHeight() * 3 / 2;
        if (null == yuvI420 || yuvI420.capacity() < size) {
            yuvI420 = ByteBuffer.allocate(size);
        }
        yuvI420.position(0);

        //获取Y数据，getPixelStride 在Y分量下，总是1，表示Y数据时连续存放的。
        int pixelStride = planes[0].getPixelStride();
        ByteBuffer yBuffer = planes[0].getBuffer();
        //行步长，表示一行的最大宽度，可能等于imageProxy.getWidth(),
        // 也可能大于imageProxy.getWidth()，比如有补位的情况。
        int rowStride = planes[0].getRowStride();
        //这个字节数组表示，在读取Y分量数据时，跳过补位的部分，因为这些补位的部分没有实际数据，只是为了字节对齐，
        // 如果没有补位 rowStride 等于 imageProxy.getWidth()，这就是一个空数组，否则，数组的长度就刚好是要跳过的长度。
        byte[] skipRow = new byte[rowStride - imageProxy.getWidth()];
        //这个数组，表示了这一行真实有效的数据。
        byte[] row = new byte[imageProxy.getWidth()];
        //循环读取每一行
        for (int i = 0; i < imageProxy.getHeight(); i++) {
            yBuffer.get(row);
            //每一行有效的数据拼接起来就是Y数据。
            yuvI420.put(row);
            //因为最后一行，没有无效的补位数据，不需要跳过，不是最后一行，才需要跳过无效的占位数据。
            if (i < imageProxy.getHeight() - 1) {
                yBuffer.get(skipRow);
            }
        }

        // 获取U V数据
        // Y分量数据时连续存储的，U V分量可能出现交叉存储，
        pixelStride = planes[1].getPixelStride();
        // 如果pixelStride值为1，表示UV是分别存储的，planes[1] ={UUUU},planes[2]={VVVV},
        // 这个情况还是比较容易获取的，

        // 如果pixelStride 为2，表示UV是交叉存储的,planes[1] ={UVUV},planes[2]={VUVU}，
        // 这个情况，要获取UV，就要拿一个，丢一个，交替取出，同时，也需要考虑跳过无效的补位数据。
        for (int i = 0; i < 3; i++) {
            ImageProxy.PlaneProxy planeProxy = planes[i];
            int uvPixelStride = planeProxy.getPixelStride();
            //如果U V是交错存放，行步长就等于imageProxy.getWidth()，同时要考虑有占位数据，会大于imageProxy.getWidth()
            // 如果U V是分离存放，行步长就等于imageProxy.getWidth() /2，同时要考虑有占位数据，会大于imageProxy.getWidth()/2
            int uvRowStride = planeProxy.getRowStride();
            ByteBuffer uvBuffer = planeProxy.getBuffer();

            //一行一行的处理，uvWidth表示了有效数据的长度。
            int uvWidth = imageProxy.getWidth() / 2;
            int uvHeight = imageProxy.getHeight() / 2;

            for (int j = 0; j < uvHeight; j++) {
                //每次处理一行中的一个字节。
                for (int k = 0; k < uvRowStride; k++) {
                    //跳过最后一行没有占位的数据，
                    if (j == uvHeight - 1) {
                        //UV没有混合在一起，
                        if (uvPixelStride == 1) {
                            //大于有效数据后，跳出内层循环（k < uvRowStride），不用关心最后的占位数据了。
                            // 因为最后一行没有占位数据。
                            if (k >= uvWidth) {
                                break;
                            }
                        } else if (uvPixelStride == 2) {
                            //UV没有混合在一起，大于有效数据后，跳出内层循环（k < uvRowStride），
                            // 不用关心最后的占位数据了。
                            // 因为最后一行没有占位数据。注意这里的有效数据的宽度是imageProxy.getWidth()。
                            //这里为什么要减1呢？因为在UV混合模式下，常规情况是UVUV，但是可能存在UVU的情况，
                            // 就是最后的V是没有的，如果不在这里 减1，在接下来get时，会报越界异常。
                            if (k >= imageProxy.getWidth() - 1) {
                                break;
                            }
                        }
                    }
                    //对每一个字节，分别取出U数据，V数据。
                    byte bt = uvBuffer.get();
                    //uvPixelStride == 1表示U V没有混合在一起
                    if (uvPixelStride == 1) {
                        //k < uvWidth表示是在有效范围内的字节。
                        if (k < uvWidth) {
                            yuvI420.put(bt);
                        }
                    } else if (uvPixelStride == 2) {
                        //uvPixelStride == 2 表示U V混合在一起。只取偶数位小标的数据，才是U / V数据，
                        // 奇数位，占位符数据都丢弃，同时这里的有效数据长度是imageProxy.getWidth()，
                        // 而不是imageProxy.getWidth() /2，
                        if (k < imageProxy.getWidth() && (k % 2 == 0)) {
                            yuvI420.put(bt);
                        }
                    }

                }
            }
        }

        //全部读取到YUV数据，以I420格式存储的字节数组。
        byte[] result = yuvI420.array();
//        //Camera 角度的旋转处理。分别以顺时针旋转Y， U ，V。
//        if (rotationDegrees == 90 || rotationDegrees == 270) {
//            result = rotation(result, imageProxy.getWidth(), imageProxy.getHeight(), rotationDegrees);
//        }
        return result;
    }

}
