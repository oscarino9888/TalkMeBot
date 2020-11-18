package com.ibm.demo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.text_to_speech.v1.model.SynthesizeOptions;
import com.ibm.watson.text_to_speech.v1.util.WaveUtils;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

public class WatsonDemo {

public static void main(String[] args) throws JavaLayerException {
	IamAuthenticator authenticator = new IamAuthenticator("t_nSM8Gok7o8FvSdnx9krmjWTN4tv67wNJHPe25nC45y");
	TextToSpeech textToSpeech = new TextToSpeech(authenticator);
	textToSpeech.setServiceUrl("https://api.eu-gb.text-to-speech.watson.cloud.ibm.com/instances/48d18836-fd75-446f-836c-6b2bb144c364");

	try {
	  SynthesizeOptions synthesizeOptions =
	    new SynthesizeOptions.Builder()
	      .text("<speak>  ciao lupo</speak>")
	      .accept("audio/mp3")
	      .voice("it-IT_FrancescaV3Voice")
	      .build();

	  InputStream inputStream =
	    textToSpeech.synthesize(synthesizeOptions).execute().getResult();
	  InputStream in = WaveUtils.reWriteWaveHeader(inputStream);
	  AdvancedPlayer player = new AdvancedPlayer(in,javazoom.jl.player.FactoryRegistry.systemRegistry().createAudioDevice());
		 player.setPlayBackListener(new PlaybackListener() {
		 @Override
		 public void playbackStarted(PlaybackEvent evt) {
		 System.out.println("Inizio");
		 }

		 @Override
		 public void playbackFinished(PlaybackEvent evt) {
		 System.out.println("Fine");
		 }
		 });


		 // play it!
		 player.play();

	  OutputStream out = new FileOutputStream("C:\\Users\\oscar\\Desktop\\Audio\\hello_world.wav");
	  byte[] buffer = new byte[1024];
	  int length;
	  while ((length = in.read(buffer)) > 0) {
	    out.write(buffer, 0, length);
	  }

	  out.close();
	  in.close();
	  inputStream.close();
	} catch (IOException e) {
	  e.printStackTrace();
	}
}	
	
	
}
