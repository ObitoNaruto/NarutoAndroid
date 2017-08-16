LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
SILK     := silk
LOCAL_MODULE    := silk_common
LOCAL_SRC_FILES :=  $(SILK)/src/SKP_Silk_A2NLSF.c \
	$(SILK)/src/SKP_Silk_CNG.c \
	$(SILK)/src/SKP_Silk_HP_variable_cutoff_FIX.c \
	$(SILK)/src/SKP_Silk_LBRR_reset.c \
	$(SILK)/src/SKP_Silk_LPC_inv_pred_gain.c \
	$(SILK)/src/SKP_Silk_LPC_synthesis_filter.c \
	$(SILK)/src/SKP_Silk_LPC_synthesis_order16.c \
	$(SILK)/src/SKP_Silk_LP_variable_cutoff.c \
	$(SILK)/src/SKP_Silk_LSF_cos_table.c \
	$(SILK)/src/SKP_Silk_LTP_analysis_filter_FIX.c \
	$(SILK)/src/SKP_Silk_LTP_scale_ctrl_FIX.c \
	$(SILK)/src/SKP_Silk_MA.c \
	$(SILK)/src/SKP_Silk_NLSF2A.c \
	$(SILK)/src/SKP_Silk_NLSF2A_stable.c \
	$(SILK)/src/SKP_Silk_NLSF_MSVQ_decode.c \
	$(SILK)/src/SKP_Silk_NLSF_MSVQ_encode_FIX.c \
	$(SILK)/src/SKP_Silk_NLSF_VQ_rate_distortion_FIX.c \
	$(SILK)/src/SKP_Silk_NLSF_VQ_sum_error_FIX.c \
	$(SILK)/src/SKP_Silk_NLSF_VQ_weights_laroia.c \
	$(SILK)/src/SKP_Silk_NLSF_stabilize.c \
	$(SILK)/src/SKP_Silk_NSQ.c \
	$(SILK)/src/SKP_Silk_NSQ_del_dec.c \
	$(SILK)/src/SKP_Silk_PLC.c \
	$(SILK)/src/SKP_Silk_VAD.c \
	$(SILK)/src/SKP_Silk_VQ_nearest_neighbor_FIX.c \
	$(SILK)/src/SKP_Silk_ana_filt_bank_1.c \
	$(SILK)/src/SKP_Silk_apply_sine_window.c \
	$(SILK)/src/SKP_Silk_array_maxabs.c \
	$(SILK)/src/SKP_Silk_autocorr.c \
	$(SILK)/src/SKP_Silk_biquad.c \
	$(SILK)/src/SKP_Silk_biquad_alt.c \
	$(SILK)/src/SKP_Silk_burg_modified.c \
	$(SILK)/src/SKP_Silk_bwexpander.c \
	$(SILK)/src/SKP_Silk_bwexpander_32.c \
	$(SILK)/src/SKP_Silk_code_signs.c \
	$(SILK)/src/SKP_Silk_control_audio_bandwidth.c \
	$(SILK)/src/SKP_Silk_control_codec_FIX.c \
	$(SILK)/src/SKP_Silk_corrMatrix_FIX.c \
	$(SILK)/src/SKP_Silk_create_init_destroy.c \
	$(SILK)/src/SKP_Silk_dec_API.c \
	$(SILK)/src/SKP_Silk_decode_core.c \
	$(SILK)/src/SKP_Silk_decode_frame.c \
	$(SILK)/src/SKP_Silk_decode_parameters.c \
	$(SILK)/src/SKP_Silk_decode_pitch.c \
	$(SILK)/src/SKP_Silk_decode_pulses.c \
	$(SILK)/src/SKP_Silk_decoder_set_fs.c \
	$(SILK)/src/SKP_Silk_detect_SWB_input.c \
	$(SILK)/src/SKP_Silk_div_oabi.c \
	$(SILK)/src/SKP_Silk_enc_API.c \
	$(SILK)/src/SKP_Silk_encode_frame_FIX.c \
	$(SILK)/src/SKP_Silk_encode_parameters.c \
	$(SILK)/src/SKP_Silk_encode_pulses.c \
	$(SILK)/src/SKP_Silk_find_LPC_FIX.c \
	$(SILK)/src/SKP_Silk_find_LTP_FIX.c \
	$(SILK)/src/SKP_Silk_find_pitch_lags_FIX.c \
	$(SILK)/src/SKP_Silk_find_pred_coefs_FIX.c \
	$(SILK)/src/SKP_Silk_gain_quant.c \
	$(SILK)/src/SKP_Silk_init_encoder_FIX.c \
	$(SILK)/src/SKP_Silk_inner_prod_aligned.c \
	$(SILK)/src/SKP_Silk_interpolate.c \
	$(SILK)/src/SKP_Silk_k2a.c \
	$(SILK)/src/SKP_Silk_k2a_Q16.c \
	$(SILK)/src/SKP_Silk_lin2log.c \
	$(SILK)/src/SKP_Silk_log2lin.c \
	$(SILK)/src/SKP_Silk_noise_shape_analysis_FIX.c \
	$(SILK)/src/SKP_Silk_pitch_analysis_core.c \
	$(SILK)/src/SKP_Silk_pitch_est_tables.c \
	$(SILK)/src/SKP_Silk_prefilter_FIX.c \
	$(SILK)/src/SKP_Silk_process_NLSFs_FIX.c \
	$(SILK)/src/SKP_Silk_process_gains_FIX.c \
	$(SILK)/src/SKP_Silk_quant_LTP_gains_FIX.c \
	$(SILK)/src/SKP_Silk_range_coder.c \
	$(SILK)/src/SKP_Silk_regularize_correlations_FIX.c \
	$(SILK)/src/SKP_Silk_resampler.c \
	$(SILK)/src/SKP_Silk_resampler_down2.c \
	$(SILK)/src/SKP_Silk_resampler_down2_3.c \
	$(SILK)/src/SKP_Silk_resampler_down3.c \
	$(SILK)/src/SKP_Silk_resampler_private_AR2.c \
	$(SILK)/src/SKP_Silk_resampler_private_ARMA4.c \
	$(SILK)/src/SKP_Silk_resampler_private_IIR_FIR.c \
	$(SILK)/src/SKP_Silk_resampler_private_copy.c \
	$(SILK)/src/SKP_Silk_resampler_private_down4.c \
	$(SILK)/src/SKP_Silk_resampler_private_down_FIR.c \
	$(SILK)/src/SKP_Silk_resampler_private_up2_HQ.c \
	$(SILK)/src/SKP_Silk_resampler_private_up4.c \
	$(SILK)/src/SKP_Silk_resampler_rom.c \
	$(SILK)/src/SKP_Silk_resampler_up2.c \
	$(SILK)/src/SKP_Silk_residual_energy16_FIX.c \
	$(SILK)/src/SKP_Silk_residual_energy_FIX.c \
	$(SILK)/src/SKP_Silk_scale_copy_vector16.c \
	$(SILK)/src/SKP_Silk_scale_vector.c \
	$(SILK)/src/SKP_Silk_schur.c \
	$(SILK)/src/SKP_Silk_schur64.c \
	$(SILK)/src/SKP_Silk_shell_coder.c \
	$(SILK)/src/SKP_Silk_sigm_Q15.c \
	$(SILK)/src/SKP_Silk_solve_LS_FIX.c \
	$(SILK)/src/SKP_Silk_sort.c \
	$(SILK)/src/SKP_Silk_sum_sqr_shift.c \
	$(SILK)/src/SKP_Silk_tables_LTP.c \
	$(SILK)/src/SKP_Silk_tables_NLSF_CB0_10.c \
	$(SILK)/src/SKP_Silk_tables_NLSF_CB0_16.c \
	$(SILK)/src/SKP_Silk_tables_NLSF_CB1_10.c \
	$(SILK)/src/SKP_Silk_tables_NLSF_CB1_16.c \
	$(SILK)/src/SKP_Silk_tables_gain.c \
	$(SILK)/src/SKP_Silk_tables_other.c \
	$(SILK)/src/SKP_Silk_tables_pitch_lag.c \
	$(SILK)/src/SKP_Silk_tables_pulses_per_block.c \
	$(SILK)/src/SKP_Silk_tables_sign.c \
	$(SILK)/src/SKP_Silk_tables_type_offset.c \
	$(SILK)/src/SKP_Silk_warped_autocorrelation_FIX.c

