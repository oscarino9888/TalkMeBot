
	package com.amazonaws.demos.polly;

	import java.io.IOException;
import com.amazonaws.demos.polly.BotClass;
	import javazoom.jl.decoder.JavaLayerException;

	public class MainTest {

		
		public static void main(String[] args) throws IOException, JavaLayerException {
			
			BotClass bot=new BotClass();
			bot.communication();
			
		}
	}


