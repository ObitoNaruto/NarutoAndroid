/*
 * Copyright (C) 2009 The Sipdroid Open Source Project
 * 
 * This file is part of Sipdroid (http://www.sipdroid.org)
 * 
 * Sipdroid is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this source code; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

 
#include <jni.h>

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>

/* Define codec specific settings */
#define MAX_ENCODE_BYTES_PER_FRAME      250 // Equals peak bitrate of 100 kbps
#define MAX_DECODE_BYTES_PER_FRAME      1024
#define MAX_INPUT_FRAMES                5
#define FRAME_LENGTH_MS                 20
#define MAX_API_FS_KHZ                  48
#define MAX_FRAME_LENGTH                480
#define MAX_LBRR_DELAY                  2
// the header length of the RTP frame (must skip when en/decoding)
#define	RTP_HDR_SIZE	                12

#include <android/log.h> 

#define LOG_TAG "silk" // text for log tag 

#include "SKP_Silk_SDK_API.h"

//#define DEBUG_SILK


int max_frame = 160;

/* encoder parameters */
static int encoder_codec_open = 0;
void      *psEnc;

/* default settings */
SKP_int32 API_fs_Hz = 24000;
SKP_int32 max_internal_fs_Hz = API_fs_Hz;
SKP_int32 targetRate_bps = 25000;
SKP_int32 packetSize_ms = 20;
SKP_int32 frameSizeReadFromFile_ms = 20;
SKP_int32 packetLoss_perc = 0;
SKP_int32 complexity_mode = 0;
SKP_int32 DTX_enabled = 0, INBandFEC_enabled = 0, quiet = 0;
SKP_SILK_SDK_EncControlStruct encControl; // Struct for input to encoder


/* decoder parameters */
static int decoder_codec_open = 0;
void      *psDec;
SKP_SILK_SDK_DecControlStruct DecControl;

/************************* Error Log  *******************************/
void Print_Decode_Error_Msg(int errcode) {
	switch (errcode) {
		case SKP_SILK_DEC_INVALID_SAMPLING_FREQUENCY:
		#ifdef DEBUG_SILK
			__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
				"!!!!!!!!!!! Decode_Error_Message: %d\nOutput sampling frequency lower than internal decoded sampling frequency\n", errcode);
		#endif
			break;
		case SKP_SILK_DEC_PAYLOAD_TOO_LARGE:
		#ifdef DEBUG_SILK
			__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
				"!!!!!!!!!!! Decode_Error_Message: %d\nPayload size exceeded the maximum allowed 1024 bytes\n", errcode);
			break;
		#endif
		case SKP_SILK_DEC_PAYLOAD_ERROR:
		#ifdef DEBUG_SILK
			__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
				"!!!!!!!!!!! Decode_Error_Message: %d\nPayload has bit errors\n", errcode);
		#endif
			break;
	}
}

void Print_Encode_Error_Msg(int errcode) {
	switch (errcode) {
		case SKP_SILK_ENC_INPUT_INVALID_NO_OF_SAMPLES:
		#ifdef DEBUG_SILK
			__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
				"!!!!!!!!!!! Decode_Error_Message: %d\nInput length is not a multiplum of 10 ms, or length is longer than the packet length\n", errcode);
		#endif
			break;
		case SKP_SILK_ENC_FS_NOT_SUPPORTED:
		#ifdef DEBUG_SILK
			__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
				"!!!!!!!!!!! Decode_Error_Message: %d\nSampling frequency not 8000, 12000, 16000 or 24000 Hertz \n", errcode);
		#endif
			break;
		case SKP_SILK_ENC_PACKET_SIZE_NOT_SUPPORTED:
		#ifdef DEBUG_SILK
			__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
				"!!!!!!!!!!! Decode_Error_Message: %d\nPacket size not 20, 40, 60, 80 or 100 ms\n", errcode);
		#endif
			break;
		case SKP_SILK_ENC_PAYLOAD_BUF_TOO_SHORT:
		#ifdef DEBUG_SILK
			__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
				"!!!!!!!!!!! Decode_Error_Message: %d\nAllocated payload buffer too short \n", errcode);
		#endif
			break;
		case SKP_SILK_ENC_INVALID_LOSS_RATE:
		#ifdef DEBUG_SILK
			__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
				"!!!!!!!!!!! Decode_Error_Message: %d\nLoss rate not between 0 and 100 percent\n", errcode);
		#endif
			break;
		case SKP_SILK_ENC_INVALID_COMPLEXITY_SETTING:
		#ifdef DEBUG_SILK
			__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
				"!!!!!!!!!!! Decode_Error_Message: %d\nComplexity setting not valid, use 0, 1 or 2\n", errcode);
		#endif
			break;
		case SKP_SILK_ENC_INVALID_INBAND_FEC_SETTING:
		#ifdef DEBUG_SILK
			__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
				"!!!!!!!!!!! Decode_Error_Message: %d\nInband FEC setting not valid, use 0 or 1\n", errcode);
		#endif
			break;
		case SKP_SILK_ENC_INVALID_DTX_SETTING:
		#ifdef DEBUG_SILK
			__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
				"!!!!!!!!!!! Decode_Error_Message: %d\nDTX setting not valid, use 0 or 1\n", errcode);
		#endif
			break;
		case SKP_SILK_ENC_INTERNAL_ERROR:
		#ifdef DEBUG_SILK
			__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
				"!!!!!!!!!!! Decode_Error_Message: %d\nInternal encoder error\n", errcode);
		#endif
			break;
	}
}
/********************************************************/
/*************************  Utils   *******************************/
void swap_endian(
    SKP_int16       vec[],
    SKP_int         len
)
{
    SKP_int i;
    SKP_int16 tmp;
    SKP_uint8 *p1, *p2;

    for( i = 0; i < len; i++ ){
        tmp = vec[ i ];
        p1 = (SKP_uint8 *)&vec[ i ]; p2 = (SKP_uint8 *)&tmp;
        p1[ 0 ] = p2[ 1 ]; p1[ 1 ] = p2[ 0 ];
    }
}
/********************************************************/