LOCAL_C_INCLUDES += $(LOCAL_PATH)/$(SILK)/src $(LOCAL_PATH)/$(SILK)/interface
LOCAL_CFLAGS = -O3
LOCAL_ARM_MODE := arm
LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog
include $(BUILD_STATIC_LIBRARY)

##pre-build
#include $(CLEAR_VARS)
#
#LOCAL_MODULE            := silk_common-prebuilt
#LOCAL_SRC_FILES         := $(LOCAL_PATH)/libsilk_common.a
#LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/$(SILK)/src $(LOCAL_PATH)/$(SILK)/interface
#include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := silk_jni
LOCAL_SRC_FILES := silk_jni.cpp
LOCAL_C_INCLUDES += $(LOCAL_PATH)/$(SILK)/src $(LOCAL_PATH)/$(SILK)/interface
LOCAL_CFLAGS = -O3
LOCAL_STATIC_LIBRARIES :=  silk_common
LOCAL_ARM_MODE := arm
LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog
include $(BUILD_SHARED_LIBRARY)


# native cache
include $(CLEAR_VARS)
LOCAL_MODULE    := AlipayBitmapNative
LOCAL_SRC_FILES := $(LOCAL_PATH)/AlipayBitmapNative.c
LOCAL_LDLIBS    := -lm -llog -ljnigraphics

include $(BUILD_SHARED_LIBRARY)

# fresco bitmaps
include $(CLEAR_VARS)
LOCAL_MODULE := bitmaps
LOCAL_SRC_FILES := \
	$(LOCAL_PATH)/bitmaps/Bitmaps.c

LOCAL_CFLAGS += -Wall -Wextra -Werror -std=c11
LOCAL_CFLAGS += -fvisibility=hidden
LOCAL_LDLIBS := -ljnigraphics

include $(BUILD_SHARED_LIBRARY)