package br.com.conf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

public class Conecta extends Activity{
	HttpURLConnection con = null;
	URL url = null;
	String result = null;
	String request;
	private ReturnCallback private_callback = null;
	
	public interface ReturnCallback{
        void complete(String result);
    }

	/**
	 * Solicita nova conexão
	 * @param request
	 */
	public  String setRequest(String r,ReturnCallback b){
			request = r;
			private_callback = b;
			Runnable run = new Runnable(){
				public void run(){
					try{
						url = new URL(request);
						con = (HttpURLConnection) url.openConnection();
						con.setRequestMethod("POST");
						result = Conecta.readStream(con.getInputStream());
						Log.e("Solicitou",request);
						Log.e("Retornou",result);
					}catch(Exception e){
						e.printStackTrace();
						Log.e("Erro","Erro na conexão");
					}finally{
						con.disconnect();
						Log.e("Desconecta","Desconectado");
					}
					
					runOnUiThread(new Runnable(){

						@Override
						public void run() {
							private_callback.complete(result);
						}
						
					});
				}
			};
			new Thread(run).start();
		
		return result;
	}
	
	/**
	 * Lê nova stream
	 * @param InputStream
	 */
	public static String readStream(InputStream in){
		BufferedReader reader = null;
		StringBuilder builder = new StringBuilder();
		try{
			reader = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while((line = reader.readLine()) != null){
				builder.append(line+"\n");
			}
		}catch(IOException e){
			Log.e("Erro","Erro na Leitura");
			e.printStackTrace();
		}finally{
			if(reader!=null){
				try{
					reader.close();
				}catch(IOException e){
					Log.e("Erro","Erro ao fechar");
					e.printStackTrace();
				}
			}
		}
		return builder.toString();
	}
	
}
