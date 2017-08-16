package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.encode;

public class VideoHelper {
    public static void NV21ToYUV420Planar(final byte[] input, final byte[] output,
                                          final int width, final int height) {
        final int frameSize = width * height;
        final int qFrameSize = frameSize / 4;

        System.arraycopy(input, 0, output, 0, frameSize); // Y

        for (int i = 0; i < qFrameSize; i++) {
            output[frameSize + i] = input[frameSize + i + i + 1]; // Cb (U)
            output[frameSize + i + qFrameSize] = input[frameSize + i + i]; // Cr (V)
        }
    }

   
    
    public static void rotateYUV420SPClockwiseDegree90(byte[] src, byte[] dst, int width, int height)
    {
        int nWidth = 0, nHeight = 0;
        int wh = 0;
        int uvHeight = 0;
        if(width != nWidth || height != nHeight) {
            nWidth = width;
            nHeight = height;
            wh = width * height;
            uvHeight = height >> 1;//uvHeight = height / 2
        }

        //旋转Y
        int k = 0;
        for(int i = 0; i < width; i++){
            int nPos = width - 1;
            for(int j = 0; j < height; j++)
            {
                dst[k ++] = src[nPos - i];
                nPos += width;
            }
        }

        for(int i = 0; i < width; i+=2){
            int nPos = wh + width - 1;
            for(int j = 0; j < uvHeight; j++) {
                dst[k] = src[nPos - i - 1];
                dst[k + 1] = src[nPos - i];
                k += 2;
                nPos += width;
            }
        }
        return;

    }

    public static void rotateYUV420SPAntiClockwiseDegree90(byte[] data, byte[] output, int width, int height)
    {
        // Rotate the Y luma
        int i = 0;
        for (int x = 0; x < width; x++) {
            for (int y = height - 1; y >= 0; y--) {
                output[i++] = data[y * width + x];
            }
        }
        // Rotate the U and V color components
        int ustart = width * height;
        i = ustart * 3 / 2 - 1;

        int pos = height >> 1;
        for (int x = width - 1; x > 0; x = x - 2) {
            for (int y = 0; y < pos; y++) {
                output[i --] = data[ustart + (y * width) + x];
                output[i --] = data[ustart + (y * width) + (x - 1)];
            }
        }
        return;
    }
    
    public static byte[] cropYUV420(byte[] data,int imageW,int imageH,int newImageH)
    {
		int cropH;
		int i,j,count,tmp;
		byte[] yuv = new byte[imageW*newImageH*3/2];
 
		cropH = (imageH - newImageH)/2;
 
		count = 0;
		for(j=cropH;j<cropH+newImageH;j++){
			for(i=0;i<imageW;i++){
				yuv[count++] = data[j*imageW+i];
			}
		}
 
		//Cr Cb
		tmp = imageH+cropH/2;
		for(j=tmp;j<tmp + newImageH/2;j++){
			for(i=0;i<imageW;i++){
				yuv[count++] = data[j*imageW+i];
			}
		}
 
		return yuv;
	}
    
    static public void encodeYUV420SP(byte[] yuv420sp, int[] argb,
            int width, int height) {
            final int frameSize = width * height;

            int yIndex = 0;
            int uvIndex = frameSize;

            int a, R, G, B, Y, U, V;
            int index = 0;
            for (int j = 0; j < height; j++) {
                for (int i = 0; i < width; i++) {

                    a = (argb[index] & 0xff000000) >> 24; // a is not used obviously
                    R = (argb[index] & 0xff0000) >> 16;
                    G = (argb[index] & 0xff00) >> 8;
                    B = (argb[index] & 0xff) >> 0;

                    // well known RGB to YUV algorithm
                    Y = ( (  66 * R + 129 * G +  25 * B + 128) >> 8) +  16;
                    U = ( ( -38 * R -  74 * G + 112 * B + 128) >> 8) + 128;
                    V = ( ( 112 * R -  94 * G -  18 * B + 128) >> 8) + 128;

                    // NV21 has a plane of Y and interleaved planes of VU each sampled by a factor of 2
                    //    meaning for every 4 Y pixels there are 1 V and 1 U.  Note the sampling is every other
                    //    pixel AND every other scanline.
                    yuv420sp[yIndex++] = (byte) ((Y < 0) ? 0 : ((Y > 255) ? 255 : Y));
                    if (j % 2 == 0 && index % 2 == 0) { 
                        yuv420sp[uvIndex++] = (byte)((V<0) ? 0 : ((V > 255) ? 255 : V));
                        yuv420sp[uvIndex++] = (byte)((U<0) ? 0 : ((U > 255) ? 255 : U));
                    }

                    index ++;
                }
            }
        }
}