extern "C"
JNIEXPORT jint JNICALL Java_com_alipay_android_phone_mobilecommon_multimediabiz_biz_audio_silk_SilkApi_openEncoder
  (JNIEnv *env, jobject obj, jint complexity, jint sampleRate, jint targetRate) {
	int ret;
	SKP_int32 encSizeBytes;
	SKP_SILK_SDK_EncControlStruct encStatus;  // Struct for status of encoder


	if (encoder_codec_open++ != 0)
		return (jint)0;

		
    /* Create Encoder */
	ret = SKP_Silk_SDK_Get_Encoder_Size( &encSizeBytes );

#ifdef DEBUG_SILK
    __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, 
            "SKP_Silk_SDK_Get_Encoder_Size ret = %d, encSizeBytes = %d\n", ret, encSizeBytes);
#endif	

    psEnc = malloc( encSizeBytes );

    /* Reset Encoder */
        ret = SKP_Silk_SDK_InitEncoder( psEnc, &encStatus );
#ifdef DEBUG_SILK
    if( ret ) {
		__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, 
            "SKP_Silk_SDK_InitEncoder returned %d", ret );
    }
#endif
    
    /* Set Encoder parameters */
    max_frame							= sampleRate * 20 / 1000;
    API_fs_Hz 							= sampleRate;
    targetRate_bps                      = targetRate;
    encControl.API_sampleRate        	= API_fs_Hz;
	encControl.maxInternalSampleRate 	= 24000;
	encControl.packetSize            	= ( packetSize_ms * API_fs_Hz ) / 1000;
	encControl.packetLossPercentage  	= packetLoss_perc;
	encControl.useInBandFEC          	= INBandFEC_enabled;
	encControl.useDTX                	= DTX_enabled;
	encControl.complexity            	= complexity_mode;
	encControl.bitRate               	= ( targetRate_bps > 0 ? targetRate_bps : 0 );
	
	return (jint)0;
}

extern "C"
JNIEXPORT jint JNICALL Java_com_alipay_android_phone_mobilecommon_multimediabiz_biz_audio_silk_SilkApi_encode
    (JNIEnv *env, jobject obj, jshortArray lin, jint offset, jbyteArray encoded, jint size) {

    jbyte	  enc_payload[ MAX_ENCODE_BYTES_PER_FRAME * MAX_INPUT_FRAMES ];
    jshort    in[ MAX_FRAME_LENGTH * MAX_INPUT_FRAMES ];	
	int ret, i, frsz = max_frame;
	SKP_int16 nBytes;
	unsigned int lin_pos = 0;
	
	if (!encoder_codec_open)
		return 0;
		
#ifdef DEBUG_SILK
    __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, 
            "encoding frame size: %d\toffset: %d\tmax_frame: %d\n", size, offset, max_frame);
#endif


	for (i = 0; i < size; i += max_frame) {
#ifdef DEBUG_SILK
		__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, 
            "encoding frame size: %d\toffset: %d i: %d\n", size, offset, i); 		
#endif
		int delta = size - i;
		//length fix
		if (delta < max_frame)
		{
			frsz = delta;
			#ifdef DEBUG_SILK
			__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
						"encoding frame delta: %d\toffset: %d i: %d, size: %d\n", delta, offset, i, size);
			#endif
		}
		env->GetShortArrayRegion(lin, offset + i, frsz, in);
        /* max payload size */
        nBytes = MAX_ENCODE_BYTES_PER_FRAME * MAX_INPUT_FRAMES;

        ret = SKP_Silk_SDK_Encode( psEnc, &encControl, in, (SKP_int16)frsz, (SKP_uint8 *)enc_payload, &nBytes );
        if( ret ) {
        #ifdef DEBUG_SILK
			__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, 
				"!!!!!!!! SKP_Silk_Encode returned: %d\n", ret);
		#endif
			Print_Encode_Error_Msg(ret);
            break;
        }
