package com.lutfi.spchallenge;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;
import org.springframework.stereotype.Service;

@Service
public class AudioHelper {
    public void convertMP4ToWAV(String inputPath, String outputPath) throws FrameGrabber.Exception, FrameRecorder.Exception {
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputPath)) {
            grabber.start();

            // Create recorder for WAV output
            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputPath, grabber.getAudioChannels());

            // Configure audio settings
            recorder.setFormat("wav");
            recorder.setSampleRate(grabber.getSampleRate());
            recorder.setAudioChannels(grabber.getAudioChannels());
            recorder.setAudioCodec(avcodec.AV_CODEC_ID_PCM_S16LE); // Standard WAV codec

            recorder.start();

            Frame frame;
            // Only process audio frames
            while ((frame = grabber.grab()) != null) {
                if (frame.samples != null) {
                    recorder.record(frame);
                }
            }

            recorder.stop();
            recorder.close();
        }
    }
}
