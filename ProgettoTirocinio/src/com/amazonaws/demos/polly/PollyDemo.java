package com.amazonaws.demos.polly;
import java.io.IOException;
import java.io.InputStream;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.polly.AmazonPollyClient;
import com.amazonaws.services.polly.model.DescribeVoicesRequest;
import com.amazonaws.services.polly.model.DescribeVoicesResult;
import com.amazonaws.services.polly.model.OutputFormat;
import com.amazonaws.services.polly.model.SynthesizeSpeechRequest;
import com.amazonaws.services.polly.model.SynthesizeSpeechResult;
import com.amazonaws.services.polly.model.Voice;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

/*
 * 
 */
	public class PollyDemo {
		private AmazonPollyClient polly;
		private Voice voice;
		
		@SuppressWarnings("deprecation")
		public PollyDemo(Region region,String voiceId) {
			// create an Amazon Polly client in a specific region
			polly = new AmazonPollyClient(new DefaultAWSCredentialsProviderChain(),
			 new ClientConfiguration());
			polly.setRegion(region);
			// Create describe voices request.
			DescribeVoicesRequest describeVoicesRequest = new DescribeVoicesRequest();
			// Synchronously ask Amazon Polly to describe available TTS voices.
			DescribeVoicesResult describeVoicesResult =
					polly.describeVoices(describeVoicesRequest);
			 voice = describeVoicesResult.getVoices().get(0);
			voice.setId(voiceId);
			}
		
		/*
		 * 
		 */
        public InputStream synthesize(String text, OutputFormat format) throws IOException {
         SynthesizeSpeechRequest synthReq = new SynthesizeSpeechRequest().withText(text).withVoiceId(voice.getId()).withOutputFormat(format);
		 SynthesizeSpeechResult synthRes = polly.synthesizeSpeech(synthReq);
		 return synthRes.getAudioStream();
		 }
 
        
    public void audioSynth(String parlato) throws IOException, JavaLayerException{
    	//get the audio stream
    	InputStream speechStream = this.synthesize(parlato,OutputFormat.Mp3);
    	 //create an MP3 player
    	AdvancedPlayer player = new AdvancedPlayer(speechStream,javazoom.jl.player.FactoryRegistry.systemRegistry().createAudioDevice());
		 player.setPlayBackListener(new PlaybackListener() {
		 @Override
		 public void playbackStarted(PlaybackEvent evt) {
		
		 }

		 @Override
		 public void playbackFinished(PlaybackEvent evt) {
		 }
		 });


		 // play it!
		 player.play();
    }
        
      
		} 