#ifdef DEBUG_SILK
			__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, 
				"Enocded nBytes: %d\n", nBytes); 		
#endif

        /* Write payload*/
		env->SetByteArrayRegion(encoded, lin_pos, nBytes, enc_payload);
		lin_pos += nBytes;
	}
#ifdef DEBUG_SILK
	__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, 
        "encoding **END** frame size: %d\toffset: %d i: %d lin_pos: %d\n", size, offset, i, lin_pos);
#endif		

    return (jint)lin_pos;
}

extern "C"
JNIEXPORT void JNICALL Java_com_alipay_android_phone_mobilecommon_multimediabiz_biz_audio_silk_SilkApi_closeEncoder
    (JNIEnv *env, jobject obj) {

	if (--encoder_codec_open != 0)
		return;
    /* Free Encoder */
    free( psEnc );
}

/*************************  Decode part   ****************************/
extern "C"
JNIEXPORT jint JNICALL Java_com_alipay_android_phone_mobilecommon_multimediabiz_biz_audio_silk_SilkApi_openDecoder
  (JNIEnv *env, jobject obj, jint sampleRate) {
	int ret;
	SKP_int32 decSizeBytes;

#ifdef DEBUG_SILK
    __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
            "Java_com_alipay_android_phone_mobilecommon_multimediabiz_biz_audio_silk_SilkApi_openDecoder sampleRate = %d\n", sampleRate);
#endif

	if (decoder_codec_open++ != 0)
		return (jint)0;

	/* Set Decoder parameters */
    API_fs_Hz 							= sampleRate;
    DecControl.API_sampleRate        	= API_fs_Hz;
    /* Initialize to one frame per packet, for proper concealment before first packet arrives */
    DecControl.framesPerPacket = 1;


    /* Create Encoder */
	ret = SKP_Silk_SDK_Get_Decoder_Size( &decSizeBytes );

#ifdef DEBUG_SILK
    __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
            "SKP_Silk_SDK_Get_Decoder_Size ret = %d, decSizeBytes = %d, DecControl.API_sampleRate = %d\n",
             ret, decSizeBytes, DecControl.API_sampleRate);
#endif

    psDec = malloc( decSizeBytes );

    /* Reset Decoder */
    ret = SKP_Silk_SDK_InitDecoder( psDec );
#ifdef DEBUG_SILK
    if( ret ) {
		__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
            "SKP_Silk_SDK_InitEncoder returned %d", ret );
    }
#endif


#ifdef DEBUG_SILK
    __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
        "##### AFTER SKP_Silk_SDK_InitDecoder ********  decoder_codec_open: %d\n", decoder_codec_open);
#endif
	return (jint)0;
}

extern "C"
JNIEXPORT jint JNICALL Java_com_alipay_android_phone_mobilecommon_multimediabiz_biz_audio_silk_SilkApi_decode
    (JNIEnv *env, jobject obj, jbyteArray encoded, jshortArray lin, jint size) {

    int ret, pos = 0;
    SKP_int16 len;

    jbyte buffer [MAX_DECODE_BYTES_PER_FRAME * MAX_INPUT_FRAMES * ( MAX_LBRR_DELAY + 1 ) ];
    jshort output_buffer[( MAX_FRAME_LENGTH << 1 ) * MAX_INPUT_FRAMES ];

#ifdef DEBUG_SILK
    __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
        "##### BEFORE DECODE ********  decoder_codec_open: %d\n", decoder_codec_open);
#endif

	if (!decoder_codec_open)
		return 0;

#ifdef DEBUG_SILK
	__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
        "##### BEGIN DECODE ********  decoding frame size: %d\n", size);
#endif
    // all data, no head
    env->GetByteArrayRegion(encoded, 0, size, buffer);

    ret = SKP_Silk_SDK_Decode( psDec, &DecControl, 0, (SKP_uint8 *) buffer, size, output_buffer, &len );
    if( ret ) {
    #ifdef DEBUG_SILK
        __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
            "!!!!!!!! SKP_Silk_SDK_Decode returned: %d\n", ret);
    #endif
        Print_Decode_Error_Msg(ret);
    }
#ifdef DEBUG_SILK
    __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
        "##### DECODED length: %d\t Frame #: %d\n", len, size);
#endif

//    swap_endian(output_buffer, len);

    env->SetShortArrayRegion(lin, 0, len, output_buffer);
	return (jint)len;
}

extern "C"
JNIEXPORT void JNICALL Java_com_alipay_android_phone_mobilecommon_multimediabiz_biz_audio_silk_SilkApi_closeDecoder
    (JNIEnv *env, jobject obj) {

	if (--decoder_codec_open != 0)
		return;
    /* Free decoder */
    free( psDec );
}
